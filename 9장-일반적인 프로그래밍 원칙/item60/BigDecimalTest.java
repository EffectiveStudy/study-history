package item60;

import java.math.BigDecimal;

public class BigDecimalTest {

    public static void main(String[] args) {
        BigDecimal bigDecimal = new BigDecimal(
            "999999999999999999999999999999999.8888888888888888888");
        System.out.println("bigDecimal = " + bigDecimal);
        Integer integer = Integer.valueOf(3);
        int b = integer + 3;
    }
}
