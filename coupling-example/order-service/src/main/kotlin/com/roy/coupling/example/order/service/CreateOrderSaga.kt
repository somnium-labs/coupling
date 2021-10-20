package com.roy.coupling.example.order.service

import com.roy.coupling.common.producer.MessageProducer
import com.roy.coupling.example.common.messaging.commands.ReserveCreditCommand
import com.roy.coupling.example.common.messaging.common.RejectionReason
import com.roy.coupling.example.common.messaging.sagas.CreateOrderSagaData
import com.roy.coupling.example.order.domain.Order
import com.roy.coupling.orchestration.SagaDsl
import org.springframework.stereotype.Component

@Component
class CreateOrderSaga(
    private val orderService: OrderService,
    private val messageProducer: MessageProducer,
) : SagaDsl {
    suspend fun start(data: CreateOrderSagaData) {
        saga {
            invokeLocal { create(data) }.compensate { reject(data) }.bind()
            invokeParticipant { reserveCredit(data) }
//                .onReply(CustomerNotFound::class) { handleCustomerNotFound(data) }
//                .onReply(CustomerCreditLimitExceeded::class) { handleCustomerCreditLimitExceeded(data) }
                .bind()
            invokeLocal { approve(data) }.bind()
        }.build()
    }

    private suspend fun create(data: CreateOrderSagaData): Order {
        return orderService.createOrder(data.orderDetails).also {
            data.orderId = it.id!!
        }
    }

    private suspend fun approve(data: CreateOrderSagaData) {
        orderService.approveOrder(data.orderId)
    }

    private suspend fun reject(data: CreateOrderSagaData) {
        orderService.rejectOrder(data.orderId, data.rejectionReason!!)
    }

    private suspend fun reserveCredit(data: CreateOrderSagaData) {
        val orderId = data.orderId
        val customerId = data.orderDetails.customerId
        val orderTotal = data.orderDetails.orderTotal
        val command = ReserveCreditCommand(customerId, orderId, orderTotal)
        messageProducer.send("customerService", command)
    }

    private fun handleCustomerNotFound(data: CreateOrderSagaData) {
        data.rejectionReason = RejectionReason.UNKNOWN_CUSTOMER
    }

    private fun handleCustomerCreditLimitExceeded(data: CreateOrderSagaData) {
        data.rejectionReason = RejectionReason.INSUFFICIENT_CREDIT
    }
}