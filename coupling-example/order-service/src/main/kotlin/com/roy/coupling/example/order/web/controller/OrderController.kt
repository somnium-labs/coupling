package com.roy.coupling.example.order.web.controller

import com.roy.coupling.example.common.messaging.common.OrderDetails
import com.roy.coupling.example.common.messaging.sagas.CreateOrderSagaData.Companion.INVALID_ORDER_ID
import com.roy.coupling.example.order.domain.OrderRepository
import com.roy.coupling.example.order.service.OrderSagaService
import com.roy.coupling.example.order.web.dto.CreateOrderRequest
import com.roy.coupling.example.order.web.dto.CreateOrderResponse
import com.roy.coupling.example.order.web.dto.GetOrderResponse
import kotlinx.coroutines.flow.toList
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class OrderController(
    private val orderSagaService: OrderSagaService,
    private val orderRepository: OrderRepository,
) {
    @PostMapping("/orders")
    suspend fun createOrder(@RequestBody createOrderRequest: CreateOrderRequest): CreateOrderResponse {
        val order =
            orderSagaService.createOrder(OrderDetails(createOrderRequest.customerId, createOrderRequest.orderTotal))
        return CreateOrderResponse(order?.id ?: INVALID_ORDER_ID)
    }

    @GetMapping("/orders/{orderId}")
    suspend fun getOrder(@PathVariable orderId: Long): ResponseEntity<GetOrderResponse> {
        return orderRepository.findById(orderId)?.let { order ->
            ResponseEntity(GetOrderResponse(order.id!!, order.state, order.rejectionReason), HttpStatus.OK)
        } ?: ResponseEntity(HttpStatus.NOT_FOUND)
    }

    @GetMapping("/orders/customer/{customerId}")
    suspend fun getOrdersByCustomerId(@PathVariable customerId: Long): ResponseEntity<List<GetOrderResponse>> {
        return ResponseEntity(orderRepository.findAllByCustomerId(customerId).toList().map { order ->
            GetOrderResponse(order.id!!, order.state, order.rejectionReason)
        }, HttpStatus.OK)
    }
}