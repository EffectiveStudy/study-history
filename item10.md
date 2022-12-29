# equals 는 일반 규약을 지켜 재정의 하라


```
자바의 Object는 객체를 만드는 구체 클래스지만 기본적으로 상속해서 사용하도록 설계됨  
final 이 아닌 메소드(equals, hashCode, toString, clone, finalize)는 재정의(overriding)를 염두하고 설계됨
재정의시 지켜야하는 일반 규약이 정의 되어 있음
따라서 규약에 맞게 재정의 해야함
```
---

### equals 재정의를 하면 안되는 경우

* 각 인스턴스가 본질적으로 고유한 경우
* 인스턴스의 논리적 동치성을 검사할 일이 없는 경우
* 상위 클래스에서 재정의한 equals 가 하위 클래스에도 딱 맞는 경우
* 클래스가 private 이거나 package-private 인 경우
* 값 클래스 라도 같은 인스턴스가 둘이상 만들어짐을 보장하는 경우

---

### equals 메소드 구현시 일반 규약

* 반사성
  * null 이 아닌 모든 참조값 x 에 대해, x.equals(x) 는 true  
* 대칭성
  * null 이 아닌 모든 참조값 x,y 에 대해, x.equals(y) 가 true 면 y.equals(x) 도 true
* 추이성
  * null 이 아닌 모든 참조값 x,y,z 에 대해, x.equals(y) 가 true, y.equals(z) 가 true 면 x.equals(z) 도 true 
* 일관성
  * null 이 아닌 모든 참조값 x,y 에 대해, x.equals(y) 는 true 혹은 false 항상 같은 값을 반환
* null-아님
  * null 이 아닌 모든 참조값 x 에 대해, x.equals(null) 는 false

---

### equals 메소드 구현 방법

1. == 연산자를 사용해 입력이 자기 자신의 참조인지 확인
2. instanceof 연산자로 입력이 올바른 타입인지 확인
3. 입력을 올바른 타입으로 형변환
4. 입력객체와 자기 자신의 대응되는 핵심 필드들이 모두 일치하는지 확인

=> equals 구현 완료시 3가지를 체크해보자
* 대칭성, 추이성, 일관성

---

### equals 메소드 구현시 주의 사항

* hasCode 도 반드시 재정의
* 너무 복잡하게 해결하려하지 말자
  * 필드들의 동치성 검사만 해도 됨
  * File 클래스가 동일한 파일을 가르키는지 확인할 필요가 없음
* Object 타입 외의 매개변수의 equals 메소드를 선언하지말자
* IDE, AutoValue 등의 도구를 이용하자(휴먼 에러 방지)

---

# 핵심 정리

```
꼭 필요한 경우가 아니며 equals 메소드 정의를 하지 말자.
재정의시 핵심필드 를 모두 빠짐없이 비교하며 5가지 일반 규약을 지켜야한다.
```
