## Comparable을 구현할지 고민하라
Comparable의 유일 무이한 메서드
그래서 comparable인터 페이스를 구현한다는 말은 comapreTo 메서드를 구현한다는 말이랑 똑같다.

https://docs.oracle.com/javase/8/docs/api/java/lang/Comparable.html

1. 동치성 비교뿐만 아니라 순서 비교가 가능하다
    * 반환 값이 -1,0,1이므로 순서 비교가 가능하다.
    * 검색,극단값 계산,자동정렬 되는 컬렉션 관리도 쉽게할 수 있음 => test
2. 제너릭하다
3. 비교하는 두객체의 데이터 타입이 다르면 exception
4. 물론 비교가 가능해지기도 한데 ,공통 인터페이스를 매개로 비교한다
5. 숫자 비교 : 단순히 크다(1),같다(0),작다(-1)의 결과값을 리턴
6. 문자열의 비교 : 같다(0), 그 외 양수.음수값 같이 많은 결과를 반환

### CompareTo 규약
=> 전반적으로 Object.equals 메서드와 비슷하다.

* 반사성 (필수)<br>
두 객체참조의 순서를 바꿔 비교해도 예상한 결과가 나와야 함.
(x.compareTo(y) < 0) 이라면 (y.compareTo(x) > 0) 이다.<br>
x.compareTo(y)가 Exception을 발생시킨다면
y.compareTo(x) 또한 Exception을 발생시켜야 한다.

* 추이성 (필수)<br>
x.compareTo(y) < 0 이고<br>
y.compareTo(z) < 0 이라면<br>
x.compareTo(z) < 0 이다.<br>

* 대칭성 (필수)
크기가 같은 객체들끼리는 항상 같아야한다.<br>
x.compareTo(y) == 0 으로 인해 x, y 두 객체의 크기가 같다면,
x.compareTo(z) 와 y.compareTo(z) 는 같아야한다.<br>
* 그 외 (권고지만 꼭 지키면 좋은 것!)
compareTo와 equals의 결과가 같아야한다.

* Comparable은 제너릭 인터페이스
    * 인수의 타입이 컴파일 시점에 정해진다
    * 입력 인수의 타입을 확인하거나 형변환할 필요가 없다
    * 타입이 잘못되면 알아서 컴파일 에러 발생
* null을 인수로 넣을 경우 NPE를 던져야한다
    * null의 멤버에 접근 순간 NPE
* 객체 참조 필드를 비교시 , compareTo를 재귀적으로 호출
* 객체 참조 필드 비교시, compareTo메서드를 재귀적으로 호출하는데, 이때 Comparable을 구현하지 않은 필드나 표준이 아닌 순서로 비교해야한다면 Comperator를 대신 사용한다.
* 

hashCode 규약을 지키지 못하면 해시를 사용하는 다른 클래스와 어울리지 못하듯, compareTo 메서드 규약을 지키지 못하면 Comparable 인터페이스를 상속 받는 다른 클래스들과 어울리지 못한다.
==>이해 불가...issue 등록 완

* 비교시 주의사항
* 관계연산자 <,>사용을 지양.
* 핵심필드가 여러개라면 중요한 순서부터 비교하자
* Comperator 인터페이스가 제공하는 일련의 생성 메서드도 있다.=> 시간이 느려진다고 함.
    * comparingInt는 객체 참조를 int 타입 키에 매핑하는 키 추출 함수를 인수로 받아 그 키를 기준으로 순서를 정하는 메서드이다.
    * 모든 숫자를 커버할 수 있다.
    * 객체 참조용 비교 생성 메서드도 준비 됨.

    ```
    // 자바의 숫자용 기본 타입을 모두 커버한다.
    .comparingInt(ToIntFunction keyExtractor);                       // int, short
    .comparingLong(ToLongFunction keyExtractor);                     // long
    .comparingDouble(ToDoubleFunction keyExtractor);                 // float, double
    
    // 객체 참조용 비교자 생성 메서드
    .comparing(Function keyExtractor);                               // 키의 자연적 순서
  
    // 객체 참조용 보조 비교자 생성 메서드
    .thenComparing(Function keyExtractor);                           // 키의 자연적 순서를 비용한 보조 비교
    .thenComparing(Comparator keyComparator);                        // 원본 키에 보조 비교자 추가
    .thenComparing(Function keyExtractor, Comparator keyComparator); // 키와 비교자 모두 추가  
   ```

* 비교시 단순 뺄셈은 지양하자 => 범위로 인한 오버 플로우 발생이 가능하다

```
순서를 고려하는 값 클래스를 작성해야한다면 꼭 Comperable 인터페이스를 구현하여 인스턴스들을 쉽게 정렬,검색,비교.
compareTo메서드에서 필드의 값을 비교할 때 관계 연산자 사용을 지양하고 대신, compare메서드나 Comparator 인터페이스의 비교자 생성 메서드를 사용하자
```
