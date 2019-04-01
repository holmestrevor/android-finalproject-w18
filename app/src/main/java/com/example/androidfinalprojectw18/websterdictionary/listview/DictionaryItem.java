package com.example.androidfinalprojectw18.websterdictionary.listview;

public class DictionaryItem {

    private long id;
    private String word, pronunciation;
    private String[] definitions;

    /**
     * Default constructor for DictionaryItem object. Contains dummy values that are only used for testing.
     */
    public DictionaryItem() {
        this("Pasta", "päs-tə", new String[]{ "A dough of flour, eggs and water made in different shapes and dried (as spaghetti or macaroni) or used fresh (as ravioli)", "A dish of cooked pasta"});
    }

    /**
     * Constructor for a DictionaryItem object that takes a word, its pronunciation, and its definitions as arguments.
     * @param word A word
     * @param pronunciation The pronunciation of the word, using phonetic symbols.
     * @param definitions An array of definitions.
     */
    public DictionaryItem(String word, String pronunciation, String[] definitions) {
        setWord(word);
        setPronunciation(pronunciation);
        setDefinitions(definitions);
    }

    /**
     * Get the Database ID of the object.
     * @return Long, Database ID of the object.
     */
    public long getId() {
        return id;
    }

    /**
     * Set the Database ID of the object.
     * @param id long to be passed
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Get the word.
     * @return String, word
     */
    public String getWord() {
        return word;
    }

    /**
     * Set the word.
     * @param word String to be passed
     */
    public void setWord(String word) {
        this.word = word;
    }

    /**
     * Get the pronunciation of the word.
     * @return String, pronunciation.
     */
    public String getPronunciation() {
        return pronunciation;
    }

    /**
     * Set the pronunciation of the word.
     * @param pronunciation String to be passed
     */
    public void setPronunciation(String pronunciation) {
        this.pronunciation = pronunciation;
    }

    /**
     * Get the definitions of the word.
     * @return Array of Strings
     */
    public String[] getDefinitions() {
        return definitions;
    }

    /**
     * Set the definitions of the word.
     * @param definitions String Array to be passed.
     */
    public void setDefinitions(String[] definitions) {
        this.definitions = definitions;
    }
}
