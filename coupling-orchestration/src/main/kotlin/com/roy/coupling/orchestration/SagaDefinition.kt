package com.roy.coupling.orchestration

import com.roy.coupling.core.composeErrors
import com.roy.coupling.core.nonFatalOrThrow
import com.roy.coupling.core.runReleaseAndRethrow
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
import java.util.concurrent.atomic.AtomicReference

class SagaDefinition(
    private val stack: AtomicReference<List<suspend () -> Unit>> = AtomicReference(emptyList())
) : SagaEffect {

    override suspend fun <A> Saga<A>.bind(): A =
        when (this) {
            is SagaStep -> bind()
            is LocalStep -> action()
            is ParticipantStep -> action()
            is SagaBuilder -> bind()
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
        composeErrors(all = errors)?.let { throw it }
    }

    private suspend fun <F> ParticipantStep<F>.bind() {

    }

    private suspend fun <F> SagaStep<F>.bind(): F {
        try {
            val value = action()
            withContext(NonCancellable) {
                stack.updateAndGet {
                    listOf(suspend { compensation(value) }) + it
                }
            }
            return value
        } catch (e: CancellationException) {
            runReleaseAndRethrow(e) { return@runReleaseAndRethrow }
        } catch (t: Throwable) {
            runReleaseAndRethrow(t.nonFatalOrThrow()) { return@runReleaseAndRethrow }
        }
    }

    private suspend fun <A> SagaBuilder<A>.bind(): A {
        return block(this@SagaDefinition)
    }
}