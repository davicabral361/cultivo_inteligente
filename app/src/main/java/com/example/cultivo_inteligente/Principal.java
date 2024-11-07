package com.example.cultivo_inteligente;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class Principal extends AppCompatActivity {

    private Spinner etTipoSolo, etEpocaAno;
    private TextView tvResultadoSolo, tvPrevisaoTempo;
    private Button btnPrevisaoTempo, btnVerificarSolo, btnTutorial, btnLogout;
    private boolean isBtnVerificarSoloClicked = false;
    private RequestQueue requestQueue;
    private DatabaseHelper dbHelper;

    private static final String API_KEY = "616341d3b36f3fec1d8b2bd286ee45b3"; // Substitua pela sua chave da API do OpenWeatherMap
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/weather?q=";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        etTipoSolo = findViewById(R.id.etTipoSolo);
        String[] solo = {"Arenoso", "Argiloso", "Humoso", "Calcário"};
        etEpocaAno = findViewById(R.id.etEpocaAno);
        String[] estacoes = {"Primavera", "Verão", "Outono", "Inverno"};
        tvResultadoSolo = findViewById(R.id.tvResultadoSolo);
        tvPrevisaoTempo = findViewById(R.id.tvPrevisaoTempo);
        btnVerificarSolo = findViewById(R.id.btnVerificarSolo);
        btnPrevisaoTempo = findViewById(R.id.btnPrevisaoTempo);
        btnTutorial = findViewById(R.id.btnTutorial);
        btnLogout = findViewById(R.id.btnLogout);

        // Desabilitar o segundo botão inicialmente
        btnTutorial.setEnabled(false);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, solo);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        etTipoSolo.setAdapter(adapter);

        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, estacoes);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        etEpocaAno.setAdapter(adapter2);

        // Inicializar a fila de requisições HTTP
        requestQueue = Volley.newRequestQueue(this);

        // Instanciar o banco de dados dentro do onCreate()
        dbHelper = new DatabaseHelper(this);

        // Configurar ação do botão de logout
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirecionar para a tela de login e finalizar a Activity atual
                Intent intent = new Intent(Principal.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Verificar se o solo é adequado para plantio e inserir dados
        btnVerificarSolo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tipoSolo = etTipoSolo.getSelectedItem().toString();
                String epocaAno = etEpocaAno.getSelectedItem().toString();

                // Inserir dados no banco de dados
                boolean resultado = dbHelper.inserirSolo(tipoSolo, epocaAno);
                if (resultado) {
                    Toast.makeText(Principal.this, "Solo " + tipoSolo + " adicionado para a estação " + epocaAno + ".", Toast.LENGTH_SHORT).show();

                    // Listar solos após a inserção
                    String listaSol = dbHelper.listarSols();
                    tvResultadoSolo.setText(listaSol);
                } else {
                    Toast.makeText(Principal.this, "Erro ao adicionar solo.", Toast.LENGTH_SHORT).show();
                }

                // Verificar se o solo é adequado para plantio
                Cursor cursor = dbHelper.getReadableDatabase().rawQuery(
                        "SELECT * FROM solo WHERE TIPO_SOLO = ? AND ESTACAO = ?",
                        new String[]{tipoSolo, epocaAno}
                );

                if (cursor != null && cursor.moveToFirst()) {
                    tvResultadoSolo.append("\n\n" + "Solo " + tipoSolo + " é adequado para o plantio na " + epocaAno + ".");
                } else {
                    tvResultadoSolo.append("\n\n" + "O solo pode não ser ideal para essa época.");
                }

                if (cursor != null) cursor.close();

                isBtnVerificarSoloClicked = true;
                btnTutorial.setEnabled(true); // Habilitar o segundo botão
            }
        });

        btnPrevisaoTempo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cidade = "São Paulo"; // Pode permitir o usuário digitar a cidade
                obterPrevisaoTempo(cidade);
            }
        });

        btnTutorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isBtnVerificarSoloClicked) {
                    String tipoSolo = etTipoSolo.getSelectedItem().toString();
                    String epocaAno = etEpocaAno.getSelectedItem().toString();

                    // Enviar dados para a Activity de tutorial
                    Intent intent = new Intent(Principal.this, Tutorial.class);
                    intent.putExtra("TIPO_SOLO", tipoSolo);
                    intent.putExtra("EPOCA_ANO", epocaAno);
                    startActivity(intent);
                }
            }
        });


    }

    private void obterPrevisaoTempo(String cidade) {
        String url = BASE_URL + cidade + "&appid=" + API_KEY + "&units=metric";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject main = response.getJSONObject("main");
                            JSONObject wind = response.getJSONObject("wind");
                            String temperatura = main.getString("temp");
                            String velocidadeVento = wind.getString("speed");

                            String previsao = "Temperatura: " + temperatura + "°C\n" +
                                    "Velocidade do vento: " + velocidadeVento + " m/s";
                            tvPrevisaoTempo.setText(previsao);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("API_ERROR", "Erro na API: " + error.getMessage());
                tvPrevisaoTempo.setText("Erro ao obter previsão do tempo.");
            }
        });

        requestQueue.add(request);
    }
}