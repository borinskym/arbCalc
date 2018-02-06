package com.romanobori;

public interface ApiClient {

    public ArbOrders getOrderBook(String symbol);
    public MyArbOrders getMyOrders();
    public void addArbOrder(NewArbOrder order);
    public void cancelOrder(long orderId);
    public void cancelAllOrders();
    public void withdrawal(long withrawalId);
    public ArbWallet getWallet();

}
