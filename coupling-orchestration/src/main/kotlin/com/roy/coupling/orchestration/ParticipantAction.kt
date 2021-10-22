package com.roy.coupling.orchestration

class ParticipantAction<A>(val action: suspend SagaStep.() -> A) : Saga<A> {
    override infix fun compensate(compensate: suspend () -> Unit): ParticipantStep<A> {
        return ParticipantStep(action, compensate)
    }
}