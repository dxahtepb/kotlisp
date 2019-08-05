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
            ")" -> throw ParserException("parseSpecialSymbol: unexpected token at ${reader.peek()?.getOffset()}")
            "[" -> parseSequence(reader, "]", LispVector())
            "]" -> throw ParserException("parseSpecialSymbol: unexpected token at ${reader.peek()?.getOffset()}")
            "{" -> parseHashMap(reader, LispHashMap())
            "}" -> throw ParserException("parseSpecialSymbol: unexpected token at ${reader.peek()?.getOffset()}")
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
        reader.advance()

        return when (token?.getText()?.get(0)) {
            null -> throw ParserException("parseSymbol: unexpected token (null)")
            else -> LispText(token.getText())
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
}