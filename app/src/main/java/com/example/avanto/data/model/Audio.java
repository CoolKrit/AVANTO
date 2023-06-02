package com.example.avanto.data.model;

public class Audio {
    private int img;
    private String title;
    private String filePath;
    private int duration;

    public Audio(int img, String title, String filePath, int duration) {
        this.img = img;
        this.title = title;
        this.filePath = filePath;
        this.duration = duration;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
