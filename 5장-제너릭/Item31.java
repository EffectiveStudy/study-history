package com.example.effective.java.chapter2;

import java.util.*;

// 한정적 와일드카드를 사용해 API 유연성을 높이라
public class Item31<E> {

    // 매개변수화 타입은 불공변이다.
    // List<String>은 List<Object>의 하위 타입이 아니다.
    // List<String>은 List<Object>가 하는 일을 제대로 수행하지 못하니 하위 타입이 될수 없다.
    // => 리스코프 치환 원칙에 위반

    // 예전 방식의 스택 클래스 : 제네릭만 사용하였다.
    public static class Stack<E> {
        private E[] elements;
        private int size = 0;
        private static final int DEFAULT_INITIAL_CAPACITY = 16;

        @SuppressWarnings("unchecked")
        public Stack() {
            elements = (E[]) new Object[DEFAULT_INITIAL_CAPACITY];
        }

        public void push(E e) {
            ensureCapacity();
            elements[size++] = e;

        }

        public E pop() {
            if (size == 0) {
                throw new EmptyStackException();
            }
            E result = elements[--size];
            elements[size] = null;
            return result;
        }

        public boolean isEmpty() {
            return size == 0;
        }

        private void ensureCapacity() {
            if (elements.length == size) {
                elements = Arrays.copyOf(elements, 2 * size + 1);
            }
        }

        // 일련의 원소를 스택에 넣는 메서드 추가
        public void pushAllOriginal(Iterable<E> src) {
            // src의 원소 타입이 스택의 원소 타입과 일치하지 않으면 문제가 발생! => 에러발생!
            // Stack<Number>에 Iterable<Integer>를 담으면 하위타입으로 인식해 되야할거 같지만?
            // => 불공변이기 때문에 에러가 발생한다.
            for (E e : src) {
                push(e);
            }
        }

        // 수정해보자
        // 한정적 와일드카드 타입을 활용하면 가능하다
        public void pushAllChange(Iterable<? extends E> src) {
            for (E e : src) {
                push(e);
            }
        }

        // Stack 안의 모든 원소를 주어진 컬렉션으로 옮겨 담는 메서드 추가
        public void popAllOriginal(Collection<E> dst) {
            while (!isEmpty()) {
                dst.add(pop());
            }
        }

        public void popAllChange(Collection<? super E> dst) {
            while (!isEmpty()) {
                dst.add(pop());
            }
        }

        public <E> Stack<E> union(Iterable<? extends E> s1, Iterable<? extends E> s2) {
            return null;
        }

    }

    // 1차결론 => 유연성을 극대화 하려면 원소의 생산자나 소비자용 입력 매개변수에 와일드카드 타입을 사용하라!
    // 언제 어떤 와일드카드 타입을 써야하는지? 다음 공식을 대입해보자
    // => PECS: producer-extends, consumer-super
    // 매개변수화 타입 T가 생산자라면 <? extends T>를 사용하고, 소비자라면 <? super T>를 사용하라.
    public void pushAllChange(Iterable<? extends E> src) {
        // src를 통해서 E[] 인스턴스를 "생산"하므로 <? extends E>를 사용했다.
    }

    public void popAllChange(Collection<? super E> dst) {
        // E[] 인스턴스를 "소비"해서 dst를 만들어내므로 <? super E>를 사용했다.
    }
    // 즉, 입력 매개변수가 생산자와 소비자 역할을 동시에 한다면 와일드카드 타입을 써도 좋을게 없다.

    // 추가) 언제가 생산이며 언제가 소비?
    // 파라미터(Integer) <-> 필드(E[]) <-> 파라미터(Object)의 관계를 생각해보자
    // 그리고 모든 기준을 필드로 두면 된다.
    // 보통의 다형성의 상황을 생각해보면
    private Number integer = new Integer(1);
    // Integer(하위객체)로 인해 Number(상위객체)가 "생산"된다.
    // [매개변수]로 인해 필드가 "생산"된다. => public void pushAllChange(Iterable<? extends E> src)
    // Number(상위객체)를 "소비"하여 Integer(하위객체)를 만든다.
    // 필드를 "소비"하여 [매개변수]를 만든다 => public void popAllChange(Collection<? super E> dst)
    // 즉, 생산과 소비의 기준을 필드 자체로 둔다!

    // 제대로 사용한다면 클래스 사용자는 와일드카드 타입이 쓰였다는 사실조차 의식하지 못할 것이다.
    // 클래스 사용자가 와일드카드 타입을 신경 써야 한다면 그 API에 무슨 문제가 있을 가능성이 크다.
    // 위에 union 함수를 보자.

    // 와일드카드와 관련해 논의해야 할 주제가 하나 더 남았다! (저번주에 성윤님이 질문하신 내용에 답변이 될수도?)
    // 둘중에 뭐가 나을지?
    // 공개 API라면 두 번째(와일드 카드) 방식이 낫다.
    // 왜? 어떤 리스트든 이 메서드에 넘기면 명시한 인덱스의 원소들을 교환해 줄 것이다.
    // 신경 써야 할 타입 매개변수도 없다
    // => 즉, 따로 E 타입을 명시하여 타입의 제한을 주는것보다 유연한 경우를 원하면 와일드카드가 좋다.
    public static <E> void swap1(List<E> list, int i, int j) {
        list.set(i, list.set(j, list.get(i)));
    }

    public static void swap2(List<?> list, int i, int j) {
        // 왜 에러가 날까?
        // List<?>에는 null을 제외하고 어느 값도 넣을수가 없기 때문!
        //list.set(i, list.set(j, list.get(i)));
        // 다행히 런타임 에러를 다분이 낼 수 있는 형변환 같은것을 안써도 대응이 가능하다.
        // 와일드카드 타입의 실제 타입을 알려주는 메서드를 "private" 도우미 메서드로 따로 작성하여 활용하는 것이다.
        swapHelper(list, i, j);
    }

    private static <E> void swapHelper(List<E> list, int i, int j) {
        list.set(i, list.set(j, list.get(i)));
    }

}
