# 명명 패턴 보다 애너테이션을 사용하라 

---

java 1.5 이전에는 명명패턴을 적용하여 특별히 다뤄야하는 요소를 구분하였음

예시로 junit 3 은 테스트 메소드의 이름을 test 로 시작하여야 했음

단점

* 오타 발생시 문제가 생김
    * tsetSafetyOverride 로 지으면 테스트 통과 
* 올바른 프로그램 요소만 사용되리라 보증 불가
    * TestSafetyMechanisms 으로 지어도 의도한 테스트는 전혀 수행 안됨 
* 프로그램 요소를 매개변수로 전달할 마땅한 방법이 없음  

애너테이션을 사용하면 위 단점을 해결 가능

---

### 핵심정리
> 애너테이션으로 할 수 있는 일을 명명 패턴으로 처리 할 이유는 없음

---

## Tmi

### junit

junit 3 최초 릴리즈 날짜를 찾지 못함

대신 필수 jdk 버전이 1.2+ 따라서 애너테이션이 나온 1.5 버전 이전에 릴리즈 되었을 가능성이 높음

[stackoverflow - Which version of JUnit is compatible with JDK 1.3?](https://stackoverflow.com/questions/19244678/which-version-of-junit-is-compatible-with-jdk-1-3)

[자바 버전 릴리즈 위키](https://en.wikipedia.org/wiki/Java_version_history)

junit4 는 애너테이션 기반으로 수정됨

junit 개발자는 Kent Beck 2000-11-24 에 소스포지에 공개 했다고 함

[Kent Beck님이 트위터에 공유하신 private 메소드 테스트 사이트](http://shoulditestprivatemethods.com)



### 애너테이션

애너테이션 종류

* built-in annotation
  * @Override
  * @Deprecated
  * @SuppressWarning
  * @SafeVarargs
  * @FunctionalInterface
* meta annotation
  * @Retention
  * @Documented
  * @Target
  * @Inherited
  * @Repeatable
* custom annotation


`@Repeatable` java 1.8 에 추가됨


### 명명패턴 적용 사례? (본인의 생각입니다.)

* spring data jpa

```java
@Repository
public interface UserRepository extends JpaRepository< User, Long > {

    List<User> findFirst5ByLastnameAndFirstnameOrderByUserIdDesc(String lastName, String firstName);

    Boolean existsByStartDateLessThanEqual(LocalDateTime startDate);

    long countByFirstnameIgnoreCaseLike(String firstName);
}
```
하나의 명명패턴 아닐까?

* aspectj pointcut expression 을 이름으로 매핑한다면?

```java
execution(* get*(*)) : 이름이 get으로 시작하고 1개의 파라미터를 갖는 메소드 호출
```
에너테이션을 이용하는것이 좋지 않을까?

```java
@annotation(MyTest)
```

---

참고

[위키백과](https://ko.wikipedia.org/wiki/JUnit) 

[JUnit 창시자 Kent Beck이 소개하는 private method 테스트](https://okky.kr/articles/860464)

[Which version of JUnit is compatible with JDK 1.3?](https://stackoverflow.com/questions/19244678/which-version-of-junit-is-compatible-with-jdk-1-3)

[Java Annotation이란?](https://nesoy.github.io/articles/2018-04/Java-Annotation)

[[JPA] Spring Data JPA - Query Methods (쿼리 메서드)](https://kim-oriental.tistory.com/34)

[스프링(Spring) AOP : AspectJ Pointcut 표현식 (Feat. 프로젝트에서 꼭 활용할 내용들)](https://maeryo88.tistory.com/200)

[Chapter 6. Aspect Oriented Programming with Spring](https://docs.spring.io/spring-framework/docs/2.0.x/reference/aop.html)

---

[스터디 발표 소스 repository](https://github.com/EffectiveStudy/leesangho/tree/main/src/main/java/com/github/sangholee/dev/effectivejavastudy/study07_item39)
