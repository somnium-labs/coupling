package com.roy.coupling.orchestration

import com.roy.coupling.common.logging.Logger.Companion.log
import com.roy.coupling.messaging.ReplyFailure
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class StepTest {
    enum class StepStatus { SUCCESS, COMPENSATION, FAILURE, NONE }

    data class SagaData(
        var status: StepStatus = StepStatus.SUCCESS,
        var stepCount: Int = 0,
        var compensationCount: Int = 0
    )

    @Test
    fun compensationTest() = runBlocking {
        val sagaDefinition = SagaBuilder<SagaData> {
            // step 1
            // step2가 실패해서 compensate 실행
            local(::success).compensate(::compensate).bind()

            // step 2
            participant(::failure).bind()

            // step 3
            // step2가 실패할거라서 실행되지 않음
            local(::notExecute).bind()
        }

        val data = SagaData(StepStatus.NONE)
        sagaDefinition.transact(data)

        assertEquals(StepStatus.COMPENSATION, data.status) // 보상작업 실행 기대
        assertEquals(1, data.stepCount) // 스텝1까지 성공
    }

    @Test
    fun totalCompensationTest() = runBlocking {
        val sagaDefinition = SagaBuilder<SagaData> {
            // step 1
            // step4가 실패해서 보상 실행
            local(::success).compensate(::compensate).bind()

            // step 2
            // step4가 실패해서 보상 실행
            participant(::success).compensate(::compensate).bind()

            // step 3
            // 보상 작업 없음
            participant(::success).bind()

            // step 4
            local(::failure).bind()
        }

        val data = SagaData(StepStatus.NONE)
        sagaDefinition.transact(data)

        assertEquals(StepStatus.COMPENSATION, data.status)
        assertEquals(2, data.compensationCount) // 보상작업 실행 2회
        assertEquals(3, data.stepCount) //  스텝3까지 성공
    }

    @Test
    fun successCase() = runBlocking {
        val sagaDefinition = SagaBuilder<SagaData> {
            local(::success).compensate(::compensate).bind()
            participant(::success).compensate(::compensate).bind()
            participant(::success).compensate(::compensate).bind()
        }

        val data = SagaData(StepStatus.NONE)
        sagaDefinition.transact(data)

        assertEquals(StepStatus.SUCCESS, data.status)
        assertEquals(0, data.compensationCount) // 보상작업 실행 0회
        assertEquals(3, data.stepCount) //  스텝3까지 성공
    }

    private fun compensate(data: SagaData) {
        log.info { "compensate" }
        data.status = StepStatus.COMPENSATION
        data.compensationCount++
    }

    private fun execute(value: String, data: SagaData) {
        log.info { "$value(): $data" }
    }

    private fun notExecute(data: SagaData) {
        log.info { "notExecute: $data" }
    }

    private fun success(data: SagaData) {
        data.stepCount++
        data.status = StepStatus.SUCCESS
        execute("foo", data)
    }

    private fun failure(data: SagaData) {
        execute("bar", data)
        data.status = StepStatus.FAILURE
        throw ReplyFailure()
    }
}
