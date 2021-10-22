package com.roy.coupling.common.messaging.replies

import com.roy.coupling.common.messaging.commands.CommandReplyOutcome
import com.roy.coupling.common.support.JsonSupport
import kotlin.reflect.KClass
import kotlin.reflect.jvm.jvmName

class ReplyHandler<C : Reply>(
    private val replyClass: KClass<C>,
    private val handler: suspend (CommandReplyOutcome, C) -> Unit,
) {
    val className: String
        get() = replyClass.jvmName

    suspend fun invoke(outcome: CommandReplyOutcome, payload: String) {
        val reply = JsonSupport.deserialize(payload, replyClass)
        handler.invoke(outcome, reply)
    }
}