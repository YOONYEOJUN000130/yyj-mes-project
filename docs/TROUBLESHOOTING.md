# Troubleshooting Tickets

이 문서는 `Virtual PLC 기반 MES 생산 데이터 수집 및 분석 시스템`을 구현하면서 발생했거나, 운영 관점에서 의도적으로 확인한 문제 상황을 Ticket 형식으로 정리한 문서입니다.

단순히 에러를 해결한 기록이 아니라, 제조 IT 시스템에서 발생할 수 있는 장애를 원인, 로그, 조치, 재발 방지 관점으로 정리하는 것을 목표로 했습니다.

---

## Ticket-001. Oracle DB 컨테이너 미실행으로 인한 DB 연결 실패

**상태:** 해결  
**분류:** Database / Docker  
**발생 상황:** MES Server 실행 시 Oracle DB에 연결하지 못하거나, SQL Developer에서 DB 접속이 되지 않음

### 주요 로그 또는 증상

```text
Connection refused
IO Error: The Network Adapter could not establish the connection
```

또는 Docker 컨테이너 목록 확인 시 Oracle 컨테이너가 실행 중이지 않음

```bash
docker ps
```

### 원인

Oracle DB를 Docker 컨테이너로 실행하도록 구성했지만, MES Server 실행 전에 Oracle 컨테이너가 먼저 실행되지 않았습니다.

### 해결 방법

Oracle 컨테이너를 먼저 실행했습니다.

```bash
docker start yyj-oracle
docker ps
```

컨테이너 실행 후 MES Server를 다시 실행했습니다.

### 재발 방지

- 프로젝트 실행 순서를 문서화합니다.
- MES Server 실행 전 `docker ps`로 Oracle 컨테이너 상태를 확인합니다.
- 향후 Docker Compose를 도입하여 DB와 애플리케이션 실행 순서를 자동화할 수 있습니다.

---

## Ticket-002. SQL Developer 접속 설정 오류

**상태:** 해결  
**분류:** Database Tool / Oracle  
**발생 상황:** Oracle SQL Developer에서 Docker Oracle DB 접속 설정이 헷갈림

### 주요 설정

```text
Username: mes_user
Password: mes1234
Hostname: localhost
Port: 1521
Service name: FREEPDB1
```

### 원인

Oracle은 MySQL/MariaDB와 달리 SID 또는 Service Name 개념을 사용합니다. Docker Oracle 이미지에서는 기본 접속 대상이 `FREEPDB1`이므로 Service Name을 정확히 입력해야 했습니다.

### 해결 방법

SQL Developer에서 새 접속을 생성하고 Service Name에 `FREEPDB1`을 입력했습니다.

### 재발 방지

- DB 접속 정보를 README 또는 운영 문서에 명시합니다.
- SQL Developer 접속 설정 화면을 포트폴리오 문서에 함께 정리합니다.

---

## Ticket-003. PLC 이벤트 API 요청 시 404 Not Found 발생

**상태:** 해결  
**분류:** REST API / Controller Mapping  
**발생 상황:** `curl`로 PLC 이벤트 API를 호출했지만 404 에러가 발생함

### 주요 로그 또는 증상

```json
{
  "status": 404,
  "error": "Not Found",
  "path": "/api/plc/events"
}
```

### 원인

MES Server에 `/api/plc/events` 요청을 받을 Controller가 아직 준비되지 않았거나, 서버가 최신 코드로 실행되지 않은 상태였습니다.

### 해결 방법

`PlcEventApiController`에서 다음 API를 구현하고 MES Server를 다시 실행했습니다.

```text
POST /api/plc/events
```

정상 요청 후 아래와 같은 응답을 확인했습니다.

```json
{
  "eventId": "EVT-20260706-000001",
  "message": "PLC 이벤트가 저장되었습니다."
}
```

### 재발 방지

- API를 만든 뒤 서버를 재실행합니다.
- URL, HTTP Method, Controller Mapping을 함께 확인합니다.
- API 테스트는 Virtual PLC 구현 전 `curl`로 먼저 검증합니다.

---

## Ticket-004. MES Server 미실행 상태에서 Virtual PLC 요청 실패

**상태:** 확인 완료  
**분류:** System Integration / Network  
**발생 상황:** Virtual PLC Simulator는 실행 중이지만 MES Server가 꺼져 있는 경우 이벤트 전송 실패

### 예상 로그 또는 증상

```text
Connection refused
I/O error on POST request for "http://localhost:8080/api/plc/events"
```

### 원인

