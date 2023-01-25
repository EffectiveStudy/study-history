package item30;

import java.util.Collection;
import java.util.Objects;

public class Code30_3 {

  /**
   * 재귀적 타입 한정 <br>
   * 1. 자기 자신이 들어간 표현식을 사용하여 타입 매개변수의 허용범위를 한정할 수 있음.<br>
   * 2. 재귀적 타입 한정은 주로 타입의 자연적 순서를 정하는 Comparable 인터페이스와 함계 쓰임. <br>
   * 3.
   */
  public static <E extends Comparable<E>> E max(Collection<E> c) {
    if (c.isEmpty()) {
     throw new RuntimeException();
    }

    E result = null;
    for (E e : c) {
      if (result == null || e.compareTo(result) > 0) {
        result = Objects.requireNonNull(e);
      }
    }

    return result;
  }


}
