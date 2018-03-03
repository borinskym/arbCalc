import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.impl.BinanceApiRestClientImpl;
import com.romanobori.PropertyHandler;
import org.junit.BeforeClass;

import java.io.IOException;
import java.util.Properties;

public class BinanceClientTest {
//
//
    private static String apiKey;
    private static String apiSecret;
    private final BinanceApiRestClient client = new BinanceApiRestClientImpl(apiKey, apiSecret);


    @BeforeClass
    public static void setup() throws IOException {
        System.out.println("Working Directory = " +
                System.getProperty("user.dir"));
        Properties p = PropertyHandler.loadProps("src/test/resources/props");
        apiKey = p.getProperty("BINANCE_API_KEY");
        apiSecret = p.getProperty("BINANCE_API_SECRET");
    }


//    @Test
//    public void getListenKey() {
//        System.out.println(client.startUserDataStream());
//    }
//    @Test
//    public void pr(){
//        System.out.println(apiKey);
//    }`
//    @Test
//    public void getOpenOrders(){
//        System.out.println(
//                client.getOrderBook("NEOBTC", 1000)
//        );
//    }
//
//    @Test
//    public void getMyOrders() throws NoSuchAlgorithmException, InvalidKeyException, IOException {
//        System.out.println(
//                client.getOpenOrders(new OrderRequest("VIBEETH"))
//        );
//    }
//    @Test
//    public void newOrder(){
//        client.newOrder(new NewOrder("VIBEETH", OrderSide.SELL, OrderType.LIMIT, TimeInForce.GTC,
//                "126", "0.0021550"));
//    }
//
//    @Test
//    public void removeOrder(){
//        client.cancelOrder(new CancelOrderRequest("VIBEETH",new Long(1273711)));
//    }
}
