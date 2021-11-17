# Coupling

- Saga Orchestration for Microservices Using the Outbox Pattern.
- Debezium is used for CDC services.
- eventuate처럼 다양한 프레임워크와 메시지 브로커를 지원하지 않고 스프링과 카프카만 지원

### 사용방법

- orchestrator

```
// saga 정의
// step이 순차적으로 실행되며, step이 실패하면 이전 step에 정의된 compensate이 역으로 모두 수행 됨
// step은 local, participant, await 3개임
private val sagaDefinition = SagaBuilder<CreateOrderSagaData> {
        transactionalOperator.executeAndAwait { // action에서 수행되는 db query가 transaction 처리 됨
            local(::create).compensate(::reject).bind() // reply가 실패면 compensate 수행 됨
            participant(::reserveCredit).bind()
        }
        await().bind() // participant의 reply를 받을 때 까지 일시 중단
        local(::approve).bind()  // reply가 성공일때 수행 됨
    }.onSuccess(CustomerCreditReserved::class)
        .onFailure(CustomerNotFound::class, ::handleCustomerNotFound)
        .onFailure(CustomerCreditLimitExceeded::class, ::handleCustomerCreditLimitExceeded)

// 정의한 saga를 실제로 수행 함
sagaDefinition.transact(data)
```

### 테스트

1. 환경 구성

```
$ docker-compose up

# m1 사용자
$ docker-compose -f docker-compose-m1.yml

# connecte가 mysql, kafka와 연결될 때 까지 대기해야 합니다.
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
