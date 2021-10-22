package com.roy.coupling.example.order.config

import com.roy.coupling.common.config.KafkaConfiguration
import com.roy.coupling.common.config.R2dbcConfiguration
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@Import(value = [R2dbcConfiguration::class, KafkaConfiguration::class])
class OrderServiceConfiguration {
//    @Bean
//    fun messageProducer(): MessageProducer {
//        return R2dbcMessageProducer()
//    }
}