package com.chausov.kotlisp.parser

import com.chausov.kotlisp.lang.LispList
import com.chausov.kotlisp.lang.LispText
import com.chausov.kotlisp.lang.LispType
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
            else -> parseSymbol(reader)
        }
    }

    private fun parseSpecialSymbol(reader: TokenReader): LispType =
        when (reader.peek()?.getText()) {
            "(" -> parseList(reader)
            ")" -> throw ParserException("parseSpecialSymbol: unexpected token at ${reader.peek()?.getOffset()}")
            else -> throw ParserException("parseSpecialSymbol: unexpected token at ${reader.peek()?.getOffset()}")
        }

    private fun parseList(reader: TokenReader): LispList {
        reader.advance()

        val result = LispList()
        while (true) {
            val form = when (reader.peek()?.getText()) {
                null -> throw ParserException("parseSpecialSymbol: unexpected token (null)")
                ")" -> {
                    reader.advance()
                    null
                }
                else -> parseForm(reader)
            }
            if (form != null) {
                result.addChild(form)
            } else {
                break
            }
        }

        return result
    }

    private fun parseSymbol(reader: TokenReader): LispType {
        val token = reader.peek()
        reader.advance()

        return when (token?.getText()?.get(0)) {
            null -> throw ParserException("parseSymbol: unexpected token (null)")
            else -> LispText(token.getText())
        }
    }
}