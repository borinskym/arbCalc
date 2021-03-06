package com.romanobori.state;

import com.google.common.util.concurrent.AtomicDouble;
import com.romanobori.datastructures.ConditionStatus;
import com.romanobori.datastructures.LimitOrderDetails;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

public class OrderConditionObserver implements Runnable, AmountChangedObserver {
    Function<LimitOrderDetails, ConditionStatus> condition;
    private CountDownLatch countDownLatch;
    private Function<String, Boolean> cancellation;
    private String orderId;
    private AtomicDouble amount;
    private AtomicDouble price;
    private AtomicBoolean orderFilled = new AtomicBoolean(false);

    public OrderConditionObserver(Function<LimitOrderDetails, ConditionStatus> condition,
                                  Function<String, Boolean> actionIfNotMet, String orderId,
                                  CountDownLatch countDownLatch, LimitOrderDetails orderDetails) {
        this.condition = condition;
        this.cancellation = actionIfNotMet;
        this.orderId = orderId;
        this.countDownLatch = countDownLatch;
        this.amount = new AtomicDouble(orderDetails.getAmount());
        this.price = new AtomicDouble(orderDetails.getPrice());
    }

    @Override
    public void run() {
        while (!orderFilled.get()) {
            if (actionBreaked(condition)) {
                cancellation.apply(orderId);
                System.out.println("the command cancelled");
                countDownLatch.countDown();
                break;
            }
        }
    }

    private boolean actionBreaked(Function<LimitOrderDetails, ConditionStatus> condition) {
        ConditionStatus conditionStatus = condition.apply(
                new LimitOrderDetails(orderId, price.get(), amount.get())
        );
        return conditionStatus.isFailed();
    }


    @Override
    public void updateInfo(String type, double newAmount) {
        if(type.equals("FILLED")){
            System.out.println("THE ORDER IS FILLED !!!!");
            orderFilled.set(true);
            countDownLatch.countDown();
        }else if(type.equals("PARTIAL")){
            System.out.println("THE ORDER IS FILLED PARTIALLY!!!!");
            amount.set(newAmount);
        }
    }
}

