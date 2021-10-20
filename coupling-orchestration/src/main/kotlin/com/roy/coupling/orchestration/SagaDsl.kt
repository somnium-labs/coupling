package com.roy.coupling.orchestration

interface SagaDsl {
    fun <F> saga(block: suspend SagaEffect.() -> F): SagaBuilder<F> {
        return SagaBuilder(block)
    }
}
