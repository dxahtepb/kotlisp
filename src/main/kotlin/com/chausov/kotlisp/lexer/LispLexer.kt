package com.chausov.kotlisp.lexer

import java.util.Collections.unmodifiableMap
import java.util.stream.Collectors


class LispLexer(private val myText: String) : Lexer {
    private companion object Patterns {
        val TOKEN_PATTERNS: Map<String, Regex> = unmodifiableMap(linkedMapOf(
            "WHITESPACE" to Regex("[\\s,]+"),
            "COMMENT" to Regex(";.*"),
            "TWO_CHARACTER" to Regex("~@"),
            "SPECIAL_CHARACTER" to Regex("[\\[\\]{}()'`~^@]"),
            "STRING" to Regex("\"(?:\\\\.|[^\\\\\"])*\"?"),
            "SYMBOLS" to Regex("[^\\s\\[\\]{}('\"`,;)]+")
        ))
    }

    private var myPosition: Int = 0
    private var current: Token = advance()

    override fun advance(): Token {
        var nextToken = LispToken(LispTokenTypes.UNKNOWN, "", myPosition)
        if (myPosition >= myText.length) {
            nextToken = LispToken(LispTokenTypes.getType("EOF"), "", myPosition)
        }
        else {
            for ((name, regex) in TOKEN_PATTERNS) {
                val matchResult = regex.find(myText, myPosition)
                if (matchResult != null && !matchResult.range.isEmpty() && matchResult.range.first == myPosition) {
                    nextToken = LispToken(LispTokenTypes.getType(name), myText.substring(matchResult.range), myPosition)
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