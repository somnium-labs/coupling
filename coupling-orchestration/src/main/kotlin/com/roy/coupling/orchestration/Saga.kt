package com.roy.coupling.orchestration

import arrow.continuations.generic.AtomicRef
import arrow.core.nonFatalOrThrow
import arrow.core.prependTo
import arrow.fx.coroutines.ExitCase
import arrow.fx.coroutines.Platform
import arrow.fx.coroutines.guaranteeCase
import com.roy.coupling.common.logging.Logger.Companion.log
import com.roy.coupling.messaging.ReplyFailure
import com.roy.coupling.messaging.commands.CommandReplyOutcome
import kotlinx.coroutines.channels.Channel

class Saga<SagaData : Any>(
    private val channel: Channel<CommandReplyOutcome>,
    val data: SagaData
) : SagaDefinition<SagaData> {
    private val stack: AtomicRef<List<suspend () -> Any>> = AtomicRef(emptyList())

    override suspend fun SagaStep<SagaData>.bind() {
        when (this) {
            is Full -> bind()
            is Part -> action(data)
            is SagaBuilder -> bind()
            is AwaitPart -> receive()
            is AwaitFull -> bind()
        }
    }

    private suspend fun receive() {
        when (channel.receive()) {
            CommandReplyOutcome.SUCCESS -> log.trace { "success participant step" }
            CommandReplyOutcome.FAILURE -> throw ReplyFailure()
        }
    }

    private suspend fun Full<SagaData>.bind() {
        return guaranteeCase({ action(data) }) { exitCase ->
            when (exitCase) {
                is ExitCase.Completed -> stack.updateAndGet {
                    suspend { compensation(data) } prependTo it
                }
                is ExitCase.Cancelled -> Unit
                is ExitCase.Failure -> Unit
            }
        }
    }

    private suspend fun AwaitFull<SagaData>.bind() {
        return guaranteeCase({ receive() }) { exitCase ->
            when (exitCase) {
                is ExitCase.Completed -> stack.updateAndGet { compensations ->
                    compensation.let { compensation ->
                        suspend { compensation(data) } prependTo compensations
                    }
                }
                is ExitCase.Cancelled -> Unit
                is ExitCase.Failure -> Unit
            }
        }
    }

    private suspend fun SagaBuilder<SagaData>.bind() {
        return f(this@Saga)
    }

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
