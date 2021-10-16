package com.roy.coupling.test

import com.roy.coupling.common.config.KafkaConfiguration
import com.roy.coupling.common.config.R2dbcConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Import

@SpringBootApplication
@Import(value = [KafkaConfiguration::class, R2dbcConfiguration::class])
class CouplingTestApplication

fun main(args: Array<String>) {
    runApplication<CouplingTestApplication>(*args)
}
