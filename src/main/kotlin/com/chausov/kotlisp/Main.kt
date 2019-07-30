package com.chausov.kotlisp

import com.chausov.kotlisp.lexer.LispLexer
import com.chausov.kotlisp.lexer.tokenize

fun lispRead(): String {
    print("> ")
    val tokens = tokenize(LispLexer(readLine()?:""))
    return tokens.toString()
}

fun lispEval(input: String): String {
    return input
}

fun lispPrint(input: String) {
    println(input)
}

fun rep() {
    lispPrint(lispEval(lispRead()))
}

fun repl() {
    while (true) {
        try {
            rep()
        } catch (ex: Exception) {
            System.err.println(ex)
        }
    }
}

fun main(args: Array<String>) {
    println("Hello, World")
    repl()
}

