package com.roy.coupling.example.order.service

import com.roy.coupling.example.common.messaging.common.OrderDetails
import com.roy.coupling.example.common.messaging.sagas.CreateOrderSagaData
import com.roy.coupling.example.order.domain.Order
import com.roy.coupling.example.order.domain.OrderRepository
import org.springframework.stereotype.Service

@Service
class OrderSagaService(
    private val orderRepository: OrderRepository,
    private val createOrderSaga: CreateOrderSaga,
) {
    suspend fun createOrder(orderDetails: OrderDetails): Order? {
        val data = CreateOrderSagaData(orderDetails)
        createOrderSaga.start(data)
        return orderRepository.findById(data.orderId)
    }
}