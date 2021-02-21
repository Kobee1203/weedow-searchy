package com.weedow.searchy.mongodb.utils

import org.assertj.core.api.AbstractStringAssert
import org.assertj.core.api.AbstractThrowableAssert
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.nio.charset.StandardCharsets


internal class JSMinTest {

    @Test
    fun blockCommentsAreRemoved() {
        JsMinAssertions.assertThat("/* remove me */\nvar a=3;")
            .doesNotContain("remove me", "/*", "*/")
            .isEqualTo("var a=3;")
    }

    @Test
    fun lineCommentsAreRemoved() {
        val lineCommentCode = """
            var a=3;
            // remove me
            var b=3;
            """.trimIndent()
        JsMinAssertions.assertThat(lineCommentCode)
            .doesNotContain("remove me", "//")
            .contains("var a=3;", "var b=3;")
            .isEqualTo("var a=3;var b=3;")
    }

    @Test
    fun simpleStatementsAreKept() {
        val simpleStatement = "var a=3;"
        JsMinAssertions.assertThat(simpleStatement).isEqualTo(simpleStatement)
    }

    @Test
    fun linebreaksAreRemoved() {
        val multilineStatements = "var a=3;\n\nvar b=3;\n\nvar c=3;"
        JsMinAssertions.assertThat(multilineStatements).isEqualTo("var a=3;var b=3;var c=3;")
    }

    @Test
    fun multipleSpacesAreRemoved() {
        val multiSpaceStatement = "var    a    =3;"
        JsMinAssertions.assertThat(multiSpaceStatement).isEqualTo("var a=3;")
    }

    @Test
    fun functionBlocksAreOnlined() {
        val function = """
            function() {
            var a=3;
            }
            """.trimIndent()
        JsMinAssertions.assertThat(function).isEqualTo("function(){var a=3;}")
    }

    @Test
    fun unterminatedBlockCommentsResultInError() {
        val unterminated = "/* comment but not correctly terminated /"
        JsMinAssertions.assertThatThrownBy(unterminated)
            .isInstanceOf(JSMin.UnterminatedCommentException::class.java)
    }

    @Test
    fun windowsLineBreaksAreRemoved() {
        val windowsBreaks = "var a=3;\r\nvar b=3;"
        JsMinAssertions.assertThat(windowsBreaks).doesNotContain("\r").isEqualTo("var a=3;var b=3;")
    }

    @Test
    fun tabsAreRemoved() {
        val tabs = "\t\tvar a=3;\n\t\tvar b=3;"
        JsMinAssertions.assertThat(tabs).doesNotContain("\t").isEqualTo("var a=3;var b=3;")
    }

    @Test
    fun doubleQuotedStringsAreUntouched() {
        val stringWithSpaces = "string   with  spaces"
        val doubleQuotedStringAssignment = "var a=\"$stringWithSpaces\";"
        JsMinAssertions.assertThat(doubleQuotedStringAssignment).isEqualTo(doubleQuotedStringAssignment)
    }

    @Test
    fun singleQuotedStringsAreUntouched() {
        val stringWithSpaces = "string   with  spaces"
        val singleQuotedStringAssignment = "var a='$stringWithSpaces';"
        JsMinAssertions.assertThat(singleQuotedStringAssignment).isEqualTo(singleQuotedStringAssignment)
    }

    @Test
    fun unterminatedStringLiteralsLeadToError() {
        val unterminated = "var a=\"unterminated string literal;"
        JsMinAssertions.assertThatThrownBy(unterminated)
            .isInstanceOf(JSMin.UnterminatedStringLiteralException::class.java)
    }

    @Test
    fun quotedStringLiteralsMayContainControlCharacters() {
        JsMinAssertions.assertThat("var a=\"line 1\\line 2\"").isEqualTo("var a=\"line 1\\line 2\"")
    }

    @Test
    fun blocksStayBlocks() {
        val block = "{var a=3;}"
        JsMinAssertions.assertThat(block).isEqualTo(block)
    }

    @Test
    fun operationsAreUnharmed() {
        val operation = "var a=b+\"string 2\""
        JsMinAssertions.assertThat(operation).isEqualTo(operation)
    }

    @Test
    fun regexLiteralsAreUnharmed() {
        val regex = "var re=/ab+c/;"
        JsMinAssertions.assertThat(regex).isEqualTo(regex)
    }

    @Test
    fun regexLiteralsWithSetsAreUnharmed() {
        val regex = "var re=/ab+c[a-zA-Z\\r\\n\\t]/;"
        JsMinAssertions.assertThat(regex).isEqualTo(regex)
    }

    @Test
    fun regexLiteralsWithUnterminatedSetsFailToMinimize() {
        val regex = "var re=/ab+c[a-zA-Z\\r\\n\\t/;"
        JsMinAssertions.assertThatThrownBy(regex)
            .isInstanceOf(JSMin.UnterminatedRegExpLiteralException::class.java)
    }

    @Test
    fun regexLiteralsContainingSlashesFailToMinimize() {
        val regex = "var re=/ab+c//;"
        JsMinAssertions.assertThatThrownBy(regex)
            .isInstanceOf(JSMin.UnterminatedRegExpLiteralException::class.java)
    }

    @Test
    fun regexLiteralsWithoutTerminationFailToMinimize() {
        val regex = "var re=/ab+c\\;"
        JsMinAssertions.assertThatThrownBy(regex)
            .isInstanceOf(JSMin.UnterminatedRegExpLiteralException::class.java)
    }

