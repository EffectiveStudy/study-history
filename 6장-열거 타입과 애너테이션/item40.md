### item40 - @Override 애노테이션을 일관되게 사용하라

@Override 에너테이션이 달렸다는 것은 상위타입의 매서드를 재정의 했음을 나타낸다.
```java
    public Bigram(char first, char second) {
        this.first = first;
        this.second = second;
    }

    public boolean equals(Bigram b) {
        return b.first == first && b.second == second;
    }

    public int hashCode() {
        return 31 * first + second;
    }

    public static void main(String[] args) {
        Set<Bigram> s = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            for (char ch = 'a'; ch <= 'z'; ch++) {
                s.add(new Bigram(ch, ch));
            }
        }
        System.out.println(s.size());
    }
```

기대값 - 26 , 출력값 - 260

일단 , 작성자는 equals 메서드를 재정의하려 한 것으로 보이고 hashCode도 함께 재정의해야 한다는 사실을 잊지 않았다

----
* 아이템 10

equals는 논리적 동치성을 확인해야한다.
Integer끼리 객체가 같은지 비교하고 싶은게 아니라 , 그 안의 값을 비교하고 싶다.
동치관계를 만족시켜야함.
반사성 - 객체는 자기 자신과 같아야함 

대칭성 - 서로에 대한 동치여부에 똑같이 답해야함.

추이성 - 1 = 2 이고 2 = 3이라면 1=3 도 같아야한다.

일관성 - 두 객체는 영원히 같아야한다.

(1) == 연산자를 사용해 입력이 자기 자신의 참조인지 확인한다.

(2) instance of 연산자로 입력이 올바른 타입인지 확인한다.

(3)입력을 올바른 타입으로 형변환한다.

(4) 입력 객체와 자기 자신의 대응되는 핵심필드들이 일치하는지 하나씩 검증한다.

---
* 아이템 11

equals가 두객체가 같다고 판단했으면 같은 해쉬코드를 반환해야한다.

---

그런데 안타깝게도 equals를 "재정의(overriding)"한 게 아니라 "다중정의(overloading, 아이템 52)"해버렸다.

Object의 equals를 재정의하려면 매개변수 타입을 Object로 해야만 하는데, 그렇게 하지 않은 것

Object에서 상속한 equals와는 별개인 equals를 **새로 정의**한 꼴이 되었다.

equals는 == 연산자와 똑같이 객체 식별성(identity)만을 확인한다.
따라서 같은 소문자를 소유한 바이그램 10개 각각이 서로 다른 객체로 인식되고, 결국 260을 출력한 것이다.
추가적으로 equals는 논리적 동치성을 확인해야 한다.(안의 내용물이 같은가?)


@Override 애너테이션을 달아 Object.equals를 재정의한다는 의도를 명시하면, 컴파일러가 오류를 찾아낼 수 있다.
![img_2.png](img_2.png)

컴파일 오류
java: method does not override or implement a method from a supertype


```java
@Override
public boolean equals(Object o) {
    if(this == o){
        return true
        }   //객체의 동일성 판단
    if (!(o instanceof Bigram)) {
    return false; //타입을 비교
    }
    Bigram b = (Bigram) o; // 타입 캐스팅
    return b.first == first && b.second == second; //내부 값하나 하나 비교
}
```
상위 클래스의 메서드를 재정의하려는 모든 메서드에 @Override 애너테이션을 달자. -> 컴파일러가 에러를 잡아줌


예외는 한 가지뿐이다.
구체 클래스에서 상위 클래스의 추상 메서드를 재정의할 때는 굳이 @Override를 달지 않아도 된다.

구체 클래스인데 아직 구현하지 않은 추상 메서드가 남아 있다면 컴파일러가 그 사실을 바로 알려주기 때문이다.

IDE와 컴파일러 덕분에 우리는 의도한 재정의만 정확하게 해낼 수 있는 것이다.

IDE와 컴파일러가 제공하는 기능을 잘 활용하자.
