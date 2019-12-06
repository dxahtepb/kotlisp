package com.chausov.kotlisp.run

import com.chausov.kotlisp.lexer.lispTokenize
import com.chausov.kotlisp.parser.LispParser
import org.junit.Test
import kotlin.test.assertEquals

class EvalTest {

    private fun doTest(text: String, expected: String) {
        val ast = LispParser().parse(lispTokenize(text))
        val evaluated = LispDefaultEvaluateProcessor().eval(ast, DEFAULT_ENV)
        assertEquals(expected, evaluated.toString())
    }

    @Test
    fun testWithoutCalculation() {
        listOf(
            "123" to "123",
            "()" to "()"
        ).forEach { pair -> doTest(pair.first, pair.second) }
    }

    @Test
    fun testSimple() {
        listOf(
            "(+ 1 2)" to "3",
            "(+ 5 (* 2 3))" to "11",
            "(- (+ 5 (* 2 3)) 3)" to "8",
            "(/ (- (+ 5 (* 2 3)) 3) 4)" to "2",
            "(/ (- (+ 515 (* 87 311)) 302) 27)" to "1010",
            "(* -3 6)" to "-18",
            "(/ (- (+ 515 (* -87 311)) 296) 27)" to "-994"
        ).forEach { pair -> doTest(pair.first, pair.second) }
    }

    @Test
    fun testVector() {
        listOf(
            "[1 2 (+ 1 2)]" to "[1 2 3]"
        ).forEach { pair -> doTest(pair.first, pair.second) }
    }
}
