package com.roy.coupling.messaging.commands

import com.roy.coupling.messaging.MessageProducer
import com.roy.coupling.messaging.OutboxEventMessage
import org.springframework.stereotype.Component
import kotlin.reflect.KClass
import kotlin.reflect.jvm.jvmName

@Component
class CommandHandlers(private val messageProducer: MessageProducer) {

    companion object {
        val commandHandlers: HashMap<String, CommandHandler<*>> = hashMapOf()

        fun <C : Command> onCommand(commandClass: KClass<C>, handler: suspend (C) -> OutboxEventMessage?) {
            commandHandlers[commandClass.jvmName] = CommandHandler(commandClass, handler)
        }
    }
}