    @Test
    fun shouldHandleSlashAndIsolatedSingleQuoteInRegexes() {
        val script = "var slashOrDoubleQuote=/[/']/g;"
        JsMinAssertions.assertThat(script).isEqualTo(script)
    }

    @Test
    fun shouldNotMinifyInsideQuasiLiterals() {
        JsMinAssertions.assertThat("var a = `x = y`;").isEqualTo("var a=`x = y`;")
    }

    @Test
    fun shouldRemoveByteOrderMark() {
        JsMinAssertions.assertThat("\uFEFFvar a = 1;").isEqualTo("var a=1;")
    }

    @Test
    fun shouldNotRemoveLineBreakBeforeExclamationMark() {
        JsMinAssertions.assertThat("var a = 1\n!true\nconsole.log(a)").isEqualTo("var a=1\n!true\nconsole.log(a)")
    }

    @Test
    fun shouldNotRemoveLineBreakBeforeTilde() {
        JsMinAssertions.assertThat("var a = 1\n~true\nconsole.log(a)").isEqualTo("var a=1\n~true\nconsole.log(a)")
    }

    @Test
    fun shouldNotRemoveSpaceBetweenPlusSigns() {
        JsMinAssertions.assertThat("console.log(1\n+ +1)").isEqualTo("console.log(1\n+ +1)")
    }

    @Test
    fun shouldNotRemoveSpaceBetweenMinusSigns() {
        JsMinAssertions.assertThat("console.log(1\n- -1)").isEqualTo("console.log(1\n- -1)")
    }

    @Test
    fun shouldRemoveInlineComments() {
        JsMinAssertions.assertThat("var r = 1; // some comment").isEqualTo("var r=1;")
    }

    @Test
    fun shouldRemoveBlockComments() {
        JsMinAssertions.assertThat("var r = 1; /* some comment */").isEqualTo("var r=1;")
    }

    @Test
    fun shouldNotProcessRegexpAfterOperator() {
        JsMinAssertions.assertThat("1 + /a  a/;").isEqualTo("1+/a  a/;")
        JsMinAssertions.assertThat("1 - /a  a/;").isEqualTo("1-/a  a/;")
        JsMinAssertions.assertThat("1 * /a  a/;").isEqualTo("1* /a  a/;")
        JsMinAssertions.assertThat("1 / /a  a/;").isEqualTo("1/ /a  a/;")
        // Not sure why this should work, ~ was added in the original JSMin but the expression below is not valid
        // Javascript.
        JsMinAssertions.assertThat("1 ~ /a  a/;").isEqualTo("1~/a  a/;")
    }

    @Test
    fun shouldProcessRegexpContainingCurlyBraces() {
        JsMinAssertions.assertThat("return /\\d{1,2}[\\/\\-]\\d{1,2}[\\/\\-]\\d{2,4}/.test(s);").isEqualTo("return/\\d{1,2}[\\/\\-]\\d{1,2}[\\/\\-]\\d{2,4}/.test(s);")
    }

    @Test
    fun shouldProcessRegexpContainingSemicolumn() {
        JsMinAssertions.assertThat("return /a;/.test(s);").isEqualTo("return/a;/.test(s);")
    }

    @Test
    fun shouldFailOnInlineCommentAfterUnclosedRegexp() {
        // Make this fail to be consistent with the original JSMin, although this is in fact valid Javascript if comment is
        // a defined variable.
        JsMinAssertions.assertThatThrownBy("var r = /a //comment")
            .isInstanceOf(JSMin.UnterminatedRegExpLiteralException::class.java)
    }

    @Test
    fun shouldFailOnUnclosedBlockCommentAfterUnclosedRegexp() {
        // same comment as above
        JsMinAssertions.assertThatThrownBy("var r = /a/*comment")
            .isInstanceOf(JSMin.UnterminatedRegExpLiteralException::class.java)
    }

    @Test
    fun minimizeFullJsFile() {
        val inputStream = javaClass.getResourceAsStream("/map_contains_value.js")
        JsMinAssertions.assertThat(inputStream)
            .isEqualTo(String(javaClass.getResourceAsStream("/map_contains_value.min.js").readAllBytes(), StandardCharsets.UTF_8))
    }

    private class JsMinAssertions(private val inputStream: InputStream) {

        constructor(jsToMinify: String) : this(ByteArrayInputStream(jsToMinify.toByteArray()))

        companion object {
            fun assertThat(jsToMinify: String): AbstractStringAssert<*> {
                val actuallyMinifiedText = JsMinAssertions(jsToMinify).minify()
                return Assertions.assertThat(actuallyMinifiedText)
            }

            fun assertThat(jsToMinify: InputStream): AbstractStringAssert<*> {
                val actuallyMinifiedText = JsMinAssertions(jsToMinify).minify()
                return Assertions.assertThat(actuallyMinifiedText)
            }

            fun assertThatThrownBy(actuallyMinifiedText: String): AbstractThrowableAssert<*, out Throwable> {
                return Assertions.assertThatThrownBy { JsMinAssertions(actuallyMinifiedText).minify() }
            }
        }

        private fun minify(): String {
            val byteArrayOutputStream = ByteArrayOutputStream()
            JSMin(inputStream, byteArrayOutputStream).jsmin()
            return byteArrayOutputStream.toString()
        }
    }

}