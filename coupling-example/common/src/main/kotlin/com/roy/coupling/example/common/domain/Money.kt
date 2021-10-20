package com.roy.coupling.example.common.domain

import java.math.BigDecimal

data class Money(
    var amount: BigDecimal
) {
    constructor(i: Int) : this(BigDecimal(i))
    constructor(s: String) : this(BigDecimal(s))

    operator fun plus(other: Money): Money {
        return Money(amount.add(other.amount))
    }

    operator fun minus(other: Money): Money {
        return Money(amount.subtract(other.amount))
    }

    operator fun compareTo(other: Money): Int {
        return this.amount.compareTo(other.amount)
    }

    fun add(other: Money): Money {
        return Money(amount.add(other.amount))
    }

    fun subtract(other: Money): Money {
        return Money(amount.subtract(other.amount))
    }

    companion object {
        val ZERO = Money(0)
    }
}