package org.springframework.samples.petclinic.prox;

import org.junit.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StoreTest {

    @Test
    public void testPay() {
        Payment cashPerf = new Cash();
        Store store = new Store(cashPerf);
        store.butSomething(100);
    }

}
