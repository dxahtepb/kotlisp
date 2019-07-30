package com.chausov.kotlisp.lexer

class LispTokenType(private val myName: String): TokenType {
    override fun getName(): String = myName

}