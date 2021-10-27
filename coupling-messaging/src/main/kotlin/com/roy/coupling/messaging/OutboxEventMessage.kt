package com.roy.coupling.messaging

data class OutboxEventMessage(
    val headers: MutableMap<String, String>,
    val payload: Message,
)
