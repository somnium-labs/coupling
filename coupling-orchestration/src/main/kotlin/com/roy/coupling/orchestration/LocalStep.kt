package com.roy.coupling.orchestration

class LocalStep<A>(
    val action: suspend SagaStep.() -> A,
    val compensation: suspend () -> Unit
) : Saga<A>