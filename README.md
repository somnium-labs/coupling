# Coupling

- Saga Orchestration for Microservices Using the Outbox Pattern.
- Debezium is used for CDC services.
- eventuate처럼 다양한 프레임워크와 메시지 브로커를 지원하지 않고 스프링과 카프카만 지원

### 테스트

1. 환경 구성

```
$ docker-compose up

# m1 사용자
$ docker-compose -f docker-compose-m1.yml
```

2. debezium connector 등록

```
connect-api.rest

### Register connector
POST http://{{connect-host}}/connectors
```

3. example service 실행

- coupling-example
    - coupling-example-order-service
    - coupling-example-customer-service

4. example-api.rest 에 구성된 API를 호출하여 테스트

### TODO

- reply 핸들러를 app이 시작 될 때 한 번만 등록
    - 현재는 saga를 생성할때 매번 등록되고 있음.
- reply 전송시, command에서 받은 aggregateId를 그대로 전송할 수 있게 구조 수정
- saga dsl 개선 필요
    - 구조가 썩 마음에 들지 않음
    - 2개 이상의 participant를 병렬로 처리??
        - 현재는 순차적으로 처리할 수 밖에 없는 구조
- 모듈 분리
    - coupling-participant, coupling-orchestration 확실히 분리
