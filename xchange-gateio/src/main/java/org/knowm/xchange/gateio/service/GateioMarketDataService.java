package org.knowm.xchange.gateio.service;

import java.io.IOException;

import org.knowm.xchange.Exchange;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.OrderBook;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.knowm.xchange.dto.marketdata.Trades;
import org.knowm.xchange.gateio.GateioAdapters;
import org.knowm.xchange.gateio.dto.marketdata.GateioDepth;
import org.knowm.xchange.gateio.dto.marketdata.GateioTicker;
import org.knowm.xchange.gateio.dto.marketdata.GateioTradeHistory;
import org.knowm.xchange.service.marketdata.MarketDataService;

public class GateioMarketDataService extends GateioMarketDataServiceRaw implements MarketDataService {

  /**
   * Constructor
   *
   * @param exchange
   */
  public GateioMarketDataService(Exchange exchange) {

    super(exchange);
  }

  @Override
  public Ticker getTicker(CurrencyPair currencyPair, Object... args) throws IOException {

    GateioTicker ticker = super.getGateioTicker(currencyPair.base.getCurrencyCode(), currencyPair.counter.getCurrencyCode());

    return GateioAdapters.adaptTicker(currencyPair, ticker);
  }

  @Override
  public OrderBook getOrderBook(CurrencyPair currencyPair, Object... args) throws IOException {

    GateioDepth gateioDepth = super.getGateioOrderBook(currencyPair.base.getCurrencyCode(), currencyPair.counter.getCurrencyCode());

    return GateioAdapters.adaptOrderBook(gateioDepth, currencyPair);
  }

  @Override
  public Trades getTrades(CurrencyPair currencyPair, Object... args) throws IOException {

    GateioTradeHistory tradeHistory = (args != null && args.length > 0 && args[0] != null && args[0] instanceof String)
        ? super.getGateioTradeHistorySince(currencyPair.base.getCurrencyCode(), currencyPair.counter.getCurrencyCode(), (String) args[0])
        : super.getGateioTradeHistory(currencyPair.base.getCurrencyCode(), currencyPair.counter.getCurrencyCode());

    return GateioAdapters.adaptTrades(tradeHistory, currencyPair);
  }

}
