package com.roy.coupling.orchestration.config

import com.roy.coupling.consumer.KafkaMessageConsumer
import com.roy.coupling.consumer.config.KafkaConfiguration
import com.roy.coupling.producer.R2dbcMessageProducer
import com.roy.coupling.producer.config.R2dbcConfiguration
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@Import(
    value = [
        KafkaConfiguration::class,
        R2dbcConfiguration::class,
        KafkaMessageConsumer::class,
        R2dbcMessageProducer::class,
    ]
)
class CouplingOrchestrationConfiguration
