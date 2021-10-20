package com.roy.coupling.core

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext

fun nonFatal(t: Throwable): Boolean {
    return when (t) {
        is VirtualMachineError,
        is ThreadDeath,
        is InterruptedException,
        is LinkageError,
        is CancellationException -> false
        else -> true
    }
}

fun Throwable.nonFatalOrThrow(): Throwable =
    if (nonFatal(this)) this else throw this

suspend inline fun runReleaseAndRethrow(original: Throwable, crossinline f: suspend () -> Unit): Nothing {
    try {
        withContext(NonCancellable) { f() }
    } catch (e: Throwable) {
        original.addSuppressed(e.nonFatalOrThrow())
    }
    throw original
}

fun composeErrors(all: List<Throwable>): Throwable? =
    all.firstOrNull()?.let { first ->
        composeErrors(first, all.drop(1))
    }

fun composeErrors(first: Throwable, rest: List<Throwable>): Throwable {
    rest.forEach { if (it != first) first.addSuppressed(it) }
    return first
}
