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
            LispTokenTypes.SYMBOLS -> parseSymbol(reader)
            LispTokenTypes.SPECIAL_CHARACTER -> parseSpecialSymbol(reader)
            LispTokenTypes.STRING -> parseString(reader)
            else -> parseSymbol(reader)
        }
    }

    private fun parseSpecialSymbol(reader: TokenReader): LispType =
        when (reader.peek()?.getText()) {
            "(" -> parseSequence(reader, ")", LispList())
            "[" -> parseSequence(reader, "]", LispVector())
            "{" -> parseHashMap(reader, LispHashMap())
            else -> throw ParserException("parseSpecialSymbol: unexpected token at ${reader.peek()?.getOffset()}")
        }

    private fun parseSequence(reader: TokenReader, endSymbol: String, sequence: LispSequence): LispSequence {
        reader.advance()

        while (true) {
            val form = when (reader.peek()?.getText()) {
                null -> throw ParserException("parseSpecialSymbol: unexpected token (null)")
                endSymbol -> {
                    reader.advance()
                    null
                }
                else -> parseForm(reader)
            } ?: break
            sequence.addChild(form)
        }
        return sequence
    }

    private fun parseSymbol(reader: TokenReader): LispType {
        val token = reader.peek()
        return when (token?.getText()?.get(0)) {
            null -> throw ParserException("parseSymbol: unexpected token (null)")
            ':' -> parseKeyword(reader)
            else -> {
                reader.advance()
                val asNumber = token.getText().toBigIntegerOrNull()
                if (asNumber != null) {
                    LispNumber(asNumber)
                } else {
                    LispSymbol(token.getText())
                }
            }
        }
    }

    private fun parseString(reader: TokenReader): LispString {
        val token = reader.peek()
        reader.advance()

        val tokenText = token?.getText() ?: throw ParserException("parseString: unexpected token (null)")

        if (tokenText.last() != '"') {
            throw ParserException("parseString: unbalanced string started at ${token.getOffset()}")
        }

        return LispString(tokenText.substring(1, tokenText.length - 1))
    }

    private fun parseHashMap(reader: TokenReader, map: LispHashMap): LispHashMap {
        reader.advance()

        while (true) {
            val key = when (reader.peek()?.getText()) {
                null -> throw ParserException("parseHashMap: unexpected token (null)")
                "}" -> {
                    reader.advance()
                    null
                }
                else -> parseForm(reader)
            } ?: break
            if (key is LispHashable) {
                val value = when (reader.peek()?.getText()) {
                    null -> throw ParserException("parseHashMap: unexpected token (null)")
                    "}" -> {
                        reader.advance()
                        null
                    }
                    else -> parseForm(reader)
                } ?: break //todo: add parser warning here
                map.put(key, value)
            } else {
                throw ParserException("parseHashMap: ${key.javaClass.simpleName} cannot be a map key")
            }
        }
        return map
    }

    private fun parseKeyword(reader: TokenReader): LispKeyword {
        val token = reader.peek() ?: throw ParserException("parseKeyword: unexpected token (null)")
        reader.advance()
        return LispKeyword(token.getText().drop(1))
    }
}