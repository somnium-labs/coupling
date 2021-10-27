package com.roy.coupling.messaging

interface MessageProducer {
    suspend fun send(channel: String, message: OutboxEventMessage)
}
