package com.example.tiktaktok;

import android.graphics.Bitmap;

import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.ArrayList;

public class Player implements Serializable {
    public static int countPlayers = 0;
    private int playerId;
    private String name;
    private int score;
    private int picture;
    private String password;
    private boolean isMyTurn;
    public String X_or_O;
    public boolean isActive;

    public Player(){
        this.name = "";
        this.score = 0;
        this.picture = 0;
        this.playerId = countPlayers;
        this.password = "";
        countPlayers++;
        isMyTurn = false;
        isActive = false;

    }

    public Player(String name, int score, int picture, String password) {
        this.name = name;
        this.score = score;
        this.picture = picture;
        this.playerId = countPlayers;
        this.password = password;
        countPlayers++;
        isMyTurn = false;
        isActive = false;
    }

    public boolean isMyTurn() {
        return isMyTurn;
    }

    public void setMyTurn(boolean myTurn) {
        isMyTurn = myTurn;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Player(String name, String password, int picture) {
        this.name = name;
        this.score = 0;
        this.password = password;
        this.picture = picture;
        this.playerId = countPlayers;
        countPlayers++;
    }

    public int getPicture() {
        return picture;
    }

    public void setPicture(int picture) {
        this.picture = picture;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getScore() {
        return score;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return super.equals(obj);
    }

}
