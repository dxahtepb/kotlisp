package com.chausov.kotlisp.run


import com.chausov.kotlisp.lang.*

class LispDefaultEvaluateProcessor {
    fun eval(ast: LispType, env: Environment): LispType {
        return when (ast) {
            is LispList -> {
                if (ast.children.isEmpty()) {
                    ast
                } else {
                    apply(ast, env)
                }
            }
            else -> evaluateAst(ast, env)
        }
    }

    private fun apply(ast: LispList, env: Environment): LispType {
        val first = ast.children.first()
        if (first !is LispSymbol) {
            throw InvocationException("Cannot invoke ${ast.javaClass}")
        }
        return when (first.text) {
            "let*" -> {
                applyAsFunction(evaluateAst(ast, env), env)
            }
            "def!" -> {
                applyAsFunction(evaluateAst(ast, env), env)
            }
            else -> applyAsFunction(evaluateAst(ast, env), env)
        }
    }

    private fun applyAsFunction(ast: LispType, env: Environment): LispType {
        if (ast !is LispList) {
            throw InvocationException("Cannot invoke ${ast.javaClass}")
        }
        val functionName = ast.children.first() as LispSymbol
        val function = env.get(functionName) as? LispFunction
        val args = ast.children.subList(1, ast.children.size)
        return function?.invoke(args)
            ?: throw InvocationException("Cannot invoke $functionName with arguments $args")
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