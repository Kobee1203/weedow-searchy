package com.weedow.searchy.mongodb.utils

import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.io.PushbackInputStream

/**
 * JsMin.java.
 *
 * Copyright (c) 2006 John Reilly (www.inconspicuous.org) This work is a translation from C to Java of jsmin.c published by Douglas Crockford.
 * Permission is hereby granted to use the Java version under the same conditions as the jsmin.c on which it is based.
 *
 * http://www.crockford.com/javascript/jsmin.html
 *
 * @param in Input representing a JS content to be minified
 * @param out Output to write the minified JS content
 */
class JSMin(
    `in`: InputStream,
    private val out: OutputStream
) {
    private val `in`: PushbackInputStream = PushbackInputStream(`in`)
    private var theA = NUL
    private var theB = NUL
    private var theX = EOF
    private var theY = EOF

    companion object {
        private const val EOF = -1
        private const val NUL = 0
        private const val INVISIBLE_CHAR = 0xEF

        private const val LINE_FEED = '\n'.toInt() // 10
        private const val CARRIAGE_RETURN = '\r'.toInt() // 13
        private const val SPACE = ' '.toInt() // 32
        private const val EXCLAMATION_MARK = '!'.toInt() // 33
        private const val DOUBLE_QUOTE = '"'.toInt() // 34
        private const val DOLLAR = '$'.toInt() // 36
        private const val AMPERSAND = '&'.toInt() // 38
        private const val SINGLE_QUOTE = '\''.toInt() // 39
        private const val ASTERISK = '*'.toInt() // 42
        private const val LF = '('.toInt() // 40
        private const val RF = ')'.toInt() // 41
        private const val PLUS = '+'.toInt() // 43
        private const val COMMA = ','.toInt() // 44
        private const val MINUS = '-'.toInt() // 45
        private const val SLASH = '/'.toInt() // 47
        private const val ZERO = '0'.toInt() // 48
        private const val NINE = '9'.toInt() // 57
        private const val COLONS = ':'.toInt() // 58
        private const val EQUAL = '='.toInt() // 61
        private const val QUESTION_MARK = '?'.toInt() // 63
        private const val UPPERCASE_A = 'A'.toInt() // 65
        private const val UPPERCASE_Z = 'Z'.toInt() // 90
        private const val LSB = '['.toInt() // 91
        private const val BACKSLASH = '\\'.toInt() // 92
        private const val RSB = ']'.toInt() // 93
        private const val UNDERSCORE = '_'.toInt() // 95
        private const val BACK_QUOTE = '`'.toInt() // 96
        private const val LOWERCASE_A = 'a'.toInt() // 97
        private const val LOWERCASE_Z = 'z'.toInt() // 122
        private const val LB = '{'.toInt() // 123
        private const val PIPE = '|'.toInt() // 124
        private const val RB = '}'.toInt() // 125
        private const val TILDE = '~'.toInt() // 126

        /**
         * isAlphanum -- return true if the character is a letter, digit, underscore,
         * dollar sign, or non-ASCII character.
         */
        fun isAlphanum(c: Int): Boolean {
            return (c in LOWERCASE_A..LOWERCASE_Z || c in ZERO..NINE || c in UPPERCASE_A..UPPERCASE_Z || c == UNDERSCORE || c == DOLLAR || c == BACKSLASH || c > TILDE)
        }
    }

    /**
     * get -- return the next character from stdin. Watch out for lookahead. If
     * the character is a control character, translate it to a space or linefeed.
     */
    private fun get(): Int {
        val c = `in`.read()
        if (c >= SPACE || c == LINE_FEED || c == EOF) {
            return c
        }
        return if (c == CARRIAGE_RETURN) LINE_FEED else SPACE
    }

    /**
     * Get the next character without getting it.
     */
    private fun peek(): Int {
        val lookaheadChar = `in`.read()
        `in`.unread(lookaheadChar)
        return lookaheadChar
    }

    /**
     * next -- get the next character, excluding comments. peek() is used to see if a '/' is followed by a '/' or '*'.
     */
    private fun next(): Int {
        var c = get()
        if (c == SLASH) {
            when (peek()) {
                SLASH -> do {
                    c = get()
                } while (c > LINE_FEED)
                ASTERISK -> {
                    get()
                    while (c != SPACE) {
                        when (get()) {
                            ASTERISK -> if (peek() == SLASH) {
                                get()
                                c = SPACE
                            }
                            EOF -> throw UnterminatedCommentException()
                        }
                    }
                }
            }
        }
        theY = theX
        theX = c
        return c
    }

    /**
     * action -- do something! What you do is determined by the argument:
     *
     *  * 1 Output A. Copy B to A. Get the next B.
     *  * 2 Copy B to A. Get the next B. (Delete A).
     *  * 3 Get the next B. (Delete B).
     *
     * action treats a string as a single character. Wow!
     *
     * action recognizes a regular expression if it is preceded by ( or , or =.
     */
    private fun action(d: Int) {
        when (d) {
            1 -> action1()
            2 -> action2()
            3 -> action3()
        }
    }

    private fun action1() {
        write(theA)
        if (theA == theB && (theA == PLUS || theA == MINUS) && theY != theA) {
            write(SPACE)
        }

        action2()
    }

    private fun action2() {
        theA = theB

        if (theA == SINGLE_QUOTE || theA == DOUBLE_QUOTE || theA == BACK_QUOTE) {
            while (true) {
                write(theA)
                theA = get()
                if (theA == theB) {
                    break
                }
                if (theA <= LINE_FEED) {
                    throw UnterminatedStringLiteralException()
                }
                if (theA == BACKSLASH) {
                    write(theA)
                    theA = get()
                }
            }
        }

        action3()
    }

    private fun action3() {
        theB = next()
        if (theB == SLASH && (theA == LF || theA == COMMA || theA == EQUAL || theA == COLONS || theA == LSB || theA == EXCLAMATION_MARK || theA == AMPERSAND || theA == PIPE || theA == QUESTION_MARK || theA == PLUS || theA == MINUS || theA == TILDE || theA == ASTERISK || theA == SLASH || theA == LB || theA == LINE_FEED)) {
            write(theA)
            if (theA == SLASH || theA == ASTERISK) {
                write(SPACE)
            }
            write(theB)
            while (true) {
                theA = get()
                if (theA == LSB) {
                    while (true) {
                        write(theA)
                        theA = get()
                        if (theA == RSB) {
                            break
                        }
                        if (theA == BACKSLASH) {
                            write(theA)
                            theA = get()
                        }
                        if (theA <= LINE_FEED) {
                            throw UnterminatedRegExpLiteralException()
                        }
                    }
                } else if (theA == SLASH) {
                    when (peek()) {
                        SLASH, ASTERISK -> throw UnterminatedRegExpLiteralException()
                    }
                    break
                } else if (theA == BACKSLASH) {
                    write(theA)
                    theA = get()
                }
                if (theA <= LINE_FEED) {
                    throw UnterminatedRegExpLiteralException()
                }
                write(theA)
            }
            theB = next()
        }
    }

    private fun write(c: Int) {
        if (c != NUL) {
            out.write(c)
        }
    }

    /**
     * jsmin -- Copy the input to the output, deleting the characters which are insignificant to JavaScript.
     * Comments will be removed. Tabs will be replaced with spaces. Carriage returns will be replaced with linefeeds. Most spaces and linefeeds will be removed.
     */
    @Throws(
        IOException::class,
        UnterminatedRegExpLiteralException::class,
        UnterminatedCommentException::class,
        UnterminatedStringLiteralException::class
    )
    fun jsmin() {
        if (peek() == INVISIBLE_CHAR) {
            get()
            get()
            get()
        }
        //theA = LINE_FEED
        action(3)
        while (theA != EOF) {
            when (theA) {
                SPACE -> if (isAlphanum(theB)) {
                    action(1)
                } else {
                    action(2)
                }
                LINE_FEED -> when (theB) {
                    LB, LSB, LF, PLUS, MINUS, EXCLAMATION_MARK, TILDE -> action(1)
                    SPACE -> action(3)
                    else -> if (isAlphanum(theB)) {
                        action(1)
                    } else {
                        action(2)
                    }
                }
                else -> when (theB) {
                    SPACE -> {
                        if (isAlphanum(theA)) {
                            action(1)
                        } else {
                            action(3)
                        }
                    }
                    LINE_FEED -> when (theA) {
                        RB, RSB, RF, PLUS, MINUS, DOUBLE_QUOTE, SINGLE_QUOTE, BACK_QUOTE -> action(1)
                        else -> if (isAlphanum(theA)) {
                            action(1)
                        } else {
                            action(3)
                        }
                    }
                    else -> action(1)
                }
            }
        }
        out.flush()
    }

    class UnterminatedCommentException : Exception()

    class UnterminatedStringLiteralException : Exception()

    class UnterminatedRegExpLiteralException : Exception()

}