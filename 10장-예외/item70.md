# 복구할수있는 상황에는 Checked 예외를, 프로그래밍 오류에는 Unchecked 예외를.

## TL;DR

자바에서 예외로 문제 상황을 알리고 싶은 경우, `기본 지침`은 다음과 같다.
- if 복구할수 있는 상황, throw Checked Exception with 복구에 필요한 정보 알려주는 메서드.
- if 프로그래밍 오류, throw Unchecked Exception.
- 확실하지 않은가? throw Unchecked Exception.
- Throwable은 정의하지도 말아라. Checked도, Unchecked도 아니니.

## Q. 자바의 예외 종류?

자바는 문제 상황을 알리는 타입(throwable)로서 다음 3가지를 제공.

1. Checked 예외 (`Exception`상속)
2. Unchecked 예외 - 런타임 예외 (`RuntimeException`상속)
3. Unchecked 예외 - Error (`Error`상속)

### 1. Exception

[ What? ]
- `java.lang.Exception Class & SubClasses`
- 애플리케이션 코드레벨에서, 예외상황 발생시 사용
- 다음 2가지 예외 존재
    - 1. **Checked (`Exception`상속. 비관적 처리기법)**
    - 2. **Un-checked(`RuntimeException`상속. 낙관적 처리 기법)**

#### Checked Exception (비관적 처리기법)

[ What? ]
- `복구가능성`이 있는 예외 구분
- 명시적인 예외처리 강제
 
[ Exam? ]
- Java: IOException
- JDBC: SQLException
- 비지니스 예외(꼭 비지니스 예외를 Checked로 처리할 필요는 없지만 안정성을 위해 Checked로 구분하는 케이스도 존재)
 
[ When to use? ] 
- 예전엔 복구할 가능성이 조금이라도 보이는 예외를 Checked로 구분했지만,
- 현재로선 복잡성, 생산성을 이유로 `복구할 가능성이 확실한 예외`만을 Checked로 구분하는 추세.
 
[ Pros? ]
- 명시적인 예외처리 강제로 인한 상대적 안전성

[ Cons? ]
- 불필요한 예외처리 강제
- 실제로는 복구 불가능한 경우가 많아(특히 WebApp), Runtime예외로 래핑해서 전파하는 경우가 많다

#### Un-checked Exception (`RuntimeException`상속. 낙관적 처리 기법)

[ What? ]
- 시스템 장애 or 휴먼 에러등으로 인한 프로그램상 오류 발생시 의도적으로 발생
- 명시적인 예외처리 강제 하지 않는다

[ Exam? ]
- NPE, IAE, ISE, etc.

[ When to use? ]
- 복구 불가능한 예외상황 발생시.
- 복구 불가능한 Checked예외 래핑.
 
[ Pros? ]
- 명시적인 예외처리 강제하지 않으므로, 필요할 경우에만 명시적으로 throws, catch가능

[ Cons? ]
- 안정성
- 일어날 수 있는 예외상황을 미리 파악하고, 예방하려는 더 많은 주의가 필요하다.

### 2. Error

[ What? ]
- `java.lang.Error Class & SubClasses`
- 시스템레벨에서, 비정상적인 상황 발생시 사용.
- 따라서, JVM에서 발생시키며, 어플리케이션코드에서 핸들링할 이유도 필요도 없다.

[ Exam? ]
- OOME, ThreadDeath

## Q. 그럼, 언제 어떤 예외 종류를 써야 하죠? (Best Practice)?

### Summary 

1. Checked 예외 (`Exception`상속)
   - 복구가능할 경우
2. Unchecked 예외 - 런타임 예외 (`RuntimeException`상속)
   - 프로그래밍 오류이거나 복구 여부가 애매한 경우
3. Unchecked 예외 - Error (`Error`상속)
   - 비즈니스 로직에서 쓸 일 없으니 상속하지도 말고, 직접 던지는 일도 없어야 한다.
- Note : 위 예외를 상속하지 않는 throwable은 사용하지 말라.

### Detail 

1. Checked 예외 (Java특)
   - 호출하는 쪽에서 `복구가능`할 경우.
   - 이때, Checked예외는 호출자가 해당 예외를 처리하거나 전파하도록 강제시킨다.
   - 즉, API설계자는, 사용자에게 해당 예외를 핸들링하라고 명시한다.
   - 복구에 필요한 정보를 알려주는 메서드를 제공하는 것이 좋다.

