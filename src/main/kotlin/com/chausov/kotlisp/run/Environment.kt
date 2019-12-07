package com.chausov.kotlisp.run

import com.chausov.kotlisp.lang.*
import java.math.BigInteger

open class Environment(private val parent: Environment?) {
    protected val env: MutableMap<LispSymbol, LispType> = HashMap()

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

class FunctionEnvironment(parent: Environment, params: Map<LispSymbol, LispType>) : Environment(parent) {
    init {
        env.putAll(params)
    }
}

// todo: specify function parameters and return type and check it explicitly
val DEFAULT_ENV = Environment(mapOf(
    LispSymbol("+") to LispFunction("operator plus") { params ->
        params.reduce { lhs, rhs -> lhs as LispNumber + rhs as LispNumber }
    },
    LispSymbol("-") to LispFunction("operator minus") { params ->
        params.reduce { lhs, rhs -> lhs as LispNumber - rhs as LispNumber }
    },
    LispSymbol("*") to LispFunction("operator times") { params ->
        params.reduce { lhs, rhs -> lhs as LispNumber * rhs as LispNumber }
    },
    LispSymbol("/") to LispFunction("operator divide") { params ->
        params.reduce { lhs, rhs -> lhs as LispNumber / rhs as LispNumber }
    },
    LispSymbol("list") to LispFunction("list") { params ->
        LispList(params)
    },
    LispSymbol("list?") to LispFunction("is list") { params ->
        lispConditionCheck(params.first() is LispList)
    },
    LispSymbol("empty?") to LispFunction("is empty") { params ->
        lispConditionCheck((params.first() as LispList).children.isEmpty())
    },
    LispSymbol("count") to LispFunction("count") { params ->
        val count = (params.first() as? LispSequence)?.children?.size?.toBigInteger() ?: BigInteger.ZERO
        return@LispFunction LispNumber(count)
    },
    LispSymbol("=") to LispFunction("equals") { params ->
        lispConditionCheck(params.getOrNull(0) == params.getOrNull(1))
    },
    LispSymbol("<") to LispFunction("less") { params ->
        lispArithmeticCompare(params) { lhs, rhs -> lhs < rhs }
    },
    LispSymbol(">") to LispFunction("greater") { params ->
        lispArithmeticCompare(params) { lhs, rhs -> lhs > rhs }
    },
    LispSymbol("<=") to LispFunction("less or equals") { params ->
        lispArithmeticCompare(params) { lhs, rhs -> lhs <= rhs }
    },
    LispSymbol(">=") to LispFunction("greater or equals") { params ->
        lispArithmeticCompare(params) { lhs, rhs -> lhs >= rhs }
    },
    LispSymbol("prn") to LispFunction("print") { params ->
        Printer.printAst(params.first())
        return@LispFunction LispConstants.NIL
    }
))

private fun lispConditionCheck(condition: Boolean): LispConstant =
    if (condition) LispConstants.TRUE else LispConstants.FALSE

private fun lispArithmeticCompare(
    params: List<LispType>,
    condition: (BigInteger, BigInteger) -> Boolean
): LispConstant {
    val lhs = params.getOrNull(0) as LispNumber
    val rhs = params.getOrNull(1) as LispNumber
    return lispConditionCheck(condition.invoke(lhs.number, rhs.number))
}

class SymbolNotFoundException(symbol: LispSymbol) : Exception("Unknown symbol $symbol")