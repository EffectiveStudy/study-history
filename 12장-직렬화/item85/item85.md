### item 85 - 자바 직렬화의 대안을 찾으라

### 직렬화란 무엇인가요?

어떤 데이터를 다른 데이터의 형태로 변환하는 것을 말합니다
* Serializable 란? 객체 to 바이트 스트림
* Deserializble란? 바이트스트림 to 객체

![img.png](item85(1).png)
### 바이트 스트림이란?

스트림은 데이터의 흐름, 마치 클라이언트가 서버에게 데이터를 보내면, 이러한 출발지와 목적지로 입출력하기 위한 통로

자바는 이런 입출력 스트림의 기본 단위를 바이트로 두고, 입력으로는 InputStream, 출력으로는 OutputStream라는 추상클래스로 구현

----

### 왜 직렬화를 사용하는가?

목적지로 자바 객체를 보낸다면, 이게 자바 객체구나 알 수가 없음.
그래서 모두 다 통용되는 것으로 변환해주어야합니다 
이것이 바로 직렬화이며, 바이트스트림으로 통용시킵니다

### 바이트 직렬화 예시

```java

    @Test
    void socket() throws IOException {
        Socket socket = new Socket();
        socket.connect(new InetSocketAddress("localhost", 8080));

        byte[] bytes;
        String message;

        OutputStream os = socket.getOutputStream(); // 출력 스트림
        message = "적재적소 스터디원들 파이팅~";
        bytes = message.getBytes(StandardCharsets.UTF_8);
        System.out.println(bytes);// 문자열 -> 바이트
        os.write(bytes); // 출력 스트림에 바이트를 쓰고
        os.flush(); // flush를 날리면 그 소켓으로 출력이 된다

        String str = new String(bytes);
        System.out.println(str);
    }
```

### 어떻게 객체에서 바이트 직렬화를 하는가?

객체에 Serializable이라는 인터페이스를 구현하면 됨.
참고로 Serializable은 안에 아무것도 없는 마커 인터페이스(말 그대로 직렬화를 할 수 있는 객체라고 알려주는 것)

목적지까지 가는 바이트 스트림에 writeObject을 해주면 된다.

```java
@DisplayName("Serializable을 구현한 Person 객체 직렬화 테스트")
@Test
void writeObjectTest() throws IOException {
    Person person = new Person("bingbong", 21);

    byte[] serializedPerson;
    try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
        try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(person);
            // 직렬화된 Person 객체
            serializedPerson = baos.toByteArray();
        }
    }
    // 요런 식으로 나옴
    // -84, -19, 0, 5, 115, 114, 0, 57, 99, 111, 109, 46, 98, 105, 110, 103, 98, 111, 110, 103, 46, 101, 102, 102, 101, 99, 116, 105, 118, 101, 106, 97, 118, 97, 46, 105, 116, 101, 109, 56, 53, 46, 83, 101, 114, 105, 97, 108, 105, 122, 97, 98, 108, 101, 84, 101, 115, 116, 36, 80, 101, 114, 115, 111, 110, -126, 113, -121, -86, 125, 92, 57, 9, 2, 0, 2, 73, 0, 3, 97, 103, 101, 76, 0, 4, 110, 97, 109, 101, 116, 0, 18, 76, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 83, 116, 114, 105, 110, 103, 59, 120, 112, 0, 0, 0, 21, 116, 0, 8, 98, 105, 110, 103, 98, 111, 110, 103
    assertThat(serializedPerson).isNotEmpty();
}

static class Person implements Serializable {
    private String name;
    private int age;

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }
}
```

----

### 근데 왜 직렬화가 문제야? 

공격범위가 넓어 방어하기 어렵다,
직렬화를 하고 나서 역직렬화 시 문제가 생김.

1. 객체를 읽는 readObject 메서드는 클래스에 존재하는 모든 타입의 객체를 만들어 낼 수 있다
   - private 필드가 있더라고, readObject을 이용하여 값을 읽어낼 수 있다.
    - readObject 역직렬화 시 사용 되는데 , 이는 또 다른 생성자이기에 역 직렬화 시 값을 변경할 수 있다.(뇌피셜)
![img_1.png](item85(2).png)
2. 바이트 스트림을 역 직렬화 할때 해당 타입아느이 모든 코드를 수행할 수 있다.
   - 이는 바이트 스트림을 역직렬화할 때 해당 클래스의 생성자, 초기화 블록, 메서드 등을 실행할 수 있다는 것을 의미

3. 모든 타입이 공격 범위

4. 용량도 BIG

5. 역직렬화를 잘못하면, 시간이 오래 걸리게됨
    - 역직렬화를 수행하는 과정에서, 클래스의 생성자와 메서드 등을 실행하게 되는데 이러한 과정이 복잡하고 시간이 많이 걸릴 수 있기 때문이다



-----

### 그럼 어떻게 해야하나?

가장 좋은 방안은 역직렬화 하지 않는 것.

직렬화를 피할 수 없고 역직렬화한 데이터가 안전한지 확신할 수 없다면, 

java 9의 ObjectInputFilter를 사용하는 것도 방법
이는 데이터 스트림이 역직렬화 되기 전에 필터를 적용하여 특정 클래스를 받아들이거나 거부할 수 있다.


-------
출처


https://www.youtube.com/watch?v=3iypR-1Glm0&t=547s
https://github.com/Meet-Coder-Study/book-effective-java/blob/main/12%EC%9E%A5/85_%EC%9E%90%EB%B0%94_%EC%A7%81%EB%A0%AC%ED%99%94%EC%9D%98_%EB%8C%80%EC%95%88%EC%9D%84_%EC%B0%BE%EC%9C%BC%EB%9D%BC_%EC%9D%B4%ED%98%B8%EB%B9%88.md
