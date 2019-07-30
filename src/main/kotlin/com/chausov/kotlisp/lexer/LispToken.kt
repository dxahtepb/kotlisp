package com.chausov.kotlisp.lexer

class LispToken(private val myTokenType: TokenType, private val myText: String): Token {
    override fun getText(): String = myText

    override fun getTokenType(): TokenType = myTokenType

    override fun toString(): String = "${myTokenType.getName()}($myText)"
}