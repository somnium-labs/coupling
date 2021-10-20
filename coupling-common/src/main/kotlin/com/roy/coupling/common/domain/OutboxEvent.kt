package com.roy.coupling.common.domain

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
    // null이 아니면 update를 하는데 Persistable를 상속받아서 isNew를 오버라이딩 하면 되지만 지저분함
    @Id val id: String? = null,

    val aggregateType: String,
    val aggregateId: String,
    val commandType: String,
    val payload: String,
    val creationTime: Instant,
)
