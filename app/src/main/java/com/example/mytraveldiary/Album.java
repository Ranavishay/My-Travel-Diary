package com.example.mytraveldiary;

import java.io.Serializable;
import java.util.List;

public class Album implements Serializable {

    private String name;
    private String date;
    private String userId; // user ID of the user in the realtime database
    private List<Page> pages;

    public Album() {
    }

    public Album(String name, String date, String userId, List<Page> pages) {
        this.name = name;
        this.date = date;
        this.userId = userId;
        this.pages = pages;
    }

    public String getName() {

        return name;
    }

    public String getDate() {

        return date;
    }

    public String getUserId() {

        return userId;
    }

    public List<Page> getPages() {

        return pages;
    }
}
