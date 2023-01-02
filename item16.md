# public 클래스에서는 public 필드가 아닌 접근자 메서드를 사용하라

---

패키지 바깥에서 접근 가능한 클래스라면 접근자를 제공해서 캡슐화의 이점을 살리자

public 클래스의 필드가 불변이라도 직접 노출하지 말자
    
* 불변식 보장 가능
* 표현방식 변경 불가
* 부수작업 수행 불가

---

# 핵심정리

```
public 클래스는 가변 필드를 노출하면 안된다.
package-private 혹은 private 중첩 클래스의 경우는 종종 필드를 노출하는 편이 나을때가 있다. 
```

---

[스터디 발표 소스 repository](https://github.com/EffectiveStudy/leesangho/tree/main/src/main/java/com/github/sangholee/dev/effectivejavastudy/study03_item16)
