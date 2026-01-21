## OTA Supplier Connector Domain

### 1. Domain Purpose

본 도메인은 외부 OTA(Online Travel Agency) 공급사로부터 숙소 정보를 수집하여,
내부 시스템에서 **일관되고 안전하게 관리**하기 위한 연동 도메인이다.

이 도메인의 최우선 목적은 다음과 같다.

* 외부 API 재시도 상황에서도 **중복 데이터가 발생하지 않도록 보장**한다.
* 공급사별로 상이한 데이터 구조를 내부 기준으로 **표준화**한다.
* 연동 실패가 전체 시스템 장애로 전파되지 않도록 **경계를 명확히 한다**.

본 도메인은 “빠르게 많이 적재”보다 **정확성과 안정성**을 우선한다.

---

### 2. Core Concepts

#### Supplier

외부에서 숙소 정보를 제공하는 주체를 의미한다.

* 예: Expedia, Booking.com 등
* 각 Supplier는 서로 다른 API 규격과 데이터 포맷을 가진다.

#### SupplierType

시스템 내부에서 Supplier를 구분하기 위한 식별 값이다.

* 외부 문자열이 아닌 내부 Enum 또는 코드 값으로 관리한다.
* 로그, 식별, 멱등성 판단의 기준이 된다.

#### Accommodation

외부 Supplier로부터 전달받아 내부에서 관리하는 숙소 정보이다.

* 시스템 내에서 관리되는 최소 단위의 핵심 도메인 객체이다.
* 반드시 하나의 Supplier에 속한다.

#### ExternalId

Supplier가 정의한 숙소의 고유 식별자이다.

* Supplier마다 형식과 의미가 다를 수 있다.
* 내부 시스템에서는 SupplierType과 함께 숙소를 식별하는 기준으로 사용된다.

---

### 3. Domain Rules (Invariants)

본 도메인에서 반드시 지켜져야 하는 규칙은 다음과 같다.

1. Accommodation은 내부 통합 숙소가 아닌, **외부 OTA 공급사 기준 숙소**이다.
2. 하나의 Accommodation은 반드시 하나 SupplierType에 속한다.
3. 숙소의 유일성은 (SupplierType + ExternalId) 조합으로 판단한다.
4. 동일한 숙소에 대한 연동 요청이 여러 번 수행되더라도 결과는 동일해야 한다. (멱등성 보장)
5. 외부 공급사의 데이터는 항상 내부 데이터보다 우선하며, 재시도 시에는 업데이트로 처리한다.
6. 연동 실패는 "데이터가 존재하지 않음"과 명확히 구분되어야 한다.

---

### 4. Idempotency Consideration

외부 Supplier API는 네트워크 오류, 타임아웃 등으로 인해 동일 요청이 반복 호출될 수 있다.

본 도메인은 이를 **비정상 상황이 아닌 전제 조건**으로 간주하며,
다음 기준을 통해 멱등성을 보장한다.

* Accommodation은 (SupplierType, ExternalId) 기준으로 식별된다.
* 동일 키에 대한 중복 적재는 허용하지 않는다.
* 재시도 시에도 데이터 정합성이 유지되어야 한다.
* 상태 기반 멱등성 처리
   * 기존 Mapping이 있으면 Update, 신규 Mapping 없으면 Insert 
   * DB Unique Constraint는 최후 안전장치로 사용

---

### 5. Observability & Retry

연동 도메인은 장애 발생 시 빠른 원인 파악이 중요하다.

이를 위해 다음과 같은 관측 가능성을 고려한다.

```text
[TRACE_ID=1][SUPPLIER=EXPEDIA][STEP=FETCH][RESULT=FAIL][REASON=TIMEOUT]
[TRACE_ID=2][SUPPLIER=BOOKING][STEP=PERSIST][RESULT=SUCCESS]
```

#### 로그 설계
- 모든 연동 요청은 requestId(traceId) 기준으로 추적. 
- 단계별 로그 포함: Fetch, Parse, Persist 등. 
- 로그만으로도 장애 공급사, 단계, 원인 식별 가능.

#### 재시도 정책
- 외부 API 호출 실패 시 최대 3회 재시도, 지수 백오프(Exponential Backoff) 적용. 
- 재시도 후에도 실패 시 운영자가 재처리 가능하도록 상태 기록 및 알림. 
- 재시도 과정에서도 멱등성 보장.
- 동시성 상황에서는 동일 `AccommodationMapping` 생성 시도 시, `DB Constraint` 위반은 정상 시나리오로 처리하며, 애플리케이션 레벨에서는 충돌 로그를 남기고 기존 매핑을 반환한다.

---

### 6. Success / Failure Definition

#### Sync Success

본 도메인에서 연동 성공이란, 외부 OTA 공급사로부터 수신한 숙소 데이터가
도메인 규칙을 위반하지 않은 상태로 내부 시스템에 **저장 또는 갱신**된 상태를 의미한다.

* 신규 저장이든, 기존 데이터의 갱신이든 결과는 동일하게 성공으로 간주한다.
* 데이터 변경이 없더라도 정상 흐름을 통해 처리되었다면 성공으로 판단한다.

