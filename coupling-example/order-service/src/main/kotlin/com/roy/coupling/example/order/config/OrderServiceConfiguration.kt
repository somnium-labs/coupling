package com.roy.coupling.example.order.config

import com.roy.coupling.messaging.MessageDispatcher
import com.roy.coupling.orchestration.config.CouplingOrchestrationConfiguration
import com.roy.coupling.producer.R2dbcMessageProducer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@Import(value = [CouplingOrchestrationConfiguration::class])
class OrderServiceConfiguration {
    @Bean
    fun messageDispatcher(
        messageProducer: R2dbcMessageProducer
    ): MessageDispatcher {
        return MessageDispatcher(messageProducer)
    }
}
