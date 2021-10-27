package com.roy.coupling.example.customer.service

import com.roy.coupling.example.common.messaging.commands.ReserveCreditCommand
import com.roy.coupling.example.common.messaging.replies.CustomerCreditLimitExceeded
import com.roy.coupling.example.common.messaging.replies.CustomerCreditReserved
import com.roy.coupling.example.common.messaging.replies.CustomerNotFound
import com.roy.coupling.example.customer.domain.CustomerCreditLimitExceededException
import com.roy.coupling.example.customer.domain.CustomerNotFoundException
import com.roy.coupling.messaging.MessageDispatcher
import com.roy.coupling.messaging.OutboxEventMessage
import com.roy.coupling.messaging.replies.ReplyBuilder
import org.springframework.stereotype.Service
import org.springframework.transaction.reactive.TransactionalOperator
import org.springframework.transaction.reactive.executeAndAwait

@Service
class CustomerCommandHandler(
    private val customerService: CustomerService,
    private val transactionalOperator: TransactionalOperator,
) {
    init {
        MessageDispatcher
            .onCommand(ReserveCreditCommand::class, ::handleReserveCredit)
    }

    private suspend fun handleReserveCredit(cmd: ReserveCreditCommand): OutboxEventMessage? {
        return transactionalOperator.executeAndAwait {
            return@executeAndAwait try {
                customerService.reserveCredit(cmd.customerId, cmd.orderId, cmd.orderTotal)
                ReplyBuilder.withSuccess(CustomerCreditReserved())
            } catch (e: CustomerNotFoundException) {
                ReplyBuilder.withFailure(CustomerNotFound())
            } catch (e: CustomerCreditLimitExceededException) {
                ReplyBuilder.withFailure(CustomerCreditLimitExceeded())
            }
        }
    }
}
