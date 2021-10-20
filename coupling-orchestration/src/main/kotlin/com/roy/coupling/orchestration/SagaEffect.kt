package com.roy.coupling.orchestration

interface SagaEffect {
    suspend fun <F> Saga<F>.bind(): F

    suspend fun <F> invokeLocal(action: suspend SagaEffect.() -> F): LocalStep<F> {
        return LocalStep(action)
    }

    fun <F> invokeParticipant(action: suspend SagaEffect.() -> F): ParticipantStep<F> {
        return ParticipantStep(action)
    }
}