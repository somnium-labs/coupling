package com.roy.coupling.orchestration

class SagaStep<F>(
    val action: suspend SagaEffect.() -> F,
    val compensation: suspend (F) -> Unit
) : Saga<F>