package com.roy.coupling.example.customer.service

import com.roy.coupling.common.logging.Logger.Companion.log
import com.roy.coupling.common.messaging.MessageDispatcher
import com.roy.coupling.common.messaging.replies.ReplyBuilder
import com.roy.coupling.common.producer.R2dbcMessageProducer
import com.roy.coupling.example.common.messaging.commands.ReserveCreditCommand
import com.roy.coupling.example.common.messaging.replies.CustomerCreditLimitExceeded
import com.roy.coupling.example.common.messaging.replies.CustomerCreditReserved
import com.roy.coupling.example.common.messaging.replies.CustomerNotFound
import com.roy.coupling.example.customer.domain.CustomerCreditLimitExceededException
import com.roy.coupling.example.customer.domain.CustomerNotFoundException
import org.springframework.stereotype.Component

@Component
class CustomerCommandHandler(
    private val customerService: CustomerService,
    private val r2dbcMessageProducer: R2dbcMessageProducer,
) {
    init {
        MessageDispatcher.onCommand(ReserveCreditCommand::class, ::reserveCredit)
    }

    private suspend fun reserveCredit(cmd: ReserveCreditCommand) {
        val message = try {
            customerService.reserveCredit(cmd.customerId, cmd.orderId, cmd.orderTotal)
            ReplyBuilder.withSuccess(cmd.customerId.toString(), CustomerCreditReserved())
        } catch (e: CustomerNotFoundException) {
            ReplyBuilder.withFailure(cmd.customerId.toString(), CustomerNotFound())
        } catch (e: CustomerCreditLimitExceededException) {
            ReplyBuilder.withFailure(cmd.customerId.toString(), CustomerCreditLimitExceeded())
        } catch (e: Throwable) {
            log.error { e.message }
            throw e
        }
        // TODO: 이걸 굳이 CDC를 통해 전달할 이유가?
        r2dbcMessageProducer.send("customer.reply", message)
    }
}
