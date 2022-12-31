package com.example.effective.java.chapter2;

import java.util.*;

// equals를 재정의하려거든 hashCode도 재정의하라
public class Item11 {

    // equals를 재정의한 클래스 모두에게 hashCode도 재정의해야 한다!
    // 왜 ? hashCode 일반 규약을 어기게 되기 때문

    public class NotOverrideHashCode {

        private int depth_1;
        private int depth_2;
        private int depth_3;

        public NotOverrideHashCode(int depth_1, int depth_2, int depth_3) {
            this.depth_1 = depth_1;
            this.depth_2 = depth_2;
            this.depth_3 = depth_3;
        }

        public boolean equals(Object object) {
            if (this == object) return true;
            if (object == null || getClass() != object.getClass()) return false;
            if (!super.equals(object)) return false;
            NotOverrideHashCode that = (NotOverrideHashCode) object;
            return depth_1 == that.depth_1 && depth_2 == that.depth_2 && depth_3 == that.depth_3;
        }

    }

    public class OverrideHashCode {

        private int depth_1;
        private int depth_2;
        private int depth_3;

        public OverrideHashCode(int depth_1, int depth_2, int depth_3) {
            this.depth_1 = depth_1;
            this.depth_2 = depth_2;
            this.depth_3 = depth_3;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            OverrideHashCode that = (OverrideHashCode) o;
            return depth_1 == that.depth_1 && depth_2 == that.depth_2 && depth_3 == that.depth_3;
        }

        @Override
        public int hashCode() {
            return Objects.hash(depth_1, depth_2, depth_3);
        }
    }

    // Object 명세
    // 1. equals(Object)가 두 객체를 같다고 판단했다면, 두 객체의 hashCode는 똑같은 값을 반환해야 한다.

    public void notOverrideFunction() {
        System.out.println();
        System.out.println("====================================================");
        System.out.println(" equals를 오버라이딩하고, hashCode는 오버라이딩 하지 않았을때 ");
        System.out.println("====================================================");

        Map<NotOverrideHashCode, String> phoneBook = new HashMap<>();

        NotOverrideHashCode number1 = new NotOverrideHashCode(010, 1234, 5678);
        NotOverrideHashCode number2 = new NotOverrideHashCode(010, 1234, 5678);
        phoneBook.put(number1, "김은혜");

        System.out.println("* 동일한 내용을 가진 두 객체가 같은가 ? " + number1.equals(number2));
        System.out.println("* 동일한 내용을 가진 다른 객체로 데이터를 가져온다면 ? " + phoneBook.get(number2));
    }

    public void overrideFunction() {
        System.out.println();
        System.out.println("====================================================");
        System.out.println("        equals와 hashCode 모두 오버라이딩 했을때         ");
        System.out.println("====================================================");

        Map<OverrideHashCode, String> phoneBook = new HashMap<>();

        OverrideHashCode number1 = new OverrideHashCode(010, 1234, 5678);
        OverrideHashCode number2 = new OverrideHashCode(010, 1234, 5678);
        phoneBook.put(number1, "김은혜");

        System.out.println("* 동일한 내용을 가진 두 객체가 같은가 ? " + number1.equals(number2));
        System.out.println("* 동일한 내용을 가진 다른 객체로 데이터를 가져온다면 ? " + phoneBook.get(number2));
    }

    // Object 명세
    // 2. equals(Object)가 두 객체를 다르다고 판단했더라도, 두 객체의 hashCode가 서로 다른 값을 반환할 필요는 없다.
    // 단 다른 객체에 대해서는 다른 값을 반환해야 해시테이블의 성능이 좋아진다.

    public class SameHashCode {

        private String idx;

        public SameHashCode(String idx) {
            this.idx = idx;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SameHashCode that = (SameHashCode) o;
            return Objects.equals(idx, that.idx);
        }

        @Override
        public int hashCode() {
            return 42;
        }

    }

    public class NotSameHashCode {

        private String idx;

        public NotSameHashCode(String idx) {
            this.idx = idx;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            NotSameHashCode that = (NotSameHashCode) o;
            return Objects.equals(idx, that.idx);
        }

