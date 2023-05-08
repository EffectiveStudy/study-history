# readObject 메서드는 방어적으로 작성하라

----

불변을 유지하기 위해 방어적 복사 코드로 작성

```java
public class Period {

    private final Date start;
    private final Date end;

    public Period(Date start, Date end) {
        this.start = new Date(start.getTime());
        this.end = new Date(end.getTime());
        if (this.start.compareTo(this.end) > 0) {
            throw new IllegalArgumentException(start + " after " + end);
        }
    }

    public Date start() {
        return new Date(start.getTime());
    }

    public Date end() {
        return new Date(end.getTime());
    }

    @Override
    public String toString() {
        return start + " - " + end;
    }
}
```

하지만 직렬화를 하려고 `implements Serializable` 한다면 불변이 깨지게 됨

`readObject` 메서드는 매개변수로 바이트 스트림을 받는 일종의 생성자

따라서 `readObject` 에서 유효성 검사

```java
private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
    s.defaultReadObject ();
    
    // 불변식을 만족하는지 검사
    if (start.compareTo(end) > 0) {
        throw new InvalidObjectException(start + " after " + end);
    }
}
```

가변공격의 예시

```java
package com.github.sangholee.dev.effectivejavastudy.study17_item88;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;

public class MutablePeriod {

    private final Period period;

    public final Date start;

    public final Date end;

    public MutablePeriod() {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bos);

            //유효한 Period 인스턴스를 직렬화한다.
            out.writeObject(new Period(new Date(), new Date()));

            /*
             * 악의적인 '이전 객체 참조', 즉 내부 Date 필드로의 참조를 추가한다.
             * 상세 내용은 자바 객체 직렬화 명세의 6.4절을 참고
             */
            byte[] ref = {0x71, 0, 0x7e, 0, 5}; // 참조 #5
            bos.write(ref); // 시작 start 필드 참조 추가
            ref[4] = 4; //참조 #4
            bos.write(ref); // 종료(end) 필드 참조 추가

            // Period 역직렬화 후 Date 참조를 훔친다.
            ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
            period = (Period) in.readObject();
            start = (Date) in.readObject();
            end = (Date) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new AssertionError(e);
        }
    }

    public static void main(String[] args) {
        MutablePeriod mp = new MutablePeriod();
        Period p = mp.period;
        Date pEnd = mp.end;

        pEnd.setYear(78);
        System.out.println(p);

        pEnd.setYear(69);
        System.out.println(p);
    }
}

```

`Period` 인스턴스는 불변식을 유지하면 생성 되었지만 내부값 수정이 가능함

따라서 역직렬화인 `readObject` 메서드 수행시 클라이언트가 소유해서는 안되는 객체 참조 필드를 모두 방어적 복사 하여야함

```java
private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
    s.defaultReadObject ();

    // 가변 요소들 방어적으로 복사
    start = new Date(start.getTime());
    end = new Date(end.getTime());

    // 불변식을 만족하는지 검사
    if (start.compareTo(end) > 0) {
        throw new InvalidObjectException(start + " after " + end);
    }
}
```

### 기본 `readObject` 메서드를 써도 좋은 경우

`transient` 필드를 제외한 모든 필드 값을 매개변수로 받아 유효성 검사 없이 필드에 대입하는 `public 생성자` 를 추가해도 된다면 O

그게 아니라면 `커스텀 readObject` 메서드 혹은 `직렬화 프록시 패턴` (item90) 사용

---

### 핵심 정리

> `readObject` 메서드는 마치 public 생성자 처럼 작성
> 
> `readObject` 는 어떤 바이트 스트림이던 유효한 인스턴스를 만들어야 함
>
> 진짜 직렬화된 인스턴스만 넘어온다고 생각하면 안됨
> 
> 안전한 `readObject` 메서드 작성 지침
>   * `private` 객체 참조 필드는 방어적으로 복사 해야함
>   * 모든 불변식 검사 어긋나면 `InvalidObjectException` 던지기
>   * 역직렬화 후 객체 그래프 전체의 유효성 검사시 `ObjectInputValidation` 인터페이스 사용
>   * 직접적이든 간접적이든 재정의 가능한 메서드는 호출 X
> 

---

[스터디 발표 소스 repository](https://github.com/EffectiveStudy/leesangho/tree/main/src/main/java/com/github/sangholee/dev/effectivejavastudy/study17_item88)
