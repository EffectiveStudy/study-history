# TL;DR

> Executor Framework를 이용해 작업 실행 메커니즘을 분리하자.

일반적으로, 작업 큐를 수동으로 만들거나 스레드를 직접 다루는 것은 추천하지 않는다.

Thread를 직접 다루면 `작업 단위와 수행 메커니즘 역할` 모두 수행하게 되므로.

따라서, Executor Framework를 이용해 실행 메커니즘을 분리하자.

- 작업 단위를 나타내는 개념 : Task (`Runnable, Callable`)
- Task 수행 메커니즘 : Executor Service

# Q. Executor Framework ?

[ What ]

>  Collection 프레임워크가 데이터 모음을 담당하듯, Executor Framework가 작업 수행 담당.

- `작업 실행 메커니즘과 작업의 관심사를 분리`.

- 조금 더 풀어서 말하면, `작업 등록(Task Submission)과 작업 실행(Task Execution)`을 분리하는 표준 방법.

- 내부적으로 `Producer-Consumer 패턴` 이용.

- 각 작업은 `Runnable`형태로 정의되며, Executore 인터페이스를 구현한 ExecutorService는 작업의 라이프 사이클 관리.

[ 예제 ]

- 일단 코드부터 보자.
```java

public interface Executor {
	void execute(Runnable command):
}

/**  
 *  무제한(Unbounded) 큐에서 작동하는 단일 스레드를 사용하는 실행자 생성. 
 *  작업은 순차적으로 실행되도록 보장. 
 *  하나 이상의 작업이 활성화 되지 않는다. 
 *  실행 중 장애로 단일 스레드 종료시, 새 스레드 생성하여 작업 수행. 
 */
ExecutorService executorService = Executors.newSingleThreadExecutor();  
  
// 실행할 작업 넘기기 (Runnable)  
executorService.execute(() -> System.out.println("hello, executor framework"));  
  
// executor 종료시키기 (종료 안되면 VM 자체가 종료 안됨)  
executorService.shutdown();

```

- Executor를 이용해 간단한 웹서버를 만들어보자 

```java
public class TaskExecutionWebServer {  

    private static final int THREAD_NO = 100;  

    private static final Executor executor = Executors.newFixedThreadPool(THREAD_NO);  
  
    public static void main(String[] args) throws IOException {  
        ServerSocket serverSocket = new ServerSocket(80);  
        while (true) {  
            // blocks until a connection is made.  
            final Socket connection = serverSocket.accept();  
  
            // Task 정의  
            Runnable task = () -> {  
                try {  
                    handleRequest(connection);  
                } catch (IOException e) {  
                    throw new RuntimeException(e);  
                }  
            };  
  
            // Task 수행 임의  
            executor.execute(task);  
        }  
  
    }  
  
    private static void handleRequest(Socket connection) throws IOException {  
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));  
        System.out.println("handle request : " + in.readLine());  
    }  
  
}
```


[ Pros ]

- 관심사 분리를 통해, 작업 등록과 작업 수행을 독립적으로 실행 가능해진다.

- 이를 통해, 원하는 태스크 수행 정책 자유롭게 변경 가능 🌟

	- 수행 정책은 애플리케이션의 성능과 반응 속도가 요구에 따라 자주 바뀔 수 있는데, 이를 손쉽게 반영가능.

- 성능 튜닝, 실행 과정 관리, 모니터링, 로깅, 예외발생시 방법 정의 설정 가능.


[ 제공하는 기능 ]

- 정적 팩토리 이용해 손쉽게 여러 종류의 스레드 풀(ExecutoreService) 생성 가능.

	- 스레드 개수를 고정시키거나 다이나믹하게 필요에 따라 늘리거나 줄어들게 설정 가능.

	- `ThreadPoolExecutor`이용해 커스터마이즈도 가능

- Task 완료되기까지 기다리기(Specific task or all tasks)

- Task Result 차례대로 받기([`ExecutorCompletionService`](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ExecutorCompletionService.html))

