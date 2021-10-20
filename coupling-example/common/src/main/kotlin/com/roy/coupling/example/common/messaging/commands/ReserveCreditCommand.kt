package com.roy.coupling.example.common.messaging.commands

import com.roy.coupling.common.commands.Command
import com.roy.coupling.example.common.domain.Money
import org.springframework.data.annotation.Id

data class ReserveCreditCommand(
    @field:Id val customerId: Long, // TODO: https://stackoverflow.com/questions/52345291/bean-validation-not-working-with-kotlin-jsr-380
    val orderId: Long,
    val orderTotal: Money
) : Command