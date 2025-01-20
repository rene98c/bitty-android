package com.rene98c.bittyprice.core;

public class BitcoinPriceDisplay implements NumbersDisplay {

    @Override
    public String getText() {
        double price = new PriceApiClient().getPrice();
        return "$"+((int)price);
    }
    @Override
    public int UpdateFrequency() {
        return 4 * 1000;
    }
}
