package org.knowm.xchange.gateio;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order.OrderType;
import org.knowm.xchange.dto.account.Wallet;
import org.knowm.xchange.dto.marketdata.OrderBook;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.knowm.xchange.dto.marketdata.Trade;
import org.knowm.xchange.dto.marketdata.Trades;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.dto.trade.OpenOrders;
import org.knowm.xchange.gateio.dto.account.GateioFunds;
import org.knowm.xchange.gateio.dto.marketdata.GateioCurrencyPairs;
import org.knowm.xchange.gateio.dto.marketdata.GateioDepth;
import org.knowm.xchange.gateio.dto.marketdata.GateioTicker;
import org.knowm.xchange.gateio.dto.marketdata.GateioTickers;
import org.knowm.xchange.gateio.dto.marketdata.GateioTradeHistory;
import org.knowm.xchange.gateio.dto.trade.GateioOpenOrders;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GateioAdapterTest {

  Collection<CurrencyPair> currencyPairs;

  @Before
  public void before() throws JsonParseException, JsonMappingException, IOException {

    // Read in the JSON from the example resources
    InputStream is = GateioAdapterTest.class.getResourceAsStream("/marketdata/example-pairs-data.json");

    // Use Jackson to parse it
    ObjectMapper mapper = new ObjectMapper();
    GateioCurrencyPairs currencyPairs = mapper.readValue(is, GateioCurrencyPairs.class);

    this.currencyPairs = currencyPairs.getPairs();
  }

  @Test
  public void testAdaptOpenOrders() throws IOException {

    // Read in the JSON from the example resources
    InputStream is = GateioAdapterTest.class.getResourceAsStream("/trade/example-order-list-data.json");

    // Use Jackson to parse it
    ObjectMapper mapper = new ObjectMapper();
    GateioOpenOrders openOrders = mapper.readValue(is, GateioOpenOrders.class);

    OpenOrders adaptedOpenOrders = GateioAdapters.adaptOpenOrders(openOrders, currencyPairs);

    List<LimitOrder> adaptedOrderList = adaptedOpenOrders.getOpenOrders();
    assertThat(adaptedOrderList).hasSize(1);

    LimitOrder adaptedOrder = adaptedOrderList.get(0);
    assertThat(adaptedOrder.getType()).isEqualTo(OrderType.ASK);
    assertThat(adaptedOrder.getOriginalAmount()).isEqualTo("0.384");
    assertThat(adaptedOrder.getCurrencyPair()).isEqualTo(CurrencyPair.LTC_BTC);
    assertThat(adaptedOrder.getId()).isEqualTo("12941907");
    assertThat(adaptedOrder.getTimestamp()).isNull();
    assertThat(adaptedOrder.getLimitPrice().doubleValue())
        .isEqualTo(new BigDecimal("0.010176").divide(new BigDecimal("0.384"), RoundingMode.HALF_EVEN).doubleValue());
  }

  @Test
  public void testAdaptTrades() throws IOException {

    InputStream is = GateioAdapterTest.class.getResourceAsStream("/marketdata/example-trades-data.json");

    // Use Jackson to parse it
    ObjectMapper mapper = new ObjectMapper();
    GateioTradeHistory tradeHistory = mapper.readValue(is, GateioTradeHistory.class);

    Trades adaptedTrades = GateioAdapters.adaptTrades(tradeHistory, CurrencyPair.BTC_CNY);

    List<Trade> tradeList = adaptedTrades.getTrades();
    assertThat(tradeList).hasSize(2);

    Trade trade = tradeList.get(0);
    assertThat(trade.getType()).isEqualTo(OrderType.ASK);
    assertThat(trade.getOriginalAmount()).isEqualTo("0.0129");
    assertThat(trade.getCurrencyPair()).isEqualTo(CurrencyPair.BTC_CNY);
    assertThat(trade.getPrice()).isEqualTo("3942");
    assertThat(trade.getTimestamp()).isEqualTo(new Date(1393908191000L));
    assertThat(trade.getId()).isEqualTo("5600118");
  }

  @Test
  public void testAdaptAccountInfo() throws IOException {

    // Read in the JSON from the example resources
    InputStream is = GateioAdapterTest.class.getResourceAsStream("/account/example-funds-data.json");

    // Use Jackson to parse it
    ObjectMapper mapper = new ObjectMapper();
    GateioFunds funds = mapper.readValue(is, GateioFunds.class);

    Wallet wallet = GateioAdapters.adaptWallet(funds);

    assertThat(wallet.getBalances()).hasSize(4);
    assertThat(wallet.getBalance(Currency.BTC).getTotal()).isEqualTo("0.00010165");

    assertThat(wallet.getBalance(Currency.BTC).getAvailable()).isEqualTo(new BigDecimal("0.00010165"));
    assertThat(wallet.getBalance(Currency.BTC).getFrozen()).isEqualTo(BigDecimal.ZERO);
    assertThat(wallet.getBalance(Currency.LTC).getAvailable()).isEqualTo(new BigDecimal("0.00166859"));
    assertThat(wallet.getBalance(Currency.LTC).getFrozen()).isEqualTo(new BigDecimal("0.384"));
  }

  @Test
  public void testAdaptTicker() throws IOException {

    // Read in the JSON from the example resources
    InputStream is = GateioAdapterTest.class.getResourceAsStream("/marketdata/example-tickers-data.json");

    // Use Jackson to parse it
    ObjectMapper mapper = new ObjectMapper();
    GateioTickers tickers = mapper.readValue(is, GateioTickers.class);

    Map<CurrencyPair, GateioTicker> tickerMap = tickers.getTickerMap();

    Ticker ticker = GateioAdapters.adaptTicker(CurrencyPair.BTC_CNY, tickerMap.get(CurrencyPair.BTC_CNY));
    assertThat(ticker.getLast()).isEqualTo("3400.01");
    assertThat(ticker.getHigh()).isEqualTo("3497.41");
    assertThat(ticker.getLow()).isEqualTo("3400.01");
    assertThat(ticker.getAsk()).isEqualTo("3400.17");
    assertThat(ticker.getBid()).isEqualTo("3400.01");
    assertThat(ticker.getVolume()).isEqualTo("347.2045");
  }

  @Test
  public void testAdaptOrderBook() throws IOException {

    // Read in the JSON from the example resources
    InputStream is = GateioAdapterTest.class.getResourceAsStream("/marketdata/example-depth-data.json");

    // Use Jackson to parse it
    ObjectMapper mapper = new ObjectMapper();
    GateioDepth depth = mapper.readValue(is, GateioDepth.class);

    OrderBook orderBook = GateioAdapters.adaptOrderBook(depth, CurrencyPair.LTC_BTC);

    List<LimitOrder> asks = orderBook.getAsks();
    assertThat(asks).hasSize(3);

    LimitOrder ask = asks.get(0);
    assertThat(ask.getLimitPrice()).isEqualTo("0.1785");
    assertThat(ask.getOriginalAmount()).isEqualTo("1324.111");
    assertThat(ask.getType()).isEqualTo(OrderType.ASK);
    assertThat(ask.getTimestamp()).isNull();
    assertThat(ask.getCurrencyPair()).isEqualTo(CurrencyPair.LTC_BTC);
  }
}
