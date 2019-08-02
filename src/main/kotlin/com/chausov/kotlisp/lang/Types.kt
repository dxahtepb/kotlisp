package com.chausov.kotlisp.lang

import org.apache.commons.text.StringEscapeUtils

interface LispType: Type

open class LispSequence protected constructor() : LispType {

    protected val children: MutableList<LispType> = ArrayList()

    fun addChild(child: LispType) {
        children.add(child)
    }
}

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

class LispText(val text: String): LispType {
    override fun toString(): String {
        return text
    }
}

class LispString(text: String) : LispType {
    private val text: String = unescape(text)

    private fun unescape(str: String): String {
        return StringEscapeUtils.unescapeJava(str)
    }

    override fun toString(): String {
        return "\"$text\""
    }
}
