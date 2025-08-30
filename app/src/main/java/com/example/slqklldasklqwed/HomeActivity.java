package com.example.slqklldasklqwed;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class HomeActivity extends AppCompatActivity {

    DBManager db;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        db = new DBManager(this);
        prefs = getSharedPreferences("MojePrefs", MODE_PRIVATE);

        int userId = prefs.getInt("userID", -1);

        // Dohvati ime i prezime korisnika iz baze
        SQLiteDatabase dbRead = db.getReadableDatabase();
        Cursor cursor = dbRead.rawQuery("SELECT imePrezime FROM Korisnik WHERE id=?",
                new String[]{String.valueOf(userId)});
        if(cursor.moveToFirst()){
            String imePrezime = cursor.getString(0);
            toolbar.setTitle("Dobrodošli nazad, " + imePrezime + "!");
        }
        cursor.close();
    }

    // Dodavanje menija u Toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }


    // Klik na stavku menija
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menu_moje_beleske){
            // Otvori BeleškeFragment
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, new BeleskeFragment())
                    .addToBackStack(null)
                    .commit();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
