# Item38 확장할 수 있는 열거 타입이 필요하면 인터페이스를 사용하라.

## 타입안전 열거 패턴과 열거 패턴

타입 안전 열거 패턴은 앨거 패턴이 지원되기 전인 JDK1.5 하위 버전에서 열거 패턴과 비슷한 기능을 이용하기 위해 사용하던 패턴

```java
public class Suit {

    public static final Suit CLUBS = new Suit("clubs");
    public static final Suit DIAMONDS = new Suit("diamonds");
    public static final Suit HEARTS = new Suit("hearts");
    public static final Suit SPADES = new Suit("spades");
    private final String name;

    private Suit(String name) {
        this.name = name;
    }

    public static void main(String[] args) {
        Suit clubs = Suit.CLUBS;
        clubs.print();
    }
}
```

![img.png](enumfrom1.5.png)

https://www.javacamp.org/designPattern/enum.html

* 열거 패턴은 거의 모든 상황에서 타입 안전 열거 패턴보다 우수 함
* 단, 타입 안전 열거 패턴은 확장이 가능하나 열거 패턴은 확장이 불가능함.
    * 기반 타입과 확장 타입과의 관계를 생각했을 때 확장이 된다고 해서 반드시 좋은것은 아님 [아직 이해는 잘 안됨]
* 대부분의 경우 확장과 열거패턴은 어울리지 않지만 연산 코드는 확장의 성질과 맞기 때문에 열거 패턴에서 이를 활용할 수 있는 방법은 인터페이스를 사용하는 것.

## 열거패턴에서 인터페이스를 활용 해서 확장 연산을 추가하는 방법

인터페이스를 활용하여 확장 타입 생성

```java
public interface Operation {

    double apply(double x, double y);
}
```

```java
public enum BasicOperation implements Operation {
    PLUS("+") {
        @Override
        public double apply(double x, double y) {
            return x + y;
        }
    },
    MINUS("-") {
        @Override
        public double apply(double x, double y) {
            return x - y;
        }
    },
    TIMES("*") {
        @Override
        public double apply(double x, double y) {
            return x * y;
        }
    },
    DIVIDE("/") {
        @Override
        public double apply(double x, double y) {
            return x / y;
        }
    };

    private final String symbol;

    BasicOperation(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public String toString() {
        return symbol;
    }
}
```

```java
public enum ExtendOperation implements Operation {

    EXP("^") {
        @Override
        public double apply(double x, double y) {
            return Math.pow(x, y);
        }
    },
    REMAINDER("%") {
        @Override
        public double apply(double x, double y) {
            return x % y;
        }
    };

    private String symbol;

    ExtendOperation(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public String toString() {
        return symbol;
    }
}
```

아래와 같이 인터페이스를 사용해서 확장타입을 자유롭게 사용 가능하다.
```java
public static void main(String[] args) {
    double x = 3.5;
    double y = 4.3;
    test(ExtendOperation.class, x, y);
}

private static <T extends Enum<T> & Operation> void test(//신박하다.. 이런거 처음봤다...
    Class<T> opEnumType, double x, double y) {
    for (Operation op : opEnumType.getEnumConstants()) {
        System.out.printf("%f %s %f = %f%n", x, op, y, op.apply(x, y));
    }
}
```

## 정리
> 열거 타입 자체를 확장할 수 없지만 인터페이스와 인터페이스를 구현하는 열거 타입을 함께 활용해 같은 효과를 낼 수 있다.
