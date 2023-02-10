# int 상수 대신 열거타입을 사용하라

---

열거 타입이 없던 `Java 1.5` 이전에는 정수 열거 패턴의 int 상수를 많이 사용 했음

---

정수 열거 패턴의 int 상수
```java
public class Constant {

    public final static int APPLE_FUJI = 0;
    public final static int APPLE_PIPPIN = 1;
    public final static int APPLE_GRANNY_SMITH = 2;
}
```
단점

* 타입 안정성 보장 안됨
  * 서로 다른 상수지만 비교 가능
  * 상수 기능 이외의 int 연산 가능 (의도하지않은 부가기능)
  * 혹시 int 같은 값을 할당하는 경우 같은 실수 가능 (휴먼 에러 발생가능)
* 표현력도 별로
* 문자열 출력 까다로움
* 클라이언트에 상수 값이 새겨짐
  * 혹시라도 상수 값의 해당하는 int 값 변경시 내용이 모두 변경됨



열거 타입
```java
public enum Apple {
    FUJI, PIPPIN, GRANNY_SMITH
}
```
장점

* 단순함 (class 의 형태)
* 인스턴스를 하나씩만 만듬
* 타입 안정성 보장
  * 컴파일시 타입 안정성 제공
* toString 메소드 제공
* 열거타입 전체 순회도 간편
* 클라이언트에 상수 값이 새겨지지 않음
  * 열거타입의 순서나 값을 바꿔도 문제가 없음
* 기본적으로 클래스이므로 고차원 추상화 개념도 가능


```shell
# 자바 컴파일
# -XD-printflat option 을 이용하여 자바코드로 변환되는 것을 직접 확인
javac -XD-printflat src/main/java/com/github/sangholee/dev/effectivejavastudy/study06_item34/Apple.java

# 자바 역 assembler 실행
javap -p build.classes.java.main.com.github.sangholee.dev.effectivejavastudy.study06_item34.Apple
```
---

상수와 특정 데이터 연결

```java
public enum Planet {
    MERCURY(3.302e+23, 2.439e6),
    VENUS(4.869e+24, 6.052e6),
    EARTH(5.975e+24, 6.378e6),
    MARS(6.419e+23, 3.393e6),
    JUPITER(1.899e+27, 7.149e7),
    SATURN(5.685e+26, 6.027e7),
    URANUS(8.683e+25, 2.556e7),
    NEPTUNE(1.024e+26, 2.477e7);

    private final double mass;           // 질량(단위: 킬로그램)
    private final double radius;         // 반지름(단위: 미터)
    private final double surfaceGravity; // 표면중력(단위: m / s^2)

    // 중력상수(단위: m^3 / kg s^2)
    private static final double G = 6.67300E-11;

    Planet(double mass, double radius) {
        this.mass = mass;
        this.radius = radius;
        surfaceGravity = G * mass / (radius * radius);
    }

    public double mass()           { return mass; }
    public double radius()         { return radius; }
    public double surfaceGravity() { return surfaceGravity; }

    public double surfaceWeight(double mass) {
        return mass * surfaceGravity;  // F = ma
    }
}
```

* 생성자에서 주입받은 데이터를 필드를 저장하여 데이터와 상수를 연결 할 수 있음
* `surfaceWeight` 과 같이 행성에서 무게를 반환하는 기능을 제공하는 것 처럼 메소드로 기능 제공이 가능 


---

상수별 메소드 구현

```java
public enum Operation {

    PLUS("+") {
        public double apply(double x, double y) { return x + y; }
    },
    MINUS("-") {
        public double apply(double x, double y) { return x - y; }
    },
    TIMES("*") {
        public double apply(double x, double y) { return x * y; }
    },
    DIVIDE("/") {
        public double apply(double x, double y) { return x / y; }
    };

    private final String symbol;

    Operation(String symbol) { this.symbol = symbol; }

    public abstract double apply(double x, double y);
}
```

일반적으로

클래스 로드 시점

