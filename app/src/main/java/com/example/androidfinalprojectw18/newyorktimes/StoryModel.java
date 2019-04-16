package com.example.androidfinalprojectw18.newyorktimes;

public class StoryModel {
    Long id;
    String title;
    String author;
    String headLine;
    String url;
    String imageURL;

    public StoryModel(Long id, String title, String headLine, String author, String url, String imageURL) {
        this.id = id;
        this.title = title;
        this.headLine = headLine;
        this.author = author;
        this.url = url;
        this.imageURL = imageURL;
    }

    public StoryModel() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHeadLine() {
        return headLine;
    }

    public void setHeadLine(String headLine) {
        this.headLine = headLine;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
}
