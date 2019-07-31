package com.chausov.kotlisp.lang

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