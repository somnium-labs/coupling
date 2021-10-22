package com.roy.coupling.example.customer.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal

@Table("customer_credit_reservation")
data class CreditReservation(
    var customerId: Long,
    var amount: BigDecimal,
) {
    @Id
    var id: Long? = null
}
