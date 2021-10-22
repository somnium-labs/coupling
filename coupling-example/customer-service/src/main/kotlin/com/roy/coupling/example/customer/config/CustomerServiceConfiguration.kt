package com.roy.coupling.example.customer.config

import com.roy.coupling.common.config.KafkaConfiguration
import com.roy.coupling.common.config.R2dbcConfiguration
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories

@Configuration
@EnableR2dbcRepositories(basePackages = ["com.roy.coupling.example.customer.domain"])
@Import(
    value = [
        KafkaConfiguration::class,
        R2dbcConfiguration::class,
    ]
)
class CustomerServiceConfiguration