* 클래스의 인스턴스 생성
* 클래스의 정적 메소드 호출
* 클래스의 정적 변수 할당
* 클래스의 정적 변수 사용 (final x)

인스턴스 초기화 순서

1. 정적 블록
2. 정적 변수
3. 생성자

열거타입의 경우

클래스 로드 시점에 JVM Method 영역에 할당되며 heap 영역에 인스턴화 됨

=> item 3 에 싱글톤 설명에서 잠시 나옴

명세서에서는 암시적인 필드가 초기화 될때 생성된다고 함

> 결론적으로 인스턴스 생성이 먼저고 정적 변수 초기화는 나중에 이루어짐 


[열거 타입의 변수의 명세](https://docs.oracle.com/javase/specs/jls/se8/html/jls-8.html#jls-8.9.3)

또 다른 예시

주중의 일당을 주는 계산식

```java
public enum PayrollDay {
    MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY;

    private static final int MINS_PER_SHIFT = 8 * 60;

    int pay(int minutesWorked, int payRate) {
        int basePay = minutesWorked * payRate;

        int overtimePay = minutesWorked <= MINS_PER_SHIFT
                ? 0
                : (minutesWorked - MINS_PER_SHIFT) * payRate / 2;

        return basePay + overtimePay;
    }

}
```

위 열거타입은 아주 적절하게 추상화 되었음

하지만 주말에 일당을 주는 경우가 생김

상수별 메소드 구현 방식으로 수정

```java
public enum PayrollDayMethodImpl {
  MONDAY {
    @Override
    int pay(int minutesWorked, int payRate) {
      int basePay = minutesWorked * payRate;

      int overtimePay = minutesWorked <= MINS_PER_SHIFT
              ? 0
              : (minutesWorked - MINS_PER_SHIFT) * payRate / 2;

      return basePay + overtimePay;
    }
  },
  TUESDAY {
    @Override
    int pay(int minutesWorked, int payRate) {
      int basePay = minutesWorked * payRate;

      int overtimePay = minutesWorked <= MINS_PER_SHIFT
              ? 0
              : (minutesWorked - MINS_PER_SHIFT) * payRate / 2;

      return basePay + overtimePay;
    }
  },
  ... 생략
  SATURDAY {
    @Override
    int pay(int minutesWorked, int payRate) {
      int basePay = minutesWorked * payRate;

      int overtimePay = basePay / 2;

      return basePay + overtimePay;
    }
  },
  SUNDAY {
    @Override
    int pay(int minutesWorked, int payRate) {
      int basePay = minutesWorked * payRate;

      int overtimePay = basePay / 2;

      return basePay + overtimePay;
    }
  };

  private static final int MINS_PER_SHIFT = 8 * 60;

  abstract int pay(int minutesWorked, int payRate);
}
```

해당 예시인 메소드 구현의 단점으로는 상수끼리 코드 공유가 안됨 => 너무 많은 중복

코드 공유를 위해 switch 문을 통해 수정

```java
public enum PayrollDaySwitch {
    MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY;

    private static final int MINS_PER_SHIFT = 8 * 60;

    int pay(int minutesWorked, int payRate) {
        int basePay = minutesWorked * payRate;

        int overtimePay = switch (this) {
            case SATURDAY, SUNDAY -> basePay / 2;
            default -> minutesWorked <= MINS_PER_SHIFT
                    ? 0
                    : (minutesWorked - MINS_PER_SHIFT) * payRate / 2;
        };

        return basePay + overtimePay;
    }

}
```

하지만 열거타입에 새로운 값이 추가되면 이슈가 발생 할 수 있음

---

전략적 열거 타입 패턴

상수별 메소드 구현 방식의 단점을 전략 패턴을 적용하여 해결가능

[전략 패턴](https://ko.wikipedia.org/wiki/전략_패턴)

> 전략 패턴이란?
> 
> 실행 중에 알고리즘을 선택할 수 있게 하는 행위 소프트웨어 디자인 패턴
>
> 전략은 알고리즘을 사용하는 클라이언트와는 독립적으로 다양하게 만든다
> 
> * 특정한 계열의 알고리즘들을 정의하고
> * 각 알고리즘을 캡슐화하며 
> * 이 알고리즘들을 해당 계열 안에서 상호 교체가 가능하게 만든다.


전략패턴을 적용한 경우
```java
public enum PayrollDay {
    MONDAY(WEEKDAY), TUESDAY(WEEKDAY), WEDNESDAY(WEEKDAY),
    THURSDAY(WEEKDAY), FRIDAY(WEEKDAY),
    SATURDAY(WEEKEND), SUNDAY(WEEKEND);

    private final PayType payType;

    PayrollDay(PayType payType) { this.payType = payType; }

    int pay(int minutesWorked, int payRate) {
        return payType.pay(minutesWorked, payRate);
    }

    enum PayType {
        WEEKDAY {
            int overtimePay(int minsWorked, int payRate) {
                return minsWorked <= MINS_PER_SHIFT ? 0 :
                        (minsWorked - MINS_PER_SHIFT) * payRate / 2;
            }
        },
        WEEKEND {
            int overtimePay(int minsWorked, int payRate) {
                return minsWorked * payRate / 2;
            }
        };

        abstract int overtimePay(int mins, int payRate);
        private static final int MINS_PER_SHIFT = 8 * 60;

        int pay(int minsWorked, int payRate) {
            int basePay = minsWorked * payRate;
            return basePay + overtimePay(minsWorked, payRate);
        }
    }

}
```

switch 문은 열거타입의 상수별 동작을 구현하는데 적합하지 않음 => ocp 를 어길 가능성 있음

하지만 switch 문을 쓰기 적절한 때 
* 기존 열거 타입에 상수별 동작을 혼합할때 
* 추가하려는 메소드가 의미상 열거 타입에 속하지 않을때
* 종종 쓰이지만 열거타입안에 포함될 만큼 유용하지 않을때

> switch 문으로 충분하면 사용하자

```java
public class OperationAdaptor {

  public static Operation inverse(Operation op) {
    return switch (op) {
      case PLUS -> Operation.MINUS;
      case MINUS -> Operation.PLUS;
      case TIMES -> Operation.DIVIDE;
      case DIVIDE -> Operation.TIMES;
      default -> throw new AssertionError("잘못된 연산");
    };
  }
}
```

---

### 핵심 정리
> 열거 타입은 더 읽기 쉽고 안전하며 강력함
> 
> 상수와 특정 데이터를 연결하거나 상수마다 다르게 동작해야한다면 명시적 생성자나 메소드를 사용
> 
> 하나의 메소드가 상수별로 다르게 동작해야한다면 switch 문 대신 상수별 메소드 구현을 통해 해결
> 
> 열거 타입 상수가 일부 같은 동작을 공유한다면 전략 열거 타입 패턴 사용


---

참고

[stackoverflow - Static (and final) field initialization in an enum in Java](https://stackoverflow.com/questions/29858910/static-and-final-field-initialization-in-an-enum-in-java)

[쟈미의 devlog - [Effective Java] item 34. int 상수 대신 열거 타입을 사용하라](https://jyami.tistory.com/102)

[5.열거 타입과 애너테이션](https://catsbi.oopy.io/4678b976-bd7e-4353-b4f0-04c06f66df03)

[클래스는 언제 로딩되고 초기화되는가? (feat. 싱글톤)](https://velog.io/@skyepodium/클래스는-언제-로딩되고-초기화되는가)

[Java Enum이란](https://honbabzone.com/java/java-enum/)

[열거 타입의 명세](https://docs.oracle.com/javase/specs/jls/se8/html/jls-8.html#jls-8.9.3)

[전략 패턴](https://ko.wikipedia.org/wiki/전략_패턴)

---

[스터디 발표 소스 repository](https://github.com/EffectiveStudy/leesangho/tree/main/src/main/java/com/github/sangholee/dev/effectivejavastudy/study06_item34)
