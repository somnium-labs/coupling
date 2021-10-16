package com.roy.coupling.common.producer

import com.roy.coupling.common.OutboxEvent
import com.roy.coupling.common.OutboxEventRepository
import com.roy.coupling.common.messaging.Message
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.*

@Component
class R2dbcMessageProducer(
    private val outboxEventRepository: OutboxEventRepository,
) : MessageProducer {

    override suspend fun send(message: Message) {
        val outboxEvent = OutboxEvent(
            id = UUID.randomUUID().toString(),
            aggregateType = message.aggregateType,
            aggregateId = message.aggregateId,
            commandType = message.commandType,
            payload = message.payload,
            creationTime = Instant.now()
        )

        outboxEventRepository.save(outboxEvent)
        CoroutineScope(Dispatchers.IO).launch { outboxEventRepository.delete(outboxEvent) }
    }
}
