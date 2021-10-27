package com.roy.coupling.orchestration

class Full<SagaData>(
    val action: suspend (SagaData) -> Any,
    val compensation: suspend (SagaData) -> Any
) : SagaStep<SagaData>
