package com.roy.coupling.common.messaging.replies

import com.roy.coupling.common.messaging.MessageHeaders
import com.roy.coupling.common.messaging.Messages
import com.roy.coupling.common.messaging.OutboxEventMessage
import com.roy.coupling.common.messaging.commands.CommandReplyOutcome
import kotlin.reflect.jvm.jvmName

class ReplyBuilder {
    companion object {
        private fun <T : Reply> with(
            aggregateId: String,
            reply: T,
            outcome: CommandReplyOutcome
        ): OutboxEventMessage<T> {
            val headers = mapOf(
                MessageHeaders.TYPE to Messages.REPLY.name,
                MessageHeaders.AGGREGATE_ID to aggregateId,
                ReplyHeaders.TYPE to reply::class.jvmName,
                ReplyHeaders.OUTCOME to outcome.name,
            )
            return OutboxEventMessage(headers, reply)
        }

        fun <T : Reply> withSuccess(aggregateId: String, reply: T): OutboxEventMessage<T> {
            return with(aggregateId, reply, CommandReplyOutcome.SUCCESS)
        }

        fun <T : Reply> withFailure(aggregateId: String, reply: T): OutboxEventMessage<T> {
            return with(aggregateId, reply, CommandReplyOutcome.FAILURE)
        }
    }
}