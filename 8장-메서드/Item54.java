package com.example.effective.java.chapter2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

// null이 아닌, 빈 컬렉션이나 배열을 반환하라
public class Item54 {

    private final List<String> cheesesInStock = Arrays.asList("치즈1", "치즈2", "치즈3");

    // 매장의 재고 치즈를 반환
    public List<String> getCheeses() {
        return cheesesInStock.isEmpty() ? null : new ArrayList(cheesesInStock);
    }
    // 1. 재고가 없다고 해서 특별히 취급할 이유가 있을까?
    // 2. null을 반환받는 클라이언트는 어떨까?
    public void client() {
        List<String> cheeses = getCheeses();
        if (cheeses != null) {

        }
        // 매번 널체크를 해줘야한다.
        // 번거롭다. 만약 빼먹으면 NPE 발생할 가능성이 다분하다.
    }
    // 3. 재고가 없는 경우를 특별 취급하는 경우도 처리하는 쪽에서 번거롭다.

    // 빈 컨테니어를 만들어서 반환하게 되면 비용이 드니 null을 반환하는게 낫지 않을까?
    // => 성능 분석 결과 성능 저하의 주범이라고 확인되지 않는 한 이정도의 성능 차이는 신경 쓸 수준이 못된다.
    // => 빈 컬렉션이나 배열은 매번 생성하지 않고도 반환할 수 있다.
    public List<String> getCheeses2() {
        return cheesesInStock.isEmpty() ? Collections.emptyList() : new ArrayList(cheesesInStock);
        // emptyList()
        // emptySet()
        // emptyMap() 등등
    }
    // 중요한것은 불변 컬렉션이여야 한다는것!

    // 배열도 마찬가지!
    private static final String[] EMPTY_CHEESE_ARRAY = new String[0];
    // 길이가 0인 배열은 모두 불변이기 때문이다.
    public String[] getCheeses3() {
        return cheesesInStock.toArray(EMPTY_CHEESE_ARRAY);
    }

}
