package com.chausov.kotlisp.run


import com.chausov.kotlisp.lang.*

val ENV: Map<String, LispFunction> = hashMapOf(
    "+" to LispFunction {
            params: List<LispType> -> params.reduce { lhs, rhs -> lhs as LispNumber + rhs as LispNumber}
    },
    "-" to LispFunction {
            params: List<LispType> -> params.reduce { lhs, rhs -> lhs as LispNumber - rhs as LispNumber}
    },
    "*" to LispFunction {
            params: List<LispType> -> params.reduce { lhs, rhs -> lhs as LispNumber * rhs as LispNumber}
    },
    "/" to LispFunction {
            params: List<LispType> -> params.reduce { lhs, rhs -> lhs as LispNumber / rhs as LispNumber}
    }
)

class LispDefaultEvaluateProcessor {
    fun eval(ast: LispType, env: Map<String, LispFunction>): LispType {
        return when (ast) {
            is LispList -> {
                if (ast.children.isEmpty()) {
                    ast
                } else {
                    applyAsFunction(evaluateAst(ast, env), env)
                }
            }
            else -> evaluateAst(ast, env)
        }
    }

    private fun applyAsFunction(ast: LispType, env: Map<String, LispFunction>): LispType {
        if (ast is LispList) {
            val functionName = ast.children.first()
            val function = env[functionName.toString()]
            val args = ast.children.subList(1, ast.children.size)
            return function?.invoke(args)
                ?: throw InvocationException("Cannot invoke $functionName with arguments $args")
        }
        throw InvocationException("Cannot invoke ${ast.javaClass}")
    }

    private fun evaluateAst(ast: LispType, env: Map<String, LispFunction>): LispType {
        return when (ast) {
            is LispList -> {
                LispList(ast.children.map { element -> eval(element, env) })
            }
            is LispSymbol -> {
                if (env.containsKey(ast.text)) {
                    ast
                } else {
                    throw NoSuchSymbolException("Unknown symbol ${ast.text}")
                }
            }
            else -> ast
        }
    }
}

class NoSuchSymbolException(reason: String): Exception(reason)
class InvocationException(reason: String): Exception(reason)