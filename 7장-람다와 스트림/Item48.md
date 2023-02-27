# Summary
`계산 정확도` 및 `성능 개선 검증`이 된후에만! 스트림 파이프라인 병렬화를 진행하라.

# Why?
- Java8이후, 동시성 프로그램을 작성하기 쉬워졌다.
- `parallel` 메서드만 한번 호출하면 알아서 파이프라인을 Fork-Join프레임워크를 이용해 병렬화.
- 하지만, 여전히 올바르고 빠르게 작성하는 일은 여전히 어렵다.

- 즉, 손쉬운 병렬화가 가능해졌지만,  동시성 프로그래밍시 주의해야 하는 `안정성(safety)`과, `응답성(liveness)`은 여전히 고려해야 한다.

# Bad Example : 메르센 소수 찾기
새로운 메르센 소수를 찾을때마다, 그 전 소수를 찾을때보다 2배 오래 더 걸린다.
즉, 원소 하나 계산 비용이, 그 이전까지의 원소 전부를 계산한 비용을 합친것 만큼 든다.


## 순차 스트림
```java
static void sequentialStream() {  
    primes().map(p -> TWO.pow(p.intValueExact()).subtract(ONE))  
            .filter(mersenne -> mersenne.isProbablePrime(50))  
            .limit(20)  
            .forEach(System.out::println);  
}
```
Result : Runs in 6.5s

## 병렬 스트림
```java
static Stream<BigInteger> primes() {  
    return Stream.iterate(TWO, BigInteger::nextProbablePrime);  
}
static void parallelStream() {  
    primes().map(p -> TWO.pow(p.intValueExact()).subtract(ONE))  
            .parallel()   // 손쉬운 병렬화
            .filter(mersenne -> mersenne.isProbablePrime(50))  
            .limit(20)  
            .forEach(System.out::println);  
}
```
Result : Very, very slowly. I gave up after half an hour.

## 왜 병렬 스트림이 느릴까?
위 예제에서는 다음과 같은 문제점이 존재.

- Stream라이브러리가 어떻게 병렬화 할지 찾기 못함(!!)
- 데이터 소스가, `Stream.iterate` 일 경우, 병렬화로는 성능 개선 안됨.
	- Why? 무한스트림을 병렬화할경우, 어떻게 작업을 split evenly할지 예측을 할 수 없다.
- 중간 연산이 `limit()`일 경우, 병렬화로는 성능 개선 안됨.
	- 특히 limit의 경우, 요소의 순서에 의존하며, 원소를 몇개 더 처리한후, 제한된 개수이후의 결과는 버릴수도 있음.
 

# 그럼 어떨때 병렬스트림을 쓸 수 있죠?
병렬처리 및 동시성 제어로 인한 오버헤드를 고려해야 하기에, 다음과 같은 사항을 고려하자.

## 1. 데이터 소스 원소 개수(N)가 많으면 좋다
- `소스 데이터 원소 개수 N`이 충분히 많아야 한다. (최소 `만개` 이상)

## 2. 각 원소마다 수행할 함수 연산(Q) 비용이 비싸면 좋다
- 각 원소마다 수행할 `연산 비용 Q`이 충분히 비싸야 한다.
- 즉 이 연산 비용이 병렬화에 드는 추가 비용을 상쇄하지 못한다면 성능향상은 미미.

## 3. 스트림 구성 자료구조 : 쉽게 split하며 참조지역성이 뛰어나면 좋다
- 내부적으로 Fork-join 프레임워크를 이용하기에, `쉽게 split` 가능하며 `참조지역성`이 뛰어난 `자료구조` 여야한다.
- 나누는 작업은 `Spliterator`가 담당. Stream이나 Iterable의 `spliterator()`로 획득가능.

### Good Example
`Array, ArrayList, HashMap, HashSet, range`
- 데이터를 원하는 크기로 나누어 다수의 스레드로 손쉽게 나눌수 있다.
- 이웃한 원소의 참조들이 메모리에 연속되어 저장되어있기에 참조지역성이 뛰어나다.
- Custom Spliterator를 구현할수 도 있다.

### Bad Example
`LinkedList, Stream.iterate`
- LinkedList : split하기 위해선 모든 요소를 탐색해야 하기에.
- Stream.iterate : 무한스트림일 경우 어떻게 split해야 할지 추론이 어려우며, iterate는 본질적으로 순차적이다.(이전 연산 결과에 따라 다음 함수 입력이 달라지기에)

## 4. 중간연산 : 요소의 순서에 의존하지 않아야 한다

### Good Example
`findAny, allMatch, anyMatch, nonMatch`
- 요소 순서에 의존하지 않고, 조건에 맞으면 바로 반환

### Bad Example
`findFirst, limit`
- 요소 순서에 의존


## 5. 종단연산 : 병합과정이 싸야 한다
-  `쉽게 결과를 merge가능한` `종단 연산` 이여야 한다.

### Good Example
- `reduce`
- 모든 원소를 재귀 적으로 하나로 합칠수 있다.

### Bad Example
- `grouping, collect`
- 가변 축소를 수행하는 메서드는 병렬화에 적합하지 않다.

##  6. 연산 : 공유된 가변 상태를 피하라.
- parallelStream()은 스레드 안전성을 제공하지 않기에, 동시성 제어와 같은 추가비용이 들어간다.

## 7. 병렬 스트림 파이프라인은 공통의 포크-조인 풀에서 수행되는 것을 고려해야 한다.
- 잘못된 파이프라인 하나가 시스템의 다른 부분의 성능에까지 악영향을 줄수 있음을 유의.
 
## 8. Boxing의 오버헤드도 고려해야 한다
- 기본형 특화 스트림을 되도록 이용하라.(`IntStream, LongStream, DoubleStream`)


# Good Example : n보다 작거나 같은 소수 개수 구하기

## 순차 스트림
```java
static long findPiBySequentialStream(long n) {  
    return LongStream.rangeClosed(2, n)  
            .mapToObj(BigInteger::valueOf)  
            .filter(i -> i.isProbablePrime(50))  
            .count();  
}
```
Result : n = 10^8일경우, 31s

## 병렬 스트림
- 무한 스트림 사용 X
	- 얼마나 많은 원소들을 작업할지 컴파일러가 인식하여, 적절히 split 가능.
- LongStream을 이용하여 불필요한 박싱, 언박싱 회피
```java
static long findPiByParallelStream(long n) {  
    return LongStream.rangeClosed(2, n)  
		    .parallel()   // 손쉬운 병렬화
            .mapToObj(BigInteger::valueOf)  
            .filter(i -> i.isProbablePrime(50))  
            .count();  
}
```
Result : n = 10^8일경우, 9.2s


# Reference
- https://www.infoq.com/presentations/effective-java-third-edition/
- https://stackoverflow.com/questions/30825708/java-8-using-parallel-in-a-stream-causes-oom-error
- 모던자바인액션7장

# Next
- 모던자바인액션7장 참고하여 스트림 벤치마킹 테스트
