package com.chausov.kotlisp.run

import com.chausov.kotlisp.lang.LispType
import com.chausov.kotlisp.lexer.lispTokenize
import com.chausov.kotlisp.parser.LispParser
import java.io.EOFException

fun lispRead(): LispType {
    print("> ")
    val tokens = lispTokenize(readLine() ?: throw EOFException())
    return LispParser().parse(tokens)
}

fun lispEval(ast: LispType): LispType {
    return ast
}

fun lispPrint(ast: LispType) {
    println(ast)
}

fun rep() {
    lispPrint(lispEval(lispRead()))
}

fun repl() {
    while (true) {
        try {
            rep()
        } catch (ex: EOFException) {
            return
        } catch (ex: Exception) {
            System.err.println(ex)
        }
    }
}

fun main(args: Array<String>) {
    println("Kotlisp interpreter")
    repl()
}

