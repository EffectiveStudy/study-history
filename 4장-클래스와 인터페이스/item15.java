package com.example.effective.java.chapter2;

import java.util.*;

// 클래스와 멤버의 접근 권한을 최소화하라
public class Item15 {

    // 잘 설계된 컴포넌트는 모든 내부 구현을 완벽히 숨겨, 구현과 API를 깔끔하게 분리한다.
    // 정보 은닉, 캡슐화라고 하는 소프트웨어 설계의 근간이 되는 원리이다.

    // 정보 은닉의 장점?
    // 1. 시스템 개발 속도를 높힌다.
    // 2. 시스템 관리 비용을 낮춘다.
    // 3. 성능 최적화에 도움을 준다.
    // 4. 소프트웨어 재사용성을 높인다.
    // 5. 큰 시스템을 제작하는 난이도를 낮춰준다.

    // 정보 은닉을 해보자!
    // 모든 클래스와 멤버의 접근성을 가능한 한 좁혀야 한다.

    // 내부 클래스는 톱레벨 클래스가 아니라 private도 가능하지만
    // 기본적으로 톱레벨 클래스에는 package-private(default)나 public을 붙힐수 있다.

    // public은 공개 API가 된다.
    public static class HidingPublic {

        void functionDefault() {
            System.out.println("package-private 접근자 메소드");
        }

    }

    // package-private는 해당 패키지 안에서만 쓸 수 있다.
    static class HidingDefault {
        // 즉, 패키지 외부에서 쓸 이유가 없다면 package-private를 사용하자.

    }

    // 한 클래스(Item15)에서만 접근할 클래스(InnerClass)라면 톱레벨 클래스로 구성하지 말고 private static으로 중첩 시키자.
    // 바깥 클래스(Item15)에서만 접근할 수 있다!
    private static class InnerClass {

    }

    // 멤버에 부여할 수 있는 접근 수준은 네가지이다.
    // 1. private : 멤버를 선언한 톱레벨 클래스에서만 접근할 수 있다.
    // 2. package-private : 멤버가 소속된 패키지 안의 모든 클래스에서 접근할 수 있다.
    // 3. protected : package-private의 접근 범위를 포함하며, 이 멤버를 선언한 클래스의 하위 클래스에서도 접근할 수 있다.
    // 4. public: 모든 곳에서 접근 할 수 있다.
    // 최대한 좁은 범위의 접근 수준을 부여하고 필요에 따라 접근 수준을 점점 넓히자.

    // 멤버의 접근성을 좁히지 못하게 방해하는 제약이 하나 있다.
    // 상위 클래스의 메서드를 재정의할 때는 그 접근 수준을 상위 클래스에서보다 좁게 설정할 수 없다는 것이다.
    // 리스코프 치환원칙 : 상위 클래스의 인스턴스는 하위 클래스의 인스턴스로 대체해 사용할 수 있어야 한다.
    public static class Item15_2 extends HidingPublic {

        /*@Override
        private void functionDefault() {
            super.functionDefault();
        }*/

        @Override
        public void functionDefault() {
            super.functionDefault();
        }

    }

    // public 클래스의 인스턴스 필드는 되도록 public이 아니어야 한다.
    // 왜? thread-safe하지 않기 떄문이다.
    public int count = 0;

    // 예외사항 : 추상 개념을 완성하는데 꼭 필요한 구성요소로써의 상수
    // 단 기본 타입 값이나 불변 객체를 참조해야한다.
    public static final String STUDY_TITLE = "적재적소";

    // 만약 사이즈가 0이 아닌 배열을 사용한다면?
    public static final String[] STRING_ARRAY = new String[5];

    // 이럴땐 이렇게
    // 1. private로 일단 만들고 public 불변 리스트로 변환
    private static final String[] PRIVATE_ARRAY = new String[5];
    public static final List<String> PUBLIC_ARRAY = Collections.unmodifiableList(Arrays.asList(PRIVATE_ARRAY));
    // 2. 복사본을 반환하는 public 메서드 구현
    public static final String[] values() {
        return PRIVATE_ARRAY.clone();
    }

    // Java9 에선 모듈 시스템이라는 개념이 도입되면서 패키지들의 묶음을 나타내는 모듈이라는 개념이 등장!

}
