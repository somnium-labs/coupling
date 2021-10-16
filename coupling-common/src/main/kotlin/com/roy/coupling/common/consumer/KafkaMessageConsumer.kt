package com.roy.coupling.common.consumer

import com.roy.coupling.common.config.LifecycleConfiguration
import com.roy.coupling.common.logging.Logger.Companion.log
import kotlinx.coroutines.channels.Channel
import org.springframework.boot.context.event.ApplicationStartedEvent
import org.springframework.context.annotation.Import
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.config.KafkaListenerEndpointRegistry
import org.springframework.kafka.support.Acknowledgment
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component

@Component
@Import(LifecycleConfiguration::class)
class KafkaMessageConsumer(
    private val kafkaListenerEndpointRegistry: KafkaListenerEndpointRegistry,
    private val applicationStartedEvent: Channel<ApplicationStartedEvent>
) {
    private lateinit var messageHandler: (String, String) -> Unit

    suspend fun start(messageHandler: (String, String) -> Unit) {
        // listner 등록(registerListenerContainer)을 기다려야 한다.
        applicationStartedEvent.receive()

        this.messageHandler = messageHandler
        val container = kafkaListenerEndpointRegistry.getListenerContainer(LISTNER_ID)
        container?.start() ?: throw RuntimeException("")
    }

    fun stop() {
        kafkaListenerEndpointRegistry.getListenerContainer(LISTNER_ID)?.stop()
    }

    @KafkaListener(
        id = LISTNER_ID,
        idIsGroup = false,
        autoStartup = "false",
        topics = ["#{topicFactory.split(',')}"],
        containerFactory = "kafkaListenerContainerFactory"
    )
    fun listen(
        acknowledgment: Acknowledgment,
        @Header("id") id: String,
        @Header("aggregate_id") aggregateId: String,
        @Header("command_type") commandType: String,
        @Payload payload: String,
    ) {
        try {
            log.info("aggregateId: $aggregateId")
            messageHandler.invoke(commandType, payload)
            acknowledgment.acknowledge() // manual commit
        } catch (e: Exception) {
            stop()
        }
    }

    companion object {
        const val LISTNER_ID = "outbox-event-listener"
    }
}
