# TL;DR

- 문자열 잘못 사용하면, 유연하지 않고 느리며, 파싱해야 하며 오류 가능성도 있다.
- 밑과 같은 타입이 적절할경우, 문자열 말고 해당 타입을 사용하라.
- `수치 타입(int, BigInteger), Enum 타입, 혼합 타입`

# Q. String보다 다른 타입이 적절한경우??

## 혼합 타입 - 여러 정보를 합쳐 String KEY를 구성하는 경우

```java
String compoundKey = className + "#" + i.next;


private String getCompoundKey(String className, int seq) {
    return className + "#" + seq;
}

private String getClassNameFromKey(String compoundKey) {
    return compoundKey.split("#")[0];
}

private String getSeqFromKey(String compoundKey) {
    return compoundKey.split("#")[1];
}
```

### Problem

- **“#”가 다른 요소에 쓰였다면?**
    
    **→** 해당 키의 파싱이 제대로 이루어 지지 않는다.
    
- **각 요소를 개별로 접근하고 싶다면?**
    
    → 파싱이 필요하게 되고 느리며, 오류 가능성 증가
    
- **해당 Key의 `equals(), toString(), compareTo() 메서드`를 이용하고 싶다면?**
    
    → String이 제공하는 기능에 의존하게 된다
    

### Solution

- **전용 Value Class 작성**

```java
private static class CompoundKey {
    private static final String SEPARATOR = "#";
    private final String className;
    private final int seq;
    private final String value;

    private CompoundKey(String className, int seq) {
        validateClassName(className);
        validateSeq(seq);
        this.className = className;
        this.seq = seq;
        this.value =  className + SEPARATOR + seq;
    }

    public static CompoundKey from(String className, int seq) {
        // Flyweight 패턴 적용 가능
        return new CompoundKey(className, seq);
    }

    private void validateClassName(String className) {
        Objects.requireNonNull(className);
        if (className.contains(SEPARATOR)) {
            throw new IllegalArgumentException("# 포함하지 마세여");
        }
    }

    private void validateSeq(int seq) {
        if (seq < 0) {
            throw new IllegalArgumentException("0보다 작으면 안되여");
        }
    }

    public String getClassName() {
        return className;
    }

    public int getSeq() {
        return seq;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompoundKey that = (CompoundKey) o;
        return seq == that.seq && className.equals(that.className);
    }

    @Override
    public int hashCode() {
        return Objects.hash(className, seq);
    }

    @Override
    public String toString() {
        return "CompoundKey{" +
                "className='" + className + '\'' +
                ", seq=" + seq +
                '}';
    }
}
```
