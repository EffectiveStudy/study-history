package com.example.effective.java.chapter2;

import java.util.*;

// 자원을 직접 명시하지 말고 의존 객체 주입을 사용하라
public class Item5 {

    // 클래스 내부에 자원이 Set 이라는 인터페이스에 의존을 한다.
    private Set<Item1> set;

    // 어떤 Set 구현체에 의존하냐에 따라서 값이 달라질 것이다.
    public Item1 getFirst() {
        Iterator<Item1> iterator = set.iterator();
        while(iterator.hasNext()){
            return iterator.next();
        }
        return null;
    }

    public void add(Item1 item) {
        set.add(item);
    }

    // 이런 사용하는 자원에 따라 동작이 달라지는 클래스에는 정적 유틸리티 클래스나 싱글턴 방식이 적합하지 않다.
    // 그래서 인스턴스를 생성할 때 생성자에 필요한 자원을 넘겨주는 방식으로 의존 객체를 주입해준다.
    public Item5(Set<Item1> list) {
        this.set = Objects.requireNonNull(list);
    }

    public class Item5Test {
        public void function() {
            Item5 item1 = new Item5(new HashSet<>());
            Item5 item2 = new Item5(new LinkedHashSet<>());
            Item5 item3 = new Item5(new TreeSet<>());

            item1.getFirst();
            item2.getFirst();
            item3.getFirst();
        }
    }

    // 매번 setter 클래스를 이용해서 필요한 Set 구현체를 주입할 수도 있지만
    // 멀티스레드 환경에서 굉장히 좋지 않은 방식이다.
    // setter 메소드를 열어놓으면
    // 한번 new로 할당된 객체가 언제 어디서나 누구든 내부 Set 구현체를 변경할 수 있기 때문이다.
    // 그래서 setter 방식을 사용하지 않고 생성자 의존주입을 사용하면 불변(아이템 17)객체로 사용 가능하다.
    public void setSet(Set<Item1> set) {
        this.set = set;
    }

    // 정적 유틸리티나 싱글턴을 사용하면 의존하는 Set 인터페이스가 유연하지 못하다.
    private static Set<Item1> set2 = new TreeSet<>();

    public static void remove(Item1 item) {
        set2.remove(item);
    }

    // 팩터리 메소드 패턴을 활용하면 주입받는 Set 구현체를 좀더 유연하고 다이나믹하게 생성하여 의존할 수 있다.
    public static class SetCreator {
        public static Set<Item1> create(int type) {
            if (type == 0) {
                return new HashSet<>();
            }
            if (type == 1) {
                return new LinkedHashSet<>();
            }
            return new TreeSet<>();
        }
    }

    public class Item5Test2 {
        public void function() {
            Item5 item = new Item5(SetCreator.create(0));
            item.add(new Item1());
            item.remove(new Item1());
        }
    }

}
