package com.example.effective.java.chapter2;

import java.util.List;
import java.util.Map;

// 인스턴스화를 막으려거든 private 생성자를 사용하
public class Item4 {

    // 자바는 기본적으로 public 생성자를 만들어 주기 때문에
    // 인스턴스화를 막기 위해선 private 생성자를 추가하여야 한다.
    private Item4() {
        throw new AssertionError();
    }

    // 인스턴스화를 막아야 하는 경우?
    public static List<Item1> list;
    public static Map<String, Item1> map;

    public static int halfSize() {
        return list.size() / 2;
    }

    public static boolean isExist(String key) {
        return map.get(key) == null;
    }

    // 정적 필드와 정적 메소드로만 이루어진 유틸성 클래스 같은 경우는
    // 인스턴스로 만들려고 설계된것이 아니라 인스턴스 할 필요가 없다
    // 불필요한 인스턴스화는 오히려 리소스 낭비다!

}
