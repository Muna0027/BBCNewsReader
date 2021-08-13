package com.example.bbcnewsreader.beans;

public class Item {

    private long id;
    private final String title;
    private final String description;
    private final String link;
    private final String date;

    public Item(long id, String title, String description, String link, String date) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.link = link;
        this.date = date;
    }

    // Getters
    public long getId() {
        return id;
    }
    public String getTitle() {
        return title;
    }
    public String getDescription() {
        return description;
    }
    public String getLink() {
        return link;
    }
    public String getDate() {
        return date;
    }

    // Setters
    public void setId(long id) { this.id = id; }

}