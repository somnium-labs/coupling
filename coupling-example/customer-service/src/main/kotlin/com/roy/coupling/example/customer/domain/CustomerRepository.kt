package com.roy.coupling.example.customer.domain

import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface CustomerRepository : CoroutineCrudRepository<Customer, Long>
