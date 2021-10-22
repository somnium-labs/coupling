package com.roy.coupling.common.messaging

import com.roy.coupling.common.logging.Logger.Companion.log
import com.roy.coupling.common.messaging.commands.Command
import com.roy.coupling.common.messaging.commands.CommandHandler
import com.roy.coupling.common.messaging.commands.CommandReplyOutcome
import com.roy.coupling.common.messaging.replies.Reply
import com.roy.coupling.common.messaging.replies.ReplyHandler
import kotlin.reflect.KClass
import kotlin.reflect.jvm.jvmName

class MessageDispatcher {
    companion object {
        private val commandHandlers: HashMap<String, CommandHandler<*>> = hashMapOf()
        private val replyHandlers: HashMap<String, ReplyHandler<*>> = hashMapOf()

        fun <C : Command> onCommand(commandClass: KClass<C>, handler: suspend (C) -> Unit) {
            commandHandlers[commandClass.jvmName] = CommandHandler(commandClass, handler)
        }

        fun <C : Reply> onReply(replyClass: KClass<C>, handler: suspend (CommandReplyOutcome, C) -> Unit) {
            replyHandlers[replyClass.jvmName] = ReplyHandler(replyClass, handler)
        }

        suspend fun invokeCommand(payload: String, commandType: String) {
            log.trace { "received command: $commandType" }
            commandHandlers[commandType]?.invoke(payload)
                ?: log.warn { "Unregistered command: $commandType" }
        }

        suspend fun invokeReply(outcome: String, payload: String, replyType: String) {
            log.trace { "received reply: $replyType" }
            replyHandlers[replyType]?.invoke(CommandReplyOutcome.valueOf(outcome), payload)
                ?: log.warn { "Unregistered reply: $replyType" }
        }
    }
}