package com.roy.coupling.example.order

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories

@SpringBootApplication
@EnableR2dbcRepositories // org.jetbrains.kotlinx:kotlinx-coroutines-reactor 없으면 repository 스캔 못함
class OrderServiceMain

fun main(args: Array<String>) {
    runApplication<OrderServiceMain>(*args)
}