package com.chausov.kotlisp.lexer

interface Token {
    fun getText(): String
    fun getTokenType(): TokenType
}