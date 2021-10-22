package com.roy.coupling.common.consumer

import com.roy.coupling.common.messaging.MessageDispatcher.Companion.invokeCommand
import com.roy.coupling.common.messaging.MessageDispatcher.Companion.invokeReply
import com.roy.coupling.common.messaging.MessageHeaders
import com.roy.coupling.common.messaging.Messages
import com.roy.coupling.common.messaging.commands.CommandHeaders
import com.roy.coupling.common.messaging.replies.ReplyHeaders
import com.roy.coupling.common.support.JsonSupport
import kotlinx.coroutines.runBlocking
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component

@Component
class KafkaMessageConsumer {
    @KafkaListener(
        id = LISTNER_ID,
        idIsGroup = false,
        autoStartup = "true",
        topics = ["#{topicFactory.split(',')}"],
        containerFactory = "kafkaListenerContainerFactory"
    )
    fun listen(
        acknowledgment: Acknowledgment,
        @Header("id") id: String,
        @Header("event_header") eventHeader: String,
        @Payload payload: String,
    ) {
        // TODO: command랑 reply 스레드를 따로 분리해도 되지 않을까?
        runBlocking {
            try {
                val headers = JsonSupport.deserialize<Map<String, String>>(eventHeader)
                when (headers[MessageHeaders.TYPE]) {
                    Messages.COMMAND.name -> invokeCommand(payload, headers[CommandHeaders.TYPE]!!)
                    Messages.REPLY.name -> invokeReply(
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