- Executor Service가 종료하길 기다리기([`awaitTermination()`](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ExecutorService.html#awaitTermination-long-java.util.concurrent.TimeUnit-))

- Task 주기적으로 실행하기([`ScheduledThreadPoolExecutor`](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ScheduledThreadPoolExecutor.html))

- Fork-Join Task 지원(Java7+)

- ...


[ Tip ]

- `newCachedThreadPool`은 운영환경에서 사용하지 말자.

	- 큐에 쌓이지 않고 즉시 스레드에 위임되어 실행.
	- 가용 스레드 없으면 새로 생성.
	- 즉, 스레드 개수가 고정되지 않아, CPU이용률이 치솟을 확률이 높다.
	- 따라서, `FixedThreadPool` 이용하거나 통제 가능한 `ThreadPoolExecutor` 직접 사용하자.


[ Refer ]

- https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ExecutorService.html


# TMI

## Q. Executors의 정적 팩토리 메서드가 제공하는 대표적인 스레드 풀 종류?

- `newFixedThreadPool` : 
	- 작업 등록시 실제 작업 스레드 하나씩 생성.
	- 생성할수 있는 스레드 최대 개수는 고정.
- newCachedThreadPool :
	- 스레드 수에 제한을 두지 않고 동적으로 새로 생성, 쉬는 스레드 종료.
	- 큐에 쌓이지 않고 즉시 스레드에 위임되어 실행.
	- 가용 스레드 없으면 새로 생성.
	- 즉, 스레드 개수가 고정되지 않아, CPU이용률이 치솟을 확률이 높다.
- `newScheduledThreadPool` :
	- 일정 시간 이후 실행 or 주기적 실행 가능.
	- 스레드 수 고정되어 있는 형태의 `Executor.Timer` 클래스 기능과 유사.

## Q. Task Execution Policy에는 어떠한 것을 고려해야 하나?

👉 무엇을, 어디에서, 언제, 어떻게 실행 할 것인가를 지정.

- 작업을 어느 스레드에서 실행할 것인가?
- 작업을 어떤 순서로 실행할 것인가?
- 동시에 몇개 작업을 병렬로 진행할 것인가?
- 최대 몇개 작업이 큐에서 실행 대기할수 있게 할 것인가?
- 부하로 인해 작업을 거절해야 하는경우, 어떤 작업을 희생시킬것이며, 작업 요청한 프로그램에게는 어떻게 알릴 것인가?
- 작업 실행전후로 어떠한 동작이 필요한가?
- ...

## Q. Producer-Consumer pattern?

[ What ]

- 작업을 생산하는 쪽과, 처리하는 쪽으로 분리 하는 패턴.

[ Pros ]

- 관심사 분리 (예: Producer는 누가 어떻게 얼마나 처리하는지 몰라도 된다)

	- 의존성 감소, SRP, 재사용성, 가독성

- Producer와 Consumer가 서로 독립적으로 실행 가능해진다.

	- **`CPU bound task와 IO bound task를 나누어 독립적으로 실행`하게되면 성능을 향상시킬 수 있다.**

- **손쉽게 각 부분이 감당 가능한 부하 조정 가능**

	- 예) Consumer는 take()를 통해 작업을 가져오는데, 작업이 없으면 "알아서" 대기하기에 구현이 편리하다.

	- 예) Producer는 put()을 통해 작업을 추가하는데, 큐 사이즈 제한이 있다면 "알아서" 대기하기에 구현 편리.

		- 이를 통해, Producer는 Consumer가 소비하는 양보다 많은 양을 만들수 없도록 제한할 수 있다
  
		- 이 때, offer()를 이용하면 조금 더 동적으로 만들 수 도 있다.

			- 부하를 분배 하거나, 작업내용을 임시 영속화 시키거나...

## Q. BlockingQueue?

[ What ]

- `Producer-Consumer패턴` 구현시 유용하게 사용할수 있는 큐

[ How ]

- put(), take()시 원하는 작업을 이룰때까지 대기한다. (사이즈 제한이 있는 경우)
	- put() : 값 추가 가능할 때까지 대기
	- take() : 값을 뽑아 낼때까지 대기
	- offer() : 대기하지 않고, 값을 추가하지 못하면 알려준다.

[ 구현체 ]

- `LinkedBlockingQueue`
- `ArrayBlockingQueue`
- `PriorityBlockingQueue`
- `SynchronousQueue`


## Q. SynchronousQueue?


[ What ]

- BlockingQueue 인터페이스의 특정 구현체

- 큐를 이용하지 않고, 직접 Producer와 Consumer를 관리.

- Producer와 Consumer는, 작업을 주고받을 때까지 대기.

[ Pros ]

- 데이터 넘어가는 시간(순간)이 짧아진다. (큐에 넣어 순차적으로 진행되지 않기에)

- Consumer가 가지고 있는 정보를, Producer가 쉽게 알 수 있다. (예: 어느 Consumer가 작업 진행하는지)

[ 그럼 언제 쓸까? ]

- 충분한 Consumer가 대기중일때.

# Refer

- 자바 병렬 프로그래밍 - 2부 6장
