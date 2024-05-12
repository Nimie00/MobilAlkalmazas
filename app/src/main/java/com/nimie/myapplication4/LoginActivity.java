package com.nimie.myapplication4;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FirebaseAuth auth = FirebaseAuth.getInstance();

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        Button loginButton = findViewById(R.id.loginButton);

        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Email és jelszó megadása kötelező", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(LoginActivity.this, task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            if (user != null) {
                                // Sikeres bejelentkezés
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                finish();
                            } else {
                                // Hiba történt a bejelentkezés során
                                Toast.makeText(LoginActivity.this, "Hiba történt a bejelentkezés során", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // Hiba történt a bejelentkezés során
                            Toast.makeText(LoginActivity.this, "A bejelentkezés sikertelen volt", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        onPrepareOptionsMenu(bottomNavigationView.getMenu());
        bottomNavigationView.setSelectedItemId(R.id.nav_login);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_login) {
                startActivity(new Intent(LoginActivity.this, LoginActivity.class));
                return true;
            } else if (id == R.id.nav_register) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                return true;
            } else if (id == R.id.nav_exchange) {
                startActivity(new Intent(LoginActivity.this, ExchangeActivity.class));
                return true;
            } else if (id == R.id.nav_saved_times) {
                startActivity(new Intent(LoginActivity.this, SavedActivity.class));
                return true;
            } else if (id == R.id.nav_logout) {
                auth.signOut();
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
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

        loginItem.setChecked(true);
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
