package com.rene98c.bittyprice.core;

import java.io.Serializable;

public  class PortfolioItem implements Serializable {
    public String name;
    public float amount;
    public String ticker;


    public PortfolioItem(String name,
                         float amount,
                         String ticker)
    {
        this.name = name;
        this.amount = amount;
        this.ticker = ticker;
    }
}
