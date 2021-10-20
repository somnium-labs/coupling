package com.roy.coupling.example.order.domain

import com.roy.coupling.example.common.messaging.common.OrderDetails
import com.roy.coupling.example.common.messaging.common.OrderState
import com.roy.coupling.example.common.messaging.common.RejectionReason
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal

@Table("orders")
data class Order(
    val customerId: Long,
    var state: OrderState,
    val amount: BigDecimal,
    var rejectionReason: RejectionReason? = null,
    @Id var id: Long? = null,
) {
    constructor(orderDetails: OrderDetails) : this(
        orderDetails.customerId,
        OrderState.PENDING,
        orderDetails.orderTotal.amount
    )

    fun approve() {
        state = OrderState.APPROVED
    }

    fun reject(rejectionReason: RejectionReason) {
        state = OrderState.REJECTED
        this.rejectionReason = rejectionReason
    }

    companion object {
        fun createOrder(orderDetails: OrderDetails): Order {
            return Order(orderDetails)
        }
    }
}