## 비트 필드 대신 EnumSet을 사용하라


* 비트 필드??<br>

<<<<<<< HEAD
열거한 값들이 주로 집합으로 사용될 경우 그냥 상수를 할당해서 사용했다.
=======
열거한 값들이 주로 집합으로 사용될 경우 그냥 상수를 할당해서 썻다.
>>>>>>> 62789b9a8e39d40f616c75348b7e5b442e883594

```java
public class Bit {
    public static final int STYLE_BOLD = 1 << 0; // 1
    public static final int STYLE_ITALIC = 1 << 1; // 2
    public static final int STYLE_UNDERLINE = 1 << 2; // 4
    public static final int STYLE_STRIKETHROUGH = 1 << 3;  // 8

    public void applyStyles(int styles) {
        System.out.printf("Applying styles %s to text%n",
            Objects.requireNonNull(styles));
    }

    // 사용 예
    public static void main(String[] args) {
        Bit text = new Bit();
        text.applyStyles(STYLE_BOLD | STYLE_ITALIC);
    }
}
```

비트필드를 사용하면 비트별 연산을 합집합과 교집합과 같은 **집합 연산**을 쉽게 수행 가능.
OR연산을 통해 여러 상수를 하나의 집합에 모을 수 있다
* 단점
1. 필드값이 그대로 출력되면 단순한 정수 열거 상수를 출력할 때보다 해석하기 어렵다.
2. 비트 필드 하나에 녹아 있는 모든 원소를 순회하기도 까다로움
3. 최대 몇비트가 필요한지 예측하여 적절한 타입을 구하기도 어려움


* 대안<br>
EnumSet 클래스는 상수값으로 된 열거타입을 효과적으로 관리해준다.

  1. EnumSet 클래스는 Set 인터페이스를 구현하여 어떤 Set 구현체와도 함께 사용할 수 있으며 타입 안전하다.

  2. EnumSet 내부는 비트 벡터로 구현되어있으며, 원소가 총 64개 이하라면 EnumSet 전체를 long 변수 하나로 표현하여 비트필드에 비견되는 성능을 보여준다.

  3. removeAll과 retainAll 과 같은 대량 작업은 비트를 효율적으로 처리할 수 있는 산술 연산을 써서 구현하였다.


* Enum Set의 단점<br>
불변 Enum set을 만들 수 없다.<br>
Collections.unmodifableSet()메소드를 사용하여 Enum Set을 불변 객체로 만들 수 있다
