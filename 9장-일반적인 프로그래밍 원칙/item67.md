# 최적화는 신중히 하라

---

### 최적화 격언

> 맹목적인 어리석음을 포함해 그 어떤 핑계보다 효율성이라는 이름 아래 행해진 컴퓨팅 죄악이 더 많다 (심지어 효율을 높이지도 못하면서)
> 
> 윌리엄 울프(Wulf72)

> (전체의 97% 정도인) 자그마한 효율성은 모두 잊자. 섣부른 최적화가 만약의 근원이다. 
> 
> 도널드 크누스(Knuth74)

> 최적화를 할 때는 다음 두 규칙을 따르라.
>
> 첫 번째, 하지마라.
> 두 번째, (전문가 한정) 아직 하지 마라. 다시 말해, 완전히 명백하고 최적화되지 않은 해법을 찾을 때까지는 하지 마라.
> 
> M.A 잭슨 (Jackson75)

자바 탄생 20년 전에 격언으로 최적화는 좋은결과 보다 안좋은 결과로 이어지기 쉽고
수정하기 더 어려운 소프트웨어를 탄생시킴

`빠른 프로그램보다 좋은 프로그램을 작성하라`

성능 때문에 구조를 희생 X 

좋은 프로그램은 정보은닉 원칙에 의한 개별 구성 요소를 독립적으로 설계할 수 있어야함

구현상의 문제는 나중에도 최적화가 가능하지만 아키텍처의 결함이 성능을 제한하는 경우는
시스템 전체를 수정해야함

따라서 프로그램완성까지 성능문제를 무시하는게 아닌 설계단계부터 성능을 염두해야함

`성능을 제한하는 설계 피하라`

변경하기 어려운 설계요소 중 하나는 컴포넌트간 혹은 외부 시스템과 소통하는 것
이러한 부분은 완성후 변경이 어려울 수 있어 시스템 성능을 제한 X

`API 설계시 성능에 주는 영향을 고려하라`

1. `public` 타입을 가변으로 만들어 내부 데이터가 변경 가능했던
[item50](https://github.com/EffectiveStudy/study-history/blob/main/8%EC%9E%A5-%EB%A9%94%EC%84%9C%EB%93%9C/item50.md) 을 예시로를 들면
불필요한 방어적 복사를 계속해야함
[item16](https://github.com/EffectiveStudy/leesangho/blob/main/src/test/java/com/github/sangholee/dev/effectivejavastudy/study03_item16/Item16Test.java)


2. 컴포지션으로 해결 할수 있었지만 상속으로 설계했던
[item18](https://github.com/EffectiveStudy/study-history/blob/main/4%EC%9E%A5-%ED%81%B4%EB%9E%98%EC%8A%A4%EC%99%80%20%EC%9D%B8%ED%84%B0%ED%8E%98%EC%9D%B4%EC%8A%A4/item18.java) 를 예시로 들면
상속의 특성대로 상위 클래스에 종속되며 성능 제약까지 물려받음


3. 인터페이스가 있는데 구현타입을 사용한 
[item64]() 를 예시로 들면 특정구현체에 종속되며 더 빠른 구현헤가 나오더라도 이용하지 못함


`성능을 위해 API 왜곡하는 건 매우 좋지 않다`

왜곡된 API 는 당장은 개선의 효과를 가져다 줄수 있지만 향후 버저닝에서 계속 영향을 줄 수 있다.

`각각의 최적화 시도 전후로 성능을 측정하라`

프로파일링 도구를 통해 최적화에 집중할 곳을 찾자

시스템 규모가 커질수록 프로파일러가 더 중요함

`JProfiler`, `VisualVM`, `YourKit`, `Java Mission Control`, `NetBeans Profiler`
등이 있음

`jmh` 와 같은 벤치마킹 프레임워크를 사용하는 것도 좋음


---

### 핵심정리

> 좋은 프로그램을 작성하다보면 성능은 자연스래 따라옴
> 
> 시스템 설계시 성능을 염두해야함
> 
> 시스템 구현 완료후 성능 측정해봐야함
> 
> 프로파일러를 통해 원인을 찾아 최적화 해야함
> 
> 만족할때까지 과정 반복, 성능 측정


---

[스터디 발표 소스 repository](https://github.com/EffectiveStudy/leesangho/tree/main/src/main/java/com/github/sangholee/dev/effectivejavastudy/study13_item67)
