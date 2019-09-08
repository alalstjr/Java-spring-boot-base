package org.springframework.samples.petclinic.prox;

public class Store {

    Payment payment;

    public Store(Payment payment) {
        this.payment = payment;
    }

    public void butSomething(int amount) {
        payment.pay(100);
    }
}
