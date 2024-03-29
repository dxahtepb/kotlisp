package com.chausov.kotlisp.lexer

import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.io.File
import java.lang.StringBuilder

@RunWith(Parameterized::class)
class LispLexerTest(private val testDataDir: String) {
    companion object {
        @JvmStatic
        @Parameterized.Parameters(name="{0}")
        fun data(): Iterable<String> = listOf(
            "comments",
            "longString",
            "lotOfWhitespaces",
            "multiline",
            "simple",
            "spliceUnquote",
            "string",
            "stringEscaping",
            "stringUnbalanced"
        )
    }

    private fun readFile(relativePath: String): String {
        val fileUrl = this::class.java.getResource(relativePath)
                ?: throw IllegalStateException("Resource $relativePath not found")
        assertNotNull(fileUrl)
        return File(fileUrl.file).bufferedReader().readText()
    }

    private fun tokenizeLispCode(src: String): String {
        val builder = StringBuilder()
        tokenize(LispLexer(src)).forEach { token: Token ->
            builder.append(token.toString()).append(System.lineSeparator())
        }
        return builder.toString()
    }

    @Test
    fun doTest() {
        val testData = readFile("$testDataDir/test")
        val testExpectedData = readFile("$testDataDir/test.after")
        val tokens = tokenizeLispCode(testData)
        assertEquals(testExpectedData, tokens)
    }
}
