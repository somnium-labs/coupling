package com.roy.coupling.common

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

/**
 * @param id
 * @param aggregateType topic name: outbox.event.$aggregateType
 * @param aggregateId
 * @param commandType
 * @param payload
 */
@Table("outbox_event")
data class OutboxEvent(
    @Id val id: String,
    val aggregateType: String,
    val aggregateId: String,
    val commandType: String,
    val payload: String,
    val creationTime: Instant,
)
