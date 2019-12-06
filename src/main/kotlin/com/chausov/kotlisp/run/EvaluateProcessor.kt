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
                applyAsFunction(ast, env)
            }
            else -> applyAsFunction(ast, env)
        }
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

class InvocationException(reason: String): Exception(reason)