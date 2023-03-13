# TL;DR

메서드가 특정 조건에 값을 반환할수 없는 가능성을 염두에 둬야한다면, 옵셔널을 반환해야 할 상황일수도 있다.

하지만 성능 저하가 있기에, 성능에 민감하다면 항상 그래왔던 것처럼 null반환하거나, 예외를 던져라.

##  Q. 왜 옵셔널반환은 신중해야 할까?

옵셔널반환에는 `성능 저하`가 뒤따르기 때문에.

## Q. 그럼 어쩌란 말인가?

성능에 민감한 메서드라면 null반환하거나, 예외를 던지는 편이 좋다.

다만, 예외는 진짜 예외적인 상황에서만 사용해야 하며, 예외 생성시 스택 추적 캡처 하므로 비용이 비싸다.(Item69참조)

## Q. 왜 옵셔널 반환에는 성능저하가 뒤따르나?

결국 Optional도 `객체`이기에. 

따라서 새로 할당하고 초기화가 필요하며, 그 안에서 다시 값을 꺼내려면 다시 메서드를 호출해야 한다.

특히, 박싱된 기본타입을 옵셔널로 감싸는 경우, 두겹이나 감싸는 꼴이 된다.

이를 위해 기본형옵셔널이 존재하지만 (`OptionalInt`, `OptionalLong`, ...)

filter와 map등 기본 메서드를 지원하지 않으며 다른 옵셔널과 혼용이 불가한 단점이 있다.

---

# 부가적인 이야기들


## Q. In Java, 메서드가 특정 조건에 값을 반환할수 없는 경우, 어떻게 해야 하나?
 
Java8이후, 메서드가 특정 조건에 값을 반환할수 없는 경우가 존재하면 다음 3가지 선택지가 존재.

### 1. throw Exception
* (-) 진짜 예외적인 상황에서만 사용해야 한다
* (-) 예외 생성시 비용이 비싸다(스택 추적 전체를 캡처해야한다)

### 2. return null
- (-) 메서드 시그니처만으로 값이 없을 수 있는 경우가 있는지 알수 없다 -> `NPE 발생 가능`
* (-) 클라이언트에서 매번 null인지 의심해야 한다 -> `모든 객체의 null 확인 코드로 인한 코드 가독성이 떨어진다`
```java
// 매번 객체마다 null 체크  
String getCarInsuranceNameWithNullCheck(Person person) {  
    if (person == null) {  
        return null;  
    }  
    Car car = person.getCar();  
    if (car == null) {  
        return null;  
    }  
    Insurance insurance = car.getInsurance();  
    if (insurance == null) {  
        return null;  
    }  
    return insurance.getName();  
}
```

### 3. return Optional (Java8)

- (+) 메서드 시그니처만으로 값이 없을 수 있는 경우가 존재한다는 것을 `명시`적으로 알 수 있다.
- (+) 클라이언트에게 값이 없는 상황을 적절하게 처리하도록 `강제`할수 있다. -> 예상치 못한 NPE방지가능
- (-) 성능 저하 
- (-) NO `Serialize` -> 직렬화 불가

## Q. 애초에 왜 옵셔널이 등장했나?

예기치 않은 NPE발생과 이를 방지하기 위한 지저분한 null 검사 코드로 인한  가독성 저하를 해결하기 위해.


## Q. What is Optional?

원소를 최대 `1개` 가질수 있는 `불변 컬렉션` (`Collection<T>`를 구현한것은 아니지만 원칙상).

> 예) `Optional<T>`는 non-null인 T 타입 참조를 담거나, 혹은 아무것도 담지 않을 수 있다(Empty).


## Q. Optional 반환 방법?

### When to use Optional?

결과가 없을 수 있으며, 클라이언트가 그 상황을 특별히 처리해야 하는 경우.

### NG use case

- 컨테이너 타입은 Optional로 감싸지 말고, Empty 컨테이너를 반환하라.
	- `Collection<T>, Stream<T>, Array, Optional<T>`
	- 컨테이너에서 원소가 존재하는 경우, 객체가 존재한다고 판단할수 있기에.
	- Optional로 감쌀 경우, `컨테이너에 객체는 존재하지만, 해당 객체가 값이 없을 수 있다`는 또다른 경우를 고려해야 하게 된다.

 - 박싱 기본타입은 기본형 Optional을 사용하라.
	 - `OptionalInt, OptionalLong, ...`
	 - 단점 : filter, map과 같은 유용메서드 지원이 되지 않으며, 다른 Optional과 혼용 불가능.

- 옵셔널을 반환하는 메서드에서는 절대 null 반환하지 마라.

## Q. 클라이언트에서는 어떻게 처리 해야 하나?

```java
<E extends Comparable<E>> Optional<E> max(Collection<E> c) {  
    if (c.isEmpty()) {  
        return Optional.empty();  
    }  
    E result = null;  
    for (E e : c) {  
        if (result == null || e.compareTo(result) > 0) {  
            result = Objects.requireNonNull(e);  
        }  
    }  
    return Optional.of(result);  
}
```

### 1. 기본값 사용
```java
max(words).orElse("Default Value");
```
- 기본값 설정 비용이 클 경우 `orElseGet`사용하여, `Supplier<T>`를 넘길수 있다.

### 2. 예외 던지기
```java
max(toys).orElseThrow(MyException::new)
```
- 실제 예외가 아니라, `예외 팩터리`를 건네기 때문에, 예외가 실제로 발생하지 않는 한 예외 생성 비용은 들지 않는다!

### 3. map과 같은 유용 메서드 이용하여 fluent하게 처리
```java
parentProccess = ph.parent()
	.map(h -> String.valueOf(h.pid()))
	.orElse("N/A");
```


## Q. Optional의 또다른 쓰임새?

### 컨테이너 타입에서 Optional을 쓰지 말라

- 위에서 애기한 바와 같이, 쓸데없이 복잡성만 높일 뿐이다.
- 예) 옵셔널을 맵의 값으로 사용할 경우, 맵 안에 키가 없다는 사실을 나타내는 방법이 2가지가 되버린다.
	- 1. 맵에 값 자체가 없는 경우
	- 2. 맵에 값 자체는 존재하지만, Empty Optional인 경우.

### Instance필드에서 사용시 조심해서 사용해라 

- Optional 클래스는 필드 형식으로 사용할 것을 가정하지 않았기에, `Serializable` 인터페이스를 구현하지 않았다.
- 따라서, 도메인 모델에 Optional을 사용한다면 직렬화에 문제가 생길수 있다.
- 직렬화 모델이 필요할 경우, `Optional을 반환하는 getter 메서드`를 마련하는 것이 좋다.
```java
public class Person {
	private Car car;
	public Optional<Car> getCarAsOptional() {
		return Optional.ofNullable(car);
	}
}
```


# Reference

- 모던자바인액션 11장
