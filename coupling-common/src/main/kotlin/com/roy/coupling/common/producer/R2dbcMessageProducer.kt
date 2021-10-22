package com.roy.coupling.common.producer

import com.roy.coupling.common.domain.OutboxEvent
import com.roy.coupling.common.domain.OutboxEventRepository
import com.roy.coupling.common.messaging.Message
import com.roy.coupling.common.messaging.MessageHeaders
import com.roy.coupling.common.messaging.OutboxEventMessage
import com.roy.coupling.common.support.JsonSupport
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class R2dbcMessageProducer(
    private val outboxEventRepository: OutboxEventRepository
) : MessageProducer {
    override suspend fun <C : Message> send(channel: String, message: OutboxEventMessage<C>) {
        val outboxEvent = OutboxEvent(
            aggregateType = channel,
            aggregateId = message.headers[MessageHeaders.AGGREGATE_ID]!!,
            headers = JsonSupport.serialize(message.headers),
            payload = JsonSupport.serialize(message.payload),
            creationTime = Instant.now()
        )

        outboxEventRepository.save(outboxEvent)
//        CoroutineScope(Dispatchers.IO).launch { outboxEventRepository.delete(outboxEvent) }
    }
}
