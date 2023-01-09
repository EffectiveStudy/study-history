# clone 재정의는 주의해서 진행하라

## 클론의 사용 방법과 이상한점

### Clone 사용 규칙

![img.png](img.png)

1. x.clone() != x //복사됬으니까 달라야지
2. x.clone.getClass() == x.getClass() //복사 됬으니까 형은 같아야지
3. x.clone().equals(x) //참일수도 있고 아닐수도 있다. Item
    * new Instance 를 이용해 만드는경우 성립 가능
    * 하지만 하위 객체에서 에러가 발생할 수 있음
    * 하위 에러를 피하려면 final로 clone을 구현하면 됨
    * 근데 그럴거면 Cloneable clone 을 쓸 이유가 없음

### 가변 객체는 더 지옥이다.

우리가 원하는 clone(복제의 기능은) 원본객체의 아무런 해도 끼치지 않는 복제된 객체를 희망한다.

* 하지만 현실은? -> Stack 예제
* 배열의 경우 clone으로 복하면 좋지만
* Entry[] 와 같은 자료형 배열은 deepCopy를 해줘야 한다.

### 그외

* 상속받은 하위 clone 메서드는 throw를 제거해야 한다.
* 상속을 원하지 않는 객체는 막아둘수도 있다.

```java
    @Override
    protected final Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }
```

* Thread Safe 하도록 동기화 해야한다.

## 결론은?

```java
public Yum(Yum yum) {...}
```

* 복사 생성자와 복사 팩터리 메서드를 사용하자.
    * 언어 모순적이지 않다
    * 불필요한 검사예외를 던지지 않는다.
    * 형번환도 필요없다.
    * 해당 클래스가 구현한 인터페이스를 인수로 받을 수 있다.
    

번외 왜 이렇게 cloneable 은 구린걸까?
https://www.artima.com/articles/josh-bloch-on-design