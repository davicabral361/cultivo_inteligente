package com.example.cultivo_inteligente;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class Cadastro extends AppCompatActivity {

    private EditText etNome, etEmail, etSenha;
    private Button btnCadastrar, btnVoltar;
    private DatabaseHelper dbHelper;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        etNome = findViewById(R.id.etNome);
        etEmail = findViewById(R.id.etEmail);
        etSenha = findViewById(R.id.etSenha);
        btnCadastrar = findViewById(R.id.btnCadastrar);
        btnVoltar = findViewById(R.id.btnVoltar);

        dbHelper = new DatabaseHelper(this);

        btnCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nome = etNome.getText().toString().trim();
                String email = etEmail.getText().toString().trim();
                String senha = etSenha.getText().toString().trim();

                // Valida se todos os campos foram preenchidos
                if (nome.isEmpty() || email.isEmpty() || senha.isEmpty()) {
                    Toast.makeText(Cadastro.this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Validação básica do formato do email
                if (!isValidEmail(email)) {
                    Toast.makeText(Cadastro.this, "Email inválido", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Aqui, estamos considerando que o novo usuário é um usuário comum.
                boolean insert = dbHelper.inserirUsuario(nome, email, senha, "usuario");
                if (insert) {
                    Toast.makeText(Cadastro.this, "Cadastro realizado com sucesso", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Cadastro.this, Login.class));
                    finish();
                } else {
                    Toast.makeText(Cadastro.this, "Erro ao cadastrar", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Cadastro.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    // Método para validar o formato do email
    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}