# TL;DR

> **`프로그램 동작, Thread.yield(), 스레드 우선순위`에 의존하는 동시성 프로그램을 작성하지 말라.**

Why : 이식성과 견고성을 해치게 되기에.

Tip : `Thread.yield()`, 스레드 우선순위는 스레드 스케쥴러에 제공하는 `힌트`일 뿐이다.



# Q. 왜 스레드 스케쥴러에게 기대면 안되죠?

> **스레드 스케줄링 정책은 OS마다 달라지기에.**

OS의 스레드 스케쥴러는, 어떤 스레드를 얼마나 실행할지 정하는데, 이 정책은 OS마다 상이할 수 있다.

따라서, **정확성이나 성능이 OS 스레드 스케줄러에 따라 달라지는 프로그램**이라면, 다른 OS에 이식하기 어려워 진다.



# Q. 그럼 어떻게 이식성 좋은 프로그램 작성하죠?

최대한 스케줄링이 달라져도 일관된 방법으로 처리할수 있도록 사용.

> **`실행 가능한 스레드의 평균 수`를, 프로세서 수보다 지나치게 많도록 하지 않기**.

>  실행 준비된 스레드는 작업 완료시 까지 계속 실행되도록 하기.

이렇게 하면, 스레드 스케쥴링 정책이 다른 OS에서도 동작이 크게 달라지지 않는다.

스레드 개수가 CPU코어 수보다 많을 경우 스레드 스케쥴링 작업이 발생하기에.

Tip : 실행 가능한 스레드 수와, 전체 스레드 수는 구분해야 한다.



# Q. 그럼, `실행 가능한 스레드 수`를 어떻게 적게 유지하죠?

> 스레드 풀 크기를 적절히 설정하고, 해당 스레드 작업은 적절히 짧게 유지하자.

>  스레드 작업 완료 후에는 다음 작업이 생길때 까지 대기하도록 하자.

### 주의점 : 

- 스레드 작업이 너무 짧으면 작업 분배하는 부담이 오히려 성능을 떨어트릴 수 있다.
- 스레드가 대기 할때는 BUSY-WAIT은 아니된다.


# TMI


# Q. Thread.yield()?

> 다른 스레드에게 실행을 양보하는 스레드 상태 제어 방법중 하나.

해당 메서드를 호출한 스레드는 `RUNNABLE` 상태로 돌아가며, 

동일한 또는 상위 우선순위를 갖는 다른 스레드가 실행 기회를 가질수 있도록 한다.

이러한 우선순위 제어 메서드는 플랫폼 종속적이기에 되도록 쓰지 않는 것이 바람직하다고 한다.
(`Thread.yield()`, `Thread.sleep()``,...)


# Q. 스레드가 가질 수 있는 상태 ?

> NEW , RUNNABLE, RUNNING, WAITING(WAITING, TIME_WAITING, BLOCKED), TERMINATE

- RUNNABLE : 아직 스케줄링이 되지 않아 실행 대기 상태.
- RUNNING  : CPU할당받고 `run()` 실행 상태.
- WAITING  
	- WAITING      :  다른스레드가 notify 할 때까지 기다리는 상태.
	- TIME_WAITING :  주어진 시간동안 기다리는 상태.
	- BLOCKED      :  이용할 객체 락이 풀릴 때까지 기다리는 상태.



# Q. 스레드 우선순위?

[ What ]
-  자바 스레드API가 제공하는 우선순위 개념은, 단지 스레드 스케쥴링에 힌트를 제공.
-  자바 스레드API는 10단계 스레드 우선순위 지정 가능.

```java

from : Thread class
/**  
* The minimum priority that a thread can have.  
*/ 
public static final int MIN_PRIORITY = 1;  
  
/**  
* The default priority that is assigned to a thread.  
*/ 
public static final int NORM_PRIORITY = 5;  
  
/**  
* The maximum priority that a thread can have.  
*/ 
public static final int MAX_PRIORITY = 10;
```

[ 우선순위 지정하면 어떻게 되죠? ]
- JVM은 지정된 우선순위를 OS의 스케줄링 우선순위에 적절히 대응시키는 정도로만 사용.
- 이 대응시키는 기능이 OS마다 다르게 적용된다.
- 두개의 다른 우선순위가 같은 값으로 지정 될수도, 10보다 작은 개수의 우선순위를 제공하는 경우도 있다.

[ Best Practice ]
- 이런 이유로 대부분의 자바 어플리케이션은 모든 스레드를 같은 우선순위(NORM_PRIORITY)로 동작시킨다.
- 따라서, 일반적인 상황에서는 스레드 우선순위를 변경하지 않고 그대로 사용하는 것이 가장 현명한 방법이다.

[ 우선순위 조절시의 리스크 ]
 - 우선순위를 조절하기 시작하는 순간, OS마다 그 실행모습이 달라 질 것이고, Starvation 문제가 발생할 리스크도 생기게 된다.

# Q. busy wait vs guarded wait

[ guarded wait ]
- Object클래스의 `wait(), notify(), notifyAll()`이용한 방법.
- notify수행전까지 lock 해제하고 수행 중지하기에 리소스 사용 X
```java
// 큐에 아이템이 들어올때까지 대기.
synchronized Request getRequest() {
	while (queue.peek() == null) {
		try {
			wait();
		} catch (InterruptedException e) {
		}
	}
	return queue.remove();
}
```

[busy wait]
- `Thread.yield()`등으로 조건을 테스트하며 특정 조건이 만족할때까지 리소스를 사용하며 기다리는 방법
```java

while (!ready) {
	Thread.yield();
}

...
// In other thread
ready = true;
```
