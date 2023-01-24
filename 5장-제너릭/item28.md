# 배열보다는 리스트를 사용하라

---

배열 과 리스트의 차이

| 배열    | 리스트       |
|-------|-----------|
| 공변    | 불공변       |
| 실체화   | 소거        |

> 공변: 같이 변함 (상위,하위 타입이 적용됨)

> 불공변: 변하지 않음 (서로 상위,하위 타입이 적용되지 않음)

> 실체화: 런타임에도 자신이 담기로한 원소의 타입을 인지 및 확인

> 타입 소거: 컴파일 타임에만 검사하고 런타임에는 해당 타입정보를 알수 없는 것
---
 
* 제네릭 배열 생성 불가 => 제네릭을 사용했지만 타입이 맞지 않는 경우를 미연에 방지
* 제네릭은 실체화 불가 타입

---

### 핵심정리

> 배열은 런타임시 안전하며 제네릭은 컴파일 타임에 안전하다
> 
> 컴파일 오류나 경고가 있다면 배열을 리스트로 교체하자 

---

참고

* [[제네릭] Java에서 배열을 공변(covariant)으로 만든 이유는 무엇 ...](https://hwan33.tistory.com/24)
* [자바의 제네릭 타입 소거, 리스트에 관하여 (Java Generics Type Erasure, List)](https://jyami.tistory.com/99)
* [Java의 제네릭 타입 소거법과 Kotlin의 reified](https://velog.io/@eastperson/Java의-제네릭-타입-소거법과-Kotlin의-reified)

---

[스터디 발표 소스 repository](https://github.com/EffectiveStudy/leesangho/blob/main/src/main/java/com/github/sangholee/dev/effectivejavastudy/study05_item28)
