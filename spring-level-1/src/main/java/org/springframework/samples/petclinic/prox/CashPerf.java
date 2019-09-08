package org.springframework.samples.petclinic.prox;

import org.springframework.util.StopWatch;

public class CashPerf implements Payment {

    /**
     *  CraditCard 가 한도가 없거나 문제가 생기면 Cash 로 콜백
     */
    Payment cash = new Cash();

    @Override
    public void pay(int amount) {

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        System.out.println(amount + "신용 카드");

        stopWatch.stop();
        System.out.println(stopWatch.prettyPrint());
    }
}
