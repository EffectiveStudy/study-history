package item30;

import java.util.HashSet;
import java.util.Set;

public class Code30_1 {

  // 1. 제네릭을 사용하지 않았을 경우의 문제 코드
  private static Set union(Set s1, Set s2) {
    Set result = new HashSet(s1);
    result.addAll(s2);
    return result;
  }

  public static void main(String[] args) {
    Set s1 = Set.of("s");
    Set s2 = Set.of(1);
    union(s1, s2).forEach(System.out::println);
    union(s1, s2).forEach(arg -> System.out.println((String)arg));
  }
}
