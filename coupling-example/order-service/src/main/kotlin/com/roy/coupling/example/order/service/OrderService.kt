package com.roy.coupling.example.order.service

import com.roy.coupling.example.common.messaging.common.OrderDetails
import com.roy.coupling.example.common.messaging.common.RejectionReason
import com.roy.coupling.example.order.domain.Order
import com.roy.coupling.example.order.domain.OrderRepository
import org.springframework.stereotype.Service

@Service
class OrderService(
    private val orderRepository: OrderRepository
) {
    suspend fun createOrder(orderDetails: OrderDetails): Order {
        val order = Order.createOrder(orderDetails)
        return orderRepository.save(order)
    }

    suspend fun approveOrder(orderId: Long) {
        orderRepository.findById(orderId)?.let { order ->
            order.approve()
            orderRepository.save(order)
        }
    }

    suspend fun rejectOrder(orderId: Long, rejectionReason: RejectionReason) {
        orderRepository.findById(orderId)?.let { order ->
            order.reject(rejectionReason)
            orderRepository.save(order)
        }
    }
}
