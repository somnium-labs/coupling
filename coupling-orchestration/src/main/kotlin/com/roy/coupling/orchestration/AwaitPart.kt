package com.roy.coupling.orchestration

class AwaitPart<SagaData> : SagaStep<SagaData> {
    infix fun compensate(compensate: suspend (SagaData) -> Any): AwaitFull<SagaData> {
        return AwaitFull(compensate)
    }
}
