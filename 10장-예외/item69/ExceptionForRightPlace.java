package item69;

public class ExceptionForRightPlace {

    public static void main(String[] args) {
        ExceptionForRightPlace exceptionForRightPlace = new ExceptionForRightPlace();
        Mountain[] range = new Mountain[]{new Mountain(), new Mountain(), new Mountain()};
        exceptionForRightPlace.wrongException(range);
        exceptionForRightPlace.climb(range);
    }

    //아래의 예제가 하는 기능은 무엇일까?
    public void wrongException(Mountain[] range) {
        try {
            int i = 0;
            while (true) {
                range[i++].climb(); //배열의 범위를 벗어나면 자동으로 ArrayIndexOutOfBoundException 발생
            }
        } catch (ArrayIndexOutOfBoundsException e) {
        }
    }

    //위의 메서드와 동일한 기능을 한다.
    public void climb(Mountain[] range) {
        for (Mountain m : range) {
            m.climb();
        }
    }

    /*
    위와 같이 예외를 써서 루프를 종료한 이유가 무엇일까???
    잘못된 근거로 성능을 높여보려 한 것일 수 있다. JVM은 배열에 접근할 때마다 경계를 넘지 않는지 검사하므로
    반복문에 배열을 명시하지 않으므로 한번을 생략하려 한것하지만 이는 잘못된 추론이다.
    1. 예외는 예외 상황에 쓸 용도로 설계되었으므로 빠르게 만들어야 할 동기가 약하다. (배열 검사보다 예외가 느릴 수 있다) https://recordsoflife.tistory.com/1376
    2. 코드를 try-catch 블록 안에 넣으면 JVM이 적용할 수 있는 최적화가 제한된다. [?]
    3. 배열을 순회하는 표준 관용구는 앞서 걱정한 중복 검사를 수행하지 않는다. JVM이 알아서 최적화 해준다.
    성능이 개선되지 않는다는 문제점보다 더 큰 문제는 실제로 ArrayIndexOutOfBoundException 발생시 디버깅이 불가능 하다는 것이다.
    README로 복귀
     */

    private static class Mountain {

        public void climb() {

        }
    }

}
