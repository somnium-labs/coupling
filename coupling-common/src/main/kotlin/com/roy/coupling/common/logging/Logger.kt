package com.roy.coupling.common.logging

import mu.KLogger
import mu.KotlinLogging

class Logger {
    companion object {
        val <reified T> T.log: KLogger
            inline get() = KotlinLogging.logger { T::class.java.name }
    }
}
