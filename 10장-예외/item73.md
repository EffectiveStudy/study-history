# 추상화 수준에 맞는 예외를 던저라

---

수행하려는 일과 관련있는 예외를 던지기 위해서는 저수준 예외를 
처리하여 바깥으로 전파하여야함

=> 저수준 예외를 잡아 추상화 수준에 맞는 예외로 던저야함


### 예외 번역

```java
try {
    ... // 저수준 추상화
} catch(LowerLevelExecption e) {
    // 추상화 수준에 맞게 번역
    throw new HigherLevelException(...);    
}
```

```java
/**
 * Returns the element at the specified position in this list.
 *
 * <p>This implementation first gets a list iterator pointing to the
 * indexed element (with {@code listIterator(index)}).  Then, it gets
 * the element using {@code ListIterator.next} and returns it.
 *
 * @throws IndexOutOfBoundsException {@inheritDoc}
 */
public E get(int index) {
    try {
        return listIterator(index).next();
    } catch (NoSuchElementException exc) {
        throw new IndexOutOfBoundsException("Index: "+index);
    }
}
```


### 예외 연쇄

고수준 예외 생성자는 상위 클래스 생성자에 `원인` 전달

```java
try {
    ... // 저수준 추상화
} catch(LowerLevelExecption cause) {
    // 저수준 예외를 고수준에 실어 보냄
    throw new HigherLevelException(cause);    
}
```

대부분 표준 예외는 예외 연쇄용 생성자가 있음


```java
// 예외 연쇄용 생성자
class HigherLevelException extends Exception {
    HigherLevelException(Throwable cause) {
        super(cause);
    }
}
```

무턱대고 예외 전파 하는 것 보다 예외 번역이 우수함

하지만 남용하지말자

예외는 발생하지 않는것이 최선이고 미리 검사하여 예외를 예방하는 할 수 있음

차선책으로는 예외를 호출자까지 전파하지 않고 조용히 처리할수 있음

로깅기능을 이용하면 좋음

---

### 핵심정리

> 아래 계층의 예외를 예방하거나 상위계층에 노출하기 곤란하다면 예외번역을 사용
> 
> 예왜 연쇄를 이용하면 고수준 예외를 던지면서 원인도 알려줌

---

[스터디 발표 소스 repository](https://github.com/EffectiveStudy/leesangho/tree/main/src/main/java/com/github/sangholee/dev/effectivejavastudy/study14_item73)
