# Item 1. 생성자 대신 정적 팩터리 메서드를 고려하라

보통 객체 생성 방식 : public 생성자를 호출한다.

```java
User user = new User();
```

하지만 클래스는 이외에도 다음과 같이 public 생성자 대신 정적 팩토리 메서드를 제공할 수 있다.

```java
//정적 팩토리 메서드
    public static Question newQuestion(Long id, String title, String contents) {
        return new Question(id, title, contents);
    }

//생성자
private Question(long id, String title, String contents) {
        super(id);
        this.title = title;
        this.contents = contents;
    }
```

### **장점**

---

- **이름을 가질 수 있다.**

딱 보기에도 생성자로 인스턴스를 생성하려면 new Qustion()으로만 호출을 하여서, 객체의 특성을 잘 설명하지 못함.

또한 엉뚱한 생성자를 호출하는 실수도 할 수 있다.

반면 정적 팩토리 메서드만 잘 지으면 객체의 특성을 쉽게 묘사할 수 있다. 

위의 예시에서도 newQuestion()으로 새로운 객체를 생성해줌이(키워드로) 잘 드러난다.

- **호출될 때마다 인스턴스를 새로 생성하지 않아도 된다.**

특히 만약 immutable클래스라면? 불변임이 보장되어, 미리 인스턴스를 만들어 놓거나 생성한 인스턴스를 캐싱하여 재활용 할 수 있다. 

캐싱을 한다면 → 생성 비용이 큰 객체가 자주 요청되는 상황이라면 성능을 끌어 올려준다.

이는 flywight 패턴과 비슷함.

- 💡 flyweight 패턴이란?
    
    Factory를 만들어 인스턴스의 생성 여부를 체크하고, 인스턴스가 있다면 더 이상 생성하지 않고 기존의 인스턴스를 리턴하여 불필요한 인스턴스 생성을 줄이는 디자인 패턴이다.공유를 통해 대량의 객체들을 효과적으로 지원하는 방법이다.
    
    반복 요청에 같은 객체를 반환하는 식으로 인스턴스의 생존을 철저히 통제할 수 있다!
    
    인스턴스를 통제하면 얻은 이득?
    
    1. 클래스를 싱글턴으로 만들 수 있다.
    2. 인스턴스화 불가(noninstantiable)로 만들 수 있다.
    3. 인스턴스가 단 하나뿐임을 보장할 수 있다.

**[예시]**

lotto 미션에서 lotto 번호 45개를 생성하여 번호를 뽑는 상황

요청될 때마다 45개씩 생성하여 뽑으면 기하급수적으로 생성 비용이 커짐.

1 ~ 45 number를 가진 LOTTO_NUMBERS_POOL 리스트에 캐싱

```java
private static final int FIRST_NUM = 1;
private static final int MAX_NUM = 45;
private static final int LAST_NUM = 6;
private static final List<Integer> LOTTO_NUMBERS_POOL = new ArrayList<>();

static {
   for (int i = FIRST_NUM; i <= MAX_NUM; i++) {
      LOTTO_NUMBERS_POOL.add(i);
   }
}
```

그리고 정적 팩토리 메서드를 통해 pool에 이미 존재하는 인스턴스들을 그대로 반환해줍니다.

```java
public class RandomLottoFactory {

	private static final int FIRST_NUM = 1;
	private static final int MAX_NUM = 45;
	private static final int LAST_NUM = 6;
	private static final List<Integer> LOTTO_NUMBERS_POOL = new ArrayList<>();

	static {
		for (int i = FIRST_NUM; i <= MAX_NUM; i++) {
			LOTTO_NUMBERS_POOL.add(i);
		}
	}

	public List<Integer> randomLotto() {
		List<Integer> lottoSet = new ArrayList<>();
		Collections.shuffle(LOTTO_NUMBERS_POOL);

		for (int i = FIRST_NUM; i <= LAST_NUM; i++) {
			lottoSet.add(LOTTO_NUMBERS_POOL.get(i));
		}

		Collections.sort(lottoSet);
		return lottoSet;
	}

}
```

이렇게 코드가 개선되면 최초 1 ~ 45 까지의 LottoNumber 인스턴스 생성 비용 외에 사용하는 비용이 없다.

- **반환 타입의 하위타입 객체를 반환할 수 있는 능력이 있다**

이 능력은 반환할 객체의 클래스를 자유롭게 선택할 수 있게 하는 ‘엄청난 유연성' 을 선물한다고 한다.

```java
public class Account {
	public Account() {}
}

public class AccountFactory {
	public static Account of(int money) {
		if(money > 100) {
			return new VipAccount();
		}
		else {
			return new NormalAccount();
		}
	}
}

public class VipAccount extends Account{
	public Vip() {}
}

public class NormalAccount extends Account{
	public Normal() {}
}
```

