package com.chausov.kotlisp.run

import com.chausov.kotlisp.lang.LispFunction
import com.chausov.kotlisp.lang.LispNumber
import com.chausov.kotlisp.lang.LispSymbol
import com.chausov.kotlisp.lang.LispType

class Environment(private val parent: Environment?) {
    private val env: MutableMap<LispSymbol, LispType> = HashMap()

    constructor(map: Map<LispSymbol, LispType>) : this(null) {
        env.putAll(map)
    }

    fun set(symbol: LispSymbol, value: LispType) {
        env[symbol] = value
    }

    fun getOrNull(symbol: LispSymbol): LispType? = env[symbol] ?: parent?.getOrNull(symbol)

    fun get(symbol: LispSymbol): LispType = getOrNull(symbol) ?: throw EnvironmentNotFoundException(symbol)
}

// todo: specify function parameters and return type and check it explicitly
val DEFAULT_ENV = Environment(mapOf(
    LispSymbol("+") to LispFunction {
            params: List<LispType> -> params.reduce { lhs, rhs -> lhs as LispNumber + rhs as LispNumber }
    },
    LispSymbol("-") to LispFunction {
            params: List<LispType> -> params.reduce { lhs, rhs -> lhs as LispNumber - rhs as LispNumber}
    },
    LispSymbol("*") to LispFunction {
            params: List<LispType> -> params.reduce { lhs, rhs -> lhs as LispNumber * rhs as LispNumber}
    },
    LispSymbol("/") to LispFunction {
            params: List<LispType> -> params.reduce { lhs, rhs -> lhs as LispNumber / rhs as LispNumber}
    }
))

class EnvironmentNotFoundException(env: LispSymbol) : Exception("Environment $env not found")