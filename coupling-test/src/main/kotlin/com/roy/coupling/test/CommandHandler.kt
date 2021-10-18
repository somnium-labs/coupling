package com.roy.coupling.test

import com.roy.coupling.common.consumer.KafkaMessageConsumer
import com.roy.coupling.common.logging.Logger.Companion.log
import com.roy.coupling.test.commands.CreateOrder
import org.springframework.stereotype.Service


@Service
class CommandHandler(
    KafkaMessageConsumer: KafkaMessageConsumer
) {
    init {
        KafkaMessageConsumer.commandHandler(CreateOrder::class, ::onReceive)
        KafkaMessageConsumer.commandHandler(CreateOrder::class, ::onReceive2) // ignore
    }

    suspend fun onReceive(data: CreateOrder) {
        log.info("received: $data")
    }

    fun onReceive2(data: CreateOrder) {
        log.info("received: $data")
    }
}
