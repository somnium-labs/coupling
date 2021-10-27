package com.roy.coupling.example.order.service

import com.roy.coupling.common.logging.Logger.Companion.log
import com.roy.coupling.example.common.messaging.commands.ReserveCreditCommand
import com.roy.coupling.example.common.messaging.common.RejectionReason
import com.roy.coupling.example.common.messaging.replies.CustomerCreditLimitExceeded
import com.roy.coupling.example.common.messaging.replies.CustomerCreditReserved
import com.roy.coupling.example.common.messaging.replies.CustomerNotFound
import com.roy.coupling.example.common.messaging.sagas.CreateOrderSagaData
import com.roy.coupling.example.order.domain.Order
import com.roy.coupling.messaging.MessageProducer
import com.roy.coupling.messaging.commands.CommandBuilder
import com.roy.coupling.orchestration.SagaBuilder
import org.springframework.stereotype.Component
import org.springframework.transaction.reactive.TransactionalOperator
import org.springframework.transaction.reactive.executeAndAwait

@Component
class CreateOrderSaga(
    private val orderService: OrderService,
    private val messageProducer: MessageProducer,
    private val transactionalOperator: TransactionalOperator,
) {
    private val sagaDefinition = SagaBuilder<CreateOrderSagaData> {
        transactionalOperator.executeAndAwait {
            local(::create).compensate(::reject).bind()
            participant(::reserveCredit).bind()
        }
        await().bind()
        local(::approve).bind()
    }.onSuccess(CustomerCreditReserved::class)
        .onFailure(CustomerNotFound::class, ::handleCustomerNotFound)
        .onFailure(CustomerCreditLimitExceeded::class, ::handleCustomerCreditLimitExceeded)

    suspend fun start(data: CreateOrderSagaData) {
        sagaDefinition.transact(data)
    }

    private suspend fun create(data: CreateOrderSagaData): Order {
        log.warn { "create order" }
        return orderService.createOrder(data.orderDetails).also {
            data.orderId = it.id!!
        }
    }

    private suspend fun approve(data: CreateOrderSagaData) {
        log.warn { "approve order" }
        orderService.approveOrder(data.orderId)
    }

    private suspend fun reject(data: CreateOrderSagaData) {
        log.warn { "reject order" }
        orderService.rejectOrder(data.orderId, data.rejectionReason!!)
    }

    private suspend fun reserveCredit(data: CreateOrderSagaData) {
        val orderId = data.orderId
        val customerId = data.orderDetails.customerId
        val orderTotal = data.orderDetails.orderTotal
        val command = ReserveCreditCommand(customerId, orderId, orderTotal)
        messageProducer.send("customerService", CommandBuilder.build(command))
    }

    private fun handleCustomerNotFound(reply: CustomerNotFound, data: CreateOrderSagaData) {
        data.rejectionReason = RejectionReason.UNKNOWN_CUSTOMER
    }

    private fun handleCustomerCreditLimitExceeded(reply: CustomerCreditLimitExceeded, data: CreateOrderSagaData) {
        data.rejectionReason = RejectionReason.INSUFFICIENT_CREDIT
    }
}
