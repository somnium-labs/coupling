package com.roy.coupling.consumer

import com.roy.coupling.common.support.JsonSupport
import com.roy.coupling.messaging.MessageDispatcher
import com.roy.coupling.messaging.MessageHeaders
import com.roy.coupling.messaging.Messages
import com.roy.coupling.messaging.commands.CommandHeaders
import com.roy.coupling.messaging.replies.ReplyHeaders
import kotlinx.coroutines.runBlocking
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component

@Component
class KafkaMessageConsumer(
    private val messageDispatcher: MessageDispatcher,
) {
    @KafkaListener(
        id = LISTNER_ID,
        idIsGroup = false,
        autoStartup = "true",
        topics = ["#{topicFactory.split(',')}"],
        containerFactory = "kafkaListenerContainerFactory"
    )
    fun listen(
        acknowledgment: Acknowledgment,
        record: ConsumerRecord<String, String>,
        @Header("id") id: String,
        @Header("event_header") eventHeader: String,
        @Payload payload: String,
    ) {
        // TODO: command랑 reply 스레드를 따로 분리해도 되지 않을까?
        runBlocking {
            try {
                val headers = JsonSupport.deserialize<Map<String, String>>(eventHeader)
                when (headers[MessageHeaders.TYPE]) {
                    Messages.COMMAND.name -> messageDispatcher.invokeCommand(
                        record.topic(),
                        payload,
                        headers[CommandHeaders.TYPE]!!,
                        headers[MessageHeaders.AGGREGATE_ID]!!
                    )
                    Messages.REPLY.name -> messageDispatcher.invokeReply(
                        headers[ReplyHeaders.OUTCOME]!!, payload, headers[ReplyHeaders.TYPE]!!
                    )
                }
                acknowledgment.acknowledge() // manual commit
            } catch (e: Exception) {
                // TODO: exception handling
                println("consumer exception")
            }
        }
    }

    companion object {
        const val LISTNER_ID = "outbox-event-listener"
    }
}
