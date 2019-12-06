package com.chausov.kotlisp.run


import com.chausov.kotlisp.lang.*

class LispDefaultEvaluateProcessor {
    fun eval(ast: LispType, env: Environment): LispType =
        if (ast is LispList && ast.children.isNotEmpty()) {
            apply(ast, env)
        }
        else {
            evaluateAst(ast, env)
        }

    private fun apply(ast: LispList, env: Environment): LispType {
        val first = ast.children.first()
        if (first !is LispSymbol) {
            throw InvocationException("Cannot invoke ${ast.javaClass}")
        }
        return when (first.text) {
            "let*" -> {
                applyAsFunction(ast, env)
            }
            "def!" -> {
                def(ast, env)
            }
            else -> applyAsFunction(ast, env)
        }
    }

    //todo: add tests for def! expression
    private fun def(ast: LispList, env: Environment): LispType {
        if (ast.children.size != 3) {
            throw EvaluationException("Wrong usage of def! construction")
        }
        val symbol = ast.children[1] as LispSymbol
        val value = evaluateAst(ast.children[2], env)
        return env.set(symbol, value)
    }

    private fun applyAsFunction(ast: LispType, env: Environment): LispType {
        val evaluatedAst = evaluateAst(ast, env)
        if (evaluatedAst !is LispList) {
            throw InvocationException("Cannot invoke ${ast.javaClass}")
        }
        val function = evaluatedAst.children.first() as? LispFunction
        val args = evaluatedAst.children.subList(1, evaluatedAst.children.size)
        return function?.invoke(args)
            ?: throw InvocationException("Cannot invoke $function with arguments $args")
    }

    private fun evaluateAst(ast: LispType, env: Environment): LispType {
        return when (ast) {
            is LispList -> LispList(ast.children.map { element -> eval(element, env) })
            is LispVector -> LispVector(ast.children.map { element -> eval(element, env) })
            is LispHashMap -> LispHashMap(ast.map.mapValues { entry -> eval(entry.value, env) })
            is LispSymbol -> env.get(ast)
            else -> ast
        }
    }
}

open class EvaluationException(reason: String) : Exception(reason)
class InvocationException(reason: String): EvaluationException(reason)