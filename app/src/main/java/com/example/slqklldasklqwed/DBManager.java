package com.example.slqklldasklqwed;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DBManager extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Kolokvijum1DB";
    private static final int DATABASE_VERSION = 1;

    public DBManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Tabela korisnici
        db.execSQL("CREATE TABLE Korisnik(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "imePrezime TEXT," +
                "email TEXT UNIQUE," +
                "lozinka TEXT)");

        // Tabela beleške (3 inicijalne beleške)
        db.execSQL("CREATE TABLE Beleska(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "naslov TEXT," +
                "tekst TEXT," +
                "datum TEXT," +
                "userId INTEGER)");

        // Ubacujemo 3 beleške odmah
        db.execSQL("INSERT INTO Beleska(naslov, tekst, datum, userId) VALUES" +
                "('Prva beleška','Tekst prve beleške','2025-08-29',1)," +
                "('Druga beleška','Tekst druge beleške','2025-08-29',1)," +
                "('Treća beleška','Tekst treće beleške','2025-08-29',1)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Korisnik");
        db.execSQL("DROP TABLE IF EXISTS Beleska");
        onCreate(db);
    }

}