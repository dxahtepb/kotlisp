package com.chausov.kotlisp.run

import com.chausov.kotlisp.lang.LispType
import com.chausov.kotlisp.lexer.lispTokenize
import com.chausov.kotlisp.parser.LispParser
import java.io.EOFException

fun lispRead(): LispType {
    print("> ")
    val str = readLine() ?: throw EOFException()
    return lispParse(str)
}

fun lispParse(str: String): LispType {
    val tokens = lispTokenize(str)
    return LispParser().parse(tokens)
}

fun lispEval(ast: LispType, env: Environment): LispType {
    return LispDefaultEvaluateProcessor().eval(ast, env)
}

fun lispPrint(ast: LispType) {
    Printer.printAst(ast)
}

fun rep(env: Environment) {
    lispPrint(lispEval(lispRead(), env))
}

fun repl() {
    val env = preRunCommands(listOf(
        "(def! not (fn* (a) (if a false true)))"
    ))
    while (true) {
        try {
            rep(env)
        } catch (ex: EOFException) {
            return
        } catch (ex: Exception) {
            println(ex)
        }
    }
}

fun preRunCommands(commands: List<String>): Environment {
    val env = Environment(DEFAULT_ENV)
    for (command in commands) {
        lispEval(lispParse(command), env)
    }
    return env
}

//todo: add TravisCI builds for tests
fun main(args: Array<String>) {
    println("Kotlisp interpreter")
    repl()
}
