package com.roy.coupling.orchestration

class LocalAction<A>(val action: suspend SagaStep.() -> A) : Saga<A> {
    override infix fun compensate(compensate: suspend () -> Unit): LocalStep<A> {
        return LocalStep(action, compensate)
    }
}