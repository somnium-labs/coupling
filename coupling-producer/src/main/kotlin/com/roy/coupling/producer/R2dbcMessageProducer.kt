package com.roy.coupling.producer

import com.roy.coupling.common.domain.OutboxEvent
import com.roy.coupling.common.domain.OutboxEventRepository
import com.roy.coupling.common.support.JsonSupport
import com.roy.coupling.messaging.MessageHeaders
import com.roy.coupling.messaging.MessageProducer
import com.roy.coupling.messaging.OutboxEventMessage
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class R2dbcMessageProducer : MessageProducer {

    @Autowired
    private lateinit var outboxEventRepository: OutboxEventRepository

    override suspend fun send(channel: String, message: OutboxEventMessage) {
        val outboxEvent = OutboxEvent(
            aggregateType = channel,
            aggregateId = message.headers[MessageHeaders.AGGREGATE_ID]!!,
            headers = JsonSupport.serialize(message.headers),
            payload = JsonSupport.serialize(message.payload),
            creationTime = Instant.now()
        )

        outboxEventRepository.save(outboxEvent)
    }
}
