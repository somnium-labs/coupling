package com.roy.coupling.common.producer

import com.roy.coupling.common.messaging.Message
import com.roy.coupling.common.messaging.OutboxEventMessage

class MongoDbMessageProducer : MessageProducer {
    override suspend fun <C : Message> send(channel: String, message: OutboxEventMessage<C>) {
        TODO("Not yet implemented")
    }
}