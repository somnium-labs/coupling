package com.roy.coupling.orchestration

interface SagaDsl {
    fun <A> saga(block: suspend SagaStep.() -> A): Saga<A> = SagaBuilder(block)
}