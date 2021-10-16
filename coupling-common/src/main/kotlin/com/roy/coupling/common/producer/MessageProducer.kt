package com.roy.coupling.common.producer

import com.roy.coupling.common.messaging.Message

interface MessageProducer {
    suspend fun send(message: Message)
}
