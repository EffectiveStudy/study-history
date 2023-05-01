# wait 와 notify 보다는 동시성 유틸리티를 애용하라

---

`wait` 메서드 => 다른 스레드에서 `notify` 또는 `notifyAll` 호출 될때까지 대기

`notify` 메서드 => 다른 대기중인 임의 스레드 꺠움

`notifyAll` 메서드 => 대기중인 모든 스레드 깨움


java5 에서 고수준 동시성 유틸리티가 추가되어 

`wait`,`notify` 를 사용할 일이 많이 줄어듬

`wait`,`notify` 는 올바르게 사용하기 어려워 동시성 유틸리티를 사용하는 것이 좋음


`java.util.concurrent` 범주

1. 실행자 프레임워크
    `item80` 참고
2. 동시성 컬렉션
    
    동기화 컬렉션은 표준 컬렉션 인터페이스에 동시성처리를 추가 구현한 컬렉션 
    
    동기화를 내부에서 수행 -> 동시성 무력화 불가능 
   
    `Collections.synchronizedMap` 보다 `ConcurrentMap` 을 사용하는게 좋음
    

3. 동기화 장치

   `CountDownLatch`, `Semaphore`, `Phaser`등의 동기화 장치가 있음
    
    동기화 장치는 다른 스레드를 대기하여 서로의 작업 조율 가능

    새로운 코드라면 `wait` 과 `notify` 보다는 동시성 유틸리티를 쓰자
    

### wait 메서드를 사용하는 표준 방식

```java
synchronized (obj) {
   while (<조건이 충족되지 않았다>) {
       // 락을 놓고 꺠어나면 다시 잡는다
       obj.wait();
   }
   // 조건이 충족됐을 때의 동작을 수행한다
}
```

wait 메서드 사용시 반드시 대기 반복문 관용구 사용, 반복문 밖에서 호출 X


일반적으로 `notifyAll` 을 쓰는게 합리적 
만약 악의적으로 `notify` 를 호출하는 경우 영원히 대기하는 상황이 생김을 대비


---

### 핵심정리

> `wait` 과 `notify` 를 직접 사용하는 것은 마치 저수준 언어를 사용하는 것 
> 
> `java.util.concurrent` 를 사용하는 것은 고수준 언어를 사용하는 것 과 같음
> 
> 새로 작성할 경우는 쓰지 말고 레거시코드를 유지보수해야 하느 경우는 `while` 문 안에서 호출
> 
> `notify` 보다 `notifyAll` 을 사용하는게 일반적

---

[스터디 발표 소스 repository](https://github.com/EffectiveStudy/leesangho/tree/main/src/main/java/com/github/sangholee/dev/effectivejavastudy/study16_item81)
