# ITEM 7 . 다 쓴 객체 참조를 해제하라

```java
public class Stack {
    private Object[] elements;
    private int size = 0;
    private static final int DEFAULT_INITIAL_CAPACITY = 16;

    public Stack() {
        elements = new Object[DEFAULT_INITIAL_CAPACITY];
    }

    public void push(Object e) {
        ensureCapacity();
        elements[size++] = e;
    }

    public Object pop() {
        if (size == 0) {
            throw new EmptyStackException();
        }
        return elements[--size];
    }

    private void ensureCapacity() {
        if (elements.length == size) {
            elements = Arrays.copyOf(elements, 2 * size + 1);
        }
    }
}
```

### 메모리 누수

---

> <메모리 누수 발생 포인트>
> 
> - Stack에서 pop을 하게 되면, element 상의 포인터는 한칸 내려온다.
> - 하지만 그 이전에 element에서 참조하고 있는 객체는 그대로 있다.
> - 포인터만 한칸이 내려온 것이다.
> - element에서 참조하고 있는 그 객체(포인터가 가르키지 않는 객체)는 GC가 회수하지 못한다.
> - 결국 메모리 누수가 계속 발생한다.

### GC의 reachability

---

- reachable: 어떤 객체에 유효한 참조가 있다. (root set: 유효한 최초의 참조)
- unreachable: 어떤 객체에 유효한 참조가 없다. (GC 대상)

GC가 다 쓴 객체를 알아서 회수하기 때문에 메모리 신경을 쓰지 않아도 된다고 오해할 수 있다.

스택에서 꺼내진 객체들(pop())을 GC가 회수하지 않는다.

이 스택이 객체들의 다 쓴 참조(obsolete reference) 를 계속 갖고 있게 되고, GC 활동과 메모리 사용량이 늘어나 성능이 저하되게 된다.

### 메모리 누수의 주범

---

- Stack class는 메모리를 자신이 관리함.

stack class는 객체가 아닌 객체 참조를 담는 elements 배열로 저장소 풀을 만들어 원소를 관리한다.

클래스 내에서 인스턴스에 대한 참조(reference)를 관리하는 객체

따라서 활성의 영역에 속한 원소들이 사용되고 , 비활성 영역은 쓰이지 않는다.

- 캐시 역시 메모리 누수를 일으키는 주범

객체 참조를 캐시에 넣고 나서 ,  이 사실을 까맣게 잊을 수 있음.

- 리스너 혹은 콜백 패턴

→ 조치를 해주지 않으면 콜백이 쌓일 것이다.

### 메모리 누수 방지 방안

---

1. 직접 할당 해제 

참조가 끝날 때마다 null로 처리한다.

= pop() 실행 시 꺼내진 객체는 null 처리 해주면 된다.

```java
public Object pop(){
	if(size == 0){
		throw new EmptyStackException();
	}
	Object result = elements[--size];
	elements[size] = null;
	return result;
}
```

Null 처리된 객체에 접근하려고 하면 NPE를 던져줄 수 있음.

 그렇다고 모든 객체가 사용 된 이후에 null 처리하는 것은 좋지 않음 

1. Scope을 사용한 자동 할당 해제

Scope이 종료되는 순간 refrence가 해제되어 GC의 대상이 된다.

- ✨Item 57
    
    > 지역변수의 범위를 최소화하여라
    지역변수는 선언된 지점부터 그 지점을 포함한 블록이 끝날 때 까지 이다.<br>→ 선언과 동시에 초기화 해야한다.<br>
    ex) for문을 사용해서 변수의 유효 범위를 제안한다.<br>try-catch 와 같은 구문에서는 finally 구문에서 변수에 대한 참조를 해제한다.<br>
    →그 변수에 대한 scope가 종료되는 순간 reference가 해제되어 가비지 컬렉션의 대상이 된다.
    

3 . 객체 참조 종류 4가지

- Strong Refrence → **GC의 대상이 아니다**.
    
    ```java
    String hi = new String(’hello”)
    ```
    
    GC의 대상이 되기 위해서 null로 초기화해 
    객체에 대한 reachability 상태를 unreachable 상태로 변환 해줘야 함.
    

- Soft Reference(소프트 참조) → **상황에 따라 GC의 대상**
    
    대상 객체를 참조하는 경우가 softRefernce 객체만 존재하는 경우 GC의 대상.
    
    단, JVM 메모리가 부족한 경우(OutOfMemory직전)에만 GC의 대상이고, 그외는 굳이 제거 X
    

```java
String apple = new Apple("green");
SoftReference<Apple> softApple = new SoftReference<MyClass>(apple);
apple = null; 
ref = softApple.get();
```

- Weak References(약한 참조) → **다음 GC 실행 시 무조건 힙에서 제거**
    
    대상 객체를 참조하는 경우가 WeakRefrences객체만 존재하는 경우 → **무조건 GC의대상**
    
    WeakhashMap → key에 해당하는 객체가 더이상 사용되지 않는다고 판단되면 무조건 제거
    
    단, value가 Strong refrence라 value객체가 자신의 key를 참조하지 않도록 주의
    
    ```java
    String apple = new Apple("green");
    WeakReference<Apple> softApple = new WeakReference<Apple>(apple);
    apple = null; 
    //무조건 제거
    ref = softApple.get();
    ```
    

[[Effective Java/아이템7] 다 쓴 객체 참조를 해제하라 (tistory.com)](https://frogand.tistory.com/49)
