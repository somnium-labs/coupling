package com.roy.coupling.common.producer

import com.roy.coupling.common.commands.Command

interface MessageProducer {
    suspend fun <C : Command> send(channel: String, command: C)
}