package com.roy.coupling.orchestration

import com.roy.coupling.common.messaging.MessageDispatcher
import com.roy.coupling.common.messaging.commands.CommandReplyOutcome
import com.roy.coupling.common.messaging.replies.Reply
import kotlinx.coroutines.channels.Channel
import kotlin.reflect.KClass

class ParticipantStep<A>(
    val action: suspend SagaStep.() -> A,
    val compensation: suspend () -> Unit
) : Saga<A> {
    var channel = Channel<CommandReplyOutcome>()

    fun <C : Reply> onReply(replyClass: KClass<C>, handler: suspend (C) -> Unit): ParticipantStep<A> {
        MessageDispatcher.onReply(replyClass) { outcome, reply ->
            handler.invoke(reply)
            channel.send(outcome)
        }
        return this
    }
}