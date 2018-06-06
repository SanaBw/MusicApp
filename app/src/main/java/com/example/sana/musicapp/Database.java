package com.example.sana.musicapp;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Geocoder;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.example.sana.musicapp.models.Favorite;
import com.example.sana.musicapp.models.User;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Database extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "app.db";

    //user table
    public static final String TABLE_USER = "user";
    public static final String USER_ID = "user_id";
    public static final String USER_NAME = "user_name";
    public static final String USER_PASSWORD = "user_password";

    //favorites table
    public static final String TABLE_FAVORITES = "favorites";
    public static final String FAVORITE_ID = "favorite_id";
    public static final String FAVORITE_USER = "favorite_user";
    public static final String FAVORITE_SONG = "favorite_song";

    //location table
    public static final String TABLE_LOCATION = "location";
    public static final String LOCATION_ID = "location_id";
    public static final String LOCATION_SONG = "location_song";
    public static final String LOCATION_LOCATION = "location_location";
    public static final String LOCATION_DATE = "location_date";
    private String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USER + "(" + USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + USER_NAME + " TEXT," + USER_PASSWORD + " TEXT" + ")";
    private String DROP_USER_TABLE = "DROP TABLE IF EXISTS " + TABLE_USER;
    private String CREATE_FAVORITES_TABLE = "CREATE TABLE " + TABLE_FAVORITES + "(" + FAVORITE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + FAVORITE_USER + " TEXT," + FAVORITE_SONG + " TEXT" + ")";
    private String DROP_FAVORITES_TABLE = "DROP TABLE IF EXISTS " + TABLE_FAVORITES;
    private String CREATE_LOCATION_TABLE = "CREATE TABLE " + TABLE_LOCATION + "(" + LOCATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + LOCATION_SONG + " TEXT," + LOCATION_LOCATION + " TEXT," + LOCATION_DATE + " TEXT" + ")";
    private String DROP_LOCATION_TABLE = "DROP TABLE IF EXISTS " + TABLE_LOCATION;

    //creating database and tables
    public Database(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_FAVORITES_TABLE);
        db.execSQL(CREATE_LOCATION_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_USER_TABLE);
        db.execSQL(DROP_FAVORITES_TABLE);
        db.execSQL(DROP_LOCATION_TABLE);
        onCreate(db);
    }

    //----------------add user to user table----------------//
    public void addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(USER_NAME, user.getName());
        values.put(USER_PASSWORD, user.getPassword());
        db.insert(TABLE_USER, null, values);
        db.close();
    }

    //----------------check if user already exists----------------//
    public boolean checkUser(String username) {
        String[] columns = {USER_ID};
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = USER_NAME + " = ?";
        String[] selectionArgs = {username};
        Cursor cursor = db.query(TABLE_USER, columns, selection, selectionArgs, null, null, null);
        int cursorCount = cursor.getCount();
        cursor.close();

        //check if there are more usernames like the given one
        db.close();
        if (cursorCount > 0) {
            return true;
        } else {
            return false;
        }
    }

    //----------------check user credentials----------------//
    public boolean checkUser(String username, String password) {
        String[] columns = {USER_ID};
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = USER_NAME + " = ?" + " AND " + USER_PASSWORD + " = ?";
        String[] selectionArgs = {username, password};
        Cursor cursor = db.query(TABLE_USER, columns, selection, selectionArgs, null, null, null);
        int cursorCount = cursor.getCount();
        cursor.close();
        db.close();

        //check if there is a user with given username and password
        if (cursorCount > 0) {
            return true;
        } else {
            return false;
        }
    }

    //----------------add favorite to favorites table----------------//
    public void addFavorites(String user, String song) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FAVORITE_USER, user);
        values.put(FAVORITE_SONG, song);
        db.insert(TABLE_FAVORITES, null, values);
        db.close();
    }

    //----------------check if favorite pair already exist----------------//
    public boolean checkFavorite(String user, String song) {
        String[] columns = {FAVORITE_ID};
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = FAVORITE_USER + " = ?" + " AND " + FAVORITE_SONG + " = ?";
        String[] selectionArgs = {user, song};
        Cursor cursor = db.query(TABLE_FAVORITES, columns, selection, selectionArgs, null, null, null);
        int cursorCount = cursor.getCount();
        cursor.close();
        db.close();

        if (cursorCount > 0) {
            return true;
        } else {
            return false;
        }
    }

    //----------------get favorites song list of one user from favorites table----------------//
    public ArrayList<String> getFavs(String user) {
        ArrayList<String> favList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT favorite_song FROM favorites WHERE favorite_user=?", new String[]{user + ""});

        // Moving through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                String song = cursor.getString(cursor.getColumnIndex("favorite_song"));
                favList.add(song);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return favList;
    }

    //----------------delete favorite song of one user from favorites table----------------//
    public boolean removeFavorite(String user, String song) {
        SQLiteDatabase db = this.getReadableDatabase();
        System.out.println(user + " " + song);
        return (db.delete(TABLE_FAVORITES, FAVORITE_USER + "=? AND " + FAVORITE_SONG + "=?", new String[]{user, song})) > 0;
    }

    //----------------save city where the song is played----------------//
    public void saveCity(String song, String city, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(LOCATION_SONG, song);
        values.put(LOCATION_LOCATION, city);
        values.put(LOCATION_DATE, date);
        db.insert(TABLE_LOCATION, null, values);
        db.close();
        System.out.println("Saved.");
    }

    //----------------get all cities and dates where the song was played----------------//
    public ArrayList<String> listCity(String song) {
        ArrayList<String> cityList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM location WHERE location_song=" + "'" + song + "'", null);

        // Moving through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                String location = cursor.getString(cursor.getColumnIndex("location_location"));
                String dates = cursor.getString(cursor.getColumnIndex("location_date"));
                cityList.add(location + " " + dates);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return cityList;
    }
}



