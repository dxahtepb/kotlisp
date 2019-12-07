package com.chausov.kotlisp.lang

import org.apache.commons.text.StringEscapeUtils
import java.math.BigInteger

interface LispType: Type

interface LispAtom: LispType

open class LispSequence protected constructor(val children: List<LispType>) : LispType

interface LispHashable: LispType

class LispVector(forwardedChildren: List<LispType>) : LispSequence(forwardedChildren) {
    override fun toString(): String =
        children.joinToString(
            separator=" ",
            prefix="[",
            postfix="]"
        )
}

class LispList(forwardedChildren: List<LispType>) : LispSequence(forwardedChildren) {
    fun dropFirst(): LispList = LispList(children.drop(1))

    override fun toString(): String =
        children.joinToString(
            separator=" ",
            prefix="(",
            postfix=")"
        )
}

class LispHashMap(val map: Map<LispHashable, LispType>): LispType {
    fun get(key: LispHashable): LispType? = map[key]

    override fun toString(): String =
        map.entries.joinToString(
            separator = " ",
            prefix = "{",
            postfix = "}",
            transform = {
                entry -> "${entry.key} ${entry.value}"
            }
        )
}

class LispSymbol(val text: String): LispType {
    override fun hashCode(): Int = text.hashCode()

    override fun equals(other: Any?): Boolean =
        other is LispSymbol && text == other.text

    override fun toString(): String = text
}

class LispString(text: String): LispHashable {
    private val text: String = unescape(text)

    private fun unescape(str: String): String = StringEscapeUtils.unescapeJava(str)

    override fun hashCode(): Int = text.hashCode()

    override fun equals(other: Any?): Boolean =
        other is LispString && text == other.text

    override fun toString(): String = "\"$text\""
}

class LispKeyword(private val text: String): LispHashable {
    fun getText(): String = text

    override fun hashCode(): Int = text.hashCode()

    override fun equals(other: Any?): Boolean =
        other is LispKeyword && other.text == text

    override fun toString(): String = ":$text"
}

class LispNumber(private val number: BigInteger): LispAtom, LispHashable {
    operator fun plus(other: LispNumber): LispNumber = LispNumber(number + other.number)
    operator fun minus(other: LispNumber): LispNumber = LispNumber(number - other.number)
    operator fun times(other: LispNumber): LispNumber = LispNumber(number * other.number)
    operator fun div(other: LispNumber): LispNumber = LispNumber(number / other.number)

    override fun toString(): String = "$number"

    override fun hashCode(): Int = number.hashCode()

    override fun equals(other: Any?): Boolean =
        other is LispNumber && other.number == number
}

class LispFunction(private val name: String = "lambda", private val lambda: (List<LispType>) -> LispType) : LispType {
    fun invoke(vararg args: LispType): LispType = lambda.invoke(args.toList())

    fun invoke(args: List<LispType>): LispType = lambda.invoke(args)

    override fun toString(): String = "$<$name>"
}

class LispConstant(val name: String) : LispType {
    override fun equals(other: Any?): Boolean = other is LispConstant && other.name == name
    override fun hashCode(): Int = name.hashCode()
    override fun toString(): String = name
}

object LispConstants {
    val NIL = LispConstant("nil")
    val TRUE = LispConstant("true")
    val FALSE = LispConstant("false")

    fun getConstantByName(name: String): LispConstant? =
        when (name) {
            "nil" -> NIL
            "true" -> TRUE
            "false" -> FALSE
            else -> null
        }
}