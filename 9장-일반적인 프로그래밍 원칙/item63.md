# 문자열 연결은 느리니 주의하라

---

자바에선 (+) 연산자로 문자열 합치기 가능

작고 크기가 고정된 객체라면 상관없음

> 문자열 연결 연산자로 문자열 n 개를 잇는 시간은 n^2 에 비례

문자열은 불변이라 연결시에 양쪽의 내용을 모두 복사하므로 성능 저하 발생

성능을 위해서는 `StringBuilder` 를 사용해야함

---

### 핵심정리

> 많은 문자열 연결시에는 문자열 연결 연산(+) 대신 `StringBuilder` 의 `append` 메소드를 이용 


---

아래 내용은 궁금한 부분 찾아보면서 정리해본 내용입니다.

### 시간 복잡도 왜 n^2 일까?

String 은 기본적으로 불변함 -> 불변하려면 어떻게 해?

String 은 생성시에 새로운 char[] 배열을 생성함 -> length 만큼 할당

```java
String result = "";
for (int i = 0; i < itemNumber; i++) {
    result = result + CONCAT_STRING;
    // CONCAT_STRING 계속 생성되고 result 와 합쳐 다시 생성
    // CONCAT_STRING length 를 100 이라 하면
    // O((100 * 0) + 100) + O((100 * 1) + 100) + O((100 * 2) + 100) ... + O((100 * itemNumber -1) + 100)
    // O((1 + 2 + ... + itemNumber) 100)
    // O(itemNumber ^ 2 * 100)    
}

```


### StringBuilder, StringBuffer

Item17 에서 했음

성능 떄문에 가변 동반 클래스 제공

둘다 `AbstractStringBuilder` 상속 `StringBuffer` 는 `synchronized` 로 쓰레드 세이프 하지만
성능이 더 좋지 않음


### jdk 5.0 이상에서의 컴파일러 최적화?

jdk 5 버전 이상 부터는 문자열 연산자를 사용해서 코드를 작성하면 StringBuilder 로 수정해줌

하지만 항상 변환되는건 아님 => 컴파일러를 믿지말고 잘 만들자


### java 8 과 java 11 에서 성능 차이?

java9 에서 `makeConcatWithConstants` 를 통해 획기적으로 개선함


### 다른 방식의 문자열 연산

1. Stream 을 이용
2. StringJoiner 이용
3. String.join 이용


---

참고 자료

[junit5 테스트 수명 주기 콜백 reference docs](https://junit.org/junit5/docs/current/user-guide/#extensions-lifecycle-callbacks-timing-extension)

[java string + 연산 : 어떻게 동작하는지 알아봅시다.](https://codingdog.tistory.com/entry/java-string-연산-어떻게-동작하는지-알아봅시다)

[자바에서 문자열 합칠 때 '+' 연산을 쓰지 마세요!](https://nahwasa.com/entry/자바에서-String에-대한-연산을-쓰지-마세요-StringBuilder-StringBuffer)

[Java String + 연산은 정말 빠른가?](https://sundries-in-myidea.tistory.com/139)

[String은 항상 StringBuilder로 변환될까?](https://siyoon210.tistory.com/160)

---

[스터디 발표 소스 repository](https://github.com/EffectiveStudy/leesangho/tree/main/src/main/java/com/github/sangholee/dev/effectivejavastudy/study12_item63)
