package com.example.sana.musicapp;

import android.Manifest;
import android.app.Notification;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements LocationListener {

    public static ListView lv;
    public static String[] songs;
    public static Button btnShf, btnLO, btnF, btnPlay, btnNext, btnPrev;
    public static int length;
    public String song;
    private String city;
    private Session session;
    private Database database;
    private static final int LOCATION_PERMISSION = 1;
    private static LocationManager locationManager;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1024);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1000);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1000);
        }

        //initialization
        lv = (ListView) findViewById(R.id.songList);
        btnShf = (Button) findViewById(R.id.btnShf);
        btnLO = (Button) findViewById(R.id.btnLO);
        btnF = (Button) findViewById(R.id.btnF);
        btnPlay = (Button) findViewById(R.id.btnPlay);
        btnNext = (Button) findViewById(R.id.btnNext);
        btnPrev = (Button) findViewById(R.id.btnPrev);
        length = 0;
        session = new Session(this);
        database = new Database(this);
        ;

        //remove the player from the last user
        if (Player.mp != null) {
            Player.mp.stop();
            Player.mp.release();
            Player.mp = null;
        }

        //if no user is logged in, go to login activity
        if (!session.getloggedIn()) {
            startActivity(new Intent(MainActivity.this, Login.class));
        }

        //array list of music files from phone
        final ArrayList<File> mySongs = findSongs(Environment.getExternalStorageDirectory());

        //names of all songs found
        songs = new String[mySongs.size()];
        for (int i = 0; i < mySongs.size(); i++) {
            songs[i] = mySongs.get(i).getName().replace(".mp3", "").replace(".wav", "");
        }

        //list all songs
        ArrayAdapter<String> aa = new ArrayAdapter<String>(MainActivity.this, R.layout.song_layout, R.id.textView, songs);
        lv.setAdapter(aa);

        //song in a list clicked
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                song = lv.getAdapter().getItem(position).toString();
                getLoc();
                if (Player.mp != null) {
                    Player.mp.stop();
                    Player.mp.release();
                    Player.mp = null;
                }
                startActivity(new Intent(MainActivity.this, Player.class).putExtra("pos", position).putExtra("songlist", mySongs));
            }
        });

        //shuffle button clicked
        btnShf.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int randomInt = (new Random().nextInt(mySongs.size()));
                        startActivity(new Intent(MainActivity.this, Player.class).putExtra("pos", randomInt).putExtra("songlist", mySongs));
                    }
                }
        );

        //log out button clicked
        btnLO.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Player.mp != null) {
                            Player.mp.stop();
                            Player.mp.release();
                            Player.mp = null;
                        }
                        session.setLoggedIn(false);
                        finish();
                        startActivity(new Intent(MainActivity.this, MainActivity.class));
                    }
                }
        );

        //button for favorite songs clicked --> list favorite songs
        btnF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Favorites.class));
            }
        });

        //play button clicked
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Player.mp == null) {
                    int randomInt = (new Random().nextInt(mySongs.size()));
                    startActivity(new Intent(MainActivity.this, Player.class).putExtra("pos", randomInt).putExtra("songlist", mySongs));
                } else {
                    if (Player.mp.isPlaying()) {
                        Player.mp.pause();
                        length = Player.mp.getCurrentPosition();
                    } else {
                        Player.mp.seekTo(length);
                        Player.mp.start();
                    }
                }
            }
        });

        //next song button clicked
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Player.NextSong();
            }
        });

        //previous song button clicked
        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Player.PreviousSong();
            }
        });
    }

    //----------------find all songs on phone----------------//
    public ArrayList<File> findSongs(File root) {
        ArrayList<File> al = new ArrayList<File>();
        File[] files = root.listFiles();
        for (File singleFile : files) {
            if (singleFile.isDirectory() && !singleFile.isHidden()) {
                al.addAll(findSongs(singleFile));
            } else {
                if (singleFile.getName().endsWith(".mp3") || singleFile.getName().endsWith(".wav")) {
                    al.add(singleFile);
                }
            }
        }
        return al;
    }

    //----------------save song and location where it was played----------------//
    public void getLoc() {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1, 0, this);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    //----------------override from LocationListener, called with requestLocationUpdates----------------//
    @Override
    public void onLocationChanged(Location location) {
        getCity(location.getLatitude(), location.getLongitude());
        locationManager.removeUpdates(this);
    }

    //----------------get current city of the user and send it to db----------------//
    public void getCity(double lat, double lon) {
        String city = "";
        Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(lat, lon, 10);
            if (addresses.size() > 0) {
                for (Address a : addresses) {
                    if (a.getLocality() != null && a.getLocality().length() > 0) {
                        city = a.getLocality();
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            city = "ERROR";
        }
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        database.saveCity(song, city, date);
    }

    //----------------overrides from LocationListener----------------//
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
