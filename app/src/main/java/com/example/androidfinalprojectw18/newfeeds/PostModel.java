package com.example.androidfinalprojectw18.newfeeds;

public class PostModel {

    private String title, website, author, url, text;
    private Long id;

    public PostModel(Long id, String title, String website, String author, String url, String text) {
        this.id = id;
        this.title = title;
        this.website = website;
        this.author = author;
        this.url = url;
        this.text = text;
    }

    public PostModel() {
    }

    public Long getId(){
        return  id;
    }

    public void setId(Long id){
        this.id =  id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
