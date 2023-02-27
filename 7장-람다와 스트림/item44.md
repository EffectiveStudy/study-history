# 표준 함수형 인터페이스를 사용하라

---

자바가 람다를 지원하면서 API 트랜드도 변함

템플릿 메소드 패턴 함수객체를 받는 정적 팩토리나 생성자를 제공

---

### 템플릿 메소드 패턴은?

> 템플릿 메소드 패턴(template method pattern)은 소프트웨어 공학에서 동작 상의 알고리즘의 프로그램 뼈대를 정의하는 행위 디자인 패턴이다. 알고리즘의 구조를 변경하지 않고 알고리즘의 특정 단계들을 다시 정의할 수 있게 해준다.

![템플릿 메소드 패턴 이미지](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2F8iyLF%2FbtqZpfdHFNT%2FEJCpNFiBBWNkcKlohpPU5k%2Fimg.png)

```java
//추상 클래스 선생님
abstract class Teacher{
	
    public void start_class() {
        inside();
        attendance();
        teach();
        outside();
    }
	
    // 공통 메서드
    public void inside() {
        System.out.println("선생님이 강의실로 들어옵니다.");
    }
    
    public void attendance() {
        System.out.println("선생님이 출석을 부릅니다.");
    }
    
    public void outside() {
        System.out.println("선생님이 강의실을 나갑니다.");
    }
    
    // 추상 메서드
    abstract void teach();
}
 
// 국어 선생님
class Korean_Teacher extends Teacher{
    
    @Override
    public void teach() {
        System.out.println("선생님이 국어를 수업합니다.");
    }
}
 
//수학 선생님
class Math_Teacher extends Teacher{

    @Override
    public void teach() {
        System.out.println("선생님이 수학을 수업합니다.");
    }
}

//영어 선생님
class English_Teacher extends Teacher{

    @Override
    public void teach() {
        System.out.println("선생님이 영어를 수업합니다.");
    }
}
```

람다로 대체가 쉬움

---


### 기본형 함수형 인터페이스

| 인터페이스              | 함수 시그니처      | 예               
|--------------------|--------------|---------------------|
| UnaryOperator\<T>  | T apply(T t) | String::toLowerCase 
| BinaryOperator\<T> | T apply(T t1, T t2) | BigInteger::add
| Predicate\<T>      | boolean test(T t) | Collection::isEmpty
| Function\<T, R>    | R apply(T t) | Arrays::asList
| Supplier\<T>       | T get() | Instant::now
| Consumer\<T>       | void accept(T t) | System.out::println

`Function` 은 인수 -> 반환

`Operator` 인수 -> 인수와 같은 타입 반환

`Predicate` 인수 -> boolean 반환

`Supplier` 인수 없음 -> 반환

`Consumer` 인수 -> 반환 없음

기본 타입에 따라 long, int, double 의 변형

예시) IntPredicate, LongBinaryOperator

`Function` 의 경우는 {Source}To{Result} 의 접두어를 통해 종류가 6개

그리고 long, int, double 자료형의 변형을 매개변수를 To{Result} 처리하는 종류 3개

예시) LongToIntFunction, ToIntFunction<long[]>

기본 함수형 인터페이스에서는 인수를 2개 받는 변형이 있음

예시) BiPredicate<T,U>, BiFunction<T,U,R>, ObjDoubleConsumer\<T>


총 표준 함수형 인터페이스가 43개 존재

### -> 모두 외우기 힘듬, 필요할떄 찾아쓰자


---

기본 함수형 인터페이스 사용할때 박싱된 기본 타입을 사용하지 말자

대부분의 경우 직접 작성하는 것보다 표준 함수형 인터페이스를 사용하는 것이 나음

직접 작성해야하는 경우는 `Comparator<T>` 인터페이스를 생각하면 됨

이중 하나 이상을 만족한다면 직접 구현해야 할지 고민해야함

* 자주 쓰이고 이름 자체가 용도를 명확히 설명
* 반드시 따라야하는 규약이 있음
* 유용한 디폴드 메소드 제공

### 직접 만든 함수혐 인터페이스는 @FunctionInterface 를 사용하자

그 이유는
1. 코드나 문서를 읽을때 함수형 인터페이스임을 바로 알게됨
2. 추상 메소드를 오직 하나만 가질수 있음
3. 유지보수시 실수로 메소드 추가를 할수 없음

---

### 핵심정리

> API 설계시 람다를 염두
> 
> 표준 함수형 인터페이스를 사용하는 것이 가장 좋은 선택
> 
> 직접 새로운 함수형인터페스를 정의하는 경우가 나은 경우도 있음


---

참고

[위키백과](https://ko.wikipedia.org/wiki/%ED%85%9C%ED%94%8C%EB%A6%BF_%EB%A9%94%EC%86%8C%EB%93%9C_%ED%8C%A8%ED%84%B4)

[[Design Pattern] 템플릿 메소드 패턴(Template Method Pattern)에 대하여](https://coding-factory.tistory.com/712)

---

[스터디 발표 소스 repository](https://github.com/EffectiveStudy/leesangho/tree/main/src/main/java/com/github/sangholee/dev/effectivejavastudy/study09_item44)
