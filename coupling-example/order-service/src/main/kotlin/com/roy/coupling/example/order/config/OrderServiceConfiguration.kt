package com.roy.coupling.example.order.config

import com.roy.coupling.common.config.R2dbcConfiguration
import com.roy.coupling.common.producer.MessageProducer
import com.roy.coupling.common.producer.R2dbcMessageProducer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@Import(value = [R2dbcConfiguration::class])
class OrderServiceConfiguration {
    @Bean
    fun messageProducer(): MessageProducer {
        return R2dbcMessageProducer()
    }
}