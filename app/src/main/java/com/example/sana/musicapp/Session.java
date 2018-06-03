package com.example.sana.musicapp;

import android.content.Context;
import android.content.SharedPreferences;

public class Session {
    static SharedPreferences sharedPreferences;
    static SharedPreferences.Editor editor;
    static Context context;

    //constructor for new sessions
    public Session(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("myApp", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    //set current user's logged status
    public static void setLoggedIn(boolean loggedIn) {
        editor.putBoolean("loggedInMode", loggedIn);
        editor.commit();
    }

    //get current user's logged status
    public static boolean getloggedIn() {
        return sharedPreferences.getBoolean("loggedInMode", false);
    }

    //save info about current user
    public static void saveInfo(String username, String password) {
        sharedPreferences = context.getSharedPreferences("userInfo", context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString("username", username);
        editor.putString("password", password);
        editor.apply();
    }

    //get username of current user
    public static String getUser() {
        sharedPreferences = context.getSharedPreferences("userInfo", context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "");
        return username;
    }
}