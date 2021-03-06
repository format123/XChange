package org.knowm.xchange.gateio.dto.trade;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.gateio.dto.GateioOrderType;

import com.fasterxml.jackson.databind.ObjectMapper;

public class GateioTradeJsonTest {

  @Test
  public void testDeserializeOrderList() throws IOException {

    // Read in the JSON from the example resources
    InputStream is = GateioTradeJsonTest.class.getResourceAsStream("/trade/example-order-list-data.json");

    // Use Jackson to parse it
    ObjectMapper mapper = new ObjectMapper();
    GateioOpenOrders openOrders = mapper.readValue(is, GateioOpenOrders.class);

    assertThat(openOrders.isResult()).isTrue();
    assertThat(openOrders.getMessage()).isEqualTo("Success");

    List<GateioOpenOrder> openOrderList = openOrders.getOrders();
    Assertions.assertThat(openOrderList).hasSize(1);

    GateioOpenOrder openOrder = openOrderList.get(0);
    assertThat(openOrder.getId()).isEqualTo("12941907");
    assertThat(openOrder.getSellCurrency()).isEqualTo("LTC");
    assertThat(openOrder.getBuyCurrency()).isEqualTo("BTC");
    assertThat(openOrder.getSellAmount()).isEqualTo("0.384");
    assertThat(openOrder.getBuyAmount()).isEqualTo("0.010176");
  }

  @Test
  public void testDeserializeOrderResult() throws IOException {

    // Read in the JSON from the example resources
    InputStream is = GateioTradeJsonTest.class.getResourceAsStream("/trade/example-order-result-data.json");

    // Use Jackson to parse it
    ObjectMapper mapper = new ObjectMapper();
    GateioPlaceOrderReturn orderReturn = mapper.readValue(is, GateioPlaceOrderReturn.class);

    assertThat(orderReturn.isResult()).isTrue();
    assertThat(orderReturn.getMessage()).isEqualTo("Success");
    assertThat(orderReturn.getOrderId()).isEqualTo("123456");
  }

  @Test
  public void testDeserializeOrderStatus() throws IOException {

    // Read in the JSON from the example resources
    InputStream is = GateioTradeJsonTest.class.getResourceAsStream("/trade/example-order-status-data.json");

    // Use Jackson to parse it
    ObjectMapper mapper = new ObjectMapper();
    GateioOrderStatus orderStatus = mapper.readValue(is, GateioOrderStatus.class);

    assertThat(orderStatus.isResult()).isTrue();
    assertThat(orderStatus.getMessage()).isEqualTo("Success");
    assertThat(orderStatus.getId()).isEqualTo("12942570");
    assertThat(orderStatus.getStatus()).isEqualTo("open");
    assertThat(orderStatus.getCurrencyPair()).isEqualTo(CurrencyPair.LTC_BTC);
    assertThat(orderStatus.getType()).isEqualTo(GateioOrderType.SELL);
    assertThat(orderStatus.getRate()).isEqualTo("0.0265");
    assertThat(orderStatus.getAmount()).isEqualTo("0.384");
    assertThat(orderStatus.getInitialRate()).isEqualTo("0.0265");
    assertThat(orderStatus.getInitialAmount()).isEqualTo("0.384");
  }
}
