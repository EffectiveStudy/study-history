## item 82 - 스레드 안전성 수준을 문서화 하라

* 스레드 안정성 문서화



클래스를 개발할 때, 해당 클래스를 사용할 클라이언트들을 위하여 필요한 정보들을 주석으로 작성하여 문서화 해야한다.

아무런 설명이 없다면 클라이언트는 추측과 가정을 통해 그 클래스를 사용하게 된다.

특히 스레드 안정성에 대한 정보는 설명이 필요한 중요한 정보 중 하나이다.

스레드 안정성이 잘못되면 프로그램에 심각한 오류가 발생할 것이다.


---

//외부락과 동시성을 같이 사용하면, 성능 상의 문제점이 있다. (앗...)
// 동시성은 짜피 무력화할 수 없기에, 둘중 선택을 하라하면 동시성인가/!?!?!?


* 스레드 안정성 수준



불변(immutable): 이 클래스의 인스턴스는 마치 상수와 같아서 외부 동기화가 필요없다.(@Immutable)

무조건적 스레드 안전(unconditionally thread-safe): 이 클래스의 인스턴스는 수정될 수 있으나, 내부에서 충실히 동기화하여 별도의 외부 동기화 없이 동시에 사용해도 안전하다.
(@ThreadSafe)

조건부 스레드 안전(conditionally thread-safe): 무조건적 스레드 안전과 같으나, 일부 메서드는 동시에 사용하려면 외부 동기화가 필요하다.
(@ThreadSafe)


스레드 안전하지 않음(not thread-safe): 이 클래스의 인스턴스는 수정될 수 있다. 동시에 사용하려면 각각의 메서드 호출을 클라이언트가 선택한 외부 동기화 메커니즘으로 감싸야한다.

스레드 적대적(thread-hostil): 이 클래스는 모든 메서드 호출을 외부 동기화로 감싸더라도 멀티스레드 환경에서 안전하지 않다.
 
-----


이중 조건부 스레드 안전은 주의해서, 문서화해야한다.

어떤 순서로 호출할 때 외부 동기화가 필요한지 , 그리고 그 순서로 호출하려면 어떤 락을 얻어야하는지 알려줘야한다.

예시 ) Collections.synchronizedMap이 반환한 맵의 컬렉션 뷰를 순회하려면 그 맵을 락으로 사용해 수동으로 동기화하라

    /**
     * Returns a synchronized (thread-safe) map backed by the specified
     * map.  In order to guarantee serial access, it is critical that
     * <strong>all</strong> access to the backing map is accomplished
     * through the returned map.<p>
     *
     * It is imperative that the user manually synchronize on the returned
     * map when traversing any of its collection views via {@link Iterator},
     * {@link Spliterator} or {@link Stream}: //요기
     * <pre>
     *  Map m = Collections.synchronizedMap(new HashMap());
     *      ...
     *  Set s = m.keySet();  // Needn't be in synchronized block  // 동기화 블록 밖에 있어도 된다
     *      ...
     *  synchronized (m) {  // Synchronizing on m, not s! // s가 아닌 m으로 동기화한다.
     *      Iterator i = s.iterator(); // Must be in synchronized block
     *      while (i.hasNext())
     *          foo(i.next());
     *  }
     * </pre>
     * Failure to follow this advice may result in non-deterministic behavior.
     *
     * <p>The returned map will be serializable if the specified map is
     * serializable.
     *
     * @param <K> the class of the map keys
     * @param <V> the class of the map values
     * @param  m the map to be "wrapped" in a synchronized map.
     * @return a synchronized view of the specified map.
     */


독특한 특성의 메서드라면, 해당 메서드의 주석에 기재하도록 하자.

-----

클래스가 외부에 사용할 수 있는 락을 제공하면,
클라이언트에서 일련의 메서드 호출을 원자적으로 진행 가능하다.

하지만 이 유연성에는 대가가 따른다.
* 사용자가 서비스 거부 공격
* 동시성 컬렉션에는 적용 불가

---
IN 무조건 스레드 안전 클래스

그리하여,서비스 공격을 막으려면, 메서드 대신 비공개 락 객체를 사용해야한다.
이는 락 객체를 동기화 대상 안으로 캡술화 한 것이다. + 상속관계에서 좋은 선택이다.

TIP ) 락은 꼭 final로 선언할 것
```java
private final Object lock = new Object();

public void foo(){
    synchronzied(lock){
        
    }
    }
```

----

정리하자면,

* 조건부 스레드 안전 환경 : 어떤 순서로 호출할 때 외부 동기화가 요구되고 어떤 락을 얻어야 하는지 알려주기
* 무조건적 스레드 안전환경 : 비공개 락 객체 사용하기
* synchronized는 문서화와 관련 없음.
