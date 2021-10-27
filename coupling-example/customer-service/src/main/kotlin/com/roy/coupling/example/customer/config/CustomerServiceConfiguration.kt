package com.roy.coupling.example.customer.config

import com.roy.coupling.messaging.MessageDispatcher
import com.roy.coupling.participant.CouplingParticipantConfiguration
import com.roy.coupling.producer.R2dbcMessageProducer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories

@Configuration
@EnableR2dbcRepositories(basePackages = ["com.roy.coupling.example.customer.domain"])
@Import(value = [CouplingParticipantConfiguration::class])
class CustomerServiceConfiguration {
    @Bean
    fun messageDispatcher(
        messageProducer: R2dbcMessageProducer
    ): MessageDispatcher {
        return MessageDispatcher(messageProducer)
    }
}
