package com.roy.coupling.example.order.domain

import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface OrderRepository : CoroutineCrudRepository<Order, Long> {
    suspend fun findAllByCustomerId(customerId: Long): List<Order>
}