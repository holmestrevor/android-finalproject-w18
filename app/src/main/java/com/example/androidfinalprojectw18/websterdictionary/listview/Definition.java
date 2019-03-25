package com.example.androidfinalprojectw18.websterdictionary.listview;

public class Definition {

    private long id;
    private String word, pronunciation;
    private String[] definitions;

    public Definition() {
        this("Pasta", "päs-tə", new String[]{ "A dough of flour, eggs and water made in different shapes and dried (as spaghetti or macaroni) or used fresh (as ravioli)", "A dish of cooked pasta"});
    }

    public Definition(String word, String pronunciation, String[] definitions) {
        setWord(word);
        setPronunciation(pronunciation);
        setDefinitions(definitions);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getPronunciation() {
        return pronunciation;
    }

    public void setPronunciation(String pronunciation) {
        this.pronunciation = pronunciation;
    }

    public String[] getDefinitions() {
        return definitions;
    }

    public void setDefinitions(String[] definitions) {
        this.definitions = definitions;
    }
}
