package com.example.effective.java.chapter2;

import java.util.*;

// 상속보다는 컴포지션을 사용하라
public class Item18 {

    // 상속은 코드를 재사용하는 강력한 수단이지만, 항상 최선은 아니다!
    // 다른 패키지의 구체 클래스를 상속하는 일은 위험하다.

    // 메서드 호출과 달리 상속은 캡슐화를 깨트린다.
    // 1. 상위 클래스가 어떻게 구현되느냐에 따라 하위 클래스의 동작에 이상이 생길 수 있다.

    public static class InstrumentedHashSet<E> extends HashSet<E> {

        private int addCount = 0;

        public InstrumentedHashSet() {}

        @Override
        public boolean add(E e) {
            addCount++;
            return super.add(e);
        }

//        @Override
//        public boolean addAll(Collection<? extends E> c) {
//            addCount += c.size();
//            return super.addAll(c);
//        }

        public int getAddCount() {
            return addCount;
        }

        // 왜?
        // 상속받은 HashSet 내부의 addAll 메소드는 컬렉션을 순회하면서 개별 add 메소드를 호출한다.
        // 하지만 우리는 add 메소드도 오버라이딩 했기 때문에 내가 재정의한 add 메소드가 실행이 되어
        // 한개의 값당 두번의 카운트 증가가 일어나게 되는 것이다.

        // (1) addAll 메소드를 재정의 하지 않으면 된다. (주석)
        // -> 하지만 그렇다는 것은 상속의 재정의 기능을 제대로 활용하지 못한다는 것이고,
        // -> 내부에서 이렇게 본인 메소드를 사용하는 self-use 여부를 늘 파악해야 한다.
        // -> 이는 내가 상속받고자 하는 클래스의 모든 기술 명세를 파악해야 함을 뜻한다.

        // (2) addAll 메소드를 다른 방식으로 재정의 한다.
        @Override
        public boolean addAll(Collection<? extends E> c) {
            for (E e : c) {
                add(e);
            }
            return true;
        }
        // -> 자칫 오류를 내거나 성능을 떨어뜨릴 수도 있다.
        // -> 또한 하위 클래스에서는 접근할 수 없는 private 필드를 써야하는 상황이라면 이 방식으로는 구현이 불가능하다.

    }

    // 2. 신규 메서드 추가
    // 만약 상위 클래스가 새로운 메서드를 추가한다면?
    // 나는 리스트에 어떤 원소가 추가될때 항상 유효성 검증을 해야만 했다!
    public static class ParentList<E> {

        private List<E> list = new ArrayList<>();

        public boolean add(E e) {
            return list.add(e);
        }

        public int size() {
            return list.size();
        }

        // 그런데 갑자기...
        public boolean addAll(Collection<? extends E> c) {
            for (E e : c) {
                added(e);
            }
            return true;
        }

        public boolean added(E e) {
            return list.add(e);
        }

    }

    public static class ChildList<E> extends ParentList<E> {

        @Override
        public boolean add(E e) {
            if (e.toString().contains("은혜")) {
                super.add(e);
            }
            return true;
        }

    }

    // 이 모든 경우는 결국 메소드의 재정의가 문제였다!
    // 그럼 새로운 메서드를 추가핟다면?
    // -> 정말 운 나쁘게 상위 클래스에서 다음 릴리즈때 똑같은 구조로 메서드를 만들면 게임 끝

    // 그래서 나온 방식이 '컴포지션(composition)' 방식!
    // -> 기존 클래스를 확장하는 대신 새로운 클래스를 만들고 private 필드로 기존 클래스의 인스턴스를 참조하게 하는것!
    public static class ForwardingSet<E> implements Set<E> {

        // 클래스가 아닌 인터페이스를 상속하고 해당 인터페이스를 private 필드로 참조하여
        // 필드내의 메소드를 활용한다!
        // 구현체를 상속받는것이 아니기 때문에 내가 원하는 대로 재정의 해도 릴리즈의 변화로 내 코드가 영향을 받을 일이 없다.
        // 다른 구현체에서 백날 메소드를 내용을 변경해도 내 코드엔 아무런 영향이 없다.
        // 위에서 상속으로 사용했던 HashSet 클래스도 이런 방식!
        private final Set<E> s;

        public ForwardingSet(Set<E> s) {
            this.s = s;
        }


        @Override
        public int size() {
            return s.size();
        }

        @Override
        public boolean isEmpty() {
            return s.isEmpty();
        }

        @Override
        public boolean contains(Object o) {
            return s.contains(o);
        }

        @Override
        public Iterator<E> iterator() {
            return null;
        }

        @Override
        public Object[] toArray() {
            return new Object[0];
        }

        @Override
        public <T> T[] toArray(T[] a) {
            return null;
        }

        @Override
        public boolean add(E e) {
            return s.add(e);
        }

        @Override
        public boolean remove(Object o) {
            return false;
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            return false;
        }

        @Override
        public boolean addAll(Collection<? extends E> c) {
            return s.addAll(c);
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            return false;
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            return false;
        }

        @Override
        public void clear() {

        }
    }

    // 다른 Set 인스턴스를 감싸고 있다는 뜻에서 InstrumentedSet 같은 클래스를 래퍼 클래스 라고 말하고
    // 다른 Set에 계측 기능을 덧씌운다는 뜻에서 데코레이터 패턴이라고 한다.
    public static class InstrumentedSet<E> extends ForwardingSet<E> {

        private int addCount = 0;

        public InstrumentedSet(Set<E> s) {
            super(s);
        }

        @Override
        public boolean add(E e) {
            addCount++;
            return super.add(e);
        }

        @Override
        public boolean addAll(Collection<? extends E> c) {
            addCount += c.size();
            return super.addAll(c);
        }

        public int getAddCount() {
            return addCount;
        }

    }

    // 상속은 반드시 하위 클래스가 상위 클래스의 진짜 하위 타입인 상황에서만 쓰여야 한다!
    // 잘못된 예 : Stack (Vector를 상속받고 있음)
    // -> 과연 Stack이 Vector의 진짜 하위 클래스 인가?

    // 아무튼 최종 정리!
    // 캡슐화가 깨지는 상속관계는 잘못된 상속관계이다.
    // 상속은 캡슐화가 깨지지 않는 정말 하위 클래스의 속성인 경우에만 사용하고
    // 그러지 않다면 컴포지션 방식을 사용하자!

}
