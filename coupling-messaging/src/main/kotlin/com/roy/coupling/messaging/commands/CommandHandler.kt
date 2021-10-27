package com.roy.coupling.messaging.commands

import com.roy.coupling.common.support.JsonSupport
import com.roy.coupling.messaging.OutboxEventMessage
import kotlin.reflect.KClass
import kotlin.reflect.jvm.jvmName

class CommandHandler<C : Command>(
    private val commandClass: KClass<C>,
    private val handler: suspend (C) -> OutboxEventMessage?,
) {
    val className: String
        get() = commandClass.jvmName

    suspend fun invoke(payload: String): OutboxEventMessage? {
        val command = JsonSupport.deserialize(payload, commandClass)
        return handler.invoke(command)
    }
}
