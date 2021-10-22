package com.roy.coupling.orchestration

import arrow.continuations.generic.AtomicRef
import arrow.core.nonFatalOrThrow
import arrow.core.prependTo
import arrow.fx.coroutines.ExitCase
import arrow.fx.coroutines.Platform
import arrow.fx.coroutines.guaranteeCase
import com.roy.coupling.common.logging.Logger.Companion.log
import com.roy.coupling.common.messaging.ReplyFailure
import com.roy.coupling.common.messaging.commands.CommandReplyOutcome

class SagaDefinitionBuilder(
    private val stack: AtomicRef<List<suspend () -> Unit>> = AtomicRef(emptyList())
) : SagaStep {

    override suspend fun <A> Saga<A>.bind(): A =
        when (this) {
            is LocalStep -> bind()
            is LocalAction -> action()
            is ParticipantAction -> action()
            is SagaBuilder -> bind()
            is ParticipantStep -> bind()
        }

    private suspend fun <A> LocalStep<A>.bind(): A {
        return guaranteeCase({ action() }) { exitCase ->
            when (exitCase) {
                is ExitCase.Completed -> stack.updateAndGet {
                    suspend { compensation() } prependTo it
                }
                is ExitCase.Cancelled -> Unit
                is ExitCase.Failure -> Unit
            }
        }
    }

    private suspend fun <A> ParticipantStep<A>.bind(): A {
        return guaranteeCase({
            action().also {
                when (channel.receive()) {
                    CommandReplyOutcome.SUCCESS -> log.trace { "success participant step" }
                    CommandReplyOutcome.FAILURE -> {
                        compensation() // 현재 보상 트랜잭션 실행
                        throw ReplyFailure() // 스텝 진행 중단
                    }
                }
            }
        }) { exitCase ->
            when (exitCase) {
                is ExitCase.Completed -> stack.updateAndGet {
                    suspend { compensation() } prependTo it
                }
                is ExitCase.Cancelled -> Unit
                is ExitCase.Failure -> Unit
            }
        }
    }

    private suspend fun <A> SagaBuilder<A>.bind() =
        f(this@SagaDefinitionBuilder)


    suspend fun totalCompensation() {
        val errors = stack.get().mapNotNull { finalizer ->
            try {
                finalizer()
                null
            } catch (e: Throwable) {
                e.nonFatalOrThrow()
            }
        }
        Platform.composeErrors(all = errors)?.let { throw it }
    }
}