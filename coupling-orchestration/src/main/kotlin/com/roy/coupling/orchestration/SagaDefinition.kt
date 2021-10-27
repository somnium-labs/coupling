package com.roy.coupling.orchestration

interface SagaDefinition<SagaData> {
    suspend fun SagaStep<SagaData>.bind()

    fun <SagaData : Any> local(action: suspend (SagaData) -> Any): Part<SagaData> {
        return Part(action)
    }

    fun <SagaData> participant(
        action: suspend (SagaData) -> Any
    ): Part<SagaData> {
        return Part(action)
    }

    fun await(): AwaitPart<SagaData> {
        return AwaitPart()
    }
}
