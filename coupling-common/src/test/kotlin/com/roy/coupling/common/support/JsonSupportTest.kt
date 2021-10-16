package com.roy.coupling.common.support

import junit.framework.Assert.assertEquals
import org.junit.jupiter.api.Test

class JsonSupportTest {
    @Test
    fun serialize() {
        val map = mapOf("1" to "one", "2" to "two")
        val serialized = JsonSupport.serialize(map)
        assertEquals(serialized, """{"1":"one","2":"two"}""")
    }
}
