package com.roy.coupling.orchestration

import com.roy.coupling.messaging.MessageDispatcher
import com.roy.coupling.messaging.ReplyFailure
import com.roy.coupling.messaging.commands.CommandReplyOutcome
import com.roy.coupling.messaging.replies.Reply
import kotlinx.coroutines.channels.Channel
import kotlin.reflect.KClass

class SagaBuilder<SagaData : Any>(
    val f: suspend SagaDefinition<SagaData>.() -> Unit
) : SagaStep<SagaData> {
    private val channel = Channel<CommandReplyOutcome>(Channel.CONFLATED)
    private lateinit var data: SagaData

    suspend fun transact(data: SagaData) {
        this.data = data
        val saga = Saga(channel, data)
        try {
            f(saga)
        } catch (e: ReplyFailure) {
            saga.totalCompensation()
        } catch (e: Throwable) {
            saga.totalCompensation()
            throw e
        }
    }

    fun <T : Reply> onFailure(
        replyClass: KClass<T>,
        handler: suspend (T, SagaData) -> Unit
    ): SagaBuilder<SagaData> {
        MessageDispatcher.onReply(replyClass) { outcome, reply ->
            handler.invoke(reply, data)
            channel.send(outcome)
        }
        return this
    }

    fun <T : Reply> onSuccess(
        replyClass: KClass<T>,
        handler: (suspend (SagaData) -> Unit)? = null
    ): SagaBuilder<SagaData> {
        MessageDispatcher.onReply(replyClass) { outcome, _ ->
            handler?.invoke(data)
            channel.send(outcome)
        }
        return this
    }

    fun <T : Reply> onSuccess(
        replyClass: KClass<T>,
        handler: suspend (T, SagaData) -> Unit
    ): SagaBuilder<SagaData> {
        MessageDispatcher.onReply(replyClass) { outcome, reply ->
            handler.invoke(reply, data)
            channel.send(outcome)
        }
        return this
    }
}
