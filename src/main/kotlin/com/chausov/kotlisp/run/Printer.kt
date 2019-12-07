package com.chausov.kotlisp.run

import com.chausov.kotlisp.lang.LispType

object Printer {
    fun printAst(ast: LispType) {
        println(ast.toString(true))
    }
}
