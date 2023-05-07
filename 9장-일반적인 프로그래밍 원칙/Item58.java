package com.example.effective.java.chapter2;

import java.time.Instant;
import java.util.*;

// 전통적인 for 문보다는 for-each 문을 사용하라
public class Item58 {

    Collection<String> strs = new ArrayList<>();

    // 전통적인 for 문으로 컬렉션 순회
    public void func() {
        for (Iterator<String> i = strs.iterator(); i.hasNext();) {
            String str = i.next();
            // ...
        }
    }

    String[] strArr = new String[10];

    // 전통적인 for 문으로 배열 순회
    public void func2() {
        for (int i = 0; i < strArr.length; i++) {
            // ...
        }
    }

    // 단점?
    // 1. 반복자와 인덱스는 코드를 더럽힌다.
    // -> 우리가 필요한건 원소들 뿐이다.
    // 2. 쓰이는 요소가 많아지면 오류가 생길 가능성이 높아진다.
    // 3. 컬렉션이냐 배열이냐에 따라 코드가 많이 달라진다.
    // -> 유지보수가 굉장히 어려울 것이다. (내생각)

    // 해결법 : for-each 문을 사용하자!
    public void func3() {
        for (String s : strs) {

        }
        for (String s : strArr) {

        }
    }
    // 반복자와 인덱스도 없고, 별다른 요소 없이 원소들만 사용 할수 있다.
    // 컬렉션인지 배열인지 차이도 없이 동일한 코드모양이다.
    // 속도도 (얼추) 동일하다!
    public void func4() {
        String[] str = new String[100_000_000];
        for (int i = 0; i < str.length; i++) {
            str[i] = "원소추가";
        }
        System.out.println("전통적인 for 문으로 원소 찍기 시작 : " + Instant.now());
        for (int i = 0; i < str.length; i++) {
            String s = str[i];
        }
        System.out.println("전통적인 for 문으로 원소 찍기 끝 : " + Instant.now());

        System.out.println("for-each 문으로 원소 찍기 시작 : " + Instant.now());
        for (String s : str) {
            String s1 = s;
        }
        System.out.println("for-each 문으로 원소 찍기 끝 : " + Instant.now());
    }

    public class Card {
        private Suit suit;
        private Rank rank;

        public Card(Suit suit, Rank rank) {
            this.suit = suit;
            this.rank = rank;
        }

        @Override
        public String toString() {
            return "Card{" +
                    "suit=" + suit +
                    ", rank=" + rank +
                    '}';
        }
    }

    // 컬렉션 중첩 순환시, for-each 문의 이점이 더 커진다.
    enum Suit {CLUB, DIAMOND, HEART, SPADE}
    enum Rank {ACE, DEUCE, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN, JACK, QUEEN, KING}

    static Collection<Suit> suits = Arrays.asList(Suit.values());
    static Collection<Rank> ranks = Arrays.asList(Rank.values());

    public void func5() {
        List<Card> deck = new ArrayList<>();
        for (Iterator<Suit> i = suits.iterator(); i.hasNext();) {
            for (Iterator<Rank> j = ranks.iterator(); j.hasNext();) {
                // i.next()는 Suit 하나당 한번씩 불려야 하는데, Rank 하나당 한번씩 불리고 있다.
                // NoSuchElementException 발생!!
                Card card = new Card(i.next(), j.next());
                System.out.println("card : " + card);
                deck.add(card);
            }
        }
    }

    enum Face {ONE, TWO, THREE, FOUR, FIVE, SIX}

    Collection<Face> faces = EnumSet.allOf(Face.class);

    public void func6() {
        for (Iterator<Face> i = faces.iterator(); i.hasNext();) {
            for (Iterator<Face> j = faces.iterator(); j.hasNext();) {
                // 에러는 안나지만 모든 경우의 수가 나오지 않음
                System.out.println(i.next() + " " + j.next());
            }
        }
    }