Virtual PLC Simulator는 MES Server의 REST API로 데이터를 전송합니다. 따라서 MES Server가 실행 중이지 않으면 연결 대상이 없어 요청이 실패합니다.

### 해결 방법

MES Server를 먼저 실행한 뒤 Virtual PLC Simulator를 실행합니다.

```bash
cd mes-server
./gradlew bootRun
```

그 다음 별도 터미널에서 Virtual PLC Simulator를 실행합니다.

```bash
cd virtual-plc-simulator
./gradlew bootRun
```

### 재발 방지

- 실행 순서를 `Oracle DB -> MES Server -> Virtual PLC Simulator`로 고정합니다.
- 향후 Virtual PLC에서 재시도 로직 또는 실패 로그 저장 기능을 추가할 수 있습니다.

---

## Ticket-005. API Key 불일치로 인한 인증 실패

**상태:** 해결  
**분류:** Security / API Authentication  
**발생 상황:** Virtual PLC 요청이 MES Server에서 인증 실패 처리됨

### 주요 로그 또는 증상

```text
401 Unauthorized
Invalid PLC API Key
```

### 원인

Virtual PLC Simulator와 MES Server가 서로 다른 API Key 값을 사용하면 MES Server가 허용되지 않은 PLC 요청으로 판단합니다.

### 해결 방법

두 프로젝트의 설정값을 동일하게 맞췄습니다.

```properties
plc.api-key=${PLC_API_KEY:local-plc-api-key}
mes-server.api-key=${PLC_API_KEY:local-plc-api-key}
```

### 재발 방지

- API Key는 코드에 직접 고정하지 않고 환경변수로 관리합니다.
- 운영 환경에서는 기본값 대신 실제 환경변수를 사용합니다.
- GitHub에 민감한 키가 올라가지 않도록 주의합니다.

---

## Ticket-006. HMAC Signature 검증 실패

**상태:** 해결  
**분류:** Security / Message Integrity  
**발생 상황:** API Key는 맞지만 MES Server에서 Signature 검증에 실패함

### 주요 로그 또는 증상

```text
401 Unauthorized
Invalid HMAC Signature
```

### 원인

Virtual PLC와 MES Server가 HMAC Signature를 생성할 때 사용하는 메시지 조합 규칙 또는 Secret Key가 다르면 검증에 실패합니다.

### 해결 방법

양쪽에서 동일한 규칙으로 Signature를 생성하도록 맞췄습니다.

```text
eventId + ":" + productSerialNo + ":" + processCode + ":" + result + ":" + timestamp
```

또한 양쪽 Secret Key 설정을 동일하게 맞췄습니다.

```properties
plc.hmac-secret-key=${PLC_HMAC_SECRET_KEY:local-hmac-secret-key}
mes-server.hmac-secret-key=${PLC_HMAC_SECRET_KEY:local-hmac-secret-key}
```

### 재발 방지

- Signature 생성 규칙을 문서화합니다.
- 요청 필드 순서, 구분자, timestamp 형식을 양쪽에서 동일하게 유지합니다.
- 향후 테스트 코드를 추가하여 Signature 생성 결과를 검증할 수 있습니다.

---

## Ticket-007. Timestamp 검증 실패

**상태:** 확인 완료  
**분류:** Security / Replay Attack Prevention  
**발생 상황:** 오래된 요청 또는 서버 시간과 차이가 큰 요청이 차단됨

### 주요 로그 또는 증상

```text
401 Unauthorized
Invalid timestamp
```

### 원인

MES Server는 요청 재사용 공격을 막기 위해 timestamp 유효 시간을 검사합니다. 요청 시간이 허용 범위를 벗어나면 정상 요청이어도 차단됩니다.

### 해결 방법

Virtual PLC에서 요청 시점의 timestamp를 생성하고, MES Server에서 허용 시간 내 요청인지 확인하도록 구현했습니다.

```properties
plc.timestamp-valid-seconds=300
```

### 재발 방지

- 서버와 클라이언트의 시간 차이를 줄입니다.
- 운영 환경에서는 NTP 등을 이용해 서버 시간을 동기화합니다.
- timestamp 허용 시간은 보안성과 운영 편의성을 고려해 조정합니다.

---

## Ticket-008. 주기적 생산 이벤트가 생성되지 않음

**상태:** 해결  
**분류:** Spring Scheduler  
**발생 상황:** Virtual PLC Simulator를 실행했지만 1분 주기 생산 이벤트 생성 로그가 출력되지 않음

### 주요 로그 또는 증상

