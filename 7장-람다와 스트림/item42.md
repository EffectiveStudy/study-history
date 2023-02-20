# 익명클래스보다는 람다를 사용하라

---

함수 타입을 표현할때 추상 메소드를 하나만 담은 인터페이스를 사용해왔음

`JDK1.1` 이후로는 함수 객체를 만들때 익명 클래스를 주로 사용

```java
// 익명클래스의 인스턴스를 함수객체로 사용

List<String> words = List.of("flat", "plate", "corner", "officer", "medicine");
Collections.sort(words, new Comparator<String>() {
    @Override
    public int compare(String o1, String o2) {
        return Integer.compare(o1.length(), o2.length());
    }
});
```

자바 8 등장 후 추상메소드 한개짜리 인터페이스는 특별한 의미를 인정받아 함수형 인터페이스로 사용할 수 있음

```java
List<String> words = List.of("flat", "plate", "corner", "officer", "medicine");
Collections.sort(words,
        (o1, o2) -> Integer.compare(o1.length(), o2.length()));
```

람다식을 사용할 때 컴파일러가 타입추론 에러를 낼때만 람다식에 매개변수에 타입을 명시

=> java 8 버전에서는 타입 추론이 강화되었음

비교자 생성 메소드를 사용하면 고드를 더 간결하게 만들 수 있음

```java
List<String> words = Arrays.asList("flat", "plate", "corner", "officer", "medicine");
Collections.sort(words, Comparator.comparingInt(String::length));
```

java8 의 List 인터페이스에 추가된 sort 메소드를 이용
```java
List<String> words = Arrays.asList("flat", "plate", "corner", "officer", "medicine");
words.sort(Comparator.comparingInt(String::length));
```

추상메소드 정의가 아닌 

함수 객체를 인스턴스 필드에 두는 방식으로 구현

```java
package com.github.sangholee.dev.effectivejavastudy.study06_item34;

import java.util.function.BiFunction;
import java.util.function.DoubleBinaryOperator;

public enum OperationLambda {
    PLUS("+", (x, y) -> x + y),
    MINUS("-", (x, y) -> x - y),
    TIMES("*", (x, y) -> x * y),
    DIVIDE("/", (x, y) -> x / y);

    private final String symbol;

    private final DoubleBinaryOperator calculateLogic;

    OperationLambda(String symbol, DoubleBinaryOperator calculateLogic) {
        this.symbol = symbol;
        this.calculateLogic = calculateLogic;
    }

    public double apply(double x, double y) {
        return calculateLogic.applyAsDouble(x, y);
    }

    @Override
    public String toString() {
        return symbol;
    }
}
```

### 람다 사용시 주의점

1. 람다는 이름이 없고 문서화가 안되기 때문에 코드로 명확히 설명되지 않거나 줄수가 많아지면 람다를 쓰면 안됨 
2. 추상클래스의 인스턴스를 만들때는 익명 클래스를 써야함 
3. 람다는 자신을 참조하지 못하기 때문에 자기 자신을 참조해야할 때는 익명클래스를 써야함
4. 람다를 직렬화 하는 일을 피해야함 사용해야 한다면 private 정적 중첩 클래스 인스턴스 사용

---

### 핵심정리

> 자바 8 부터 람다가 도입됨
> 
> 익명클래스는 타입의 인스턴스를 만들때만 사용
> 
> 람다는 작은 함수객체를 쉽게 표현 가능하여 실용적으로 함수형 프로그래밍이 가능하도록 함

### 발표자의 생각
> IDE 의 도움을 받자

---

참고

[Java Lambda (2) 타입 추론과 함수형 인터페이스](https://futurecreator.github.io/2018/07/20/java-lambda-type-inference-functional-interface/)

---

[스터디 발표 소스 repository](https://github.com/EffectiveStudy/leesangho/blob/main/src/main/java/com/github/sangholee/dev/effectivejavastudy/study08_item42)
