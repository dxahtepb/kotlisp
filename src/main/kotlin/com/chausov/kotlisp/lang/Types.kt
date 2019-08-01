package com.chausov.kotlisp.lang

import org.apache.commons.text.StringEscapeUtils

interface LispType: Type

class LispList: LispType {
    private val children: MutableList<LispType> = ArrayList()

    fun addChild(child: LispType) {
        children.add(child)
    }

    fun getChildren() = children

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
