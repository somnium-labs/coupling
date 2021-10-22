package com.roy.coupling.example.customer.web.dto

import com.roy.coupling.example.common.domain.Money

data class GetCustomerResponse(
    val customerId: Long,
    val name: String,
    val creditLimit: Money
)
