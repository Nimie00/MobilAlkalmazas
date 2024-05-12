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

public class RegisterActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText, confirmPasswordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        Button registerButton = findViewById(R.id.registerButton);


        registerButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String confirmPassword = confirmPasswordEditText.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(RegisterActivity.this, "Email, jelszó és jelszó megerősítése megadása kötelező", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(RegisterActivity.this, "A jelszavak nem egyeznek", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(RegisterActivity.this, task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            if (user != null) {
                                // Sikeres regisztráció
                                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                finish();
                            } else {
                                // Hiba történt a regisztráció során
                                Toast.makeText(RegisterActivity.this, "Hiba történt a regisztráció során", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // Hiba történt a regisztráció során
                            Toast.makeText(RegisterActivity.this, "A regisztráció sikertelen volt", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        onPrepareOptionsMenu(bottomNavigationView.getMenu());
        bottomNavigationView.setSelectedItemId(R.id.nav_register);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_login) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                return true;
            } else if (id == R.id.nav_register) {
                startActivity(new Intent(RegisterActivity.this, RegisterActivity.class));
                return true;
            } else if (id == R.id.nav_exchange) {
                startActivity(new Intent(RegisterActivity.this, ExchangeActivity.class));
                return true;
            } else if (id == R.id.nav_saved_times) {
                startActivity(new Intent(RegisterActivity.this, SavedActivity.class));
                return true;
            } else if (id == R.id.nav_logout) {
                auth.signOut();
                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
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

        registerItem.setChecked(true);
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
