package com.example.cultivo_inteligente;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class Tutorial extends AppCompatActivity {

    private TextView tvRecomendacaoSemente;
    private Button btnVoltar, btnFeedback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        tvRecomendacaoSemente = findViewById(R.id.tvRecomendacaoSemente);
        btnVoltar = findViewById(R.id.btnVoltar);
        btnFeedback = findViewById(R.id.btnFeedback);

        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Tutorial.this, Principal.class);
                startActivity(intent);
                finish();
            }
        });

        btnFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Tutorial.this, Feedback.class);
                startActivity(intent);
            }
        });

        // Receber dados da Intent
        String tipoSolo = getIntent().getStringExtra("TIPO_SOLO");
        String epocaAno = getIntent().getStringExtra("EPOCA_ANO");

        // Determinar a melhor semente com base no solo e estação
        String recomendacao = obterRecomendacaoSemente(tipoSolo, epocaAno);
        tvRecomendacaoSemente.setText(recomendacao);
    }

    private String obterRecomendacaoSemente(String tipoSolo, String epocaAno) {
        switch (tipoSolo) {
            case "Arenoso":
                switch (epocaAno) {
                    case "Primavera":
                        return "Recomenda-se plantar cenoura no solo arenoso durante a primavera.";
                    case "Verão":
                        return "Recomenda-se plantar milho no solo arenoso durante o verão.";
                    case "Outono":
                        return "Recomenda-se plantar batata no solo arenoso durante o outono.";
                    case "Inverno":
                        return "Recomenda-se plantar alface no solo arenoso durante o inverno.";
                }
                break;

            case "Argiloso":
                switch (epocaAno) {
                    case "Primavera":
                        return "Recomenda-se plantar tomate no solo argiloso durante a primavera.";
                    case "Verão":
                        return "Recomenda-se plantar arroz no solo argiloso durante o verão.";
                    case "Outono":
                        return "Recomenda-se plantar beterraba no solo argiloso durante o outono.";
                    case "Inverno":
                        return "Recomenda-se plantar trigo no solo argiloso durante o inverno.";
                }
                break;

            case "Humoso":
                switch (epocaAno) {
                    case "Primavera":
                        return "Recomenda-se plantar feijão no solo humoso durante a primavera.";
                    case "Verão":
                        return "Recomenda-se plantar soja no solo humoso durante o verão.";
                    case "Outono":
                        return "Recomenda-se plantar cenoura no solo humoso durante o outono.";
                    case "Inverno":
                        return "Recomenda-se plantar espinafre no solo humoso durante o inverno.";
                }
                break;

            case "Calcário":
                switch (epocaAno) {
                    case "Primavera":
                        return "Recomenda-se plantar batata no solo calcário durante a primavera.";
                    case "Verão":
                        return "Recomenda-se plantar amendoim no solo calcário durante o verão.";
                    case "Outono":
                        return "Recomenda-se plantar alho no solo calcário durante o outono.";
                    case "Inverno":
                        return "Recomenda-se plantar cebola no solo calcário durante o inverno.";
                }
                break;
        }

        return "Nenhuma recomendação específica para o solo e estação selecionados.";
    }

}