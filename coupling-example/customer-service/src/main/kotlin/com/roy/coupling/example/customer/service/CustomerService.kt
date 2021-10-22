package com.roy.coupling.example.customer.service

import com.roy.coupling.common.logging.Logger.Companion.log
import com.roy.coupling.example.common.domain.Money
import com.roy.coupling.example.customer.domain.*
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Service

@Service
@Suppress("UNCHECKED_CAST")
class CustomerService(
    private val customerRepository: CustomerRepository,
    private val creditReservationRepository: CreditReservationRepository
) {
    suspend fun createCustomer(name: String, creditLimit: Money): Customer {
        val customer = Customer(name, creditLimit.amount)
        return customerRepository.save(customer)
    }

    suspend fun reserveCredit(customerId: Long, orderId: Long, orderTotal: Money) {
        val customer = customerRepository.findById(customerId) ?: throw CustomerNotFoundException()
        val creditReservations = creditReservationRepository.findAllByCustomerId(customerId).toList()
        val currentReservations = creditReservations.sumOf { it.amount }
        if (currentReservations + orderTotal.amount <= customer.amount) {
            log.info { "reserving credit(customer id: $customerId, order id: $orderId)" }
            creditReservationRepository.save(CreditReservation(customerId, orderTotal.amount))
        } else {
            log.info { "handling credit reservation failure (customerId: $customerId, orderId: $orderId)" }
            throw CustomerCreditLimitExceededException()
        }
    }
}
