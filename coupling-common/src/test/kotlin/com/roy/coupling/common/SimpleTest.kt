package com.roy.coupling.common

import com.fasterxml.jackson.annotation.JsonProperty
import com.roy.coupling.common.messaging.commands.Command
import com.roy.coupling.common.support.JsonSupport
import org.junit.jupiter.api.Test
import java.util.*

data class CreateOrder(
    @JsonProperty("orderId") val orderId: Int
) : Command

class SimpleTest {
    @Test
    fun uuid() {
        println(UUID.randomUUID())
    }

    @Test
    fun de() {
        val data = JsonSupport.deserialize("""{"orderId":1}""", CreateOrder::class)
        println(data)
    }
}
