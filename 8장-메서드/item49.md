# 매개변수가 유효한지 검사하라

---

메서드와 생성자의 대부분은 입력 매개변수의 값이 특정 조건을 만족하기를 바람

오류를 가능한한 빨리 (발생한 곳에서) 잡아야함


`public`, `protected` 메서드는 매개변수가 잘못됐을 때 던지느 예외를 문서화 해야함

```java
/**
 * (현재 값 mod m) 값을 반환한다. 이 메서드는
 * 항상 음이 아닌 BigInteger 를 반환한다는 점에서 remainder 메서드와 다르다. 
 *
 * @param m 계수(양수여야 한다.)
 * @return 현재 값 mod m
 * @throws ArithmeticException m이 0보다 작거나 같은면 발생한다.
 */
public BigInteger mod(BigInteger m) {
    if (m.signum() <= 0) {
        throw new ArithmeticException("계수 (m)는 양수여야 합니다. " + m);
    }

    // 계산 수행
}
```

java7 의 `Objects.requireNonNull` 메서드가 추가되어 유용함

```java
this.strategy = Objects.requireNonNull(strategy, "전략");
```

java9 에는 `Objects` 에 범위 검사 메서드들이 추가됨

`checkFromIndexSize`,`checkFromToIndex`,`checkIndex`

null 검사 메소드처럼 예외메시지를 지정할 수는 없음. 리스트와 배열 전용으로 설계


public 메서드가 아니라면 `assert` 를 사용해 유효성 검증가능

```java
public static void sort(long a[], int offset, int length) {
    assert a != null;
    assert offset >= 0 && offset <= a.length;
    assert length >= 0 && length <= a.length - offset;
}
```

나중에 쓰기위해 저장하는 매개변수와 생성자에서의 매개변수 유효성 검사는 매우 중요

메서드 몸체 실행 전 유효성 검사시 예외

* 유효성 검사 비용이 너무 높거나 실요적이지 않음
* 계산 과정에서 암묵적인 검사가 수행됨


---

### 핵심 정리

> 메서드나 생성자 작성시 매개변수 제약을 고려
> 
> 제약을 문서화 하고 코드 시작부분에 명시적으로 검사


---

[스터디 발표 소스 repository](https://github.com/EffectiveStudy/leesangho/tree/main/src/main/java/com/github/sangholee/dev/effectivejavastudy/study10_item49)