```text
Started VirtualPlcSimulatorApplication
```

위 로그까지만 출력되고 아래 로그가 보이지 않음

```text
[Virtual PLC] 주기적 생산 이벤트 생성 시작
```

### 원인

`@Scheduled` 메서드를 사용하려면 Spring Boot 애플리케이션에 `@EnableScheduling`이 필요합니다. 해당 어노테이션이 누락되어 스케줄러가 동작하지 않았습니다.

### 해결 방법

`VirtualPlcSimulatorApplication`에 `@EnableScheduling`을 추가했습니다.

```java
@EnableScheduling
@SpringBootApplication
public class VirtualPlcSimulatorApplication {
    public static void main(String[] args) {
        SpringApplication.run(VirtualPlcSimulatorApplication.class, args);
    }
}
```

### 재발 방지

- `@Scheduled`를 사용할 때 `@EnableScheduling` 설정 여부를 함께 확인합니다.
- 애플리케이션 시작 로그뿐 아니라 실제 스케줄러 실행 로그까지 확인합니다.

---

## Ticket-009. 소스 코드 변경 후 실행 결과에 반영되지 않음

**상태:** 해결  
**분류:** Build / IDE  
**발생 상황:** `@EnableScheduling`을 추가했는데도 스케줄러가 동작하지 않음

### 주요 증상

소스 코드에는 어노테이션이 추가되어 있었지만, 실제 실행되는 빌드 결과에 변경사항이 반영되지 않았습니다.

### 원인

IntelliJ 또는 Gradle 빌드 캐시로 인해 이전 컴파일 결과가 실행되고 있었습니다.

### 해결 방법

프로젝트를 Clean/Rebuild 하거나 Gradle을 다시 실행했습니다.

```bash
./gradlew clean bootRun
```

### 재발 방지

- 설정 변경 후에는 서버를 완전히 종료하고 다시 실행합니다.
- 동작이 이상하면 `clean` 후 재빌드합니다.
- IntelliJ의 Gradle Sync 상태를 확인합니다.

---

## Ticket-010. 생산 실적 화면 접근 시 404 Whitelabel Error 발생

**상태:** 해결  
**분류:** Web MVC / Thymeleaf  
**발생 상황:** 생산 실적 화면 링크를 클릭했을 때 Whitelabel 404 페이지가 표시됨

### 주요 로그 또는 증상

```text
Whitelabel Error Page
There was an unexpected error (type=Not Found, status=404).
```

### 원인

Home 화면의 링크 경로와 Controller의 실제 Mapping 경로가 일치하지 않았습니다.

예를 들어 화면에서는 `production_record` 또는 잘못된 경로를 가리키고 있었지만, Controller에서는 `/production-records` 형태로 매핑되어 있었습니다.

### 해결 방법

`home.html`의 링크와 Controller Mapping을 동일하게 맞췄습니다.

```html
<a href="/production-records">생산 실적 목록</a>
```

### 재발 방지

- 화면 링크와 Controller 경로를 함께 확인합니다.
- 경로 이름은 복수형과 하이픈 스타일을 일관되게 사용합니다.
- 새 화면을 만들 때 Controller, Template, Link를 세트로 확인합니다.

---

## Ticket-011. 병목 공정이 항상 B 공정으로만 탐지됨

**상태:** 개선 완료  
**분류:** Simulation Logic / Data Quality  
**발생 상황:** Virtual PLC가 생성하는 공정별 Cycle Time이 고정되어 B 공정만 병목으로 탐지됨

### 원인

초기 테스트 데이터에서는 B 공정의 Cycle Time을 의도적으로 길게 설정했습니다. 이 방식은 병목 분석 기능 검증에는 유용했지만, 실제 시뮬레이션처럼 다양한 상황을 만들기에는 부족했습니다.

### 해결 방법

Virtual PLC Simulator에서 공정별 Cycle Time, OK/NG 결과, 공정 누락 여부를 랜덤으로 생성하도록 변경했습니다.

### 재발 방지

- 기능 검증용 고정 데이터와 실제 시뮬레이션용 랜덤 데이터를 구분합니다.
- 랜덤 데이터 생성 범위를 문서화합니다.
- 향후 특정 시나리오를 선택할 수 있는 모드를 추가할 수 있습니다.

---

## Ticket-012. 기존 데이터 초기화 필요

**상태:** 해결  
**분류:** Test Data / Admin  
**발생 상황:** 테스트를 반복하면서 기존 PLC 이벤트, 생산 실적, 완성품 데이터가 계속 누적됨

### 원인

