package com.roy.coupling.orchestration

sealed interface Saga<A> : SagaDsl {
    infix fun compensate(compensate: suspend () -> Unit): Saga<A> =
        when (this) {
            is SagaBuilder -> LocalStep({ f(this) }, compensate)
            is LocalStep -> LocalStep(action) {
                compensate()
                this.compensation()
            }
            is ParticipantStep -> LocalStep(action) {
                compensate()
                this.compensation()
            }
            is LocalAction -> LocalStep(action, compensate)
            is ParticipantAction -> LocalStep(action, compensate)
        }

    suspend fun transact(): A =
        when (this) {
            is LocalStep -> saga { bind() }.transact()
            is ParticipantStep -> saga { bind() }.transact()
            is LocalAction -> saga { action(this) }.transact()
            is ParticipantAction -> saga { action(this) }.transact()
            is SagaBuilder -> transaction()
        }
}

