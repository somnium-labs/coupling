package com.roy.coupling.example.order.domain

import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface OrderRepository : CoroutineCrudRepository<Order, Long> {
    fun findAllByCustomerId(customerId: Long): Flow<Order>
}