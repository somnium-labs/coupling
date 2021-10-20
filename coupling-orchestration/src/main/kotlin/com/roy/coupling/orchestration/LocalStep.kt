package com.roy.coupling.orchestration

@JvmInline
value class LocalStep<F>(val action: suspend SagaEffect.() -> F) : Saga<F> {
    fun compensate(compensate: suspend (F) -> Unit): Saga<F> {
        return SagaStep(action, compensate)
    }
}