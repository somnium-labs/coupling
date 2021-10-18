package com.roy.coupling.test.commands

import com.fasterxml.jackson.annotation.JsonProperty
import com.roy.coupling.common.commands.Command

data class CreateOrder(
    @JsonProperty("orderId") val orderId: Int
) : Command