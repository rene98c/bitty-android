package com.rene98c.bittyprice.core;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public  abstract  class TotalAbstractDisplay implements NumbersDisplay {

    private final List<PortfolioItem> items;
    private final PriceApiClient apiClient;
    private final List<String> usdtPairs;
    private final String conversionCurrency = "USDT";
    private final String bitcoinDollarSymbol;
    private final boolean userHasBitcoin;
    private static final DecimalFormat bitcoinDecimalFormat = new DecimalFormat("0.00000");
    public TotalAbstractDisplay(Context context){
        PersistentAppObject appObject = new PersistentAppObject(context);
        apiClient = new PriceApiClient();
        items = appObject.readPortfolio();
        usdtPairs = items.stream().map(s->String.format("%s%s",s.ticker,conversionCurrency)).collect(Collectors.toList());
        bitcoinDollarSymbol =String.format("BTC%s",conversionCurrency);
        userHasBitcoin = usdtPairs.contains(bitcoinDollarSymbol);
        if(!calculateToDollars() && !userHasBitcoin){
             //add btc dollar symbol to calculate total in bitcoin
            usdtPairs.add(bitcoinDollarSymbol);
        }
    }

    protected abstract boolean calculateToDollars();

    public String getText() {
        JSONArray prices = apiClient.getPrices(usdtPairs);
        double bitcoinPrice = 0;
        double total = 0;
        for (int i = 0; i < prices.length(); i++) {
            try {
                JSONObject tickerPriceJson = prices.getJSONObject(i);
                double price = tickerPriceJson.getDouble("price");
                String symbol = tickerPriceJson.getString("symbol");
                String ticker = symbol.substring(0,symbol.indexOf(conversionCurrency));
                if(symbol.equals(bitcoinDollarSymbol)){
                    bitcoinPrice = price;
                    if(!userHasBitcoin) continue; //theres no portfolio entry so skip it, it was just added for bitcoin price calculation
                }
                PortfolioItem portfolioItem = items.stream().filter(x -> Objects.equals(x.ticker, ticker)).findFirst().orElseThrow();
                double amount = portfolioItem.amount;
                double itemTotalInUsd = amount * price;
                total += itemTotalInUsd;

            } catch (JSONException ex){
                throw new RuntimeException(ex);//well, this can be designed better  should only allow entering like ALGO BTC  not pairs
            }
        }
        if(calculateToDollars()){
            return String.format("$%s",Math.round(total));
        }

        double priceInBitcoin = total/bitcoinPrice;

        return String.format("₿%s",bitcoinDecimalFormat.format(priceInBitcoin));

    }

    public int UpdateFrequency() {
        return 15 * 1000;
    }
}
