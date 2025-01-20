package com.rene98c.bittyprice.core;

import android.content.Context;

public class TotalBitcoinsDisplay  extends TotalAbstractDisplay {

    public TotalBitcoinsDisplay(Context context) {
        super(context);
    }

    @Override
    protected boolean calculateToDollars() {
        return false;
    }
}
