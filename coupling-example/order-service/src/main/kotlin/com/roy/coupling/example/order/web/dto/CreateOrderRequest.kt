package com.roy.coupling.example.order.web.dto

import com.roy.coupling.example.common.domain.Money

data class CreateOrderRequest(
    val orderTotal: Money,
    val customerId: Long
)