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

    fun set(symbol: LispSymbol, value: LispType): LispType {
        env[symbol] = value
        return value
    }

    private fun getOrNull(symbol: LispSymbol): LispType? = env[symbol] ?: parent?.getOrNull(symbol)

    fun get(symbol: LispSymbol): LispType = getOrNull(symbol) ?: throw SymbolNotFoundException(symbol)
}

// todo: specify function parameters and return type and check it explicitly
val DEFAULT_ENV = Environment(mapOf(
    LispSymbol("+") to LispFunction("operator plus") {
            params: List<LispType> -> params.reduce { lhs, rhs -> lhs as LispNumber + rhs as LispNumber }
    },
    LispSymbol("-") to LispFunction("operator minus") {
            params: List<LispType> -> params.reduce { lhs, rhs -> lhs as LispNumber - rhs as LispNumber}
    },
    LispSymbol("*") to LispFunction("operator times") {
            params: List<LispType> -> params.reduce { lhs, rhs -> lhs as LispNumber * rhs as LispNumber}
    },
    LispSymbol("/") to LispFunction("operator divide") {
            params: List<LispType> -> params.reduce { lhs, rhs -> lhs as LispNumber / rhs as LispNumber}
    }
))

class SymbolNotFoundException(symbol: LispSymbol) : Exception("Unknown symbol $symbol")