package com.roy.coupling.common.consumer

import com.roy.coupling.common.commands.Command
import kotlinx.coroutines.runBlocking
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component
import kotlin.reflect.KClass
import kotlin.reflect.jvm.jvmName

@Component
class KafkaMessageConsumer {
    val commandHandlers: MutableList<CommandHandler<*>> = mutableListOf()

    fun <C : Command> commandHandler(command: KClass<C>, handler: suspend (C) -> Unit) {
        commandHandlers.add(CommandHandler(command, handler))
    }

    @KafkaListener(
        id = LISTNER_ID,
        idIsGroup = false,
        autoStartup = "true",
        topics = ["#{topicFactory.split(',')}"],
        containerFactory = "kafkaListenerContainerFactory"
    )
    fun listen(
        acknowledgment: Acknowledgment,
//        consumerRecord: ConsumerRecord<String, String>,
        @Header("id") id: String,
//        @Header("aggregate_id") aggregateId: String,
        @Header("command_type") commandType: String,
        @Payload payload: String,
    ) {
        runBlocking {
            try {
                commandHandlers.find {
                    it.commandClass.jvmName == commandType
                }?.invoke(
                    payload.substring(1, payload.length - 1).replace("\\", "")
                )  // TODO: debezium producer serializer?
                acknowledgment.acknowledge() // manual commit
            } catch (e: Exception) {
                // TODO: exception handling
            }
        }
    }

    companion object {
        const val LISTNER_ID = "outbox-event-listener"
    }
}
