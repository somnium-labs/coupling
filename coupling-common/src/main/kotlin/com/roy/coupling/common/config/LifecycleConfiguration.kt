package com.roy.coupling.common.config

import kotlinx.coroutines.channels.Channel
import org.springframework.boot.context.event.ApplicationStartedEvent
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.EventListener

@Configuration
class LifecycleConfiguration {
    private val applicationStartedEvent = Channel<ApplicationStartedEvent>(capacity = 1)

    @EventListener
    fun onAppStarted(event: ApplicationStartedEvent) {
        applicationStartedEvent.trySend(event)
    }

    @Bean
    fun applicationStartedEvent(): Channel<ApplicationStartedEvent> {
        return applicationStartedEvent
    }
}
