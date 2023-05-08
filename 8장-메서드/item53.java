package com.example.effective.java.chapter2;

import java.util.EnumSet;

// 가변인수는 신중히 사용하라
public class Item53 {

    // 가장 기본적인 가변인수 메서드
    static int sum(int... args) {
        int sum = 0;
        for (int arg : args) {
            sum += arg;
        }
        return sum;
    }
    // 가변인수 사이즈가 호출에 영향을 끼치지 않는다!

    // 호출에 영향을 끼치 메서드
    static int min(int... args) {
        if (args.length == 0) {
            throw new IllegalArgumentException("인수가 1개 이상 필요합니다.");
        }
        int min = args[0];
        for (int i = 1; i < args.length; i++) {
            if (args[i] < min) {
                min = args[i];
            }
        }
        return min;
    }
    // 가변인수 사이즈로 인해 런타임에 문제가 생기게 된다.
    // 코드도 지저분
    // 1. 유효성 검사가 명시적으로 작성됨
    // 2. 초기값의 할당으로 인해 리스트가 1 인덱스부터 돈다.
    // => 즉 for-each 를 사용할 수 없다.

    // 대안법
    static int min2(int firstArg, int... args) {
        int min = firstArg;
        for (int i = 0; i < args.length; i++) {
            if (args[i] < min) {
                min = args[i];
            }
        }
        return min;
    }
    // 평범한 매개변수와 가변인자를 함께 받으면 깔끔하다.

    // 이처럼 가변인수는 인수 개수가 정해지지 않았을때 굉장이 유용하다.
    // 하지만 성능에 민감한 경우에는 가변인수가 걸림돌이 될 수 있다.
    // => 매번 새로운 배열을 하나 할당하고 초기화해야 하기 때문

    // 대안법
    // 사용되는 가변인자 수의 확률을 확인해보자.
    public void foo() {}
    public void foo(int a1) {}
    public void foo(int a1, int a2) {}
    public void foo(int a1, int a2, int a3) {}
    public void foo(int a1, int a2, int a3, int... rest) {}

    // EnumSet의 정적 팩터리도 이 기법을 사용해 열거 타입 집합 생성 비용을 최소화한다.
    EnumSet s;

}
