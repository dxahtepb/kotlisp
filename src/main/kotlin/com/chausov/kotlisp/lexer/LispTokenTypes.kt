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

    fun getType(name: String): TokenType {
        return when (name) {
            "SPECIAL_CHARACTER" -> SPECIAL_CHARACTER
            "WHITESPACE" -> WHITESPACE
            "STRING" -> STRING
            "COMMENT" -> COMMENT
            "SYMBOLS" -> SYMBOLS
            "TWO_CHARACTER" -> TWO_CHARACTER
            "EOF" -> EOF
            else -> UNKNOWN
        }
    }
}
