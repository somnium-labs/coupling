package com.roy.coupling.common.messaging

interface Message

data class OutboxEventMessage<C : Message>(
    val headers: Map<String, String>,
    val payload: C,
)