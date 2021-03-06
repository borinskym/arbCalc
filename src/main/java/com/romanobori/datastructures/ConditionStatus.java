package com.romanobori.datastructures;

import com.sun.org.apache.xpath.internal.operations.Bool;

public class ConditionStatus {
    private Boolean isPassed;
    private double binancePrice;
    private double bitfinexPrice;
    private double amount;

    public ConditionStatus(Boolean isPassed, double binancePrice, double bitfinexPrice, double amount) {
        this.isPassed = isPassed;
        this.binancePrice = binancePrice;
        this.bitfinexPrice = bitfinexPrice;
        this.amount = amount;
    }

    public Boolean isPassed() {
        return isPassed;
    }

    public Boolean isFailed(){
        return !isPassed;
    }

    public double getBinancePrice() {
        return binancePrice;
    }

    public double getBitfinexPrice() {
        return bitfinexPrice;
    }

    public double getAmount() {
        return amount;
    }
}
