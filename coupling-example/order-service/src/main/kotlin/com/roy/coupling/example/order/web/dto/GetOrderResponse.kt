package com.roy.coupling.example.order.web.dto

import com.roy.coupling.example.common.messaging.common.OrderState
import com.roy.coupling.example.common.messaging.common.RejectionReason

data class GetOrderResponse(
    val orderId: Long,
    val orderState: OrderState,
    val rejectionReason: RejectionReason? = null,
)