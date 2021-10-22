package com.roy.coupling.example.customer.web.controller


import com.roy.coupling.example.common.domain.Money
import com.roy.coupling.example.customer.domain.CustomerRepository
import com.roy.coupling.example.customer.service.CustomerService
import com.roy.coupling.example.customer.web.dto.CreateCustomerRequest
import com.roy.coupling.example.customer.web.dto.CreateCustomerResponse
import com.roy.coupling.example.customer.web.dto.GetCustomerResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class CustomerController(
    private val customerService: CustomerService,
    private val customerRepository: CustomerRepository
) {
    @PostMapping("/customers")
    suspend fun createCustomer(@RequestBody createCustomerRequest: CreateCustomerRequest): CreateCustomerResponse {
        val customer = customerService.createCustomer(createCustomerRequest.name, createCustomerRequest.creditLimit)
        return CreateCustomerResponse(customer)
    }

    @GetMapping("/customers/{customerId}")
    suspend fun getCustomer(@PathVariable customerId: Long): ResponseEntity<GetCustomerResponse> {
        return customerRepository.findById(customerId)?.let { customer ->
            return ResponseEntity(
                GetCustomerResponse(customer.id!!, customer.name, Money(customer.amount)),
                HttpStatus.OK
            )
        } ?: ResponseEntity(HttpStatus.NOT_FOUND)
    }
}
