package com.roy.coupling.common.producer

import com.roy.coupling.common.commands.Command

class MongoDbMessageProducer : MessageProducer {
    override suspend fun <C : Command> send(channel: String, command: C) {
        TODO("Not yet implemented")
    }
}