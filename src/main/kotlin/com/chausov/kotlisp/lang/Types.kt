package com.chausov.kotlisp.lang

import org.apache.commons.text.StringEscapeUtils
import java.math.BigInteger

interface LispType: Type {
    fun toString(readable: Boolean): String = toString()
}

interface LispAtom: LispType

open class LispSequence protected constructor(val children: List<LispType>) : LispType {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is LispSequence) return false
        if (children != other.children) return false
        return true
    }

    override fun hashCode(): Int {
        return children.hashCode()
    }
}

interface LispHashable: LispType

class LispVector(forwardedChildren: List<LispType>) : LispSequence(forwardedChildren) {
    override fun toString(): String = toString(true)
    override fun toString(readable: Boolean): String =
        children.joinToString(
            separator=" ",
            prefix="[",
            postfix="]"
        ) { token -> token.toString(readable) }
}

class LispList(forwardedChildren: List<LispType>) : LispSequence(forwardedChildren) {
    fun dropFirst(): LispList = LispList(children.drop(1))

    override fun toString(): String = toString(true)
    override fun toString(readable: Boolean): String =
        children.joinToString(
            separator=" ",
            prefix="(",
            postfix=")"
        ) { token -> token.toString(readable) }
}

class LispHashMap(val map: Map<LispHashable, LispType>): LispType {
    fun get(key: LispHashable): LispType? = map[key]

    override fun toString(): String = toString(true)
    override fun toString(readable: Boolean): String =
        map.entries.joinToString(
            separator = " ",
            prefix = "{",
            postfix = "}"
        ) { entry -> "${entry.key.toString(readable)} ${entry.value.toString(readable)}" }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as LispHashMap
        if (map != other.map) return false
        return true
    }

    override fun hashCode(): Int {
        return map.hashCode()
    }
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
    private fun escape(str: String): String = StringEscapeUtils.escapeJava(str)

    override fun hashCode(): Int = text.hashCode()
    override fun equals(other: Any?): Boolean =
        other is LispString && text == other.text

    override fun toString(): String = toString(true)
    override fun toString(readable: Boolean): String =
        if (readable) "\"${escape(text)}\"" else "\"text\""
}

class LispKeyword(private val text: String): LispHashable {
    fun getText(): String = text

    override fun hashCode(): Int = text.hashCode()

    override fun equals(other: Any?): Boolean =
        other is LispKeyword && other.text == text

    override fun toString(): String = ":$text"
}

class LispNumber(val number: BigInteger): LispAtom, LispHashable {
    operator fun plus(other: LispNumber): LispNumber = LispNumber(number + other.number)
    operator fun minus(other: LispNumber): LispNumber = LispNumber(number - other.number)
    operator fun times(other: LispNumber): LispNumber = LispNumber(number * other.number)
    operator fun div(other: LispNumber): LispNumber = LispNumber(number / other.number)

    override fun toString(): String = "$number"

    override fun hashCode(): Int = number.hashCode()

    override fun equals(other: Any?): Boolean =
        other is LispNumber && other.number == number
}

class LispFunction(
    private val name: String = "lambda",
    private val lambda: (List<LispType>) -> LispType
) : LispType
{
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

val VARIADIC = LispSymbol("&")