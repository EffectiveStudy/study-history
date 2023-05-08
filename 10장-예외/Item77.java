package org.example;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

// 예외를 무시하지 말라
public class Item77 {

    // 제곧내.. 예외를 냈으면 무시하지 말아라
    // catch 블록을 비워두면 예외가 존재할 이유가 없어진다.

    public void func1() {
        try {

        } catch (Exception e) {
            // 여기서 아무것도 하지 않으면
            // 예외를 흘러보내는게 아니라 예외를 오히려 더 키우는것?
        }
        // 우리가 try catch 문을 사용했다는건 try문 안에서 사용한 어떠한 기능에서
        // 해당 exception을 던졌다는것...
        // 근데 그걸 아무런 처리를 하지 않고 무시한다면?
    }

    // 하지만 exception을 무시해도 되는 상황이 있다.
    // FileInputStream을 닫을때는 적절할 수도 있다.
    // 파일이 열때 쓰
    // 입력 전용 스트림으로 상태가 변경되지 않아서 복구할 필요가없다!
    // 아니면 실제로 (아주 간혹) 던져진 exception을 신경쓰지 않아도 될때가 있다.
    public void func2() {
        Future<Integer> f = exec.submit(planarMap::chromaticNumber);
        int numColors = 4; // Default; guaranteed sufficient for any map
        try {
            numColors = f.get(1L, TimeUnit.SECONDS);
        } catch (TimeoutException | ExecutionException | InterruptedException ignored) {
            // 왜 무시해도 되는지에 대한 코멘트를 달아주자.
        }
    }

    // (개인적인 생각)
    // 절대적인건 없다. 내가 개발하는 서비스의 특징과 기능의 설계와 기획에 따라서 천차만별일듯.
    // ex) 데이터의 동기화나 싱킹이 굉장히 중요한 서비스(은행송금, 회원가입 등등)에 Write API라면?
    // 예외를 잘 처리하여서 데이터가 일그러지지 않게 해야하고, 사용자에게 어떤 예외가 났는지 명시적으로 안내해야 할듯
    // ex) 실시간 메트릭 정보를 보여주는 모니터링 제품에 5초주기로 동시 호출하는 Read API라면?
    // 드문 예외로 인해, API 예외를 처리해서 사용자에게 서비스 이용을 막는것보단 어느정도 예외를 허용해주고
    // 정보를 계속 봐야하는 사용자의 니즈 특성상 이어서 다음 호출을 처리하는 타임아웃 정책을 사용하는게 좋을 듯
    // ex) 비동기적으로 백그라운드에서 도는 스레드는? 에러 로그를 잘남겨야하지..
    // ex) 다른 외부 API를 호출하는 경우?


}
