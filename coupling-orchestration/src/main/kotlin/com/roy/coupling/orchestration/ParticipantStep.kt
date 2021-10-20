package com.roy.coupling.orchestration

import kotlin.reflect.KClass
import kotlin.reflect.KSuspendFunction1

class ParticipantStep<F>(val action: suspend SagaEffect.() -> F) : Saga<F> {
    private val replayHandlers: MutableMap<String, KSuspendFunction1<*, Unit>> = mutableMapOf()

    fun <C : Any> onReply(replyClass: KClass<C>, replyHandler: suspend (C) -> Unit): ParticipantStep<F> {
//        replayHandlers[replyClass.qualifiedName!!] = replyHandler as KSuspendFunction1<C, Unit>
        return this
    }
}