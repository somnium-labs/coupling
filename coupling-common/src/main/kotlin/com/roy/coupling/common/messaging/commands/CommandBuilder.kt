package com.roy.coupling.common.messaging.commands

import com.roy.coupling.common.messaging.MessageHeaders
import com.roy.coupling.common.messaging.Messages
import com.roy.coupling.common.messaging.OutboxEventMessage
import org.springframework.data.annotation.Id
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaField
import kotlin.reflect.jvm.jvmName

class CommandBuilder {
    companion object {
        fun <T : Command> build(command: T): OutboxEventMessage<T> {
            // @Id가 붙은 property를 aggregateId로 사용한다.
            val aggregateId =
                command.javaClass.kotlin.memberProperties.first { it.javaField!!.getAnnotation(Id::class.java) != null }
                    .get(command)

            val headers = mapOf(
                MessageHeaders.TYPE to Messages.COMMAND.name,
                MessageHeaders.AGGREGATE_ID to aggregateId.toString(),
                CommandHeaders.TYPE to command::class.jvmName,
            )
            return OutboxEventMessage(headers, command)
        }
    }
}