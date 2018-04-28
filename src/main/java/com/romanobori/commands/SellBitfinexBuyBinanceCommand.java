package com.romanobori.commands;

import com.binance.api.client.domain.OrderSide;
import com.binance.api.client.domain.OrderType;
import com.binance.api.client.domain.TimeInForce;
import com.binance.api.client.domain.account.NewOrder;
import com.binance.api.client.impl.BinanceApiRestClientImpl;
import com.bitfinex.client.BitfinexClient;
import com.github.jnidzwetzki.bitfinex.v2.BitfinexApiBroker;
import com.github.jnidzwetzki.bitfinex.v2.entity.APIException;
import com.github.jnidzwetzki.bitfinex.v2.manager.OrderManager;
import com.romanobori.*;
import com.romanobori.datastructures.ARBTradeAction;
import com.romanobori.datastructures.ArbOrderEntry;
import com.romanobori.datastructures.ConditionStatus;
import com.romanobori.datastructures.NewArbOrderLimit;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class SellBitfinexBuyBinanceCommand extends ArbCommand {

    BitfinexClientApi bitfinexClient;
    String symbol;
    BitfinexOrderBookUpdated bitfinexOrderBookUpdated;
    BinanceOrderBookUpdated binanceOrderBookUpdated;
    BinanceApiRestClientImpl binanceClient;
    String bitfinexKey;
    String bitfinexSecret;
    String binanceKey;
    String binanceSecret;
    public SellBitfinexBuyBinanceCommand(int count, String symbol, String binanceKey, String binanceSecret, String bitfinexKey, String bitfinexSecret, BinanceOrderBookUpdated binanceOrderBookUpdated, BitfinexOrderBookUpdated bitfinexOrderBookUpdated) {
        super(count);
        this.bitfinexClient = new BitfinexClientApi(new BitfinexClient(bitfinexKey, bitfinexSecret));
        this.symbol = symbol;
        this.binanceOrderBookUpdated = binanceOrderBookUpdated;
        this.bitfinexOrderBookUpdated = bitfinexOrderBookUpdated;
        this.binanceClient = new BinanceApiRestClientImpl(binanceKey, binanceSecret);
        this.bitfinexKey = bitfinexKey;
        this.bitfinexSecret = bitfinexSecret;
        this.binanceKey = binanceKey;
        this.binanceSecret = binanceSecret;


    }

    @Override
    LimitOrderDetails firstOrder() {
        ArbOrderEntry bestAsk = bitfinexOrderBookUpdated.getLowestAsk();
        String orderId = bitfinexClient.addArbOrder( new NewArbOrderLimit(
                symbol, ARBTradeAction.SELL, 0.2, bestAsk.getPrice()
        ));
        return new LimitOrderDetails(orderId,bestAsk.getPrice());

    }

    @Override
    Supplier<ConditionStatus> placeOrderCondition() {
        return () -> {
            double binanceLowestAsk = binanceOrderBookUpdated.getLowestAsk().getPrice();
            double bitfinexLowestAsk = bitfinexOrderBookUpdated.getLowestAsk().getPrice();
            return new ConditionStatus(
                    binanceLowestAsk * 1.002504 <= bitfinexLowestAsk,
                    binanceLowestAsk, bitfinexLowestAsk);
        };
    }

    @Override
    Function<Double,ConditionStatus> keepOrderCondition() {
        return (myAskPrice) -> {
            double binanceLowestAsk = binanceOrderBookUpdated.getLowestAsk().getPrice();
            double bitfinexLowestAsk = myAskPrice;
            return new ConditionStatus(
                    binanceLowestAsk * 1.002504 <= bitfinexLowestAsk
                    && myAskPrice == bitfinexOrderBookUpdated.getLowestAsk().getPrice(),
                    binanceLowestAsk, bitfinexLowestAsk);
        };
    }

    @Override
    Consumer<String> cancelOrder() {
        return (orderId ->{
            BitfinexApiBroker client = new BitfinexApiBroker(bitfinexKey, bitfinexSecret);
            try {
                client.connect();
                OrderManager orderManager = client.getOrderManager();
                orderManager.cancelOrder(Long.parseLong(orderId));
            } catch (APIException e) {
                throw new RuntimeException("could not connect to bitfinex client");
            }
        });
    }

    @Override
    Runnable secondOrder() {
        return () -> binanceClient.newOrder(new NewOrder(
                symbol, OrderSide.BUY, OrderType.MARKET, TimeInForce.GTC, "0.2"
        ));
    }

    @Override
    OrderSuccessCallback getOrderSuccessCallback() {
        return new OrderSuccessCallbackBitfinex(new BitfinexApiBroker(bitfinexKey, bitfinexSecret));
    }

    @Override
    ArbCommand buildAnotherCommand(int count) {

        return new SellBitfinexBuyBinanceCommand(
                count, symbol, binanceKey, binanceSecret, bitfinexKey, bitfinexSecret, binanceOrderBookUpdated, bitfinexOrderBookUpdated);
    }

    @Override
    String type() {
        return "SellBitfinexBuyBinanceCommand";
    }
}