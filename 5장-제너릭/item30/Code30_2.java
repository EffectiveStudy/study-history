package item30;

import java.util.function.Function;
import java.util.function.UnaryOperator;


public class Code30_2 {

  /** 제네릭 싱글턴 팩터리 <br>
   * 항등함수를 구현한 예시 <br>
   * 1. 자바의 제네릭이 소거 방식을 사용하기에 아래와같이 하나의 제네릭 싱글턴 팩터리이면 구현 가능.
   */

  private static UnaryOperator<Object> IDENTITY_FN = (t) -> t;

  @SuppressWarnings("unchecked")
  public static <T> UnaryOperator<T> identityFunction() {
    return (UnaryOperator<T>) IDENTITY_FN;
  }

  public static void main(String[] args) {
    String[] strings = {"t", "s"};
    UnaryOperator<String> stringStringFunction = identityFunction();
    for (String s: strings) {
      System.out.println(stringStringFunction.apply(s));
    }

    Number[] numbers = {1, 2};
    UnaryOperator<Number> numberNumberFunction = identityFunction();
    for (Number s: numbers) {
      System.out.println(numberNumberFunction.apply(s));
    }
  }

  public static Function<String, String> stringStringFunction() {
    return (t) -> t;
  }

  public static Function<Number, Number> numberNumberFunction() {
    return (t) -> t;
  }

//  public static void main(String[] args) {
//    String[] strings = {"t", "s"};
//    Function<String, String> stringStringFunction = stringStringFunction();
//    for (String s: strings) {
//      System.out.println(stringStringFunction.apply(s));
//    }
//
//    Number[] numbers = {1, 2};
//    Function<Number, Number> numberNumberFunction = numberNumberFunction();
//    for (Number s: numbers) {
//      System.out.println(numberNumberFunction.apply(s));
//    }
//  }
}
