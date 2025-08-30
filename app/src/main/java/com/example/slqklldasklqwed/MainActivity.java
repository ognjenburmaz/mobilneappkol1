 package com.example.slqklldasklqwed;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    EditText etImePrezime, etEmail, etLozinka;
    Button btnRegistruj;
    DBManager db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etImePrezime = findViewById(R.id.etImePrezime);
        etEmail = findViewById(R.id.etEmail);
        etLozinka = findViewById(R.id.etLozinka);
        btnRegistruj = findViewById(R.id.btnRegistruj);

        db = new DBManager(this);
        btnRegistruj.setOnClickListener(v -> {
            String imePrezime = etImePrezime.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String lozinka = etLozinka.getText().toString().trim();

            if(emailPostoji(email)){
                Toast.makeText(this, "Email je već registrovan!", Toast.LENGTH_SHORT).show();
            } else {
                dodajKorisnika(imePrezime, email, lozinka);
                // Prelazak na LoginActivity
                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    private boolean emailPostoji(String email){
        SQLiteDatabase dbRead = db.getReadableDatabase();
        Cursor cursor = dbRead.rawQuery("SELECT * FROM Korisnik WHERE email=?", new String[]{email});
        boolean postoji = cursor.moveToFirst();
        cursor.close();
        return postoji;
    }

    private void dodajKorisnika(String imePrezime, String email, String lozinka){
        SQLiteDatabase dbWrite = db.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("imePrezime", imePrezime);
        cv.put("email", email);
        cv.put("lozinka", lozinka);
        dbWrite.insert("Korisnik", null, cv);
    }
}