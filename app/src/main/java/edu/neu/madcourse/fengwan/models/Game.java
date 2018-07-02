package edu.neu.madcourse.fengwan.models;

import com.google.firebase.database.ServerValue;

public class Game {
    String clientToken;
    int score;
    long timeStamp;

    public Game() {
    }

    public Game(int score) {
        this.score = score;
    }

    public String getClientToken() {
        return clientToken;
    }

    public void setClientToken(String clientToken) {
        this.clientToken = clientToken;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
