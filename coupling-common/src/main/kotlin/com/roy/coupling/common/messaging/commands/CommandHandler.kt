package com.roy.coupling.common.messaging.commands

import com.roy.coupling.common.support.JsonSupport
import kotlin.reflect.KClass
import kotlin.reflect.jvm.jvmName

class CommandHandler<C : Command>(
    private val commandClass: KClass<C>,
    private val handler: suspend (C) -> Unit,
) {
    val className: String
        get() = commandClass.jvmName

    suspend fun invoke(payload: String) {
        val command = JsonSupport.deserialize(payload, commandClass)
        handler.invoke(command)
    }
}