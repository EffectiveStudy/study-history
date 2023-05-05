## item75 - 예외의 상세 메시지에 실패 관련 정보를 담아라


예외를 잡지 못해 프로그램이 실패하면 자바 시스템은 그 예외의 스택 추적 정보를 자동으로 출력한다. 

스택 추적은 예외 객체의 toString 메서드를 호출해 얻는 문자열이다.

이 정보가 실패 원인을 분석해야 하는 프로그래머 혹은 SRE가 얻을 수 있는 유일한 정보인 경우가 많다. 

따라서 toString 메서드에 실패 원인에 관한 정보를 가능한 한 많이 담아 반환하는 일은 아주 중요하다.

실패 순간을 포착하려면 발생한 예외에 관여된 모든 **매개변수와 필드의 값을 실패 메시지** 에 담아야 한다.

` 보안과 관련된 정보는 주의해서 다뤄야 한다. 상세 메시지에 비밀번호나 암호 키 같은 정보까지 담아서는 안 된다. `

* 예외 관여된 모든 매개변수
* 예외 관여된 모든 필드
** 관련 문서와 소스코드가 있다면 중복되는 정보를 장황하게 쓸 필요는 없다.
* 예외 상세 메시지(실패 원인파악에 필요한 정보 중요)와 최종 사용자에게 보여줄 오류 메시지(가독성 중요)를 혼동하면 안된다.


여기서 최종 사용자란?

예외에서는 그냥 던지고 ,로그에서 상세메시지를 담는건 어떤가...?


-----

IndexOutOfBoundsException의 상세 메시지는 범위의 최솟값, 최댓값, 그리고 그 범위를 벗어났다는 인덱스의 값을 담아야 한다.

``` java
/**
* IndexOutOfBoundsException을 생성한다.
*
* @param lowerBound 인덱스의 최솟값
* @param upperBound 인덱스의 최댓값 + 1
* @param index 인덱스의 실젯값
  */
  public IndexOutOfBoundsException(int lowerBound, int upperBound, int index) {
  // 실패를 포착하는 상세 메시지를 생성한다.
  super(String.format("최솟값: %d, 최댓값: %d, 인덱스: %d", lowerBound, upperBound, index));

  // 프로그램에서 이용할 수 있도록 실패 정보를 저장해둔다.
  this.lowerBound = lowerBound;
  this.upperBoudn = upperBound;
  this.index = index;
  }
```


---

결론 : 예외에 상세한 메시지를 담아라


로그와 예외.?

