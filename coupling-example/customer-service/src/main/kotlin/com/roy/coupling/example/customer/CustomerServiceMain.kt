package com.roy.coupling.example.customer

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CustomerServiceMain

fun main(args: Array<String>) {
    runApplication<CustomerServiceMain>(*args)
}