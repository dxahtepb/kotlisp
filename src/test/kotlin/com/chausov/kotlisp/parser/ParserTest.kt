package com.chausov.kotlisp.parser

import com.chausov.kotlisp.lexer.lispTokenize
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

//todo: introduce ast-based parser tests
class ParserTest {

    private fun doTest(text: String, expected: String) {
        assertEquals(expected, LispParser().parse(lispTokenize(text)).toString())
    }

    private fun doExceptionTest(text: String) {
        try {
            LispParser().parse(lispTokenize(text)).toString()
        } catch (err: Exception) {
            return
        }
        assertTrue { false }
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
            "( + 2 (* 3 4) )" to "(+ 2 (* 3 4))",
            "(+ \"abc\" \"abc\")" to "(+ \"abc\" \"abc\")",
            "(+ [1 2 3] [1 [2 3] 4])" to "(+ [1 2 3] [1 [2 3] 4])"
        ).forEach { pair -> doTest(pair.first, pair.second) }
    }

    @Test
    fun testHashMaps() {
        listOf(
            "{}" to "{}",
            "{ }" to "{}",
            "{\"abc\" 1}" to "{\"abc\" 1}",
            "{\"a\" {\"b\" 2}}" to "{\"a\" {\"b\" 2}}",
            "{\"a\" {\"b\" {\"c\" 3}}}" to "{\"a\" {\"b\" {\"c\" 3}}}",
            "{  \"a\"  {\"b\"   {  \"cde\"     3   }  }}" to "{\"a\" {\"b\" {\"cde\" 3}}}",
            "{\"a1\" 1 \"a2\" 2 \"a3\" 3}" to "{\"a1\" 1 \"a2\" 2 \"a3\" 3}",
            "{  :a  {:b   {  :cde     3   }  }}" to "{:a {:b {:cde 3}}}",
            "{\"1\" 1}" to "{\"1\" 1}",
            "({})" to "({})"
        ).forEach { pair -> doTest(pair.first, pair.second) }

        listOf(
            "{",
            "}",
            "{1}",
            "{(1, 2, 3) 3}",
            "{:1 1 :2}",
            "{\"1\" 1 \"1\" 2}",
            "{:1 1 :1 2}"
        ).forEach { input -> doExceptionTest(input) }
    }

    @Test
    fun testConstants() {
        listOf(
            "nil" to "nil",
            "true" to "true",
            "false" to "false",
            "(if true 1)" to "(if true 1)"
        ).forEach { pair -> doTest(pair.first, pair.second) }
    }
}
