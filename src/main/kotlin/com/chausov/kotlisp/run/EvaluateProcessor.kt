package com.chausov.kotlisp.run


import com.chausov.kotlisp.lang.LispNumber
import com.chausov.kotlisp.lang.LispType

class FunctionParams()

val ENV: Map<String, Function2<LispNumber, LispNumber, LispType>> = hashMapOf(
    "+" to { lhs: LispNumber, rhs: LispNumber -> lhs + rhs},
    "-" to { lhs: LispNumber, rhs: LispNumber -> lhs - rhs},
    "*" to { lhs: LispNumber, rhs: LispNumber -> lhs * rhs},
    "/" to { lhs: LispNumber, rhs: LispNumber -> lhs / rhs}
)

class LispDefaultEvaluateProcessor {
}