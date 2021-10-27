package com.roy.coupling.orchestration

class AwaitFull<SagaData>(
    val compensation: suspend (SagaData) -> Any
) : SagaStep<SagaData>
