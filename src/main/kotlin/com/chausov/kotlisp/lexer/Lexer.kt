package com.chausov.kotlisp.lexer

interface Lexer {
    fun advance(): Token
    fun peek(): Token
    fun hasNext(): Boolean
}

fun tokenize(lexer: Lexer): List<Token> {
    val tokenList = ArrayList<Token>()
    while (lexer.hasNext()) {
        tokenList.add(lexer.peek())
        lexer.advance()
    }
    return tokenList
}