Virtual PLC가 주기적으로 데이터를 생성하기 때문에 테스트 데이터가 계속 증가했습니다. 새로운 시나리오를 확인하려면 기존 데이터를 삭제할 필요가 있었습니다.

### 해결 방법

테스트 데이터를 초기화하는 Admin 기능을 추가했습니다.

삭제 대상:

- PLC 이벤트 로그
- 생산 실적
- 완성품

유지 대상:

- A/B/C 공정 기준 정보

### 재발 방지

- 기준 정보와 거래 데이터를 구분합니다.
- 운영 환경에서는 데이터 삭제 기능에 인증/권한을 반드시 적용해야 합니다.

---

## Ticket-013. GitHub Push 시 인증 실패

**상태:** 해결  
**분류:** Git / GitHub Authentication  
**발생 상황:** GitHub에 push할 때 비밀번호 인증 실패

### 주요 로그

```text
remote: Invalid username or token. Password authentication is not supported for Git operations.
fatal: Authentication failed
```

### 원인

GitHub는 Git 작업에서 계정 비밀번호 인증을 더 이상 지원하지 않습니다. HTTPS 방식으로 push할 경우 Personal Access Token 또는 브라우저 인증이 필요합니다.

### 해결 방법

GitHub Personal Access Token을 생성한 뒤 비밀번호 입력 위치에 토큰을 입력했습니다.

### 재발 방지

- GitHub 비밀번호 대신 Personal Access Token을 사용합니다.
- 토큰은 절대 코드나 문서에 저장하지 않습니다.
- 토큰 권한은 필요한 Repository와 `Contents: Read and write` 수준으로 제한합니다.

---

## Ticket-014. Git Commit Author 정보가 이전 이메일로 기록됨

**상태:** 해결  
**분류:** Git Configuration  
**발생 상황:** GitHub에 올릴 커밋의 작성자 이메일이 기존 학교 이메일로 기록됨

### 주요 확인 명령어

```bash
git config user.name
git config user.email
git log --oneline --pretty=fuller
```

### 원인

로컬 Git 설정에 이전 이메일이 저장되어 있었습니다.

### 해결 방법

프로젝트 로컬 Git 설정을 새 GitHub 이메일로 변경했습니다.

```bash
git config user.name "YOONYEOJUN000130"
git config user.email "bwin5134@naver.com"
```

이미 생성된 커밋은 작성자 정보를 수정했습니다.

```bash
git commit --amend --reset-author --no-edit
```

### 재발 방지

- 새 프로젝트 시작 시 `git config user.name`, `git config user.email`을 먼저 확인합니다.
- GitHub에 올리기 전 `git log`로 커밋 작성자 정보를 확인합니다.

---

## Ticket-015. Oracle DB TLS 적용 중 Wallet 생성 실패

**상태:** 보류  
**분류:** Database Security / TLS  
**발생 상황:** Oracle DB와 MES Server 간 TLS 통신을 실험하기 위해 Wallet을 생성하려 했지만 실패함

### 주요 로그

```text
ERROR: No Java
$JAVA_HOME should point to valid Java runtime
```

### 원인

Oracle Wallet 생성 도구인 `orapki` 실행 시 Java Runtime 경로가 올바르게 잡히지 않았습니다.

### 해결 방법

해당 시점에서는 프로젝트 핵심 기능 구현을 우선하기 위해 TLS 적용은 보류했습니다.

### 향후 개선 방향

- Oracle 컨테이너 내부 Java Runtime 경로 확인
- `JAVA_HOME` 설정 후 Wallet 재생성
- TCPS 포트 구성
- Spring Boot JDBC URL을 TCPS 방식으로 변경
- DB 통신 암호화 적용 결과 문서화

---

## 정리

이번 프로젝트에서 경험한 문제는 단순 코드 오류뿐만 아니라 DB 실행 순서, API 경로, 인증 값 불일치, HMAC 검증, 스케줄러 설정, GitHub 인증 등 실제 운영 환경과 연결되는 요소가 많았습니다.

이를 Ticket 형태로 정리하면서 문제를 다음 관점으로 바라보는 연습을 했습니다.

- 어떤 증상이 발생했는가
- 어떤 로그로 확인했는가
- 원인은 무엇인가
- 어떻게 해결했는가
- 같은 문제가 다시 발생하지 않으려면 무엇을 문서화해야 하는가

이러한 정리는 제조 IT 시스템 운영, MES 유지보수, DB 관리, 인터페이스 장애 대응 업무와 연결될 수 있습니다.
