package com.roy.coupling.example.order.service

import com.roy.coupling.common.logging.Logger.Companion.log
import com.roy.coupling.common.messaging.ReplyFailure
import com.roy.coupling.common.messaging.commands.CommandBuilder
import com.roy.coupling.common.producer.R2dbcMessageProducer
import com.roy.coupling.example.common.messaging.commands.ReserveCreditCommand
import com.roy.coupling.example.common.messaging.common.RejectionReason
import com.roy.coupling.example.common.messaging.replies.CustomerCreditLimitExceeded
import com.roy.coupling.example.common.messaging.replies.CustomerCreditReserved
import com.roy.coupling.example.common.messaging.replies.CustomerNotFound
import com.roy.coupling.example.common.messaging.sagas.CreateOrderSagaData
import com.roy.coupling.example.order.domain.Order
import com.roy.coupling.orchestration.SagaDsl
import org.springframework.stereotype.Component
import org.springframework.transaction.reactive.TransactionalOperator
import org.springframework.transaction.reactive.executeAndAwait


@Component
class CreateOrderSaga(
    private val orderService: OrderService,
    private val messageProducer: R2dbcMessageProducer,
    private val transactionalOperator: TransactionalOperator
) : SagaDsl {

    // TODO: 매번 정의가 새로 생성되는게 별로...
    suspend fun start(data: CreateOrderSagaData) {
        try {
            saga {
                // step 1
                remote {
                    transactionalOperator.executeAndAwait {
                        create(data)
                        reserveCredit(data)
                    }
                }.compensate { reject(data) }
                    .onReply(CustomerCreditReserved::class) { /* success */ }
                    .onReply(CustomerNotFound::class) { handleCustomerNotFound(data) }
                    .onReply(CustomerCreditLimitExceeded::class) { handleCustomerCreditLimitExceeded(data) }
                    .bind()
                // step 2
                local { approve(data) }.bind()
            }.transact()
        } catch (e: ReplyFailure) {
            // nothing
        }
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

    private fun handleCustomerNotFound(data: CreateOrderSagaData) {
        data.rejectionReason = RejectionReason.UNKNOWN_CUSTOMER
    }

    private fun handleCustomerCreditLimitExceeded(data: CreateOrderSagaData) {
        data.rejectionReason = RejectionReason.INSUFFICIENT_CREDIT
    }
}