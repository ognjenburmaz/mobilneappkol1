package com.example.slqklldasklqwed;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginActivity extends AppCompatActivity {

    EditText etEmail, etLozinka;
    Button btnPrijavi;
    DBManager db;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etLoginEmail);
        etLozinka = findViewById(R.id.etLoginLozinka);
        btnPrijavi = findViewById(R.id.btnPrijavi);

        db = new DBManager(this);
        prefs = getSharedPreferences("MojePrefs", MODE_PRIVATE);

        btnPrijavi.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String lozinka = etLozinka.getText().toString().trim();

            int userId = proveriKorisnika(email, lozinka);
            if(userId != -1){
                // Sačuvaj userID u SharedPreferences
                prefs.edit().putInt("userID", userId).apply();
                // Prelazak na HomeActivity
                startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Pogrešni podaci.", Toast.LENGTH_SHORT).show();
            }
        });

        View rootLayout = findViewById(R.id.rootLayout);

//        btnPrijavi.setOnClickListener(v -> {
//            // Menjaj boju pozadine
//            rootLayout.setBackgroundColor(Color.RED);
//
//            // Ostatak tvoje login logike ide ovde
//        });
    }

    private int proveriKorisnika(String email, String lozinka){
        SQLiteDatabase dbRead = db.getReadableDatabase();
        Cursor cursor = dbRead.rawQuery("SELECT id FROM Korisnik WHERE email=? AND lozinka=?",
                new String[]{email, lozinka});
        int id = -1;
        if(cursor.moveToFirst()){
            id = cursor.getInt(0);
        }
        cursor.close();
        return id;
    }





}
