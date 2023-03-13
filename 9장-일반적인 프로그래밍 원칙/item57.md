# 지역변수의 범위를 최소화하라

---

C 와 같은 언어 중에서는 지역변수를 코드 블록 첫 머리에 선언하는 경우가 많음

자바에서도 이 방식을 따른 경우가 있음

```java
public static void main(String[] args) {
    int rectangleWidth = 10;
    int rectangleHeight = 20;
    int squareWidth = 10;
    int squareHeight = 10;
    int rhombusWidth = 10;
    int rhombusHeight = 10;
    ...

    Rectangle rectangle = new Rectangle(rectangleWidth, rectangleHeight);
    Square square = new Square(squareWidth, squareHeight);
    Rhombus rhombus = new Rhombus(rhombusWidth, rhombusHeight);
}
```

지역 변수의 범위를 줄이는 가장 좋은 방법은 `처음 쓰일때 선언하기`

```java
public static void main(String[] args) {
    int width = 10;
    int height = 20;

    Rectangle rectangle = new Rectangle(width, height);
    
    int opacity = 1.0;
    rectangle.opacity(opacity);
}
```

지역 변수를 생각없이 선언하면 사용하는 범위보다 너무 앞서 선언하거나
다 쓴 뒤에도 남아있기 쉬움

이는 의도하지 않은 실수로 이어질 수 있음

`거의 모든 지역변수는 선언과 동시에 초기화 필요`
따라서 초기화에 필요한 정보가 충분하지 않으면 선언을 미뤄야 함


하지만 `try-catch` 문에서 예외가 적용됨

```java
public static void main(String[] args) {
    Class<? extends Set<String>> cl = null;
    try {
        cl = (Class<? extends Set<String>>) Class.forName(args[0]);
    } catch (ClassNotFoundException e) {
        fatalError("클래스를 찾을 수 없습니다.");
    }
    ...    
}
```

`try` 절 바로 앞에 변수 선언 해야함

반복문에 경우는 반복 변수 의 범위가 반복문의 몸체와 반복문 키워드와 몸체 사이에 괄호 안으로 제한 됨

반복자를 사용해야하면 `for-each` 문보다 `for` 문 사용

```java
for(Iterator<Element> i = c.iterator(); i.hasNext();) {
    Element e = i.next(); //e와 i로 무언가를 한다.
}
```

반복변수의 값을 반복문 종료후에도 사용해야한다면 `while` 문보다 `for` 문 사용

`while` 문 을 사용할 때 발생할 수 있는 예시

```java
Iterator<Element> i = c.iterator();
while(i.hasNext()) {
    doSomeThing(i.next());
}
...

Iterator<Element> i2 = c2.iterator();
while(i.hasNext()) { // 복붙으로 인한 오류
    doSomeThing(i2.next());
}
```

`for` 문 으로 수정한 예시

```java
for(Iterator<Element> i = c.iterator(); i.hasNext();) {
    Element e = i.next();
} 

for(Iterator<Element> i2 = c2.iterator(); i.hasNext();) {
    Element e2 = i2.next();
} 
```

`for` 문의 경우는 컴파일 오류로 알려줌

변수의 유효 범위가 `for` 문 범위와 일치하여 같은 이름의 변수를 사용하더라도 영향을 주지 않음

또한 `while` 문 보다 짧아 가독성이 더 나음

```java
for (int i = 0; i < expensiveComputation(); i++) {
    ...// i로 무언가를 한다. 
}

// 반복시마다 값은 값을 반환하는 메서드를 호출하지 않도록 개선 

for (int i = 0, n = expensiveComputation(); i < n; i++) {
    ...// i로 무언가를 한다. 
}

```

`메서드를 작게 유지 및 한가지 기능 집중` 지역변수 범위 최소화 가능

---

### 핵심정리

> 지역변수 범위를 최소화 하려면
> 
> 1. 변수가 처음 쓰일 때 선언
> 2. 변수 선언시 초기화 필요
> 3. 메서드를 가능한 작게 유지, 한가지 기능에 집중

---

### 발표자의 생각

핵심은 메서드를 작게 유지 하고 한가지 기능만 집중하게 한다면

지역변수 선언을 생각보다 줄일 수 있으며 지역변수 갯수가 줄어들면 범위도 최소화가 쉬울 것

---

[스터디 발표 소스 repository](https://github.com/EffectiveStudy/leesangho/tree/main/src/main/java/com/github/sangholee/dev/effectivejavastudy/study11_item57)
