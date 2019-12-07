package com.chausov.kotlisp.lexer

class LispToken(private val myTokenType: TokenType, private val myText: String, private val offset: Int) : Token {
    override fun getOffset(): Int = offset

    override fun getText(): String = myText

    override fun getTokenType(): TokenType = myTokenType

    override fun toString(): String = "${myTokenType.getName()}($myText)"
}