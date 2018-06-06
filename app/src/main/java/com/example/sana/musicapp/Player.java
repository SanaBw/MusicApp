package com.example.sana.musicapp;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class Player extends AppCompatActivity {

    public static MediaPlayer mp;
    public static ArrayList<File> mySongs;
    public static int position;
    public static Uri u;
    public static Button btnPlay, btnP, btnN, btnBack, btnFav, btnLoc;
    public static TextView songName;
    private static Context context;
    private Session session;
    private Database database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        //initialize
        btnPlay = (Button) findViewById(R.id.btnPlay);
        btnN = (Button) findViewById(R.id.btnN);
        btnP = (Button) findViewById(R.id.btnP);
        songName = (TextView) findViewById(R.id.songName);
        btnBack = (Button) findViewById(R.id.btnBack);
        btnFav = (Button) findViewById(R.id.btnFav);
        btnLoc = (Button) findViewById(R.id.btnLoc);
        context = getApplicationContext();
        session = new Session(this);
        database = new Database(Player.this);

        //init for background images
        final int[] photos = {R.drawable.image1, R.drawable.image2, R.drawable.image3, R.drawable.image4, R.drawable.image5, R.drawable.image6, R.drawable.image7, R.drawable.image8, R.drawable.image9, R.drawable.image10, R.drawable.image11, R.drawable.image12, R.drawable.image13};
        final ImageView image = (ImageView) findViewById(R.id.imageView);
        Random ran = new Random();
        int j = ran.nextInt(photos.length);
        image.setImageResource(photos[j]);

        //take information about the song from the activity that called player
        Intent i = getIntent();
        Bundle b = i.getExtras();
        mySongs = (ArrayList) b.getParcelableArrayList("songlist");
        position = b.getInt("pos", 0);
        u = Uri.parse(mySongs.get(position).toString());
        if (mp != null) {
            mp.stop();
            mp.release();
            mp = null;
            btnPlay.setText(">");
        }

        //create new player and start it
        mp = MediaPlayer.create(getApplicationContext(), u);
        mp.start();
        btnPlay.setText("II");
        songName.setText(mySongs.get(position).getName().replace(".mp3", "").replace(".wav", ""));

        //play button clicked
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mp.isPlaying()) {
                    mp.pause();
                    btnPlay.setText(">");
                    MainActivity.length = mp.getCurrentPosition();
                } else {
                    mp.start();
                    btnPlay.setText("II");
                }
            }
        });

        //when song finishes
        mp.setOnCompletionListener(
                new OnCompletionListener() {
                    public void onCompletion(MediaPlayer mp) {
                        NextSong();
                    }
                }
        );

        //previous song button clicked
        btnP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreviousSong();
            }
        });

        //next song button clicked
        btnN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NextSong();
            }
        });

        //background image clicked -> change to new random one
        image.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        Random ran = new Random();
                        int j = ran.nextInt(photos.length);
                        image.setImageResource(photos[j]);
                    }
                }
        );

        //LIST text is clicked
        btnBack.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Player.super.onBackPressed();
                    }
                }
        );

        //make this song one of user's favorites
        btnFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String song = mySongs.get(position).getName();
                String user = session.getUser();
                if (!database.checkFavorite(user, song)) {
                    database.addFavorites(user, song);
                    Toast.makeText(context, "Song added to favorites", Toast.LENGTH_LONG).show();
                }
            }
        });

        //show all locations where this song has been played
        btnLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String song = mySongs.get(position).getName().replace(".mp3", "").replace(".wav", "");
                startActivity(new Intent(Player.this, Location.class).putExtra("song", song));
            }
        });
    }

    //----------------play next song----------------//
    public static void NextSong() {
        mp.stop();
        mp.release();
        mp = null;
        if (position < (mySongs.size() - 1)) {
            position = position + 1;
        } else {
            position = 0;
        }
        u = Uri.parse(mySongs.get(position).toString());
        mp = MediaPlayer.create(context, u);
        mp.start();
        btnPlay.setText("II");
        songName.setText(mySongs.get(position).getName().replace(".mp3", "").replace(".wav", ""));
    }

    //----------------play previous song----------------//
    public static void PreviousSong() {
        mp.stop();
        mp.release();
        mp = null;
        if (position - 1 < 0) {
            position = mySongs.size() - 1;
        } else {
            position = position - 1;
        }
        u = Uri.parse(mySongs.get(position).toString());
        mp = MediaPlayer.create(context, u);
        mp.start();
        btnPlay.setText("II");
        songName.setText(mySongs.get(position).getName().replace(".mp3", "").replace(".wav", ""));
    }
}
