## 인터페이스는 타입을 정의하는 용도로만 사용하라

* 상수 인터페이스? (사용 금지)
    - 메서드 없이 상수를 뜻하는 static final로 가득찬 인터페이스를 말한다.
    - 이 상수들을 사용하려는 클래스에서는 정규화된 이름을 쓰는걸 피하고자 인터페이스를 구현하고자 한다.
    - 상수는 구현에 해당하므로 , 구현체에 들어가야 맞다.
    - 상수를 쓰지 않더라도 바이너리 호환성을 위해 상수를 들고 있게 되기도한다.
```javascript
public interface PhysicalConstants {
    static final double AVOGADRO_NUMBER = ...;
    static final double BOLTZMANN_CONSTANT = ...;
}
```
* 상수를 공개할 목적이라면?
  - 그 클래스나 인터페이스 자체에 추가하자
  - Integer안에 MIN_VALUE와 같은 모습
  ![img_1.png](img_1.png)
  - 열거 타입으로 나타내기도 좋으면 추천한다.
  - 인스턴스화 할 수 없는 유틸리티 클래스에 담아 공개하자

결국 정말 중요한건 인터페이스의 역할(타입을 정의한다)에 맞게만 사용하자
