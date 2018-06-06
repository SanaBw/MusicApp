package com.example.sana.musicapp.models;

public class Location {

    private int id;
    private String song;
    private String location;
    private String date;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSong () { return song; }

    public void setSong (String song) {
        this.song = song;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String location) {
        this.date = date;
    }
}