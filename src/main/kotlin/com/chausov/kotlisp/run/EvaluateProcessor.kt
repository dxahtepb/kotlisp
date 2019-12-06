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
                builtInLet(ast, env)
            }
            "def!" -> {
                builtInDef(ast, env)
            }
            else -> applyAsFunction(ast, env)
        }
    }

    //todo: add tests for def! expression (new test type needed)
    private fun builtInDef(ast: LispList, env: Environment): LispType {
        if (ast.children.size != 3) {
            throw EvaluationException("Wrong usage of def! construction")
        }
        val symbol = ast.children[1] as LispSymbol
        val value = eval(ast.children[2], env)
        return env.set(symbol, value)
    }

    //todo: add tests for let* expression (new test type needed)
    private fun builtInLet(ast: LispList, env: Environment): LispType {
        val childEnv = Environment(env)
        if (ast.children.size != 3) {
            throw EvaluationException("Wrong usage of let* construction")
        }

        val bindings = ast.children[1]
        val expression = ast.children[2]

        if (bindings !is LispSequence) {
            throw EvaluationException("let* bindings should be a LispSequence")
        }
        if (bindings.children.size % 2 != 0) {
            throw EvaluationException("let* should have even number of bindings")
        }
        for (idx in bindings.children.indices step 2) {
            val key = bindings.children[idx] as LispSymbol
            val value = eval(bindings.children[idx + 1], childEnv)
            childEnv.set(key, value)
        }

        return eval(expression, childEnv)
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