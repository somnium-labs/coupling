package com.roy.coupling.example.customer.domain

import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface CreditReservationRepository : CoroutineCrudRepository<CreditReservation, Long> {
    fun findAllByCustomerId(customerId: Long): Flow<CreditReservation>
}