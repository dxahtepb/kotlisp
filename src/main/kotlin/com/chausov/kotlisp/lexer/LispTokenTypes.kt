package com.chausov.kotlisp.lexer

object LispTokenTypes {
    val SPECIAL_CHARACTER: TokenType = LispTokenType("SPECIAL_CHARACTER")
    val WHITESPACE: TokenType = LispTokenType("WHITESPACE")
    val STRING: TokenType = LispTokenType("STRING")
    val COMMENT: TokenType = LispTokenType("COMMENT")
    val SYMBOLS: TokenType = LispTokenType("SYMBOLS")
    val TWO_CHARACTER: TokenType = LispTokenType("TWO_CHARACTER")
    val UNKNOWN: TokenType = LispTokenType("UNKNOWN")
    val EOF: TokenType = LispTokenType("EOF")
}
