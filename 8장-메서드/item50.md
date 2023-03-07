# Summary
- 클라이언트로부터 입력받은 매개변수 또는 클라이언트에게 반환하는 요소가 가변이라면, 방어적 본사본을 만들어라.
- 이때 `방어적 복사 퍼스트` 전략 사용하자.
- 다만, 복사 비용이 너무 크거나, 해당 요소를 잘못 수정할 일이 없다고 신뢰한다면, 문서에 명시하라 ( = 책임 소재를 분명히 하기)

- 팁1 : `Date`객체는 가변이니 쓰지말고, 불변인 `LocalDateTime` or `Instant`사용하라.
- 팁2 : 애초에 불변객체를 조합해 객체를 구성하면 방어적 복사를 할 일도 적어진다.(Item17)

# Q. 왜 적시에 방어적 본사본을 만들죠?
A. 세상에 미친 클라이언트는 많으니, 알아서 자신의 클래스를 지켜라

클라이언트를 신용하지 마라. 

어떤 클라이언트가 당신의 클래스를 깨뜨리기위해 환장하고 있을 수 있다.

따라서, 방어적으로 프로그래밍해야 한다.

## 예) 클라이언트가 클래스를 깨트리는 사례(클래스 내부를 수정해 불변식파괴)

### [ 하고 싶은 것 ]

- 기간(Period)를 나타내는 객체를 만들고 싶다.
- 이때 한번 기간이 정해지면, 불변으로 가지고 싶다.
- 불변 조건 : `시작 시작(start)는 종료시간(end)보다 늦어선 안된다.`

```java
final class Period {

	private final Date start;
	private final Date end;

	public Period(Date start, Date end) {
		if (start.compareTo(end) > 0) {
			throw new IllegalArgumentException("start가 end보다 늦으면 안되죠..");
		}
		// 불변조건 : start는 end보다 늦을 수 없다.
		this.start = start;
		this.end = end;
	}

	public Date start() {
		return start;
	}

	public Date end() {
		return end;
	}
	
}
```

### [ Problem? ]

- 뭐가 문제죠? 불변클래스같은데? 시작 시간이 종료 시간보다 늦을수 없다는 불변조건도 잘 검증하고 있는데?

	-> `Date`가 가변이라는 사실을 이용하면, 쉽게 불변 조건을 깰 수 있다.

```java
Date start = new Date();
Date end = new Date();
Period p = new Period(start, end);

end.setYear(78); // 불변 조건 파.괴.
```

### [ Solution? ]

- `Date`가 가변 객체니 쓰지 말아라. 대신 `Instant`나, `LocalDateTime`과 같은 불변 클래스를 이용.
- 아니, 레거시 코드는 어쩌라구요?

	-> `가변 매개 변수를 방어적 복사 해라 (Defensive Copy)`
 
	-> `먼저 방어적 복사본을 만든 후, 유효성 검사 하는 것에 주목`

```java

// 옳게 된 Period 생성자
public Period(Date start, Date end) {

	// Defensive Copy First
	// CHANGED : 방어적 복사 먼저! 이를 통해 멀티스레딩 환경에서도 안전해질수 있다(Time-Of-Check/Time-Of-Use 공격)
	this.start = new Date(start.getTime());
	this.end = new Date(end.getTime());

	// CHANGED : 복사 후, 유효성 체크!
	if (start.compareTo(end) > 0) {
		throw new IllegalArgumentException("start가 end보다 늦으면 안되죠..");
	}
}
```

- 방어적 복사를 먼저함에 따라, 멀티스레딩 환경에서의 TOCTOU공격(Time-Of-Check/Time-Of-Use)에서 해방.

	-> TOCTOU 공격 = 유효성 검사 수행후 복사본을 만드는 찰나에 다른 스레드가 원본 객체 수정할 수 있다

- Java의 `ImmutableCollections` 내부에서도 같은 방식으로 방어적 복사 하고 있다.

```java
@SafeVarargs  
ListN(E... input) {  
    // copy and check manually to avoid TOCTOU  
    @SuppressWarnings("unchecked")  
    E[] tmp = (E[])new Object[input.length]; // implicit nullcheck of input  
    for (int i = 0; i < input.length; i++) {  
        tmp[i] = Objects.requireNonNull(input[i]);  
    }    elements = tmp;  
}
```


## 해치웠나? 이제 우리가 만든 Period객체의 인스턴스는 불변인건가? 

-> 그렇지 않다. 아직도 변경가능하다.

```java
Date start = new Date();
Date end = new Date();
Period p = new Period(start, end);

p.end().setYear(78); // 불변조건 파.괴.

```

### [ Problem? ]

