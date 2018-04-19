package com.handsfree.stonyleverage.biblepeoplequiz.Model;

/**
 * Created by hp on 4/7/2018.
 */

public class Ranking {
    private String Username;
    private long score;

    public Ranking() {
    }

    public Ranking(String username, long score) {
        Username = username;
        this.score = score;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public long getScore() {
        return score;
    }

    public void setScore(long score) {
        this.score = score;
    }
}