        @Override
        public int hashCode() {
            // 좋은 해시함수라면 서로 다른 인스턴스에 대해서는 다른 해시값을 리턴해야 한다.
            // 자동으로 제공하는 해시함수가 아닌, 자체적으로 개발을 할때는 다음을 잘 확인하자.
            // 1. 동치인 인스턴스에 대해 똑같은 해시값을 리턴할지 확인
            // 2. 파생필드는 해시 계산에서 제외해도 가능
            // 3. equals 비교에 사용되지 않을 필드는 반드시 해시 계산에 제외
            return Objects.hash(idx);
        }

    }

    public void sameHashCode() {
        System.out.println();
        System.out.println("====================================================");
        System.out.println("           hashCode가 항상 같은값을 리턴할 때            ");
        System.out.println("====================================================");

        Map<SameHashCode, Integer> sameMap = new HashMap<>();
        System.out.println("* 데이터 세팅 시작...");
        for (int i = 0; i < 100_000; i++) {
            sameMap.put(new SameHashCode(String.valueOf(i)), i);
        }
        System.out.println("* 데이터 세팅 마무리...");
        System.out.println("* 데이터 : " + sameMap.get(new SameHashCode(String.valueOf(99_999))));
    }

    public void notSameHashCode() {
        System.out.println();
        System.out.println("====================================================");
        System.out.println("             hashCode가 다른값을 리턴할 때              ");
        System.out.println("====================================================");

        Map<NotSameHashCode, Integer> sameMap = new HashMap<>();
        System.out.println("* 데이터 세팅 시작...");
        for (int i = 0; i < 1_000_000; i++) {
            sameMap.put(new NotSameHashCode(String.valueOf(i)), i);
        }
        System.out.println("* 데이터 세팅 마무리...");
        System.out.println("* 데이터 : " + sameMap.get(new NotSameHashCode(String.valueOf(999_999))));
    }

    public class HashCodeCustom {

        private int id;
        private int age;
        private int level;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            HashCodeCustom that = (HashCodeCustom) o;
            return id == that.id && age == that.age && level == that.level;
        }

        @Override
        public int hashCode() {
            // 31을 곱하는 이유? https://velog.io/@indongcha/hashCode%EC%99%80-31
            // 1. 홀수여서 : 짝수를 곱하면 Hash 충돌이 일어날 가능성이 높아지기에 홀수
            // 2. 소수여서 : 관례나 미신에 가까움 합성수인 홀수를 선택하는 사례도 존재
            int result = Integer.hashCode(id);
            result = 31 * result + Integer.hashCode(age);
            result = 31 * result + Integer.hashCode(level);
            return result;
        }

    }

    public class HashCodeDefault {

        private int id;
        private int age;
        private int level;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            HashCodeDefault that = (HashCodeDefault) o;
            return id == that.id && age == that.age && level == that.level;
        }

        @Override
        public int hashCode() {
            // IDE가 제공하는 기본 해시 함수
            // 성능이 약간 느리다.
            // 1. 해시할 필드를 담을 리스트를 생성해야하고 리스트를 순회하며 해시하기 때문
            // 2. 기본 자료형을 박싱 - 언박싱의 과정을 거치기 때문
            return Objects.hash(id, age, level);
        }

    }

    // + 추가사항
    // 1. 해시코드를 계산하는 비용이 크다면 매번 해시를 새로 계산하기 보다는 캐싱하는 방식을 고려해볼만 하다!
    // 2. Trade-Off 구간을 잘 고려하자.
    //    - 해시 함수를 계산할 때 속도를 개선? 최소한의 필드만 해싱 (필드의 양이 적어 해싱하는 행위의 시간이 줄어듬)
    //    - 해시 결과를 조회할 때 속도를 개선? 최대한의 필드만 해싱 (여러 필드를 해싱하기 때문에 중복 키가 줄어듬)
    // 3. hashCode가 반환하는 값의 생성 규칙을 API 사용자에게 자세히 공표하지 말자.

}
