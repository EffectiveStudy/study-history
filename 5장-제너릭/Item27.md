# 아이템 27 비검사 경고를 제거하라

* 제네릭을 사용하면 수많은 컴파일러 경고를 볼 수 있다.
    * 비검사 형변환 경고
    * 비검사 메서드 호출 경고
    * 비검사 매개변수화 가변인수 타입 경고
    * 비검사 변환 경고

이러한 코드를 `javac -Xlint:unchecked` 명령어로 컴파일시 `unchecked conversion` 에러가 발생한다.

가능하다면 모든 비검사 경고를 제거해야한다. 모두 제거 한다면 그 코드는 타입 안전성이 보장된다. (ClassCastException 이 발생할 일이 없어진다.)

## @SuppressWarnings

경고를 제거할 수 없지만 타입이 안전하다고 확신할 수 있다면 @SuppressWarnings("unchecked") 어노테이션을 활용해서 경고를 감춰야 한다.

* 진짜 문제를 알리는 새로운 경고를 확인하지 못할수 있다.
* @SuppressWarnings("unchecked") 어노테이션은 항상 가능한 한 좁은 범위에 적용해야 한다.  
* @SuppressWarnings("unchecked") 어노테이션을 사용할때면 경고를 무시해도 안전한 이유를 항상 주석으로 남겨야 한다.