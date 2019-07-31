package com.chausov.kotlisp.parser

import com.chausov.kotlisp.lexer.lispTokenize
import org.junit.Test
import kotlin.test.assertEquals

class ParserTest {

    private fun doTest(text: String, expected: String) {
        assertEquals(LispParser().parse(lispTokenize(text)).toString(), expected)
    }

    @Test
    fun testSimple() {
        listOf(
            "123" to "123",
            "123 " to "123",
            "abc" to "abc",
            " abc " to "abc",
            "(123 456)" to "(123 456)",
            "(123 456 789)" to "(123 456 789)",
            "( + 2 (* 3 4) )" to "(+ 2 (* 3 4))"
        ).forEach { pair -> doTest(pair.first, pair.second) }
    }
}
