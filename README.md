# Coupling

- Saga Orchestration for Microservices Using the Outbox Pattern.
- Debezium is used for CDC services.
- eventuate처럼 다양한 프레임워크와 메시지 브로커를 지원하지 않고 스프링과 카프카만 지원

### 테스트

현재 Debezium connector를 사용하여 트랜잭션 로그 추적 후, Kafka에 집어 넣는 부분까지 구현 되어 있음.

1. 환경 구성

```
$ docker-compose up
```

2. debezium connector 등록

```
$ http POST http://localhost:8083/connectors < register-mysql.json
```

또는 connect-api.rest에 구성된 POST api 실행 (host설정: http-client.env.json)

3. Run CouplingTestApplication
4. mysql 접속
5. outbox_event 테이블에 값을 insert 하면 kafka 메시지를 수신하는 것을 로그에서 확인 할 수 있음

### TODO

```
saga {
    local { create() }
    participant { reserve() }
    local { approve() }
}.withCompensation {
    reject()
}
```
