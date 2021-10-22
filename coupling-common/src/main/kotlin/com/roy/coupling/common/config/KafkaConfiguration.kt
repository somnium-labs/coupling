package com.roy.coupling.common.config

import com.roy.coupling.common.consumer.KafkaMessageConsumer
import com.roy.coupling.common.logging.Logger.Companion.log
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.*
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.listener.ContainerProperties

@EnableKafka
@Configuration
@Import(KafkaMessageConsumer::class)
class KafkaConfiguration(private val kafkaProperties: KafkaProperties) {
    @Value("\${spring.kafka.topics}")
    private val topics = listOf<String>()

    @Bean
    fun topicFactory(): String {
        log.info { "topic factory: $topics" }
        return topics.joinToString(separator = ",")
    }

    @Bean
    fun consumerFactory(): MutableMap<String, Any> {
        val props: MutableMap<String, Any> = HashMap()
        props[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = kafkaProperties.consumer.bootstrapServers
        props[ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG] = false
        props[ConsumerConfig.GROUP_ID_CONFIG] = kafkaProperties.consumer.groupId
        props[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        props[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        props[ConsumerConfig.MAX_POLL_RECORDS_CONFIG] = 100
        props[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "latest"
        return props
    }

    @Bean
    fun producerFactory(): MutableMap<String, Any> {
        val props: MutableMap<String, Any> = HashMap()
        props[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = kafkaProperties.producer.bootstrapServers
        props[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        props[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        props[ProducerConfig.ACKS_CONFIG] = "all"
        props[ProducerConfig.RETRIES_CONFIG] = Int.MAX_VALUE
        props[ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG] = 86400100
        props[ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG] = 86400000
        props[ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION] = 1
        props[ProducerConfig.BATCH_SIZE_CONFIG] = 100 * 1024
        props[ProducerConfig.LINGER_MS_CONFIG] = 100
        props[ProducerConfig.BUFFER_MEMORY_CONFIG] = 100 * 1024 * 1024
        props[ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG] = true
        return props
    }

    @Bean(name = [KafkaListenerConfigUtils.KAFKA_LISTENER_ENDPOINT_REGISTRY_BEAN_NAME])
    fun defaultKafkaListenerEndpointRegistry(): KafkaListenerEndpointRegistry {
        return object : KafkaListenerEndpointRegistry() {
            override fun registerListenerContainer(
                endpoint: KafkaListenerEndpoint,
                factory: KafkaListenerContainerFactory<*>
            ) {
                super.registerListenerContainer(endpoint, factory)
            }
        }
    }

    @Bean
    fun kafkaTemplate(): KafkaTemplate<String, String> {
        return KafkaTemplate(DefaultKafkaProducerFactory(producerFactory()))
    }

    @Bean
    fun kafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, String> {
        val factory = ConcurrentKafkaListenerContainerFactory<String, String>()
        factory.consumerFactory = DefaultKafkaConsumerFactory(consumerFactory())
        factory.containerProperties.ackMode = ContainerProperties.AckMode.MANUAL
        return factory
    }
}