    public void func7() {
        List<Card> deck = new ArrayList<>();
        for (Iterator<Suit> i = suits.iterator(); i.hasNext();) {
            // 바깥 반복문에 바깥 원소를 저장해주면 된다.
            // 에러는 없지만, 변수가 추가로 필요하다!
            Suit suit = i.next();
            for (Iterator<Rank> j = ranks.iterator(); j.hasNext();) {
                Card card = new Card(suit, j.next());
                System.out.println("card : " + card);
                deck.add(card);
            }
        }
    }

    // for-each 문이면 문제 해결
    public void func8() {
        List<Card> deck = new ArrayList<>();
        for (Suit suit : suits) {
            for (Rank rank : ranks) {
                Card card = new Card(suit, rank);
                System.out.println("card : " + card);
                deck.add(card);
            }
        }
    }

    // 하지만 for-each 문을 사용할 수 없는 경우도 존재한다.
    // 1. 파괴적인 필터링 : 컬렉션을 순회하면서 선택된 원소를 제거해야 한다면 Iterator remove() 함수가 필요
    public void func9() {
        List<Card> deck = new ArrayList<>();
        for (Iterator<Suit> i = suits.iterator(); i.hasNext();) {
            Suit suit = i.next();
            if (suit.equals(Suit.HEART)) {
                i.remove(); // 이 함수가 필요!
            }
        }
        // Java8 부터는 removeIf를 사용할 수 있다.
        suits.removeIf(s -> s.equals(Suit.HEART));
    }

    // 2. 변형 : 리스트나 배열을 순회하면서 그 원소의 값 일부 혹은 전체를 교체해야 한다면 각 요소의 인덱스가 필요
    public void func10() {
        for (int i = 0; i < strArr.length; i++) {
            String str = strArr[i];
            if (str.equals("변경")) {
                strArr[i] = "우왕";
            }
        }
    }

    // 3. 병렬 반복 : 여러 컬렉션을 병렬로 순회해야 한다면 각각의 반복자와 인덱스 변수를 사용해 엄격하고 명시적인 제어가 필요
    public void func11() {
        // 이 경우에 해당
        for (Iterator<Face> i = faces.iterator(); i.hasNext();) {
            for (Iterator<Face> j = faces.iterator(); j.hasNext();) {
                // 개발자가 i, j 값을 컨트롤 함으로써 순환 결과에 대한 좀더 세밀한 컨트롤이 가능하다.
                // 그렇기에 좀더 엄격하고 명시적인 제어를 할 수 있다.
                System.out.println(i.next() + " " + j.next());
            }
        }
    }


    // 전통적인 for 문과 for-each 문의 성능차이
    // 1. 컬렉션 구현체가 LinkedList 라면 for-each 문이 압도적으로 빠르다.
    // => for-each 문은 내부적으로 Iterator로 구현되어 있기 때문
    static ArrayList<Rank> ranks2 = new ArrayList<Rank>(Arrays.asList(Rank.values()));
    static LinkedList<Rank> ranks3 = new LinkedList<Rank>(Arrays.asList(Rank.values()));
    public void func111() {
        ranks2.get(1);
        // ArrayList의 배열에 index값을 그냥 return하기만하면 된다
        ranks3.get(1);
        // LinkedList는 배열을 이어놓은 것이 아닌 Node에 이전 Node와 다음 Node의 주소를 담아두어 연결한 Collection
        // Node를 처음부터 원하는 index위치까지 돌려서 값을 찾아야하는 복잡한 찾기과정이 발생
        // 특정 값만 바로 뽑아내는 ForEach문에 비해 For문으로 get()메소드를 사용하여 하나하나 찾게될 경우 상당히 오랜시간
    }
    // 그 외의 경우는 전통적인 for문이 근소하게 빠름 : 굳이 전통적인 for 문을 사용할만큼 빠르지가 않음


}
