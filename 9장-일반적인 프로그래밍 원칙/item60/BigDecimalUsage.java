package item60;

import java.math.BigDecimal;

public class BigDecimalUsage {

    public void usage2(String[] args) {
        final BigDecimal TEN_CENTS = new BigDecimal(".10"); //인스턴스를 생성해 줘야 한다.
        int itemsBought = 0;
        BigDecimal funds = new BigDecimal("1.00");
        for (BigDecimal price = TEN_CENTS; funds.compareTo(price) >= 0;
            price = price.add(TEN_CENTS)) { //더해주는 부분을 함수를 이용한다.
            funds = funds.subtract(price); //빼주는 부분을 함수를 이용한다
            itemsBought++;
        }
        System.out.println(itemsBought + "개 구입");
        System.out.println("잔돈(달러): " + funds);


    }

    public void usage1(String[] args) {
        double funds = 1.00;
        int itemsBought = 0;
        for (double price = 0.10; funds >= price; price += 0.10) {
            funds -= price;
            itemsBought++;
        }
        System.out.println(itemsBought + "개 구입");
        System.out.println("잔돈(달러):" + funds);
    }

}
