package com.roy.coupling.producer

import com.roy.coupling.messaging.MessageProducer
import com.roy.coupling.messaging.OutboxEventMessage

class MongoDbMessageProducer : MessageProducer {
    override suspend fun send(channel: String, message: OutboxEventMessage) {
        TODO("Not yet implemented")
    }
}
