package com.roy.coupling.messaging.replies

import com.roy.coupling.messaging.MessageHeaders
import com.roy.coupling.messaging.Messages
import com.roy.coupling.messaging.OutboxEventMessage
import com.roy.coupling.messaging.commands.CommandReplyOutcome
import kotlin.reflect.jvm.jvmName

class ReplyBuilder {
    companion object {
        private fun <T : Reply> with(
            reply: T,
            outcome: CommandReplyOutcome
        ): OutboxEventMessage {
            val headers = mutableMapOf(
                MessageHeaders.TYPE to Messages.REPLY.name,
                ReplyHeaders.TYPE to reply::class.jvmName,
                ReplyHeaders.OUTCOME to outcome.name,
            )
            return OutboxEventMessage(headers, reply)
        }

        fun <T : Reply> withSuccess(reply: T): OutboxEventMessage {
            return with(reply, CommandReplyOutcome.SUCCESS)
        }

        fun <T : Reply> withFailure(reply: T): OutboxEventMessage {
            return with(reply, CommandReplyOutcome.FAILURE)
        }
    }
}
