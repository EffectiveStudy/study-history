## item 33 - 타입 안전 이종 컨테이너를 고려하라

* 타입 안전 이종 컨테이너란? Typesafe heterogeneous<br>
Continer = 객체의 리스트 
<br> 원래는 하나의 컨테이너에 타입이 한정되어있었다. <br> ex) Set - 1개의 타입 ,Map - 2개의 타입

그런데 난 Map을 사용하는데 key에 Stirng도 넣고 , integer도 넣고 좀더 다양한 타입을 가지면 좋겠어!!<br>
**=> 타입 안전 이종 컨테이너 사용하세요**

제너릭은 타입에 유연함 + 타입을 보장해줌. 
정해진 타입말고 제너릭을 **매개변수**로 넣어주면 어떨까?

>Map <Integer,Integer> => key에 Integer밖에 못들어감<br>
Map<Class<?>,Object> => key에 많은 타입이 들어감<br>

좋은 방법이네요!!
```java
public class Favorites {

    private Map<Class<?>,Object> favorites = new HashMap<>();

    public <T> void putFavorite(Class<T> type, T instance){
        favorites.put(type,instance);
    }
    public <T> T getFavorite(Class<T> type) {
        return type.cast(favorites.get(type));
    }
}

```
* favorites안에 두개의 인스턴스를 넣은 결과
![img_1.png](img_1.png)

* 저번에 와일드 카드 사용하면 원소 못넣는다면서!! => 함정
key가 와일드 카드지,Map이 와일드 카드는 아니다.

* 이제 Key에 원하는 타입 넣기는 성공했다.!!!

---------
* 근데 잠깐 value가 Object이다.
그러면 value랑 key는 아무 상관도 없어진다는 건데 이걸 보완하기 위해서 cast를 사용해보자
```
 type.cast(favorites.get(type));
```

Class 객체에 해당하는 값을 favorites에서 꺼내주고 .<br> 
이 객체의 타입을 cast를 이용하여 동적형 변환한다.<br>
Class가 알려주는 T(타입)의 인스턴스인지 검증한다. 단순히 맞는지만 검증하는데 왜 사용하냐?.<br>
cast 메서드의 시그니처 클래스가 제너릭이다.

-----

#### 알아 두어야할 제약사항.

1. 만약 Raw 타입으로 넘기면 타입 안정성이 깨진다.
   ```
   f.putFavorite((Class) Integer.class, "Integer의 인스턴스가 아닙니다.");
   int favoriteInteger = f.getFavorite(Integer.class) // ClassCastException
   ```
   위의 코드는 putFavorite를 사용할 때는 정상적으로 동작하지만 getFavorite를 호출하면 ClassCastException이 발생한다.<br> 
이 문제를 해결하려면 애초에 put을 할 때, 동적 형변환 type.cast() 를 해주면 된다.
* 참고사항 - checkedSet,checkedList ,checkedMap 과 같은 메서드들은 이러한 방식을 적용한 컬렉션 래퍼들이다.

2. 실체화 불가 타입에는 사용할 수 없다.<br>
3. 
   즐겨찾는 String이나 String[]은 넣을 수 있어도 List<String> 은 넣을 수 없다.<br> 
List<String> 용 Class를 얻을 수 없기 때문이다.<br>
   .List< String >.class와 List< Integer >.class는 결국 List.class를 공유하므로 둘 다 똑같은 타입의 객체 참조를 반환하기 때문이다
----
어쨋든 Favorites(타입 이중 컨테이너)는 유연하다.
이 메서드들의 타입을 제한하고 싶다면? => 한정적 타입 토큰을 이용해보자.
ex ) annotation api <br>

assubclass 메서드 <br>
호출된 인스턴스 자신의 Class 객체를 인수가 명시한 클래스로 형변환한다.(변환 된다는 것은 타입이 같다는 것)

---
#### 결론
>키를 타입 그 자체로 하고 싶다면 타입 이종 컨테이너를 사용해보세요
cast를 사용한다면 value와 key의 연결성도 보장해줄 수 있습니다.
키를 타입 그 자체로 사용하는 방식을 타입 토큰이라고 합니다
https://datacadamia.com/code/design_pattern/typesafe_heterogeneous_container
