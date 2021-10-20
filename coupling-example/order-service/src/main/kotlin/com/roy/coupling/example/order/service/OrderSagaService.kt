package com.roy.coupling.example.order.service

import com.roy.coupling.example.common.messaging.common.OrderDetails
import com.roy.coupling.example.common.messaging.sagas.CreateOrderSagaData
import com.roy.coupling.example.order.domain.Order
import com.roy.coupling.example.order.domain.OrderRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OrderSagaService(
    private val orderRepository: OrderRepository,
    private val createOrderSaga: CreateOrderSaga,
) {
    @Transactional
    suspend fun createOrder(orderDetails: OrderDetails): Order? {
        val data = CreateOrderSagaData(orderDetails)
        createOrderSaga.start(data)
        return orderRepository.findById(data.orderId)
    }
}