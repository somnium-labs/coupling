package com.roy.coupling.common

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface OutboxEventRepository : CoroutineCrudRepository<OutboxEvent, Long>
