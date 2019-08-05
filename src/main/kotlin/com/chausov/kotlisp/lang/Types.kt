package com.chausov.kotlisp.lang

import org.apache.commons.text.StringEscapeUtils

interface LispType: Type

open class LispSequence protected constructor() : LispType {

    protected val children: MutableList<LispType> = ArrayList()

    fun addChild(child: LispType) {
        children.add(child)
    }
}

interface LispHashable: LispType

class LispVector: LispSequence() {
    override fun toString(): String {
        return children.joinToString(
            separator=" ",
            prefix="[",
            postfix="]"
        )
    }
}

class LispList: LispSequence() {
    override fun toString(): String {
        return children.joinToString(
            separator=" ",
            prefix="(",
            postfix=")"
        )
    }
}

class LispHashMap: LispType {
    private val map: MutableMap<LispHashable, LispType> = HashMap()

    fun put(key: LispHashable, value: LispType) {
        map[key] = value
    }

    fun get(key: LispHashable): LispType? {
        return map[key]
    }

    override fun toString(): String {
        return map.entries.joinToString(separator = " ", prefix = "{", postfix = "}", transform = {
                entry -> "${entry.key} ${entry.value}"
        })
    }
}

class LispText(val text: String): LispType {
    override fun toString(): String {
        return text
    }
}

class LispString(text: String) : LispHashable {
    private val text: String = unescape(text)

    private fun unescape(str: String): String {
        return StringEscapeUtils.unescapeJava(str)
    }

    override fun hashCode(): Int = text.hashCode()

    override fun equals(other: Any?): Boolean = if (other is LispString) text == other.text else false

    override fun toString(): String {
        return "\"$text\""
    }
}
