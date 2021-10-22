package com.roy.coupling.example.customer.domain

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Version
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal

@Table("customer")
class Customer(
    val name: String,
    val amount: BigDecimal
) {
    @Id
    var id: Long? = null

    @Version
    var version: Long? = null
}
