## item 52 다중 정의는 신중히 사용하라

**어떤 문제로 인하여 다중 정의를 신중히 사용하라고 할까?**

```java
public class CollectionClassifier {

    public static String classify(Set<?> s) {
        return "Set";
    }

    public static String classify(List<?> s) {
        return "List";
    }

    public static String classify(Collection<?> s) {
        return "Collection";
    }
}
```
테스트
```java
  @Test
    void error_다중정의() {
        Collection<?>[] collections = {
            new HashSet<String>(),
            new ArrayList<Integer>(),
            new HashMap<String, String>().values()
        };
        List<String> names = new ArrayList<>();
        // Collection 만 세번 나온다
        for (Collection<?> collection : collections) {
            names.add(CollectionClassifier.classify(collection));
        }
        Assertions.assertThat(names).isEqualTo(Arrays.asList("Set","List","Collection"));
    }
```
예상대로 나오지 않음을 확인할 수 있다.<br>
![image](https://user-images.githubusercontent.com/90383376/223461526-1d18adfb-4052-4674-a2bf-7b690058d85c.png)
---
**왜 그럴까?** 

어느 메서드를 호출할지가 컴파일 타임에 정해짐<br>
컴파일 타임에 for문 안에 collection의 타입은 항상 Collection<?>이다.<br>
런타임에는 타입이 매번 달라지지만, 호출할 메서드를 선택하는데는 영향을 주지 못함<br>
* 재정의한 메서드(Override) 는 동적(런타임 시점)으로 선택되고, 다중 정의(Overload)한 메서드는 정적(컴파일 시점에)으로 선택된다
* 다중정의된 메서드 사이에서는 객체의 런타임 타입은 전혀 중요치 않다.

---
**다중 정의가 일으키는 혼동을 피하는 방법**

1. 매개변수가 같은 다중 정의는 피하자(특히 가변 인수)
2. 메서드 이름을 다르게 지어주자.
   * 생성자는 어떻게해요?<br>
    = 정적 팩터리 메서드를 사용하자
3. 서로 다른 함수 형 인터페이스라도 같은 위치의 인수로 받아서는 안된다
    * 엥..? 이건 근데 왜지 어차피 달라서 상관 없지만, 니네가 구별 못할테니 걍 쓰지말아라 이건가요?
----
**다중 정의를 사용하려면?**

근본적으로 다르다는게 확실히 되야함.즉, 두 타입의 어느 값으로도 서로 형 변환이 안 된다는말이다.<br>
이게 왜 중요하냐면, 현재 자바의 발전으로 인하여 오토박싱을 해줌.

example - remove의 두종류<br>
![image](https://user-images.githubusercontent.com/90383376/223461580-ad5b137e-cd8f-4db0-9e4b-952ac41717a4.png)
![image](https://user-images.githubusercontent.com/90383376/223461604-979bbc58-6247-4bec-ba90-defac793456d.png)


자바4까지는 근본적으로 int와 Object가 달라서 상관이 없었는데, 제너릭과 오토박싱이 등장한 이후 주의 필요해짐

--------
**pg317에 대해서....<br>**
못알아먹겠는데, stackoverflow보니깐
https://stackoverflow.com/questions/71126688/confusion-in-overloading-println-method-in-thread-and-executorservice
그냥 자바 개발자들만 아는 이야기 같았습니다..

제가 해석하기로는 원래는 void를 반환하는 것 과 반환하지 않는 다중정의 함수간에 차이가 있는데 왜 다중정의를 선택을 못할까?
라는 책의 의문점이 아닐까..?
----


**결론**
```
그냥 지양해보자
같은 기능이라면 상관은 없어
```
