## item72 - 표준예외를 사용하라

표준을 쓰면 장점이 많습니다.

- 다른 사람들에게 코드가 익숙해집니다
- API규약을 그대로 따릅니다
- 클래스 적재 시간을 줄일 수 있습니다.
  

(1) IllegalArgumentException
- 호출자가 인수로 부적절한 값을 넘길 때 발생

(2) IllegalStateException
- 객체의 상태가 호출된 메서드를 수행하기 적합하지 않을 때
ex) 초기화 되지 않은 객체를 사용하려할 때

이외에도 더 세분화된 예외가 있습니다

* NullPointerException

* IndexOutOfException

(3) CurrentModificationException
- 단일 스레드에서 사용하려고 설계한 객체를 여러 스레드가 동시에 수행할 때

(4) UnsupportedOperationException
- 클라이언트가 요청한 동작을 대상 객체가 지원하지 않을 때
---
API문서를 꼭 참고하여서, 예외가 어떤 상황에서 던져지는지 확인해야한다.

예외의 이름뿐 아니라 예외가 던져지는 맥락도 부합할 때만 사용한다

단 , 예외는 직렬화할 수 있다. 직렬화에 많은 부담이 따르니 나만의 예외를 만들지 말자(?)...잉 !!!

Throwable 이 Serializable을 구현하고 있다...