#### Sync Failure

연동 과정에서 다음 중 하나라도 발생할 경우, 해당 연동은 실패로 간주한다.

* 외부 API 호출 실패 또는 타임아웃
* 데이터 파싱 실패
* 도메인 규칙 위반 (중복 데이터 등)
* 내부 저장 또는 갱신 실패

일부 단계라도 실패할 경우, 연동 전체를 실패로 간주하며
정상적인 처리 흐름을 타지 않은 결과는 성공으로 인정하지 않는다.

---

### 7. Out of Scope

본 프로젝트에서는 다음 영역을 다루지 않는다.

* 예약 생성 및 결제 처리
* 가격 정책 및 할인 로직
* 사용자 노출 UI 및 프론트엔드
* 실시간 재고 동기화

본 도메인은 **숙소 메타데이터 연동**에만 책임을 가진다.

---

### 8. Domain Model
**_Entity_**

**_Attribute_**

`Accommodation`: 내부 시스템에서 관리하는 숙소 정보
- `id`: Long - 내부 pk
- `name`: String - 숙소 이름
- `latitude`, `longitude`: double - 좌표
- `address`: String - 숙소 주소
- `status`: AccommodationStatus (Enum) - 숙소 상태
    - ACTIVE
    - INACTIVE
    - CLOSED
- `updatedAt`: LocalDateTime - 수정일자

`AccommodationMapping`: 외부 OTA 공급사 기준 숙소 식별자 매핑
- `id`: Long - 내부 pk
- `supplierType`: Enum - 외부 공급자 타입
    - EXPEDIA
    - BOOKING
- `externalAccommodationId`: String - 외부 공급자 키
- `accommodationId`: Long - Accommodation FK
- `createdAt`: LocalDateTime - 생성일자

**UNIQUE**(supplierType, externalAccommodationId)

---

### 9. Sync Flow

#### 전체 적인 흐름
1. 외부 공급사에서 숙소 정보 기준으로 정보를 제공
2. 내 서비스는 외부 공급자의 데이터를 그대로 저장하지 않고, 내부 도메인 모델에 맞게 가공
3. 동일한 외부 요청이 여러번 들어와도 결과는 항상 동일해야함

#### 동기화 요청 시작
- 특정 SupplierType에 대해 숙소 동기화를 시작한다.

#### 외부 OTA API 호출
- 외부 전용 DTO로만 받는다
- 도메인에 직접 전달되면 안된다

#### 외부 DTO -> 내부 도메인으로 가공
- 불필요한 필드 제거 
- 내부 규칙에 맞게 값 정규화
- 외부 식별자를 명확하게 분리해야함
- 외부 전용 DTO를 내부에 맞게 Mapper로 변환한다

#### 멱등성 기준 조회
- `SupplierType`과 `externalAccommodationId` 유니크 식별자를 통해 `AccommodationMapping`조회

#### 분기

- Mapping이 있는경우: 기존 Accommodation이 존재함을 의미
    - 정책: 
      - 숙소 정보를 Update
      - 내부 표현만 최신 상태로 갱신
      - 새로운 숙소를 생성하지 않는다.

- Mapping이 존재하지 않는 경우: 
  - 새로운 숙소로 판단
    - 처리:
      1. `Accommodation` 신규 생성
      2. 생성 된 `Accommodation`과 외부 식별자를 연결하는 `AccommodationMapping`를 생성

#### 저장 
- `AccommodationMapping`에는 (`supplierType`, `externalAccommodationId`) 유니크 제약이 존재한다.
- 동시성이나 재시도 상황
  - 중복 Mapping 생성은 DB 레벨에서 차단
  - 애플리케이션은 이를 정상 시나리오로 처리

#### 동기화 결과 판단
- 다음 중 하나라도 실패하면 동기화 전체를 실패로 간주
    - 외부 API 호출 실패 
    - DTO 파싱 실패 
    - 도메인 규칙 위반 
    - 저장 실패

일부 성공 / 일부 실패를 허용하지 않는다. \
정상적인 흐름을 타지 않으면 성공으로 보지 않는다.

---

### 10. Request State Machine

| 상태          | 의미        | 재요청 시 동작       |
|-------------|-----------|----------------|
| REQUESTED   | 동기화 요청 수신 | 외부 API 호출      |
| IN_PROGRESS | 외부 연동 중   | 중복 처리 방지       |
| SUCCESS     | 정상 완료     | 기존 결과 반환       |
| FAILED      | 처리 실패     | 재시도 정책에 따라 재처리 |
| COMPENSATED | 보정 완료     | 추가 처리 없음       |

- REQUESTED -> IN_PROGRESS: 외부 API 호출 시작 시
- IN_PROGRESS -> SUCCESS: 데이터 저장 및 도메인 룰 검증 완료 시
- IN_PROGRESS -> FAILED: 외부 API 실패, 파싱 오류, 도메인 규칙 위반 시
- FAILED -> REQUESTED: 재시도 정책에 따라 재처리 시
- SUCCESS -> COMPENSATED: 운영 보정이 필요할 때

상태 기반 멱등성 + 재시도 정책이 결합되어 동시성 및 재시도 문제 해결