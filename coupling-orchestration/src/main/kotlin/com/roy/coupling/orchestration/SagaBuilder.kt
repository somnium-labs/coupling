package com.roy.coupling.orchestration

import arrow.fx.coroutines.ExitCase
import arrow.fx.coroutines.guaranteeCase

class SagaBuilder<A>(val f: suspend SagaStep.() -> A) : Saga<A> {
    suspend fun transaction(): A {
        val sagaDefinitionBuilder = SagaDefinitionBuilder()
        return guaranteeCase({ f(sagaDefinitionBuilder) }) { exitCase ->
            when (exitCase) {
                is ExitCase.Completed -> Unit
                else -> sagaDefinitionBuilder.totalCompensation()
            }
        }
    }
}