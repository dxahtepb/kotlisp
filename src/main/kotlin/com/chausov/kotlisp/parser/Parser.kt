package com.chausov.kotlisp.parser

import com.chausov.kotlisp.lang.Type
import com.chausov.kotlisp.lexer.Token

interface Parser {
    fun parse(tokens: Iterable<Token>): Type
}