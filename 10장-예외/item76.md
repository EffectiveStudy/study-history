# Item76 - 가능한 실패원자적으로 만들라

# TL;DR

[ What? ] 
- 호출된 메서드가 실패하더라도(예외 발생하였을지라도), 해당 객체는 메서드 `호출 전 상태`를 유지하도록 하자.

[ Why? ] 
- 이를 통해, 어렵게 코드로 메서드 호출 전 상태로 돌리거나, 실패가 전파되지 않는 장점이 있다.

[ How? ] 
1. 불변 객체로 만들기
2. 객체 내부 상태 변경 전, 유효성 검사
3. 임시 복사본을 이용해, 작업 완료한 경우에 객체 바꿔치는 방법.
4. 실패하였을 경우, 코드로 작업 전 상태로 돌리는 방법.


# Q. 실패원자성이 무죠?

Failure-Atomic.

> 호출된 메서드가 실패하더라도, 해당 객체는 메서드 호출 전 상태를 유지하는 것.

- 보통 메서드 명세에 기술한 예외라면 `실패원자성`을 지켜야 하는 것이 기본 규칙.
- 이를 지키지 못한다면 실패 시 객체의 상태가 어떻게 변할 것인지를 명시해야 한다.


# Q. HOW 메서드를 실패 원자적으로 만드는 방법?

## 1) 불변객체로 만들기

애초에 생성시점에 상태가 고정되어 변하지 않으니, 

예외가 발생하였다고 하더라도 객체가 불안정한 상태에 빠지는 일이 없다.


## 2) 작업 수행 전에 유효성 검사

객체 내부 상태 변경`전`에, 잠재적인 예외의 가능성을 걸러 내는 방법.

예 : Stack.pop()

```java
public Object pop() {
	if (size == 0) throw new EmptyStackException();
	Object result = elements[--size];
	elements[size] = null; // lotering 방지
	return result;
}
```

- 객체의 내부 상태 변경 전에, 스택이 비어 있는 경우, `EmptyStackException`예외를 던지고 있다.
- 이로써, 예외가 발생하더라도 실제 객체 내부 상태(size)는 변하지 않는다.
- 즉, 실패 원자성을 갖는 메서드.


비슷한 방법이 하나 더 있다.

바로, 객체 상태 변경 `전`에, `실패할 가능성이 있는 모든 코드를 실행` 하는 방법이다.

예 : TreeMap
- TreeMap은 `Comparator order`로 원소를 정렬.
- 따라서 원소 추가시, 해당 원소는 TreeMap 기준에 따라 비교할 수 있는 타입 이어야 한다.
- 만약 비교할 수 없는 타입을 TreeMap에 추가하려고 하면, 실제 트리 변경 전(객체 상태 변경 전)에, 해당 원소가 들어갈 위치를 찾는 과정에서 예외(`ClassCastException`)를 던진다.

```java
// TreeMap의 put 메서드
public V put(K key, V value) {  

	// 생략 ...
	    
    int cmp;  
    Entry<K,V> parent;  
    Comparator<? super K> cpr = comparator;  
    if (cpr != null) {  
        do {  
            parent = t;  
            cmp = cpr.compare(key, t.key);  // Comparator 존재시, 추가 전에 해당 원소 들어갈 위치를 Comparator를 이용해 찾는다.
            if (cmp < 0)  
                t = t.left;  
            else if (cmp > 0)  
                t = t.right;  
            else  
                return t.setValue(value);  
        } while (t != null);  
    }  
    else {  
		// 생략
    }  

	// 해당 원소 들어갈 위치를 찾은 후, 엔트리 생성
    Entry<K,V> e = new Entry<>(key, value, parent);  
    
    if (cmp < 0)  
        parent.left = e;  
    else  
        parent.right = e;  
    
    // RBT를 이용해 재 정렬
    fixAfterInsertion(e);  
    
	// 생략...
	
    return null;  
}

```


## 3) 임시 복사본 이용하기

임시 본사본에서 작업 수행하고, `작업이 성공하였을 경우에만 원래 객체와 교체`하기.

예 : `Collections.sort`
```java
@SuppressWarnings({"unchecked", "rawtypes"})  
default void sort(Comparator<? super E> c) { 

	// 실제 정렬 전에 입력 원소들을 배열로 옮겨 담기.
    Object[] a = this.toArray();  
    
    // 임시 복사본(배열)을 이용해 작업하기에, 정렬 실패해도 객체는 호출 전 상태를 유지.
    Arrays.sort(a, (Comparator) c);  
    
	// 정렬 작업이 성공적으로 끝나면 리스트로 원소 옮기기.
    ListIterator<E> i = this.listIterator();  
    for (Object e : a) {  
        i.next();  
        i.set((E) e);  
    }  
}
```

## 4) 작업 실패시 복구 코드를 작성하며 작업 전 상태로 돌리기

- 디스크 기반의 Durability 보장하는 케이스에 쓰이는데 자주 쓰이진 않는다.
- 그냥 이런 기법도 있구나 하는 정도로 알아 두면 좋을 듯 하다.


# Q. 실패원자성을 항상 달성할 수 있을까?

NO; 권장되는 것은 맞지만 항상 달성할 수는 없다. 

예를 들어,

- What if `ConcurrentModificationException` occurs?
- What if `Error` occurs?
- What if `AssertionError` occurs?


# Q. 실패원자성을 항상 달성해야 할까?

TradeOff; 만약 실패 원자성을 달성하기 위한 COST 또는 COMPLEXITY가 큰 연산이라면 고려해야겠죠?




