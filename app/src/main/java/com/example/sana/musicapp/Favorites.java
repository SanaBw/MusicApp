package com.example.sana.musicapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.sana.musicapp.models.Favorite;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

public class Favorites extends AppCompatActivity {

    public static ListView lv;
    public static String[] favorites;
    private Database database;
    private Session session;
    public static File dir;
    public static String favs[];
    public static Button btnShf;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        //initialize
        lv = (ListView) findViewById(R.id.songList);
        dir = Environment.getExternalStorageDirectory();
        btnShf = (Button) findViewById(R.id.btnShf);
        database = new Database(Favorites.this);
        session = new Session(this);

        //arraylist of names of favorite songs
        final ArrayList myFavs = database.getFavs(session.getUser());

        //moved into array
        favorites = new String[myFavs.size()];
        for (int i = 0; i < myFavs.size(); i++) {
            favorites[i] = myFavs.get(i).toString();
        }

        //arraylist of files of favorite songs
        final ArrayList<File> mySongs = findFavs(dir);

        //moved into array
        favs = new String[mySongs.size()];
        for (int i = 0; i < mySongs.size(); i++) {
            favs[i] = mySongs.get(i).getName().replace(".mp3", "").replace(".wav", "");
        }

        //favorites displayed
        ArrayAdapter<String> aa = new ArrayAdapter<>(Favorites.this, R.layout.song_layout, R.id.textView, favorites);
        lv.setAdapter(aa);

        //song in a list clicked
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (Player.mp != null) {
                Player.mp.stop();
                Player.mp.release();
                Player.mp = null;
            }
            startActivity(new Intent(Favorites.this, Player.class).putExtra("pos", position).putExtra("songlist", mySongs));
            }
        });

        //long click on song deletes it from favorites
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (database.removeFavorite(session.getUser(), lv.getAdapter().getItem(position).toString())){
                    finish();
                    startActivity(new Intent(Favorites.this, Favorites.class));
                }
                return true;
            }
        });

        //shuffle button clicked
        btnShf.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(favorites.length>0){
                        int randomInt = (new Random().nextInt(mySongs.size()));
                        startActivity(new Intent(Favorites.this, Player.class).putExtra("pos", randomInt).putExtra("songlist", mySongs));
                    } else {
                        Toast.makeText(getApplicationContext(),"Add some favorite songs first", Toast.LENGTH_LONG).show();
                    }
                }
            }
        );
    }

    //----------------find files of favorite songs by comparing names of all files from all directories to names in favorites array----------------//
    public ArrayList<File> findFavs(File root) {
        ArrayList<File> al = new ArrayList<>();
        File[] files = root.listFiles();
        for (File singleFile : files) {
            if (singleFile.isDirectory() && !singleFile.isHidden()) {
                al.addAll(findFavs(singleFile));
            } else {
                if (singleFile.getName().endsWith(".mp3") || singleFile.getName().endsWith(".wav")) {
                    for (int i = 0; i < favorites.length; i++) {
                        if (singleFile.getName().equals(favorites[i])) {
                            al.add(singleFile);
                        }
                    }
                }
            }
        }
        return al;
    }
}
