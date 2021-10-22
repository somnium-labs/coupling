package com.roy.coupling.common.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

/**
 * @param id
 * @param aggregateType topic name: outbox.event.$aggregateType
 * @param aggregateId
 * @param payload
 */
@Table("outbox_event")
data class OutboxEvent(
    val aggregateType: String,
    val aggregateId: String,
    val payload: String,
    val headers: String,
    val creationTime: Instant,
    @Id var id: Long? = null,
)
