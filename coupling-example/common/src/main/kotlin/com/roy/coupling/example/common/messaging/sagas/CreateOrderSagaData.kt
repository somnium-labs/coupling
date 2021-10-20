package com.roy.coupling.example.common.messaging.sagas

import com.roy.coupling.example.common.messaging.common.OrderDetails
import com.roy.coupling.example.common.messaging.common.RejectionReason

data class CreateOrderSagaData(
    val orderDetails: OrderDetails
) {
    var orderId: Long = INVALID_ORDER_ID
    var rejectionReason: RejectionReason? = null

    companion object {
        const val INVALID_ORDER_ID = 0L
    }
}