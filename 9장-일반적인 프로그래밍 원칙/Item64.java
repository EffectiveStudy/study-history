package com.example.effective.java.chapter2;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.*;

// 객체는 인터페이스를 사용해 참조하라.
public class Item64 {

    // 먼저보는 결론 : 적합한 인터페이스만 있다면 매개변수뿐 아니라 반환값, 변수, 필드를 전부 인터페이스 타입으로 선언하라.

    // 객체의 실제 클래스를 사용해야 할 상황은 생성자로 생성할 때뿐이다.
    // 좋은 예. 인터페이스 타입으로 사용했다.
    Set<String> stringSet = new HashSet<>();
    // 나쁜 예. 클래스를 타입으로 사용했다.
    LinkedHashSet<String> stringLinkedHashSet = new LinkedHashSet<>();

    // 인터페이스를 타입으로 사용하는 습관을 길러두면 프로그램이 훨씬 유연해질 것이다.
    // Set<String> stringSet = new HashSet<>();

    // 내가 겪은 경우
    public void func1() {
        HashMap<String, String> map = new HashMap();
        map.put("key1", "value1");
        map.put("key2", "value2");
        map.put("key3", "value3");

        HashMap<String, String> resultMap = func2(map);
    }

    private HashMap<String, String> func2(HashMap<String, String> map) {
        map.remove("key1");
        return map;
    }
    // 갑자기 순서 보장이 필요하다면..

    // 주의할 점이 하나있다.
    // 원래의 클래스가 인터페이스의 일반 규약 이외의 특별한 기능을 제공하며,
    // 주변 코드가 이 기능에 기대어 동작한다면 새로운 클래스도 반드시 같은 기능을 제공해야 한다.
    // LinkedHashSet => HashSet 으로 변환하면 순서보장이 되지 않는다.
    // 근데 해당 기능을 사용하는 클라이언트가 순서보장에 의존하여 구현했다면? 문제가 발생

    // 구현 타입을 바꾸려 하는 동기는 무엇일까?
    // 원래 것보다 성능이 좋거나 멋진 신기능을 제공하기 때문일 수 있다.

    // 적합한 인터페이스가 없다면, 당연히 클래스로 참조해야 한다.
    // 1. String, BigInteger 같은 값 클래스가 그렇다.
    // => 값 클래스를 여러가지로 구현될 수 있다고 생각하고 설계하는 일은 거의 없다.
    // => 따라서 final인 경우가 많고 상응하는 인터페이스가 별도로 존재하는 경우가 드물다.
    String str = "문자열 값 클래스";

    // 2. 클래스 기반으로 작성된 프레임워크가 제공하는 객체들이다.
    // => 특정 구현 클래스보다는 (보통 추상 클래스인) 기반 클래스를 사용해 참조하는 게 좋다.
    OutputStream os = new ByteArrayOutputStream();

    // 3. 인터페이스에 없는 특별한 메서드를 제공하는 클래스들이다.
    public void func3() {
        List<Integer> list1 = new ArrayList<>();
        ArrayList<Integer> list2 = new ArrayList<>();

        // list1.ensureCapacity(3);
        list2.ensureCapacity(3);
        Integer.valueOf(5);
    }

    // 적합한 인터페이스가 없다면 클래스의 계층구조 중 필요한 기능을 만족하는 가장 덜 구체적인 클래스를 타입으로 사용하자.


}
