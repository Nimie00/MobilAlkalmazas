package com.nimie.myapplication4;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        TextView textView = findViewById(R.id.displayTextView);

        if(currentUser != null) {
            textView.setText("Bejelentkezve :" + currentUser.getEmail() + "-ként.");
        } else {
            textView.setText("Nem vagy bejelentkezve.");
        }


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        onPrepareOptionsMenu(bottomNavigationView.getMenu());
        bottomNavigationView.setSelectedItemId(R.id.nav_login);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_login) {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                return true;
            } else if (id == R.id.nav_register) {
                startActivity(new Intent(MainActivity.this, RegisterActivity.class));
                return true;
            } else if (id == R.id.nav_exchange) {
                startActivity(new Intent(MainActivity.this, ExchangeActivity.class));
                return true;
            } else if (id == R.id.nav_saved_times) {
                startActivity(new Intent(MainActivity.this, SavedActivity.class));
                return true;
            } else if (id == R.id.nav_logout) {
                auth.signOut();
                startActivity(new Intent(MainActivity.this, MainActivity.class));
                return true;
            } else {
                return false;
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
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
