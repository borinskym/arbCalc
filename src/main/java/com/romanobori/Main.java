package com.romanobori;

import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.impl.BinanceApiRestClientImpl;
import com.bitfinex.client.BitfinexClient;
import com.github.jnidzwetzki.bitfinex.v2.BitfinexApiBroker;
import com.github.jnidzwetzki.bitfinex.v2.entity.APIException;
import com.github.jnidzwetzki.bitfinex.v2.entity.BitfinexCurrencyPair;
import com.romanobori.commands.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class Main {

    public static void main(String[] args) throws ExecutionException, InterruptedException, IOException , APIException{
        Properties properties = PropertyHandler.loadProps("src/test/resources/props");

        String binanceKey = properties.getProperty("BINANCE_API_KEY");
        String binanceSecret = properties.getProperty("BINANCE_API_SECRET");
        String bitfinexKey = properties.getProperty("BITFINEX_API_KEY");
        String bitfinexSecret = properties.getProperty("BITFINEX_API_SECRET");
        String symbol = "NEOBTC";
        BitfinexCurrencyPair symbolBitfinex = BitfinexCurrencyPair.NEO_BTC;
        BitfinexOrderBookUpdated bitfinexOrderBookUpdated = new BitfinexOrderBookUpdated(
                symbol,
                new BitfinexClientApi(new BitfinexClient(bitfinexKey, bitfinexSecret)),
                new BitfinexApiBroker(bitfinexKey, bitfinexSecret),
                symbolBitfinex,false
        );

        BinanceApiRestClient binanceClient = new BinanceApiRestClientImpl(binanceKey, binanceSecret);
        String binanceListeningKey = binanceClient.startUserDataStream();
        BinanceOrderBookUpdated binanceOrderBookUpdated = new BinanceOrderBookUpdated(symbol);
        CommandsRunner commandsRunner = new CommandsRunner();
        ArbContext context = new ArbContext(
                symbol,
                binanceKey,
                binanceSecret,
                bitfinexKey,
                bitfinexSecret,
                binanceListeningKey,
                binanceOrderBookUpdated,
                bitfinexOrderBookUpdated);
        commandsRunner.start(
                Arrays.asList(
                        new BuyBinanceSellBitfinexCommand(10, context),
                        new BuyBitfinexSellBinanceCommand(10, context),
                        new SellBitfinexBuyBinanceCommand(10, context),
                        new SellBinanceBuyBitfinexCommand(10,context)
                )
        );
    }
}
