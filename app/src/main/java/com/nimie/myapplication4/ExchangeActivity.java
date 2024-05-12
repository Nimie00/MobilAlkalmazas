package com.nimie.myapplication4;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ExchangeActivity extends AppCompatActivity {
    private EditText amountEditText;
    private Spinner sourceCurrencySpinner;
    private Spinner targetCurrencySpinner;
    private final Context context = this;
    private NestedScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange);

        FirebaseAuth auth = FirebaseAuth.getInstance();

        amountEditText = findViewById(R.id.amountEditText);
        sourceCurrencySpinner = findViewById(R.id.sourceCurrencySpinner);
        targetCurrencySpinner = findViewById(R.id.targetCurrencySpinner);

        CurrencyConverter currencyConverter = new CurrencyConverter(this);
        Log.e("Currency", currencyConverter.toString());
        CurrencyConverter.getAllCurrencies(this, sourceCurrencySpinner);
        CurrencyConverter.getAllCurrencies(this, targetCurrencySpinner);

        amountEditText = findViewById(R.id.amountEditText);
        sourceCurrencySpinner = findViewById(R.id.sourceCurrencySpinner);
        targetCurrencySpinner = findViewById(R.id.targetCurrencySpinner);


        sourceCurrencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Itt kezelhetjük az eseményt, amikor a forrás pénznemet választják ki
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        targetCurrencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Itt kezelhetjük az eseményt, amikor a célpénznemet választják ki
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        int newItemId = View.generateViewId(); // Új egyedi azonosító generálása
        LinearLayout layout = findViewById(R.id.layout); // Az XML-ben meghatározott elrendezés ID-je
        TextView resultTextView3 = new TextView(this);
        resultTextView3.setId(newItemId); // Az új egyedi azonosító beállítása
        layout.addView(resultTextView3);


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        onPrepareOptionsMenu(bottomNavigationView.getMenu());
        bottomNavigationView.setSelectedItemId(R.id.nav_exchange);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_login) {
                startActivity(new Intent(ExchangeActivity.this, LoginActivity.class));
                return true;
            } else if (id == R.id.nav_register) {
                startActivity(new Intent(ExchangeActivity.this, RegisterActivity.class));
                return true;
            } else if (id == R.id.nav_exchange) {
                startActivity(new Intent(ExchangeActivity.this, ExchangeActivity.class));
                return true;
            } else if (id == R.id.nav_saved_times) {
                startActivity(new Intent(ExchangeActivity.this, SavedActivity.class));
                return true;
            } else if (id == R.id.nav_logout) {
                auth.signOut();
                startActivity(new Intent(ExchangeActivity.this, MainActivity.class));
                return true;
            } else {
                return false;
            }
        });

        Button saveAllButton = findViewById(R.id.saveAllButton);

        saveAllButton.setOnClickListener(v -> {
            LinearLayout containerLayout = findViewById(R.id.container_layout);

            if (containerLayout.getChildCount() > 0) {
                try {
                    saveAllData();
                    animateRows();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            } else {
                Toast.makeText(this, "Nincs sor, amit el tudnánk menteni", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void convertCurrency(View view) {
        // Összeg lekérése
        String amountString = amountEditText.getText().toString();
        if (amountString.isEmpty()) {
            Toast.makeText(this, "Kérem adja meg az összeget", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountString);

        String sourceCurrency = sourceCurrencySpinner.getSelectedItem().toString();
        String targetCurrency = targetCurrencySpinner.getSelectedItem().toString();

        CurrencyConverter.convertCurrency(sourceCurrency, targetCurrency, amount, new CurrencyConverter.ConversionCallback() {

            @Override
            public void onSuccess(double convertedAmount) {
                scrollView = findViewById(R.id.scroll_view);

                String result = String.format(Locale.ENGLISH,"%.6f", convertedAmount);
                Toast.makeText(ExchangeActivity.this, amountString + " " + sourceCurrency.toUpperCase() + " = " + result + " " + targetCurrency.toUpperCase(), Toast.LENGTH_SHORT).show();
                String resultText = String.format(Locale.ENGLISH,"%s %s -> %.6f %s %s", amountString, sourceCurrency, convertedAmount, targetCurrency, Calendar.getInstance().getTime().toString().split("GMT")[0]);

                TextView newItem = new TextView(context);
                newItem.setText(resultText);


                LinearLayout containerLayout = findViewById(R.id.container_layout);
                Animation slideInBottomAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_in_bottom);
                newItem.startAnimation(slideInBottomAnimation);
                containerLayout.addView(newItem);

                if (containerLayout.getChildCount() > 5) {
                    containerLayout.removeViewAt(0);
                }

                scrollView.post(() -> scrollView.fullScroll(NestedScrollView.FOCUS_DOWN));
            }

            @Override
            public void onFailure(String error) {
                Log.e("CurrencyConverter", "Error: " + error);
                String errorMessage;
                if (error.contains("NoConnectionError") || error.contains("TimeoutError")) {
                    errorMessage = "Nincs internetkapcsolat vagy időtúllépés";
                } else {
                    errorMessage = "Hiba történt az átváltás során";
                }
                Toast.makeText(ExchangeActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        MenuItem loginItem = menu.findItem(R.id.nav_login);
        MenuItem registerItem = menu.findItem(R.id.nav_register);
        MenuItem exchangeItem = menu.findItem(R.id.nav_exchange);
        MenuItem savedItem = menu.findItem(R.id.nav_saved_times);
        MenuItem logoutItem = menu.findItem(R.id.nav_logout);

        exchangeItem.setChecked(true);
        if (currentUser != null) {
            loginItem.setVisible(false);
            registerItem.setVisible(false);
            exchangeItem.setVisible(true);
            logoutItem.setVisible(true);
            savedItem.setVisible(true);

        } else {
            loginItem.setVisible(true);
            registerItem.setVisible(true);
            exchangeItem.setVisible(false);
            logoutItem.setVisible(false);
            savedItem.setVisible(false);
        }
        return true;
    }

    private void saveAllData() {
        NestedScrollView nestedScrollView = findViewById(R.id.scroll_view);
        LinearLayout containerLayout = nestedScrollView.findViewById(R.id.container_layout);

        if (containerLayout != null) {
            List<String> allData = new ArrayList<>(); // Lista az összes adat tárolására

            // Minden egyes gyerek nézet esetén hozzáadjuk az adatot a listához
            for (int i = 0; i < containerLayout.getChildCount(); i++) {
                View childView = containerLayout.getChildAt(i);

                allData.add(((TextView) childView).getText().toString());
            }

            // Értesítés küldése a sikeres mentésről
            MyNotificationHelper.sendNotification(this, containerLayout.getChildCount());

            // Az összes adat mentése a SharedPreferences-be
            saveDataForView(allData);
        }
    }

    private void saveDataForView(List<String> allData) {
        // Az utolsó mentett elem számának lekérése
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        int lastIndex = sharedPreferences.getInt("last_index", -1); // Az alapértelmezett érték -1 legyen

        // Az összes adat mentése a SharedPreferences-be
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Az összes adat mentése a SharedPreferences-be egyedi kulcsokkal, ahol az egyedi kulcsok egyre növekvő számok
        for (int i = 0; i < allData.size(); i++) {
            int currentIndex = lastIndex + 1; // Az új kulcs indexe

            editor.putString(String.valueOf(currentIndex), allData.get(i)); // "result_" + currentIndex lesz a kulcs
            lastIndex = currentIndex; // Az utolsó index frissítése
        }

        // Az utolsó index értékének mentése
        editor.putInt("last_index", lastIndex);
        editor.apply();
    }

    private void animateRows() throws InterruptedException {
        LinearLayout containerLayout = findViewById(R.id.container_layout);
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.row_animation);

        for (int i = 0; i < containerLayout.getChildCount(); i++) {
            View row = containerLayout.getChildAt(i);
            Animation uniqueAnimation = AnimationUtils.loadAnimation(context, R.anim.row_animation); // Egyedi animáció minden egyes View objektumhoz
            row.startAnimation(uniqueAnimation);
            uniqueAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    // Az animáció elindult
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    // Az animáció befejeződött, az aktuális nézetet láthatatlanná tesszük
                    row.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                    // Az animáció megismétlődött
                }
            });
        }

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            for (int i = 0; i < containerLayout.getChildCount(); i++) {
                View row = containerLayout.getChildAt(i);
                if (row.getVisibility() == View.INVISIBLE) {
                    containerLayout.removeViewAt(i);
                    i--;
                }
            }
        }, animation.getDuration() + 1000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
