package com.example.sana.musicapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class Location extends AppCompatActivity {

    public static ListView locList;
    public String[] locations;
    public ArrayList<String> locationsFromDb;
    private Database database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        //initialization
        locList = (ListView) findViewById(R.id.locList);
        database = new Database(this);
        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        String song = b.containsKey("song") ? b.getString("song") : "default";

        //list all locations from database
        locationsFromDb = database.listCity(song);
        locations = new String[locationsFromDb.size()];
        for (int i = 0; i < locationsFromDb.size(); i++) {
            locations[i] = locationsFromDb.get(i).toString();
            System.out.println(locations[i]);
        }

        //show locations in a list
        ArrayAdapter<String> aa = new ArrayAdapter<String>(Location.this, R.layout.song_layout, R.id.textView, locations);
        locList.setAdapter(aa);
    }
}
