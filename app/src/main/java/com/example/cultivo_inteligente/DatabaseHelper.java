package com.example.cultivo_inteligente;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "cultivo_inteligente.db";
    private static final int DATABASE_VERSION = 3;

    // Tabela agricultores
    private static final String TABLE_AGRICULTORES = "agricultores";
    private static final String COL_ID_AGRICULTORES = "ID";
    private static final String COL_NOME_AGRICULTORES = "NOME";
    private static final String COL_EPOCAANO_AGRICULTORES = "EPOCA_ANO";

    // Tabela usuario
    private static final String TABLE_USUARIO = "usuario";
    private static final String COL_ID_USUARIO = "ID";
    public static final String COL_NOME_USUARIO = "NOME";
    public static final String COL_EMAIL_USUARIO = "EMAIL";
    private static final String COL_SENHA_USUARIO = "SENHA";
    private static final String COL_TIPO_USUARIO = "TIPO"; // Campo para diferenciar usuário e administrador

    // Tabela solo
    private static final String TABLE_SOLO = "solo";
    private static final String COL_ID_SOLO = "ID";
    private static final String COL_TIPO_SOLO = "TIPO_SOLO";
    private static final String COL_ESTACAO_SOLO = "ESTACAO";

    // Tabela feedback
    private static final String TABLE_FEEDBACK = "feedback";
    private static final String COL_ID_FEEDBACK = "ID";
    private static final String COL_COMENTARIO = "COMENTARIO";
    private static final String COL_AVALIACAO = "AVALIACAO";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Criação da tabela solo
        db.execSQL("CREATE TABLE " + TABLE_SOLO + " (" +
                COL_ID_SOLO + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_TIPO_SOLO + " TEXT, " +
                COL_ESTACAO_SOLO + " TEXT)");

        // Criação da tabela agricultores com referência à tabela solo
        db.execSQL("CREATE TABLE " + TABLE_AGRICULTORES + " (" +
                COL_ID_AGRICULTORES + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NOME_AGRICULTORES + " TEXT, " +
                "ID_SOLO INTEGER, " +
                COL_EPOCAANO_AGRICULTORES + " TEXT, " +
                "FOREIGN KEY(ID_SOLO) REFERENCES " + TABLE_SOLO + "(" + COL_ID_SOLO + "))");

        // Criação da tabela usuario
        db.execSQL("CREATE TABLE " + TABLE_USUARIO + " (" +
                COL_ID_USUARIO + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NOME_USUARIO + " TEXT, " +
                COL_EMAIL_USUARIO + " TEXT, " +
                COL_SENHA_USUARIO + " TEXT, " +
                COL_TIPO_USUARIO + " TEXT)");

        db.execSQL("CREATE TABLE " + TABLE_FEEDBACK + " (" +
                COL_ID_FEEDBACK + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_COMENTARIO + " TEXT, " +
                COL_AVALIACAO + " REAL)");

        Log.d("DatabaseHelper", "Tabelas criadas com sucesso");
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        db.execSQL("PRAGMA foreign_keys=ON;"); // Habilita chaves estrangeiras
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_AGRICULTORES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SOLO);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USUARIO);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FEEDBACK);
        onCreate(db);
    }

    // Inserir dados na tabela solo
    public boolean inserirSolo(String tipoSolo, String epocaAno) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("TIPO_SOLO", tipoSolo);
        values.put("ESTACAO", epocaAno);

        // Tente inserir e cheque se o ID retornado é -1 (falha)
        long result = db.insert("solo", null, values);
        return result != -1; // retorna true se inserção for bem-sucedida, false se falhar
    }


    // Método para buscar o ID de um tipo de solo específico
    @SuppressLint("Range")
    public int buscarIdSoloPorTipo(String tipoSolo) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COL_ID_SOLO + " FROM " + TABLE_SOLO + " WHERE " + COL_TIPO_SOLO + " = ?", new String[]{tipoSolo});

        int idSolo = -1;
        if (cursor.moveToFirst()) {
            idSolo = cursor.getInt(cursor.getColumnIndex(COL_ID_SOLO));
        }
        cursor.close();
        db.close();
        return idSolo;
    }

    // Inserir dados na tabela agricultores com ID_SOLO
    public String inserirDados(String nome, String tipoSolo, String epocaAno) {
        int idSolo = buscarIdSoloPorTipo(tipoSolo); // Busca o ID_SOLO correspondente

        if (idSolo == -1) {
            return "Tipo de solo não encontrado. Por favor, cadastre-o primeiro.";
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put(COL_NOME_AGRICULTORES, nome);
        valores.put("ID_SOLO", idSolo); // Usa o ID do solo como referência
        valores.put(COL_EPOCAANO_AGRICULTORES, epocaAno);

        long result = db.insert(TABLE_AGRICULTORES, null, valores);
        db.close();

        if (result == -1) {
            return "Erro ao cadastrar no banco de dados";
        } else {
            return "Cadastro realizado com sucesso!";
        }
    }

    // Listar dados da tabela agricultores com informações de solo
    public Cursor listarAgricultoresComSolo() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT agricultores.*, solo.TIPO_SOLO, solo.ESTACAO " +
                "FROM " + TABLE_AGRICULTORES + " agricultores " +
                "INNER JOIN " + TABLE_SOLO + " solo ON agricultores.ID_SOLO = solo.ID";
        return db.rawQuery(query, null);
    }

    // Listar dados da tabela agricultores
    public Cursor listarDados() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_AGRICULTORES;
        return db.rawQuery(query, null);
    }

    // Listar dados da tabela usuario
    public Cursor listarUsuarios() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USUARIO;
        return db.rawQuery(query, null); // Não feche o banco aqui
    }

    public Cursor listarSolos() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_SOLO;
        return db.rawQuery(query, null); // Não feche o banco aqui
    }

    // Inserir dados na tabela usuario
    public boolean inserirUsuario(String nome, String email, String senha, String tipo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put(COL_NOME_USUARIO, nome);
        valores.put(COL_EMAIL_USUARIO, email);
        valores.put(COL_SENHA_USUARIO, senha);
        valores.put(COL_TIPO_USUARIO, tipo); // Adicionando o tipo de usuário

        long result = db.insert(TABLE_USUARIO, null, valores);
        db.close();
        return result != -1;
    }

    // Login do usuário
    public Cursor loginUsuario(String email, String senha) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_USUARIO + " WHERE " +
                COL_EMAIL_USUARIO + " = ? AND " + COL_SENHA_USUARIO + " = ?", new String[]{email, senha});
    }

    // Login do administrador
    public Cursor loginAdmin(String email, String senha) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_USUARIO + " WHERE " +
                        COL_EMAIL_USUARIO + " = ? AND " + COL_SENHA_USUARIO + " = ? AND " + COL_TIPO_USUARIO + " = ?",
                new String[]{email, senha, "administrador"});
    }

    // Método para listar todos os solos
    public String listarSols() {
        StringBuilder resultado = new StringBuilder();
        SQLiteDatabase db = this.getReadableDatabase();

        // Consulta para obter o último registro inserido
        Cursor cursor = db.rawQuery("SELECT * FROM solo ORDER BY ID DESC LIMIT 1", null);

        if (cursor != null) {
            // Verifica se existem registros
            if (cursor.moveToFirst()) {
                @SuppressLint("Range") String tipoSolo = cursor.getString(cursor.getColumnIndex("TIPO_SOLO"));
                @SuppressLint("Range") String estacao = cursor.getString(cursor.getColumnIndex("ESTACAO"));
                resultado.append("Solo: ").append(tipoSolo).append(", Estação: ").append(estacao).append("\n");
            }
            cursor.close();
        }

        return resultado.toString();
    }

    // Método para inserir feedback
    public boolean inserirFeedback(String comentario, float avaliacao) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_COMENTARIO, comentario);
        values.put(COL_AVALIACAO, avaliacao);

        long result = db.insert(TABLE_FEEDBACK, null, values);

        if (result == -1) {
            Log.e("DatabaseHelper", "Erro ao inserir feedback. Comentário: " + comentario + ", Avaliação: " + avaliacao);
        } else {
            Log.d("DatabaseHelper", "Feedback inserido com sucesso: " + comentario + ", Avaliação: " + avaliacao);
        }
        return result != -1;
    }
}
