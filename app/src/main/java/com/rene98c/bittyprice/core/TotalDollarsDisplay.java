package com.rene98c.bittyprice.core;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;



public class TotalDollarsDisplay extends TotalAbstractDisplay {

    public TotalDollarsDisplay(Context context) {
        super(context);
    }

    @Override
    protected boolean calculateToDollars() {
        return true;
    }
}

