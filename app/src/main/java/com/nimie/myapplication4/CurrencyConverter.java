package com.nimie.myapplication4;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

class CurrencyConverter extends AppCompatActivity {
    private static final String URL = "https://latest.currency-api.pages.dev/v1/currencies/"; // Add meg az API URL-jét
    private static RequestQueue requestQueue;

    public CurrencyConverter(Context context) {
        requestQueue = Volley.newRequestQueue(context);
    }


    public static void convertCurrency(final String baseCurrency, final String targetCurrency, final double amount, final ConversionCallback callback) {
        String url = URL + baseCurrency.toLowerCase() + ".json";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, response -> {
                    try {
                        JSONObject rates = response.getJSONObject(baseCurrency.toLowerCase());
                        double exchangeRate = rates.getDouble(targetCurrency.toLowerCase());
                        double convertedAmount = amount * exchangeRate;
                        callback.onSuccess(convertedAmount);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        callback.onFailure("'"+baseCurrency + "' '"+ targetCurrency + "' '" + amount+"'");
                    }
                }, error -> {
                    error.printStackTrace();
                    callback.onFailure(error.toString());
                });

        requestQueue.add(jsonObjectRequest);
    }

    public static void getAllCurrencies(Context context, Spinner spinner) {
        String url = URL + "eur.json";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, response -> {
                    try {
                        JSONObject eur = response.getJSONObject("eur");
                        Iterator<String> keys = eur.keys();

                        ArrayList<String> currencyList = new ArrayList<>();
                        int count = 0;
                        currencyList.add("eur");
                        currencyList.add("huf");
                        currencyList.add("usd");
                        currencyList.add("gbp");
                        currencyList.add("btc");
                        currencyList.add("cad");
                        currencyList.add("aud");
                        currencyList.add("chf");
                        while (keys.hasNext()) {
                            String key = keys.next();
                            if (count >= 5 && (!Objects.equals(key, "eur") && !Objects.equals(key, "huf") && !Objects.equals(key, "usd") && !Objects.equals(key, "gbp") && !Objects.equals(key, "btc") && !Objects.equals(key, "cad") && !Objects.equals(key, "aud") && !Objects.equals(key, "chf"))) { // Az első 5 elemet kihagyjuk
                                currencyList.add(key);
                            }
                            count++;
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
                                android.R.layout.simple_spinner_item, currencyList);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner.setAdapter(adapter);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("CurrencyConverter", "JSON parsing error");
                    }
                }, error -> Log.e("CurrencyConverter", "Error: " + error.toString()));

        requestQueue.add(jsonObjectRequest);
    }

    public interface ConversionCallback {
        void onSuccess(double convertedAmount);

        void onFailure(String error);
    }
}