- 접근자 메서드에서 클래스의 가변 내부 정보를 그대로 반환하고 있어, 클라이언트가 멋대로 변경가능하다.

### [ Solution? ]

- 접근자 메서드에서 가변 필드의 `방어적 복사본`을 반환하기.

```java
public Date start() {
	return new Date(start.getTime());
}

public Date end() {
	return new Date(end.getTime());
}
```

- 이로서 Peirod는 완벽한 **불변**으로 거듭났다! 따라서 더이상 내부 상태가 바뀌어 불변조건이 깨질 일이 없다!!

	-> 애초에 불변객체를 조합해 객체를 구성하면 방어적 복사를 할 일이 줄어든다 (Item17)


# Q. 그럼 불변객체를 만들기 위해 방어적 복사를 쓰는건가요?
A. 아니요. 우리가 하고자 했던 `불변조건`이 깨지는 것을 막기 위해서 방어적 복사가 필요할 뿐이다.

불변객체이면 애초에 불변조건이 깨질 위험이 없으니 불변객체를 선호하는 것.

가변객체를 우리가 생성자에서 주입받아 사용한다고 생각해보자.
> 우리는 가변객체가 변경되었을때, 우리 클래스가 문제없이 동작할지를 추가적으로 고민하게 된다.

내부 가변 객체를 클라이언트에게 건네줄때도 마찬가지다.
> 우리가 건내준 가변 객체를 클라이언트가 멋대로 변경했을때, 불변조건이 깨질것을 추가적으로 고민해야 한다.


# Q. 그럼 어떻게 방어적 복사본을 만들죠?

## Object 
- GOOD : `constructor` or `static factory method`
- BAD : `clone()` (Item13)

## Array
- GOOD : `clone()` (Item13)

## Collection
- `얕은 복사` 와 `깊은 복사` 가 존재.
- `얕은 복사`일 경우, java10부터 추가된 `List.copyOf()` 가 원본 컬렉션과의 참조는 끊기며, 읽기전용인 컬렉션을 반환하여 불변 보장.
- `깊은 복사`일 경우, 직접 구현 필요.

### `얕은 복사` 
원본의 요소와 복사본의 요소가 같은 참조를 가지기에, **진정한 방어적 복사는 되지 않는다.**
따라서 내부 요소가 `불변 객체`여야한다.


#### 1. `constrcutor`

```java
new ArrayList<>(original);
```
- 원본 컬렉션과의 참조는 끊긴다.


#### 2. `stream`
```java
original.stream()
	.collect(Collectors.toList());
```
- 원본 컬렉션과의 참조는 끊긴다.

#### 3. `unmodifiable`
```java
List<Number> copy = Collections.unmodifiableList(original);
copy.add(new Number(3));
```
- 읽기 전용인 (Read-Only) 컬렉션 반환. 
- 수정 시도하면 예외(`UnsupportedOperationException`)발생.
- **원본 컬렉션과의 참조를 끊지 않는다!**
- 즉, 단순히 값을 변경할 수 있는 수단을 제공하지 않아 Read-Only 가 된 것이지, 복사본의 불변함을 보장하진 않는다.
- 새로운 컬렉션을 새로 만드는게 아닌, 해당 컬렉션객체를 단순히 `Wrapping`하고 있기에.


#### 4. `copyOf` (Java10)
```java
List.copyOf(original);
```
- 읽기 전용인 (Read-Only) 컬렉션 반환. 
- 원본 컬렉션과의 참조는 끊긴다.


결론적으로, 어떤 복사 방법을 써야 할까?

개인적으론, 얕은 복사를 통해 방어적 복사를 수행하기 위해서는 `unmodifiable` 대신 `copyOf` 를 사용하는 것이 좋다고 생각한다.

`copyOf`를 통해 원본 컬렉션과의 참조는 끊기며, 읽기전용인 컬렉션을 반환할수 있으므로, 불변함을 보장할수 있으므로.


### `깊은 복사` 
원본의 요소와 복사본의 요소가 다른 참조를 가지기에, **진정한 의미의 방어적 복사.**
이러한 깊은 복사는 직접 작성 필요.


### 책에서 나온 방어적 복사 방법 from: Item15
```java
private static final Thing[] PRIVATE_VALUES = { ... };

// Collection일 경우, Collections.unmodifiableList() 이용
public static final List<Thing> VALUES = Collections.unmodifiableList(Arrays.asList(PRIVATE_VALUES));

// Array일 경우, clone() 이용
public static final Thing[] values() {
	return PRIVATE_VALUES.clone();
}
```



# TMI 
---
- `방어적 복사 생성시, final 객체가 아닌 경우 clone() 메서드를 이용하지 마라.` 라는 내용이 있지만,  clone()자체를 거의 사용하지 않으니 위 내용에서 제외.
