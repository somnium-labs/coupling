package com.roy.coupling.common.messaging

interface Message {
    val aggregateType: String
    val aggregateId: String // kafka의 message key로도 사용될거라서 필요하다.(동일 파티션의 메시지 순서 보장)
    val commandType: String
    val payload: String
}