다음 코드처럼 보유한 돈에 따라, Account의 하위 타입인 Vip, Normal 인스턴스를 반환할 수 있다.

자바의 Collections도 대부분의 구현체를 한개의 정적 팩터리 메서드를 통해 얻게 함.

클라이언트 입장에서 알아야 될게 적어짐.

- **입력 매개변수에 따라 매번 다른 클래스의 객체를 반환할 수 있다.**

```java
public static <E extends Enum<E>> EnumSet<E> noneOf(Class<E> elementType) {
    Enum<?>[] universe = getUniverse(elementType);
    if (universe == null)
        throw new ClassCastException(elementType + " not an enum");
    if (universe.length <= 64)
        return new RegularEnumSet<>(elementType, universe);
    else
        return new JumboEnumSet<>(elementType, universe);
}
```

EnumSet 클래스의 경우 public 생성자 없이 default 생성자만 존재한다.

대신에 정적 팩토리 메서드를 적용하였는데, universe의 크기를 계산하여 64 이하일 경우, RegularEnumSet 을, 65 이상일 경우 JumboEnumSet 을 리턴해준다.

> 클라이언트는 이 두 클래스의 존재를 모른다. 팩토리가 건내주는 객체가 RegularEnumSet인지 아니면 JumboEnumSet 인지 알지 못하며 알 필요도 없다.
> 

- **정적 팩토리 메서드를 작성하는 시점에는 반환할 객체의 클래스를 작성할 필요가 없다.**

간단하게만 이해해보면 JDBC 프레임 워크에 있는getConnections()를 예시로 들 수 있다.

getConnection을 할 때 우리는 mysql로 할지, Oracle로 연결할지 호출시에 정할 것이다.

그래서 getConnection은 작성될 시점에는 이러한 프레임워크에 종속되지 않고, 호출될 때 맞는 인스턴스를 구현해서 리턴한다. 

### 단점

---

- **상속을 하려면 public이나 protected 생성자가 필요하니 정적 팩토리 메서드만 제공하면 하위 클래스를 만들 수 없다.**

상속을 하려면 public이나 protected 생성자가 필요한데, 정적 팩터리 메서드를 사용하는 경우 기존 생성자를 private 기본생성자를 통해 외부 생성을 막아두기 때문이다.(처음 예시 참조)

- **정적 팩토리 메서드는 프로그래머가 찾기 어렵다.**

생성자처럼 API 설명에 명확히 드러나지 않으므로 사용자는 정적 팩터리 메서드 방식 클래스를 인스턴스화할 방법을 API 문서를 통해 알아내야 한다.

아래는 정적 팩터리 메서드에서 통용되는 **명명 방식**이다.

**그런데 넥스트 스텝 클린코드 강의에서 사실 정적 팩토리 메서드도 Builder의 역할이기에 다음과 같이 of보다 명사로 선언하는 것이 좋다고 한다. 이미 통상적인 명명방식이기에 참고만 해야겠다.**

- from： 매개변수를 하나 받아서 해당 타입의 인스턴스를 반환하는 형변환 메서드
    - Date d = Date.from(instant);
- of： 여러 매개변수를 받아 적합한 타입의 인스턴스를 반환하는 집계 메서드
    - Set<Rank> cards = EnumSet.of(JACK, QUEEN, KING);

- **[ Static의 메모리 ]**
    
    [https://t1.daumcdn.net/cfile/tistory/99AAAC405CEC82C032](https://t1.daumcdn.net/cfile/tistory/99AAAC405CEC82C032)
    
    하지만 Static 키워드를 통해 Static 영역에 할당된 메모리는 모든 객체가 공유하는 메모리라는 장점을 지니지만, Garbage Collector의 관리 영역 밖에 존재하므로 Static을 자주 사용하면 프로그램의 종료시까지 메모리가 할당된 채로 존재하므로 자주 사용하게 되면 시스템의 퍼포먼스에 악영향을 주게 됩니다.
    

[java에서 객체를 생성하는 다양한 방법 (techcourse.co.kr)](https://tecoble.techcourse.co.kr/post/2021-05-17-constructor/)

 [[Java] 인스턴스를 캐싱하여 성능 개선하기 (feat. 로또) (tistory.com)](https://steadyjay.tistory.com/15)

[정적 팩토리 메서드 (velog.io)](https://velog.io/@ujunhwan/%EC%A0%95%EC%A0%81-%ED%8C%A9%ED%86%A0%EB%A6%AC-%EB%A9%94%EC%84%9C%EB%93%9C)
