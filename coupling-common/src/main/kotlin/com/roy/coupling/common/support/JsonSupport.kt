package com.roy.coupling.common.support

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.PropertyAccessor
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import kotlin.reflect.KClass

class JsonSupport {
    companion object {
        val objectMapper: ObjectMapper = ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .enable(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN)
            .registerModules(KotlinModule() /*JavaTimeModule(), Jdk8Module()*/)
            .setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE)
            .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)

        fun <T> serialize(o: T): String {
            return objectMapper.writeValueAsString(o)
        }

        inline fun <reified T> deserialize(json: String): T {
            return objectMapper.readValue(json)
        }

        fun <T : Any> deserialize(json: String, targetType: KClass<T>): T {
            return objectMapper.readValue(json, targetType.java)
        }
    }
}
