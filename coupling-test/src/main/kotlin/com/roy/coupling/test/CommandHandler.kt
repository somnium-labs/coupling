package com.roy.coupling.test

import com.roy.coupling.common.consumer.KafkaMessageConsumer
import com.roy.coupling.common.logging.Logger.Companion.log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import org.springframework.boot.context.event.ApplicationStartedEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service

@Service
class CommandHandler(
    private val KafkaMessageConsumer: KafkaMessageConsumer
) {
    private val applicationStartedEventChannel = Channel<ApplicationStartedEvent>(capacity = 1)

    init {
        CoroutineScope(Dispatchers.Default).launch { run() }
    }

    private suspend fun run() {
        KafkaMessageConsumer.start { commandType, payload ->
            log.info("[$commandType] => $payload")
        }
    }

    @EventListener
    fun onAppStarted(event: ApplicationStartedEvent) {
        applicationStartedEventChannel.trySend(event)
    }
}
