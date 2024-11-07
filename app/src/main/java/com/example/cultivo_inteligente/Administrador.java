package com.example.cultivo_inteligente;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class Administrador extends AppCompatActivity {

    private ListView listViewUsuarios;
    private DatabaseHelper dbHelper;
    private ArrayList<String> usuariosList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administrador);

        listViewUsuarios = findViewById(R.id.listViewUsuarios);
        dbHelper = new DatabaseHelper(this);
        usuariosList = new ArrayList<>();

        loadUsuarios();
    }

    private void loadUsuarios() {
        Cursor cursor = dbHelper.listarUsuarios();
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                @SuppressLint("Range") String nome = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_NOME_USUARIO));
                @SuppressLint("Range") String email = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_EMAIL_USUARIO));
                usuariosList.add("Nome: " + nome + ", Email: " + email);
            }
            cursor.close();
        } else {
            Toast.makeText(this, "Nenhum usuário cadastrado", Toast.LENGTH_SHORT).show();
        }

        // Configura o adapter para o ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, usuariosList);
        listViewUsuarios.setAdapter(adapter);
    }
}