package com.example.effective.java.chapter2;

import java.util.concurrent.DelayQueue;

// 이왕이면 제네릭 타입으로 만들라
public class Item29 {

    // Object 형태로 만들어진 스택 클래스 -> 어떤 문제가 있을까?
    // 클라이언트가 스택에서 객체를 꺼낼때 Object 형태로 리턴받기 때문에 형변환을 매번 해줘야 한다.
    // 이때 런타임 오류가 날 위험이 다분하다.
    public class StackObject {
        private Object[] elements;
        private int size = 0;
        private static final int DEFAULT_INITIAL_CAPACITY = 16;

        public StackObject() {
            elements = new Object[DEFAULT_INITIAL_CAPACITY];
        }

        public void push(Object e) {
            elements[size++] = e;
        }

        public Object pop() {
            Object result = elements[--size];
            elements[size] = null;
            return result;
        }
    }

    // 제네릭 형태로 만들어진 스택 클래스
    // @SuppressWarnings("unchecked")
    public class StackGeneric<E> {
        private E[] elements;
        private int size = 0;
        private static final int DEFAULT_INITIAL_CAPACITY = 16;

        public StackGeneric() {
            // 제네릭 형태로 변경하면서 에러가 발생!
            // E와 같은 실체화 불가 타입으로는 배열을 생성할 수 없다.
            // elements = new E[DEFAULT_INITIAL_CAPACITY];

            // 해결책1 : Object 배열을 생성한 후 형변환 하기
            // 에러 -> 경고로 바뀜 (unchecked cast)
            // 하지만 우리는 이 클래스가 타입에 안전하다는 걸 알고 있다.
            // => elements 배열은 private 이다.
            // => elements 배열 자체를 응답하는 메서드를 제공하지 않는다.
            // => 즉, 클라이언트가 elements 배열을 컨트롤 할 수 없기 때문에 변경 가능성이 없다.
            // => 또한 push 메서드에서 E 타입만 받기때문에 정해진 타입만 추가할 수 있다.
            // 그러니 우리는 이 경고를 무시하도록 설정하자! @SuppressWarnings 어노테이션 사용
            elements = (E[]) new Object[DEFAULT_INITIAL_CAPACITY];
        }

        // 해결책2: elements 필드의 타입을 Object[]로 변경
        // pop() 메서드에서 새로운 에러가 발생
        // private Object[] elements;

        public void push(E e) {
            elements[size++] = e;
        }

        public E pop() {
            // E result = elements[--size];
            // E 타입으로 형변환을 하자
            // 에러 -> 경고로 바뀜
            // 이전과 마찬가지로 우리는 타입이 안전하다는걸 알기 때문에 경고를 숨겨주자
            @SuppressWarnings("unchecked")
            E result = (E) elements[--size];
            elements[size] = null;
            return result;
        }

    }

    // 두 방법 모두 나름의 지지를 얻고있다(?)
    // 첫번째 방식은 가독성이 좋다. 배열을 E[]로 아예 생성하기 때문에 딱 봐도 E 타입의 인스턴스만 사용할 것 같다.
    // 형변환을 배열 생성 시 한번만 해주면 된다!
    // 두번째 방식은 배열에서 원소를 읽을때마다 형변환을 해줘야 한다.
    // 하지만 첫번째 방식의 단점인 힙오염이 발생하지 않는다.

    // 힙오염?
    // JVM의 메모리공간인 Heap Area가 오염된 상태
    // 주로 매개변수화 타입의 변수가 타입이 다른 객체를 참조할 때 발생

    // 타입 매개변수에 제약을 줄 수도 있다.
    // <E extends Delayed> : Delayed 하위 타입만 가능하다는 것이다.
    // 한정적 타입 매개변수라고 한다!
    private DelayQueue delayQueue;

    public interface GenericInterface {
        void method();
    }

    public static class GenericClassImpl implements GenericInterface {
        @Override
        public void method() {
            System.out.println("한정적 타입 매개변수를 이용한 클래스!");
        }
    }

    public static class GenericClassNonImpl {
        public void method() {
            System.out.println("한정적 타입 매개변수를 이용하지 않은 클래스!");
        }
    }

    public static class GenericStackExtends<E extends GenericInterface> {
        private E elements;

        public void push(E e) {
            elements = e;
        }

        public E pop() {
            // 한정적 타입 매개변수를 이용하면 형변환 없이 상위 인스턴스 메서드를 호출할 수 있다.
            elements.method();
            return elements;
        }
    }

    public static class GenericStackNonExtends<E> {
        private E elements;

        public void push(E e) {
            elements = e;
        }

        public E pop() {
            // elements.method();
            // 형변환을 해야한다. 굉장히 무서운 코드! ClassCastException 예외가 날 여지가 다분하다.
            ((GenericInterface) elements).method();
            return elements;
        }
    }

}
