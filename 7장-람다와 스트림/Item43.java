package org.example;

import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// 람다보다는 메서드 참조를 사용하라
public class Item43 {

    private static ExecutorService executor = Executors.newFixedThreadPool(4);

    public static void main(String[] args) {
        function();
    }

    public static void function() {
        String key = "key";

        Map<String, Integer> map = new HashMap();
        map.merge(key, 1, (count, incr) -> count + incr);
        // BiFunction Interface는 Java에서 함수형 프로그래밍을 구현하기 위해
        // Java 버전 1.8부터 도입된 함수형 인터페이스로 두 개의 매개변수를 전달받아 특정 작업을 수행 후 새로운 값을 반환하는 경우 사용됩니다.

        System.out.println("map1 : " + map);
        map.merge(key, 1, (count, incr) -> count + incr);

        System.out.println("map2 : " + map);
        map.merge(key, 1, (a, incr) -> a + incr);

        System.out.println("map3 : " + map);
        // merge() : 키 값 함수를 인수로 받으며, 주어진 키가 맵 안에 아직 없다면 주어진 {키, 값} 쌍을 그대로 저장한다.
        // 반대로 키가 이미 있다면 세번째 인수로 받은 함수를 현재 값과 주어진 값에 적용한 다음 그 결과로 현재 값을 덮어쓴다.

        // 이대로도 깔끔해 보이나, 아직도 거추장스러운 부분이 남아있다.
        // 매개변수인 count, incr은 크게 하는 일 없이 공간을 꽤 차지한다.
        // 이럴때 좀 더 간결하게 표현할 수 있는 방법이 메서드 참조 방식이다.

        // java8 부터 Integer 클래스는 위 람다와 기능이 같은 정적 메서드 sum을 제공하기 시작했다.
        // sum 정적 메서드는 두 숫자를 인자로 받아 더하는 역할을 한다.
        // 따라서 람다 대신 이 메서드의 참조를 전달하면 똑같은 결과를 더 보기좋게 얻을 수 있다.
        map.merge(key, 1, Integer::sum);
        System.out.println("map4 : " + map);

        // 먼저보는 결론 : 메서드 참조는 람다의 간단 명료한 대안이 될수있다. 메서드 참조쪽이 짧고 명확하다면 메서드 참조를 쓰고 그렇지 않을 때만 람다를 사용하라.
        // 메서드 참조를 사용하는 편이 보통은 더 짧고 간결하다.
        // 즉, 람다로 작성할 코드를 새로운 메서드에 담은 다음 람다 대신 그 메서드 참조를 사용하는 식으로 사용하자.
        // 메서드 참조를 사용하면 적절한 이름을 지어주거나 친절한 설명을 문서로 남길 수도 있다.
        // 위의 sum도 마찬가지 누가봐도 더하는거 같다.

        // 그럼 언제 람다가 나을까?
        // 때론 람다가 메서드 참조보다 간결할 때가 있다.
        // 주로 메서드와 람다가 같은 클래스에 있을 때 그러하다.
        // 메서드 참조
        executor.submit(Item43::action);
        // 람다
        executor.submit(() -> action());
        // 이 예시는 별 차이 없어보이지만 극단적으로 긴 클래스 명이라면?
        // Item43 -> Item43Item43Item43Item43Item43.java 였다면?
        // 람다쪽이 오히려 더 깔끔하다.

        // 이러한 메서드 참조의 유형은 총 다섯가지이다.
        // 1. 정적 메서드 참조 : 위에서 사용한 방식

        // [인스턴스 메서드를 참조하는 유형]
        // 2. 한정적 메서드 참조 : 수신 객체(참조 대상 인스턴스)를 특정한다.
        // - 정적 참조와 비슷하다.
        // - 즉, 함수 객체가 받는 인수와 참조되는 메서드가 받는 인수가 똑같음
        // Instant.now()::isAfter
        // ----------------------------------
        // Instant then = Instant.now();
        // then.isAfter(then);

        // 3. 비한정적 메서드 참조 : 수신 객체를 특정하지 않는다.
        // - 함수 객체를 적용하는 시점에 수신 객체를 알려줌
        // - 스트림 파이프라인에서의 매핑과 필터 함수에 쓰임
        // String::toLowerCase
        // ------------------------------------
        // String str = "sgSDVda";
        // str.toLowerCase()


        Member member1 = new Member("김은혜", 20);
        Member member2 = new Member("홍길동", 25);

        List<Member> members = Arrays.asList(member1, member2);

        // 한정적 메서드 참조
        // 쉽게 말해 한정적 메서드 참조는 이미 존재하는 인스턴스의 메서드를 참조하는 것이다.
        Member member = new Member("김은혜", 20);
        List<String> strings1 = members.stream()
                .map(member::getIntroduce)
                .toList();
        strings1.forEach(System.out::println);

        // 비한정적 메서드 참조
        List<String> strings2 = members.stream()
                .map(Member::getIntroduce)
                .toList();
        strings2.forEach(System.out::println);


        // [팩터리 객체로 사용]
        // 4. 클래스 생성자
        // TreeMap<K, V>::new
        // ------------------------------------
        // () -> new TreeMap<k, V>()
        List<Introduce> introduces1 = strings1.stream().map(m -> new Introduce(m)).toList();
        List<Introduce> introduces2 = strings2.stream().map(Introduce::new).toList();

        // 5. 배열 생성자
        // int[]::new
        // ------------------------------------
        // len -> new int[len]

    }

    public static void action() {
        System.out.println("액션!");
    }

    public static class Member {

        private String name;
        private Integer age;

        public Member(String name, Integer age) {
            this.name = name;
            this.age = age;
        }

        public String getIntroduce() {
            return "내 이름은 " + name + "이고, 나이는 " + age + "살 이다.";
        }

        public String getIntroduce(Member member) {
            return member.getIntroduce();
        }

    }

    public static class Introduce {
        private String content;

        public Introduce(String content) {
            this.content = content;
        }
    }

    // 람다로 할 수 없는 일이라면 메서드 참조로도 할 수 없다. (애매한 예외가 하나 있다)
    // => 제네릭 함수 타입
    interface G1 {
        <E extends Exception> Object m() throws E;
    }

    interface G2 {
        <F extends Exception> String m() throws Exception;
    }

    interface G extends G1, G2 {
    }

    public static void function2() {
        // <F extends Exception> () -> String throws F
        // 제네릭 람다식이라는 문법은 존재하지 않는다.
    }

}
