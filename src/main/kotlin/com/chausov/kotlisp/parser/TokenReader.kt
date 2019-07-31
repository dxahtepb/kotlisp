package com.chausov.kotlisp.parser

import com.chausov.kotlisp.lexer.Token

class TokenReader(tokens: Iterable<Token>) {
    private val iterator = tokens.iterator()
    private var current = advance()

    fun peek(): Token? {
        return current
    }

    fun advance(): Token? {
        current = if (iterator.hasNext()) iterator.next() else null
        return current
    }
}