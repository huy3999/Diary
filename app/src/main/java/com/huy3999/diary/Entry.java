package com.huy3999.diary;

public class Entry {

    private String title, content, date;
    private int color;


    public Entry(String title, String content, int color, String date) {
        this.title = title;
        this.content = content;
        this.color = color;
        this.date = date;
    }

    public Entry(){

    }
    public void setDate(String date) {

        this.date = date;
    }
    public String getDate() {
        return date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
