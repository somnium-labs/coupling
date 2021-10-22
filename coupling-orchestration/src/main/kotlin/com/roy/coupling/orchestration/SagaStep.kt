package com.roy.coupling.orchestration

interface SagaStep {
    suspend fun <A> Saga<A>.bind(): A

    fun <A> local(action: suspend SagaStep.() -> A): LocalAction<A> {
        return LocalAction(action)
    }

    fun <A> remote(action: suspend SagaStep.() -> A): ParticipantAction<A> {
        return ParticipantAction(action)
    }
}