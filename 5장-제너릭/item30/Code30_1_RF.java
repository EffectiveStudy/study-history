package item30;

import java.util.HashSet;
import java.util.Set;

public class Code30_1_RF {

  /**
   * 제네릭을 사용하여 메서드 리팩토링 <br>
   * 1. 메서드 선언부에서 리턴타입과 입력인자들의 타입을 타입 매개변수로 명시.<br>
   * 2. 타입 매개변수 목록은 메서드의 제한자와 반환 타입 사이에 옴.<br>
   * 3. 제네릭 메서드를 사용함으로써, 컴파일타임 때에 에러를 방지할 수 있고 type safe함.<br>
   *
   */
  public static <E> Set<E> union(Set<E>s1, Set<E>s2) {
    Set<E> result = new HashSet<>(s1);
    result.addAll(s2);
    return result;
  }

  public static void main(String[] args) {
    Set<String> s1 = Set.of("Tom");
    Set<String> s2 = Set.of("Ann");
    Set<String> union = union(s1, s2);
    System.out.println(union);
  }
}
