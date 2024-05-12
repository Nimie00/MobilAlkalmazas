package com.nimie.myapplication4;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;


public class SavedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved);

        FirebaseAuth auth = FirebaseAuth.getInstance();

        RecyclerView savedRecyclerView = findViewById(R.id.savedRecyclerView);
        savedRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<SavedItem> savedItems = loadData();
        SavedItemAdapter adapter = new SavedItemAdapter(savedItems);
        savedRecyclerView.setAdapter(adapter);

        Button deleteLastDataButton = findViewById(R.id.deleteLastDataButton);
        deleteLastDataButton.setOnClickListener(v -> deleteLastDataFromLocalStorage());

        androidx.appcompat.widget.SwitchCompat  storageSwitch = findViewById(R.id.storageSwitch);
        Button moveDataButton = findViewById(R.id.moveDataButton);


        moveDataButton.setOnClickListener(v -> {
            if (!storageSwitch.isChecked()) {
                moveDataToFirebase();
            }
        });
        storageSwitch.setOnClickListener(v -> regeneratePage());




        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        onPrepareOptionsMenu(bottomNavigationView.getMenu());
        bottomNavigationView.setSelectedItemId(R.id.nav_saved_times);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_login) {
                startActivity(new Intent(SavedActivity.this, LoginActivity.class));
                return true;
            } else if (id == R.id.nav_register) {
                startActivity(new Intent(SavedActivity.this, RegisterActivity.class));
                return true;
            } else if (id == R.id.nav_exchange) {
                startActivity(new Intent(SavedActivity.this, ExchangeActivity.class));
                return true;
            } else if (id == R.id.nav_saved_times) {
                startActivity(new Intent(SavedActivity.this, SavedActivity.class));
                return true;
            } else if (id == R.id.nav_logout) {
                auth.signOut();
                startActivity(new Intent(SavedActivity.this, MainActivity.class));
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

        savedItem.setChecked(true);

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

    private void clearLocalStorage() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear(); // Összes adat törlése
        editor.apply(); // Menti a változásokat
    }

    private List<SavedItem> loadDataFromLocalStorage() {
        List<SavedItem> savedItems = new ArrayList<>();

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        Map<String, ?> allEntries = sharedPreferences.getAll();

// Kulcsok rendezése növekvő sorrendben
        List<Integer> sortedKeys = new ArrayList<>();
        for (String key : allEntries.keySet()) {
            try {
                sortedKeys.add(Integer.parseInt(key));
            } catch (NumberFormatException e) {
                //logolás
            }
        }
        Collections.sort(sortedKeys);

// Rendezett kulcsok alapján kiíratás
        for (Integer key : sortedKeys) {
            if(allEntries.get(String.valueOf(key)) != null){
                String stringValue = String.valueOf(key);
                String value = Objects.requireNonNull(allEntries.get(stringValue)).toString();
                savedItems.add(new SavedItem(stringValue, value));
            }
        }

        return savedItems;
    }


    private List<SavedItem> loadData() {
        androidx.appcompat.widget.SwitchCompat  storageSwitch = findViewById(R.id.storageSwitch);
        boolean useFirebase = storageSwitch.isChecked();
        if (useFirebase) {
            return loadDataFromFirebase();
        } else {
            return loadDataFromLocalStorage();
        }
    }

    private void moveDataToFirebase() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference userSavedRef = db.collection("usersaved");

            List<SavedItem> savedItems = loadDataFromLocalStorage();

            for (SavedItem savedItem : savedItems) {
                String userEmail = currentUser.getEmail(); // Az aktuális felhasználó email-címe
                String keyOfDownloaded = savedItem.getKey(); // A mentett sor kulcsa
                String dateTimeOfSave = Calendar.getInstance().getTime().toString().split("GMT")[0].trim(); // Az adatok mentésének időpontja
                String data = savedItem.getValue(); // A lementett adatok

                SavedDataItem savedDataItem = new SavedDataItem(userEmail, keyOfDownloaded, dateTimeOfSave, data);

                // Az egyedi kulcs létrehozása a felhasználó email címe és a keyofdownloaded mező alapján
                String uniqueKey = userEmail + "_" + keyOfDownloaded;

                // Az adott dokumentum elérése az egyedi kulcs alapján
                DocumentReference docRef = userSavedRef.document(uniqueKey);

                // Az adott dokumentum létrehozása vagy frissítése az adatokkal
                docRef.set(savedDataItem)
                        .addOnSuccessListener(aVoid -> {
                            // Sikeres hozzáadás vagy frissítés
                            Toast.makeText(SavedActivity.this, "Adatok sikeresen hozzáadva vagy frissítve a Firebase adatbázishoz", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            // Hiba történt
                            String errorMessage = e.getMessage();
                            Log.e("FirebaseError", "Hiba történt az adatok hozzáadása vagy frissítése közben a Firebase adatbázishoz: " + errorMessage);
                            Toast.makeText(SavedActivity.this, "Hiba történt az adatok hozzáadása vagy frissítése közben a Firebase adatbázishoz: " + errorMessage, Toast.LENGTH_SHORT).show();
                        });
            }
        } else {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show();
        }
        clearLocalStorage();
        regeneratePage();
    }



    private List<SavedItem> loadDataFromFirebase() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        final List<SavedItem> savedItemsout = new ArrayList<>();

        CollectionReference userSavedRef = db.collection("usersaved");

        userSavedRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            final List<SavedItem> savedItems = new ArrayList<>();
            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                SavedItem savedItem = documentSnapshot.toObject(SavedItem.class);
                savedItems.add(savedItem);
            }

            Map<String, String> dataMap = new HashMap<>();
            for (SavedItem savedItem : savedItems) {
                dataMap.put(savedItem.getKeyOfDownloaded(), savedItem.getData());
            }

            for (Map.Entry<String, String> entry : dataMap.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                savedItemsout.add(new SavedItem(key, value));
            }
        }).addOnFailureListener(e -> {
            Log.e("FirestoreError", "Error getting documents: " + e.getMessage());
        });
        return savedItemsout;
    }

    private void deleteLastDataFromFirebase() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference userSavedRef = db.collection("usersaved");

            // Első lekérdezés: a felhasználóhoz tartozó dokumentumok lekérése
            Query query = userSavedRef.whereEqualTo("useremail", currentUser.getEmail())
                    .orderBy("keyofdownloaded", Query.Direction.DESCENDING)
                    .limit(1);

            query.get().addOnSuccessListener(queryDocumentSnapshots -> {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    // Az utolsó dokumentum törlése
                    documentSnapshot.getReference().delete().addOnSuccessListener(aVoid -> {
                        // Sikeres törlés
                        Toast.makeText(SavedActivity.this, "Utolsó adat sikeresen törölve a Firebase adatbázisból", Toast.LENGTH_SHORT).show();
                    }).addOnFailureListener(e -> {
                        // Hiba történt a törlés közben
                        Log.e("FirebaseError", "Hiba történt az adat törlése közben a Firebase adatbázisból: " + e.getMessage());
                        Toast.makeText(SavedActivity.this, "Hiba történt az adat törlése közben a Firebase adatbázisból", Toast.LENGTH_SHORT).show();
                    });
                }
            }).addOnFailureListener(e -> {
                // Hiba történt az adatok lekérdezése közben
                Log.e("FirebaseError", "Hiba történt az adatok lekérdezése közben a Firebase adatbázisból: " + e.getMessage());
                Toast.makeText(SavedActivity.this, "Hiba történt az adatok lekérdezése közben a Firebase adatbázisból", Toast.LENGTH_SHORT).show();
            });
        } else {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show();
        }
        regeneratePage();
    }

    public interface FirestoreCallback {
        void onCallback(List<SavedItem> savedItems);
    }

    private void deleteLastDataFromLocalStorage() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        // Utolsó kulcs megszerzése
        int lastIndex = sharedPreferences.getInt("last_index", 0);
        String lastKey = String.valueOf(lastIndex);

        // Utolsó adat törlése a SharedPreferences-ből
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(lastKey); // Kulcs törlése
        editor.putInt("last_index", lastIndex - 1); // Utolsó index csökkentése
        editor.apply();

        // Az oldal újragenerálása
        regeneratePage();
    }

    private void regeneratePage() {
        Button deleteLastDataButton = findViewById(R.id.deleteLastDataButton);
        androidx.appcompat.widget.SwitchCompat  storageSwitch = findViewById(R.id.storageSwitch);
        List<SavedItem> newSavedItems;
        boolean useFirebase = storageSwitch.isChecked();
        if (useFirebase) {
            deleteLastDataButton.setOnClickListener(v -> deleteLastDataFromFirebase());
            newSavedItems = loadDataFromFirebase();
        } else {
            deleteLastDataButton.setOnClickListener(v -> deleteLastDataFromLocalStorage());
            newSavedItems = loadDataFromLocalStorage();
        }
        SavedItemAdapter adapter = new SavedItemAdapter(newSavedItems);
        RecyclerView savedRecyclerView = findViewById(R.id.savedRecyclerView);
        savedRecyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Toast.makeText(SavedActivity.this, "Üdvözlünk újra az alkalmazásban", Toast.LENGTH_SHORT).show();
    }
}


