package com.example.effective.java.chapter2;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

// 반환 타입으로는 스트림보다 컬렉션이 낫다.
public class Item47 {

    // 원소 시퀀스를 반환할 떄는 당연히 스트림을 사용해야 한다는 이야기를 들어봤을지 모르겠지만 스트림은 반복을 지원하지 않는다.
    // 따라서 스트림과 반복을 알맞게 조합해야 좋은 코드가 나온다!
    // 사실 Stream 인터페이스는 Iterable 인터페이스가 정의한 추상 메서드를 전부 포함할 뿐만 아니라,
    // Iterable 인터페이스가 정의한 방식대로 동작한다.
    // 그럼에도 for-each로 스트림을 반복할 수 없는 까닭은 바로 Stream이 Iterable을 확장하지 않아서다.
    private Stream stream;
    private Iterable iterable;

    // 스트림을 반복하기 위한 방법
    // 1. 어댑터 메서드를 사용한다.
    public void func1() {
        List<String> stringList = Arrays.asList("1", "2", "3");
        Stream<String> stringStream = stringList.stream();
        // 자바 타입 추론의 한계로 컴파일이 되지 않아, 강제 형변환이 필요하다.
        // 난잡하고 직관성이 많이 떨어진다.
        for (String s : (Iterable<String>) stringStream::iterator) {

        }
        // 어댑터 메서드를 사용하면 자바의 타입 추론이 문맥을 잘 파악하여 따로 형변환을 하지 않아도 된다.
        // 타입추론? 말그대로 개발자가 변수의 타입을 명시적으로 적어주지 않고도, 컴파일러가 알아서 이 변수의 타입을 대입된 리터럴로 추론하는 것이다.
        for (String s : iterableOf(stringStream)) {

        }
    }

    // 해당 메서드로 인해 스트림이 for-each를 사용할 수 있게 되었다.
    public static <E> Iterable<E> iterableOf(Stream<E> stream) {
        return stream::iterator;
    }

    // 반대의 어댑터 메서드
    public static <E> Stream<E> streamOf(Iterable<E> iterable){
        return StreamSupport.stream(iterable.spliterator(), false);
    }

    // 객체 시퀀스를 반환하는 메서드를 작성하는데,
    // 이 메서드가 오직 스트림 파이프라인에서만 쓰인다면 Stream을 반환하도록 하고
    // 반대로 반복문에서만 쓰인다면 Iterable을 반환하자.
    // 하지만 Stream과 반복문을 모두 사용하는 모두를 고려하여 이 둘을 모두 제공하는 것이 더 좋은 방법이다.

    // 2. Collection 반환
    // Collection 인터페이스는 Iterable의 하위 타입이고 Stream 메서드도 제공하니 반복과 스트림을 동시에 지원한다.
    public void func2() {
        Arrays.asList(new String[]{"1", "2", "3"});
        Stream.of(new String[]{"1", "2", "3"});
        // Arrays 역시 손쉽게 반복과 스트림을 지원할 수 있다.
    }
    // 컬렉션을 반환한다는 이유로 덩치 큰 시퀀스를 메모리에 올려선 안된다. 메모리를 잡아먹는 부분이 기하급수적으로 늘어날 수 있기 때문이다.
    // 이때 전용 컬렉션을 구현하는 방법도 생각해 볼만 하다.

    // 전용 컬렉션을 구현해도 되지만, 때로는 단순히 구현하기 쉬운 쪽을 선택하기도 한다.
    // 입력 리스트의 부분 리스트를 모두 반환하는 메서드?
    public static class SubList {
        // Stream.concat 메서드는 반환되는 Stream에 빈 리스트를 추가하며, flatMap은 모든 Stream을 하나의 Stream으로 만든다.
        public static <E> Stream<List<E>> of(List<E> list) {
            return Stream.concat(Stream.of(Collections.emptyList()),
                    prefixes(list).flatMap(SubList::suffixes));
        }

        // (a, b, c)의 prefixes는 (a), (a, b), (a, b, c) 이다.
        public static <E> Stream<List<E>> prefixes(List<E> list) {
            return IntStream.rangeClosed(1, list.size())
                    .mapToObj(end -> list.subList(0, end));
        }

        // (a, b, c)의 suffixes는 (c), (b, c), (a, b, c) 이다.
        public static <E> Stream<List<E>> suffixes(List<E> list) {
            return IntStream.rangeClosed(0, list.size())
                    .mapToObj(start -> list.subList(start, list.size()));
        }
    }

    List<String> stringList = Arrays.asList("a", "b", "c");
    public void func3() {
        Stream<List<String>> listStream = SubList.of(stringList);
        listStream.forEach(System.out::println);
    }

    public void func4() {
        for (int start = 0; start < stringList.size(); start++) {
            for (int end = start + 1; end <= stringList.size(); end++) {
                System.out.println(stringList.subList(start, end));
            }
        }
    }
}
