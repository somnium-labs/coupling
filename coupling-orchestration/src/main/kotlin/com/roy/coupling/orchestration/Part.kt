package com.roy.coupling.orchestration

class Part<SagaData>(val action: suspend (SagaData) -> Any) : SagaStep<SagaData> {
    infix fun compensate(compensate: suspend (SagaData) -> Any): Full<SagaData> {
        return Full(action, compensate)
    }
}
