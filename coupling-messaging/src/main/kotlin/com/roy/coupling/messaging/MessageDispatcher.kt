package com.roy.coupling.messaging

import com.roy.coupling.common.logging.Logger.Companion.log
import com.roy.coupling.messaging.commands.Command
import com.roy.coupling.messaging.commands.CommandHandler
import com.roy.coupling.messaging.commands.CommandReplyOutcome
import com.roy.coupling.messaging.replies.Reply
import com.roy.coupling.messaging.replies.ReplyHandler
import kotlin.reflect.KClass
import kotlin.reflect.jvm.jvmName

class MessageDispatcher(
    private val messageProducer: MessageProducer,
) {
    suspend fun invokeCommand(topic: String, payload: String, commandType: String, aggregateId: String) {
        log.trace { "received command: $commandType" }
        commandHandlers[commandType]?.let { handler ->
            val reply = handler.invoke(payload)
            if (reply != null) {
                reply.headers[MessageHeaders.AGGREGATE_ID] = aggregateId
                messageProducer.send("${topic.replace("outbox.event.", "")}.reply", reply)
            }
        } ?: log.warn { "Unregistered command: $commandType" }
    }

    suspend fun invokeReply(outcome: String, payload: String, replyType: String) {
        log.trace { "received reply: $replyType" }
        replyHandlers[replyType]?.invoke(CommandReplyOutcome.valueOf(outcome), payload)
            ?: log.warn { "Unregistered reply: $replyType" }
    }

    companion object {
        val commandHandlers: HashMap<String, CommandHandler<*>> = hashMapOf()
        val replyHandlers: HashMap<String, ReplyHandler<*>> = hashMapOf()

        fun <C : Command> onCommand(commandClass: KClass<C>, handler: suspend (C) -> OutboxEventMessage?) {
            commandHandlers[commandClass.jvmName] = CommandHandler(commandClass, handler)
        }

        fun <C : Reply> onReply(replyClass: KClass<C>, handler: suspend (CommandReplyOutcome, C) -> Unit) {
            replyHandlers[replyClass.jvmName] = ReplyHandler(replyClass, handler)
        }
    }
}
