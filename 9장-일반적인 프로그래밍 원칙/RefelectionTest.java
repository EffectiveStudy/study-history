public class RefelectionTest {

    private static class Member {}

    @DisplayName("임의의 Class 타입 객체는 Heap영역에 하나만 존재해야 한다")
    @Test
    void classTypeObjectHashCode() throws ClassNotFoundException {
        Class<Member> aClass1 = Member.class;
        Class<? extends Member> aClass2 = new Member().getClass();
        Class<?> aClass3 = Class.forName("org.example.exception.RefelectionTest$Member");
        assertTrue(aClass1.hashCode() == aClass2.hashCode()
            && aClass2.hashCode() == aClass3.hashCode()
            && aClass1.hashCode() == aClass3.hashCode()
        );
    }

    // 명령줄의 첫번째 인수로부터, Set 구현체 클래스 이름을 입력받는다.
    // 해당 클래스 객체를 리플렉션으로 생성.
    // 객체 사용은 Set<String> 인터페이스로.
    @SuppressWarnings("unchecked")
    public static void testConcreteSet(String concreteClassName, List<String> items) {
        // 리플렉션 이용키 위해 Class 타입 객체 획득
        Class<? extends Set<String>> cl = null;
        try {
            cl = (Class<? extends Set<String>>) // unchecked 형변환
                Class.forName(concreteClassName);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("해당 클래스를 찾을 수 없습니다", e);
        }
        // 생성자 획득
        Constructor<? extends Set<String>> cons = null;
        try {
            cons = cl.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("디폴트 생성자를 찾을 수 없습니다", e);
        }
        // 리플렉션 이용한 인스턴스 생성
        Set<String> s = null;
        try {
            s = cons.newInstance();
        } catch (IllegalAccessException e) {
            throw new RuntimeException("생성자에 접근할 수 없습니다", e);
        } catch (InstantiationException e) {
            throw new RuntimeException("인스턴스화 할 수 없습니다", e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException("생성자에서 예외가 발생하였습니다", e);
        }
        // 인터페이스를 이용해 사용
        s.addAll(items);
        System.out.println("Concrete Set Class Name : " + concreteClassName + ", Set Items : " + s);
    }

    public static void main(String[] args) {
        testConcreteSet("java.util.HashSet", List.of("3", "2", "1"));
        testConcreteSet("java.util.LinkedHashSet", List.of("3", "2", "1"));
    }
}
