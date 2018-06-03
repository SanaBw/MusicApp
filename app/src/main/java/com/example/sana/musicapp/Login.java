package com.example.sana.musicapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sana.musicapp.models.User;

public class Login extends AppCompatActivity {

    public static SQLiteOpenHelper openHelper;
    public static Button btnLog;
    public static Button btnR2;
    public static EditText username, password;
    private Database database;
    private Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //initialization
        openHelper = new Database(this);
        btnLog = (Button) findViewById(R.id.btnLog);
        btnR2 = (Button) findViewById(R.id.btnR2);
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        database = new Database(Login.this);
        session = new Session(this);

        //if user is already logged in, start the main activity
        if (session.getloggedIn()) {
            Intent main = new Intent(Login.this, MainActivity.class);
            startActivity(main);
            finish();
        }

        //login button clicked
        btnLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (username.length() < 1 || password.length() < 1) {
                    Toast.makeText(getApplicationContext(), "Enter username and password", Toast.LENGTH_SHORT).show();
                } else {
                    String un = username.getText().toString();
                    String pw = password.getText().toString();
                    if (database.checkUser(un, pw)) {
                        session.saveInfo(un, pw);
                        session.setLoggedIn(true);
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "User not found. Check your username and password.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        //create new account
        btnR2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent reg = new Intent(Login.this, Register.class);
            startActivity(reg);
            }
        });
    }
}
