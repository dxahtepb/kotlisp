package com.chausov.kotlisp.run


import com.chausov.kotlisp.lang.*

class LispDefaultEvaluateProcessor {
    fun eval(ast: LispType, env: Environment): LispType =
        if (ast is LispList && ast.children.isNotEmpty()) {
            apply(ast, env)
        } else {
            evaluateAst(ast, env)
        }

    private fun apply(ast: LispList, env: Environment): LispType {
        val first = ast.children.first()
        if (first !is LispSymbol) {
            return applyAsFunction(ast, env)
        }
        return when (first.text) {
            "let*" -> builtInLet(ast, env)
            "def!" -> builtInDef(ast, env)
            "do" -> builtInDo(ast, env)
            "fn*" -> builtInFn(ast, env)
            "if" -> builtInIf(ast, env)
            else -> applyAsFunction(ast, env)
        }
    }

    private fun builtInIf(ast: LispList, env: Environment): LispType {
        if (ast.children.size != 3 && ast.children.size != 4) {
            throw EvaluationException("Wrong usage of if construction")
        }

        val condition = ast.children[1]
        val thenBranch = ast.children[2]
        val elseBranch = ast.children.getOrNull(3)

        val conditionResult = eval(condition, env)
        if (conditionResult != LispConstants.FALSE
            && conditionResult != LispConstants.NIL
        ) {
            return eval(thenBranch, env)
        }
        if (elseBranch != null) {
            return eval(elseBranch, env)
        }
        return LispConstants.NIL
    }

    //todo: add tests for fn* expression (new test type needed)
    private fun builtInFn(ast: LispList, env: Environment): LispFunction {
        if (ast.children.size != 3) {
            throw EvaluationException("Wrong usage of fn* construction")
        }
        val parametersSequence = ast.children[1] as? LispSequence
            ?: throw EvaluationException("Wrong usage of fn* construction")
        val parameters = parametersSequence.children.map { e -> e as LispSymbol }
        val body = ast.children[2]
        return LispFunction { args ->
            val bindings = bindFunctionArguments(parameters, args)
            val newEnv = FunctionEnvironment(env, bindings)
            return@LispFunction eval(body, newEnv)
        }
    }

    private fun bindFunctionArguments(parameters: List<LispSymbol>, args: List<LispType>): Map<LispSymbol, LispType> {
        if (args.size != parameters.size) {
            val variadicIdx = parameters.indexOf(VARIADIC)
            if (variadicIdx == -1) {
                throw InvocationException("Wrong number of parameters for function $this")
            } else if (variadicIdx != parameters.lastIndex - 1) {
                throw InvocationException("Variadic parameter should be last parameter")
            }
        }
        val bindings: MutableMap<LispSymbol, LispType> = HashMap()
        for (idx in parameters.indices) {
            if (parameters[idx] == VARIADIC) {
                bindings[parameters[idx + 1]] = LispList(args.subList(idx, args.size))
                break
            }
            bindings[parameters[idx]] = args[idx]
        }
        return bindings
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

    //todo: add tests for do expression (new test type needed)
    private fun builtInDo(ast: LispList, env: Environment): LispType {
        val evaluated = evaluateAst(ast.dropFirst(), env) as LispList
        return evaluated.children.last()
    }

    private fun applyAsFunction(ast: LispType, env: Environment): LispType {
        val evaluatedAst = evaluateAst(ast, env)
        if (evaluatedAst !is LispList) {
            throw InvocationException("Cannot invoke ${ast.javaClass.simpleName}")
        }
        val function = evaluatedAst.children.first() as? LispFunction
        val args = evaluatedAst.dropFirst().children
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
class InvocationException(reason: String) : EvaluationException(reason)