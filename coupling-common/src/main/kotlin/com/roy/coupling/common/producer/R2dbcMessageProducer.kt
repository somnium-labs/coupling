package com.roy.coupling.common.producer

import com.roy.coupling.common.commands.Command
import com.roy.coupling.common.domain.OutboxEvent
import com.roy.coupling.common.domain.OutboxEventRepository
import com.roy.coupling.common.support.JsonSupport
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.annotation.Id
import java.time.Instant
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaField
import kotlin.reflect.jvm.jvmName

class R2dbcMessageProducer : MessageProducer {
    @Autowired
    private lateinit var outboxEventRepository: OutboxEventRepository

    override suspend fun <C : Command> send(channel: String, command: C) {

        // @Id가 붙은 property를 aggregateId로 사용한다.
        // TODO: @Id 강제 Required identifier property not found for class com.roy.coupling.common.domain.OutboxEvent!
        val aggregateId =
            command.javaClass.kotlin.memberProperties.first { it.javaField!!.getAnnotation(Id::class.java) != null }
                .get(command)

        val outboxEvent = OutboxEvent(
            aggregateType = channel,
            aggregateId = aggregateId.toString(),
            commandType = command::class.jvmName,
            payload = JsonSupport.serialize(command),
            creationTime = Instant.now()
        )

        outboxEventRepository.save(outboxEvent)
        CoroutineScope(Dispatchers.IO).launch { outboxEventRepository.delete(outboxEvent) }
    }
}
