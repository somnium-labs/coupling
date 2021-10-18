package com.roy.coupling.common.consumer

import com.roy.coupling.common.commands.Command
import com.roy.coupling.common.support.JsonSupport
import kotlin.reflect.KClass

class CommandHandler<C : Command>(
    val commandClass: KClass<C>,
    private val handler: suspend (C) -> Unit,
) {
    suspend fun invoke(payload: String) {
        val command = JsonSupport.deserialize(payload, commandClass)
        handler.invoke(command)
    }
}