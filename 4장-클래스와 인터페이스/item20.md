# 추상 클래스보다는 인터페이스를 우선하라

---

자바 8 이후로 인터페이스도 디폴트 메소드 제공

자바는 단일 상속만 지원

---

### 장점

* 믹스인 정의에 안성맞춤

* 인터페이스는 계층구조가 없는 타입 프레임워크

* 인터페이스 기능 향상 시키는 안전하고 강력한 수단

추상 골격구현 클래스 제공
> Object 의 메소드는 디폴트 메소드로 제공하지말자

템플릿 메소드 패턴

> 부모클래스에서 골격을 정의하지만 해당 알고리즘 구조를 변경하지 않고 자식클래스에서 특정 알고리즘을 재정의 할 수 있도록 함

> 장점: 중복코드 제거, 대부분의 알고리즘의 영향을 덜받게 구현 가능
> 
> 단점: 템플릿 메소드가 많으면 복잡해짐, 리스코프치환 원칙 위배할 수 있음

단순 구현 
> 추상클래스가 아닌 구체클래스로 인터페이스 구현

---

### 핵심 정리

> 일반적으로 다중 구현용 타입으로는 인터페이스가 적합
> 
> 복잡한 인터페이스라면 골격 구현을 같이 제공
> 
> 골격 구현은 가능한 인터페이스에 디폴트 메소드로 제공 
> 
> 구현 제약 떄문이라면 추상 클래스로 제공

---

참고

[[Design Pattern] 템플릿 메소드 패턴(Template Method Pattern)에 대하여](https://coding-factory.tistory.com/712)

[템플릿 메서드 패턴](https://refactoring.guru/ko/design-patterns/template-method)


---

[스터디 발표 소스 repository](https://github.com/EffectiveStudy/leesangho/tree/main/src/main/java/com/github/sangholee/dev/effectivejavastudy/study04_item20)
