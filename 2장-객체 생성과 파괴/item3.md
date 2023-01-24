# 아이템 3. private 생성자나 열거 타입으로 싱글턴임을 보증하라

* private 생성자로 싱글턴을 만들때 싱글턴이 보증되지 않는 이유
* 열거형을 사용할때 누릴 수 있는 이점

## private 생성자 + public static final 필드

```java
public class Elvis {

    public static final Elvis INSTANCE = new Elvis();

    private Elvis() {
    }

    public void leaveTheBuilding() {
        System.out.println("Elvis Leaved");
    }
}
```

## private 생성자 + 정적 팩터리 메서드

```java
public class Elvis {

    private static final Elvis INSTANCE = new Elvis();

    private Elvis() {
    }

    public static Elvis getInstance() {
        return INSTANCE;
    }

    public void leaveTheBuilding() {
        System.out.println("Elvis Leaved");
    }

}
```

## 위의 아이템들은 싱글턴임을 보장 하는가?

### Reflection 을 통해 새로운 인스턴스를 사용 가능하다.

* Reflection 에 Constructor#setAccessible(true) 사용시 생성자 접근 가능

### 직렬화 후 역직렬화 할때마다 새 인스턴스가 생성된다.

* 새 인스턴스 생성 방지를 위해 readResolve 를 구현해 줘야 한다... 짜증난다

## 열거형 사용

```java
public enum Elvis {
    INSTANCE;

    Elvis() {
    }

    public void leaveTheBuilding() {
        System.out.println("Elvis Leaved");
    }
}
```

