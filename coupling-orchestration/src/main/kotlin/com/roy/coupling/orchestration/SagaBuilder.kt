package com.roy.coupling.orchestration

import com.roy.coupling.core.runReleaseAndRethrow

@JvmInline
value class SagaBuilder<F>(val block: suspend SagaEffect.() -> F) : Saga<F> {
    suspend fun build(): F {
        val sagaDefinition = SagaDefinition()
        try {
            return block(sagaDefinition)
        } catch (t: Throwable) {
            runReleaseAndRethrow(t) { sagaDefinition.totalCompensation() }
        }
    }
}