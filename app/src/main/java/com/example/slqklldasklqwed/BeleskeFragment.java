package com.example.slqklldasklqwed;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.core.app.ActivityCompat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class BeleskeFragment extends Fragment {

    private static final int REQUEST_STORAGE_PERMISSION = 123;

    ListView lvBeleske;
    Button btnDodaj, btnFiltriraj;
    DBManager db;
    SharedPreferences prefs;
    ArrayAdapter<String> adapter;
    List<String> listaBeleski;
    boolean filtrirano = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_beleske, container, false);

        lvBeleske = view.findViewById(R.id.lvBeleske);
        btnDodaj = view.findViewById(R.id.btnDodajBelesku);
        btnFiltriraj = view.findViewById(R.id.btnFiltriraj);
        db = new DBManager(getContext());
        prefs = getContext().getSharedPreferences("MojePrefs", Context.MODE_PRIVATE);

        ucitajBeleske(false);

        btnFiltriraj.setOnClickListener(v -> {
            filtrirano = !filtrirano;
            ucitajBeleske(filtrirano);
        });

        btnDodaj.setOnClickListener(v -> {
            String permission;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // Za Android 13 i novije
                permission = Manifest.permission.READ_MEDIA_IMAGES;
            } else {
                // Za Android 12 i starije
                permission = Manifest.permission.READ_EXTERNAL_STORAGE;
            }

            if (ContextCompat.checkSelfPermission(getContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{permission}, REQUEST_STORAGE_PERMISSION);
            } else {
                otvoriFormuDodajBelesku();
            }
        });

        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                otvoriFormuDodajBelesku();
            } else {
                new AlertDialog.Builder(getContext())
                        .setTitle("Greška")
                        .setMessage("Pristup skladištu nije dozvoljen! Dozvolite pristup u podešavanjima aplikacije.")
                        .setPositiveButton("OK", null)
                        .setNegativeButton("Idi u podešavanja", (dialog, which) -> {
                            Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            intent.setData(android.net.Uri.fromParts("package", getContext().getPackageName(), null));
                            startActivity(intent);
                        })
                        .show();
            }
        }
    }

    private void ucitajBeleske(boolean samoDanas) {
        int userId = prefs.getInt("userID", -1);
        listaBeleski = new ArrayList<>();
        SQLiteDatabase dbRead = db.getReadableDatabase();

        String sql = "SELECT naslov, tekst, datum FROM Beleska WHERE userId=?";
        Cursor cursor = dbRead.rawQuery(sql, new String[]{String.valueOf(userId)});

        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
        String danas = sdf.format(new java.util.Date());

        while (cursor.moveToNext()) {
            String naslov = cursor.getString(0);
            String tekst = cursor.getString(1);
            String datum = cursor.getString(2);

            String[] delovi = datum.split("-");
            String formatiranDatum = delovi[0] + "-" +
                    String.format("%02d", Integer.parseInt(delovi[1])) + "-" +
                    String.format("%02d", Integer.parseInt(delovi[2]));

            if (samoDanas && !formatiranDatum.equals(danas)) {
                continue;
            }

            listaBeleski.add(naslov + " (" + formatiranDatum + "): " + tekst);
        }

        cursor.close();
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, listaBeleski);
        lvBeleske.setAdapter(adapter);
    }

    private void otvoriFormuDodajBelesku(){
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_dodaj_belesku, null);
        EditText etNaslov = dialogView.findViewById(R.id.etNaslov);
        EditText etTekst = dialogView.findViewById(R.id.etTekst);
        DatePicker datePicker = dialogView.findViewById(R.id.datePicker);

        new AlertDialog.Builder(getContext())
                .setTitle("Dodaj belešku")
                .setView(dialogView)
                .setPositiveButton("Sačuvaj", (dialog, which) -> {
                    String naslov = etNaslov.getText().toString();
                    String tekst = etTekst.getText().toString();
                    String datum = datePicker.getYear() + "-" +
                            (datePicker.getMonth()+1) + "-" +
                            datePicker.getDayOfMonth();

                    int userId = prefs.getInt("userID", -1);
                    SQLiteDatabase dbWrite = db.getWritableDatabase();
                    ContentValues cv = new ContentValues();
                    cv.put("naslov", naslov);
                    cv.put("tekst", tekst);
                    cv.put("datum", datum);
                    cv.put("userId", userId);
                    dbWrite.insert("Beleska", null, cv);

                    ucitajBeleske(filtrirano);
                })
                .setNegativeButton("Otkaži", null)
                .show();
    }
}