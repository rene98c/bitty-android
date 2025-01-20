package com.rene98c.bittyprice.core;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PriceApiClient {

    public JSONArray getPrices(List<String> pairs){

        String pairsString = pairs.stream().map(s -> "\"" + s + "\"").collect(Collectors.joining(","));
        String url  = String.format("https://api.binance.com/api/v3/ticker/price?symbols=[%s]", pairsString);
        String priceResponse = this.getRequest(url);
        if(priceResponse!=null)
        {
            try {
                return new JSONArray(priceResponse);
            } catch (JSONException e) {
                return  null;
            }
        }
        return  null;
    }
    public Double getPrice() {

        double price = 0;
        String priceResponse = this.getRequest("https://api.binance.com/api/v3/ticker/price?symbol=BTCUSDT");
        if(priceResponse!=null)
        {
            try {
                price = new JSONObject(priceResponse).getDouble("price");
            } catch (JSONException e) {
                price = 0;
            }
        }
        return price;
    }


    private  String getRequest(String URL){
        String response = null;
        HttpURLConnection connection = null;
        try {

            java.net.URL url = new URL(URL);

            connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");

            response = readFromConnection(connection);


        }catch (Exception e){
            return null;
        }
        finally {
            assert connection != null;
            connection.disconnect();

        }
        return response;
    }
    private    String readFromConnection(HttpURLConnection connection) throws IOException {
        StringBuilder content;
        BufferedReader input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line;
        content = new StringBuilder();
        while ((line = input.readLine()) != null) {
            content.append(line);
        }
        return content.toString();
    }

}
