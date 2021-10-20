package com.roy.coupling.common.domain

import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface OutboxEventRepository : CoroutineCrudRepository<OutboxEvent, Long>
