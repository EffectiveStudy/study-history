package item9;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Item9 {

    private static int BUFFER_SIZE = 1024;

    //전통적으로 자원이 제대로 닫힘을 보장하는 수단으로 try-finally 가 쓰였다.
    static String firstLineOfFile(String path) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(path));
        try {
            return br.readLine();
        } finally {
            br.close();
        }
    }

    //try-finally 를 사용할때 자원을 닫아야 하는 항목이 늘어날 수록 코드는 보기 안좋아진다.
    static void copy(String src, String dst) throws IOException {
        InputStream in = new FileInputStream(dst);
        try {
            OutputStream out = new FileOutputStream(dst);
            try {
                byte[] buf = new byte[BUFFER_SIZE];
                int n;
                while ((n = in.read(buf)) >= 0) {
                    out.write(buf, 0, n);
                }
            } finally {
                out.close();
            }
        } finally {
            in.close();
        }
    }

    //아래와 같이 Finally에서 둘다 처리해 주면 되지 않을까 하지만, 에러가 발생하는 경우 메모리 leak으로 연결된다.
    static void copy2() throws IOException {
        BadInputStream in = new BadInputStream();
        BadOutputStream out = new BadOutputStream();
        try {
            byte[] buf = new byte[BUFFER_SIZE];
            int n;
            while ((n = in.read(buf)) >= 0) {
                out.write(buf, 0, n);
            }
        } finally {
            out.close(); // 여기서 에러가 발생하면 in 의 자원은 Leak으로 남게 된다.
            in.close();
        }
    }

    //위와 달리 정상적으로 처리 된다.
    static void copy2_safe() throws IOException {
        BadInputStream in = new BadInputStream();
        try {
            BadOutputStream out = new BadOutputStream();
            try {
                byte[] buf = new byte[BUFFER_SIZE];
                int n;
                while ((n = in.read(buf)) >= 0) {
                    out.write(buf, 0, n);
                }
            } finally {
                out.close();
            }
        } finally {
            in.close();
        }
    }

    //또 다른 문제로는 에러가 정상적으로 출력되지 않는다
    static void noReadErrorDetail() throws IOException {
        BadInputStream in = new BadInputStream();
        try {
            in.read(null); //실질적 에러 발생 지역
        } finally {
            in.close(); //2차 에러 발생 지역
        }
    }

    //try-with-resource 는 위에서 명시한 2가지 문제인 간결한 코드와, 에러의 세부사항 추적이 가능하다는 장점이 있다.
    static void copy_with_resource() throws IOException {
        try (BadInputStream in = new BadInputStream();
            BadOutputStream out = new BadOutputStream()) {
            in.read(null); //가장 근본적으로 문제가 발생한 부분을 최 상단에서 보여준다.
            out.write(null, 0, 0);
        }
    }

    static void hash_test() {
        Integer integer = Integer.valueOf(1);
        Integer integer1 = Integer.valueOf(1);
        Integer integer2 = new Integer(1);
        System.out.println(integer == integer2);
    }

    public static void main(String[] args) throws IOException {
        hash_test();
    }
}
