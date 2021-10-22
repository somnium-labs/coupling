package com.roy.coupling.common.producer

import com.roy.coupling.common.messaging.Message
import com.roy.coupling.common.messaging.OutboxEventMessage

interface MessageProducer {
    suspend fun <C : Message> send(channel: String, message: OutboxEventMessage<C>)
}