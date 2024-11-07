package com.example.cultivo_inteligente;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Feedback extends AppCompatActivity {

    private EditText etComentario;
    private RatingBar ratingBar;
    private Button btnEnviarFeedback, btnVoltar;
    private DatabaseHelper dbHelper;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        etComentario = findViewById(R.id.etComentario);
        ratingBar = findViewById(R.id.ratingBar);
        btnEnviarFeedback = findViewById(R.id.btnEnviarFeedback);
        btnVoltar = findViewById(R.id.btnVoltar);
        dbHelper = new DatabaseHelper(this);

        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Feedback.this, Tutorial.class);
                startActivity(intent);
                finish();
            }
        });

        btnEnviarFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comentario = etComentario.getText().toString();
                float avaliacao = ratingBar.getRating();

                if (!comentario.isEmpty()) {
                    // Inserir feedback no banco de dados
                    boolean sucesso = dbHelper.inserirFeedback(comentario, avaliacao);

                    if (sucesso) {
                        Toast.makeText(Feedback.this, "Feedback enviado com sucesso!", Toast.LENGTH_SHORT).show();
                        etComentario.setText("");
                        ratingBar.setRating(0);
                    } else {
                        Toast.makeText(Feedback.this, "Erro ao enviar feedback.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(Feedback.this, "Por favor, insira um comentário.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}