package com.weedow.spring.data.search.validation.validator

data class Fraction private constructor(
        val numerator: Int,
        val denominator: Int
) : Number(), Comparable<Fraction> {

    override fun toDouble(): Double = numerator.toDouble() / denominator.toDouble()

    override fun toFloat(): Float = numerator.toFloat() / denominator.toFloat()

    override fun toLong(): Long = numerator.toLong() / denominator

    override fun toInt(): Int = numerator / denominator

    override fun toChar(): Char = toInt().toChar()

    override fun toShort(): Short = toInt().toShort()

    override fun toByte(): Byte = toInt().toByte()

    override operator fun compareTo(other: Fraction): Int {
        if (this === other) {
            return 0
        }
        if (numerator == other.numerator && denominator == other.denominator) {
            return 0
        }

        // otherwise see which is less
        val first = numerator.toLong() * other.denominator.toLong()
        val second = other.numerator.toLong() * denominator.toLong()
        return first.compareTo(second)
    }

    override fun toString(): String {
        return "$numerator/$denominator"
    }

    companion object {

        private const val serialVersionUID = 1L

        fun getFraction(numerator: Int, denominator: Int): Fraction {
            var numerator = numerator
            var denominator = denominator
            if (denominator == 0) {
                throw ArithmeticException("The denominator must not be zero")
            }
            if (denominator < 0) {
                if (numerator == Int.MIN_VALUE || denominator == Int.MIN_VALUE) {
                    throw ArithmeticException("overflow: can't negate")
                }
                numerator = -numerator
                denominator = -denominator
            }
            return Fraction(numerator, denominator)
        }

    }

}

