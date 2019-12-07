package com.chausov.kotlisp.parser

import com.chausov.kotlisp.lang.*
import com.chausov.kotlisp.lexer.LispTokenTypes
import com.chausov.kotlisp.lexer.Token

class LispParser: Parser {
    override fun parse(tokens: Iterable<Token>): LispType {
        return parse(TokenReader(tokens))
    }

    private fun parse(reader: TokenReader): LispType {
        return parseForm(reader)
    }

    //todo: maybe null is ok here and only means to skip this rep iteration
    private fun parseForm(reader: TokenReader): LispType {
        return when (reader.peek()?.getTokenType()) {
            null -> throw ParserException("parseFrom: unexpected token (null)")
            LispTokenTypes.WHITESPACE -> {
                reader.advance()
                parseForm(reader)
            }
            LispTokenTypes.SYMBOLS -> parseAtom(reader)
            LispTokenTypes.SPECIAL_CHARACTER -> parseSpecialSymbol(reader)
            LispTokenTypes.STRING -> parseString(reader)
            else -> parseAtom(reader)
        }
    }

    private fun parseSpecialSymbol(reader: TokenReader): LispType =
        when (reader.peek()?.getText()) {
            "(" -> parseList(reader)
            "[" -> parseVector(reader)
            "{" -> parseHashMap(reader)
            else -> throw ParserException("parseSpecialSymbol: unexpected token at ${reader.peek()?.getOffset()}")
        }

    private fun parseList(reader: TokenReader): LispList =
        LispList(parseSequence(reader, ")"))

    private fun parseVector(reader: TokenReader): LispVector =
        LispVector(parseSequence(reader, "]"))

    private fun parseSequence(reader: TokenReader, endSymbol: String): List<LispType> {
        val sequence = ArrayList<LispType>()
        reader.advance()
        while (true) {
            val form = when (reader.peek()?.getText()) {
                null -> throw ParserException("parseSequence: unexpected token (null)")
                endSymbol -> {
                    reader.advance()
                    null
                }
                else -> parseForm(reader)
            } ?: break
            sequence.add(form)
        }
        return sequence
    }

    private fun parseAtom(reader: TokenReader): LispType {
        val token = reader.peek()
        return when (token?.getText()?.get(0)) {
            null -> throw ParserException("parseSymbol: unexpected token (null)")
            ':' -> parseKeyword(reader)
            else -> {
                reader.advance()
                tryParseAsNumber(token)
                    ?: tryParseAsConstant(token)
                    ?: parseAsSymbol(token)
            }
        }
    }

    private fun tryParseAsNumber(token: Token): LispType? {
        val asNumber = token.getText().toBigIntegerOrNull()
        if (asNumber != null) {
            return LispNumber(asNumber)
        }
        return null
    }

    private fun tryParseAsConstant(token: Token): LispType? =
        LispConstants.getConstantByName(token.getText())


    private fun parseAsSymbol(token: Token): LispType =
        LispSymbol(token.getText())

    private fun parseString(reader: TokenReader): LispString {
        val token = reader.peek()
        reader.advance()

        val tokenText = token?.getText() ?: throw ParserException("parseString: unexpected token (null)")

        if (tokenText.last() != '"') {
            throw ParserException("parseString: unbalanced string started at ${token.getOffset()}")
        }

        return LispString(tokenText.substring(1, tokenText.length - 1))
    }

    private fun parseHashMap(reader: TokenReader): LispHashMap {
        val map = HashMap<LispHashable, LispType>()
        val tokens = parseSequence(reader, "}")
        if (tokens.size % 2 != 0) throw ParserException("parseHashMap: last key without value")

        for (i in tokens.indices step 2) {
            val key = tokens[i]
            val value = tokens[i + 1]
            if (key !is LispHashable) {
                throw ParserException("parseHashMap: ${key.javaClass.simpleName} cannot be a map key")
            }
            if (map[key] != null) {
                throw ParserException("parseHashMap: ${key.javaClass.simpleName} duplicate")
            }
            map[key] = value
        }
        return LispHashMap(map)
    }

    private fun parseKeyword(reader: TokenReader): LispKeyword {
        val token = reader.peek() ?: throw ParserException("parseKeyword: unexpected token (null)")
        reader.advance()
        return LispKeyword(token.getText().drop(1))
    }
}