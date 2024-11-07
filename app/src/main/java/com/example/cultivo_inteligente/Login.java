package com.example.cultivo_inteligente;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Login extends AppCompatActivity {

    private EditText etEmail, etSenha;
    private Button btnLogar, btnVoltar;
    private DatabaseHelper dbHelper;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);

        etEmail = findViewById(R.id.etEmail);
        etSenha = findViewById(R.id.etSenha);
        btnLogar = findViewById(R.id.btnLogar);
        btnVoltar = findViewById(R.id.btnVoltar);

        dbHelper = new DatabaseHelper(this);

        // Login de usuário
        btnLogar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString().trim();
                String senha = etSenha.getText().toString().trim();

                // Valida se os campos não estão vazios
                if (email.isEmpty() || senha.isEmpty()) {
                    Toast.makeText(Login.this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Verifica se o login é do administrador
                Cursor userRes = null;
                if (email.equals("adm@gmail.com") && senha.equals("123")) {
                    Toast.makeText(Login.this, "Login do Administrador bem-sucedido", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Login.this, Administrador.class)); // Uma nova atividade para o admin
                    finish();
                } else {
                    // Se o login do admin falhar, tente o login comum
                    userRes = dbHelper.loginUsuario(email, senha);
                    if (userRes != null && userRes.getCount() > 0) {
                        Toast.makeText(Login.this, "Login de Usuário bem-sucedido", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Login.this, Principal.class));
                        finish();
                    } else {
                        Toast.makeText(Login.this, "Usuário ou senha incorretos", Toast.LENGTH_SHORT).show();
                    }
                }

                // Fechar o cursor após o uso
                if (userRes != null) {
                    userRes.close();
                }
            }
        });

        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}