package com.chausov.kotlisp.lexer

import java.util.Collections.unmodifiableMap
import java.util.stream.Collectors


class LispLexer(private val myText: String) : Lexer {
    private companion object Patterns {
        val TOKEN_PATTERNS: Map<TokenType, Regex> = unmodifiableMap(linkedMapOf(
            LispTokenTypes.WHITESPACE to Regex("[\\s,]+"),
            LispTokenTypes.COMMENT to Regex(";.*"),
            LispTokenTypes.TWO_CHARACTER to Regex("~@"),
            LispTokenTypes.SPECIAL_CHARACTER to Regex("[\\[\\]{}()'`~^@]"),
            LispTokenTypes.STRING to Regex("\"(?:\\\\.|[^\\\\\"])*\"?"),
            LispTokenTypes.SYMBOLS to Regex("[^\\s\\[\\]{}('\"`,;)]+")
        ))
    }

    private var myPosition: Int = 0
    private var current: Token = advance()

    override fun advance(): Token {
        var nextToken = LispToken(LispTokenTypes.UNKNOWN, "", myPosition)
        if (myPosition >= myText.length) {
            nextToken = LispToken(LispTokenTypes.EOF, "", myPosition)
        }
        else {
            for ((tokenType, regex) in TOKEN_PATTERNS) {
                val matchResult = regex.find(myText, myPosition)
                if (matchResult != null && !matchResult.range.isEmpty() && matchResult.range.first == myPosition) {
                    nextToken = LispToken(tokenType, myText.substring(matchResult.range), myPosition)
                    myPosition = matchResult.range.last + 1
                    break
                }
            }
        }
        current = nextToken
        return current
    }

    override fun peek(): Token = current

    override fun hasNext(): Boolean = current.getTokenType() != LispTokenTypes.EOF
}

fun lispTokenize(text: String): List<Token> {
    return tokenize(LispLexer(text)).stream()
        .filter { token: Token ->
            token.getTokenType() != LispTokenTypes.WHITESPACE
            && token.getTokenType() != LispTokenTypes.COMMENT
        }
        .collect(Collectors.toList())
}