2. Unchecked 예외 - 런타임 예외
   - `프로그래밍 오류`일경우.
     - 즉, 전제조건 만족하지 못할 경우 (클라이언트가 해당 API의 제약을 지키지 못한 경우)
     - 예) `ArrayIndexOutOfBoundsException` : index should be in range of [0 ~ array.length - 1]
   - `복구 가능여부`가 확실하지 않은 경우
     - 예) 자원 고갈이 발생한경우, 일시적으로 부족한 경우일수도 있고 아닐 수도 있다.

3. Unchecked 예외 - Error
   - JVM레벨의 자원부족, 불변식 깨짐으로 인해 더 이상 `수행불가`한 경우
   - Application레벨에서 이 예외가 발생할 경우 개발자가 할 수 있는 일은 없다.
   - 또한 비즈니스 로직에서 개발자가 이 예외를 사용할 경우 또한 없다.
   - 예) OOME


## Q. 그럼 자바에서의 예외 처리방법에는 어떤게 있죠?

### 원칙 : Failing Loudly Fast (빠르게, 크게 짖기)

- Failing Fast : 오류 발생시점에서 최대한 가까운 곳에서 실패해야 한다
- Failing Loudly : 오류는 무시되지 않고, 상위계층으로 전파하며, 적절히 기록되어 알아 차릴수 있어야 한다.

### 예외 처리 방법

1. 복구 
2. 회피(전파)
3. 에러전환후 회피
   - `보다 의미 있는` 에러로 전환하여 전파
      - 예) 중복 키 존재할경우, JDBC SQLException => DuplicatedUserIdException으로 전환
   - `개발자가 사용하기 쉬운` 에러로 전환하여 전파
     - 예) 복구 불가 Checked예외 -> UnChecked예외로 전환
   - 이 때 cause error도 포함해 중첩예외로 만드는 것이 당연히 좋다.

## Q. 그럼 예외 처리 전략에는 어떤게 있을까요?

- 복구 불가능 Checked예외는, ASAP Un-checked로 래핑해서 전파하기
- 비지니스 예외는 Checked 예외로 만들어 적절한 복구 또는 대응 강제시키기
- 하지만 케이스에 따라 Un-checked로 두고,필요에 따라 throws 선언 하여, 불필요한 catch/throws 제거를 하는 것이 더 편할 때도 있다.

## (Optional) Q. 스프링에서는 어떠한 예외 처리 전략을 사용하나요?

### Summary 

스프링이 제공하는 `DataAccess기술`의 예외처리 전략 및 원칙 : 
1. 사용자 편의를 위해, `복구불가능 Checked예외를, Un-checked로 래핑`해서 예외 발생.
   - 기존 JDBC예외보다 의미가 명확하며, 사용하기 쉬운 예외로 전환
2. 특정 DB 및 DataAccess기술에 의존적이지 않은 `추상화 계층 예외`를 발생.

### Detail

[ Problem of JDBC ]

- JDBC는, 어댑터패턴 이용한 PSA를 통해, 각DB벤더 API에 의존치 않고 일관적인 데이터 접근 API를 제공한다.
- 하지만 세상에 완벽한 것은 없듯이, 다음과 같은 한계가 있다.
  1. 비표준SQL작성시, 결국 DB벤더에 종속적이게 된다 (DB마다 비표준SQL이 다르기에)
     - 치명적인 것은, 성능 향상을 위해 비표준SQL사용은 필수적이라는 것이다.
  2. JDBC의 Checked SQLException가 호환적이지 않다 (DB마다 에러종류와 원인이 다르기에)
     - DB마다 에러종류와 원인이 다름에도 불구하고, JDBC는 SQLException 하나로 소위 말하는 퉁친다
     - 따라서, 해당 예외에 담긴 에러코드는, 각DB벤더가 정의한 코드가 "그대로" 들어가있다.
     - 이걸 해결하기 위해, SQL상태정보를 부가정보로 제공하지만 (getSQLState()),
     - 각 DB벤더들의 JDBC 드라이버들마다 제공하는 정보에 편차가 존재하기에 문제가 해결 되진 않았다

[ Solution of Spring ]

- 기술 종속적이지 않은 `DataAccessException 추상화 계층 예외` 제공.
- DB별 에러코드를, 해당 `추상화 계층 예외클래스`에 매핑.
- 자바 `표준 데이터 접근 기술들의 예외까지 추상화`.
- 따라서, 추상화 계층 예외를 이용하여, 특정 기술에 종속적이지 않은 DAO 작성 가능.


## Refer
- 토프링 4장. Exception
