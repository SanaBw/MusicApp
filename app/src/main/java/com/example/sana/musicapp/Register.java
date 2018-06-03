package com.example.sana.musicapp;

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

public class Register extends AppCompatActivity {

    public static SQLiteOpenHelper openHelper;
    public static SQLiteDatabase db;
    public static Button btnReg;
    public static EditText username;
    public static EditText password;
    private Database database;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //initialization
        openHelper = new Database(this);
        btnReg = (Button) findViewById(R.id.btnReg);
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        database = new Database(Register.this);
        user = new User();

        //register user
        btnReg.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    db = openHelper.getWritableDatabase();
                    String un = username.getText().toString();
                    String pw = password.getText().toString();

                    //form validations
                    if(username.length()<2){
                        Toast.makeText(getApplicationContext(), "Username needs to be at least 2 characters long", Toast.LENGTH_LONG).show();
                    } else if(password.length()<3){
                        Toast.makeText(getApplicationContext(), "Password needs to be at least 3 characters long", Toast.LENGTH_LONG).show();
                    }

                    //check if user exists
                    else if (!(database.checkUser(un))) {
                        user.setName(un);
                        user.setPassword(pw);
                        database.addUser(user);
                        Toast.makeText(getApplicationContext(),"Successfully registered", Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(),"User already exists", Toast.LENGTH_LONG).show();
                    }
                }
            }
        );
    }
}