package com.example.sana.musicapp.models;

public class Favorite {

    private int id;
    private String user;
    private String song;

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public String getUser() {
        return user;
    }

    public void setUser(String name) {
        this.user = user;
    }

    public String getSong() {
        return song;
    }

    public void setSong(String password) {
        this.song = song;
    }
}