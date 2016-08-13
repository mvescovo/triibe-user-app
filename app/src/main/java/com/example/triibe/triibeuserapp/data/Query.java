package com.example.triibe.triibeuserapp.data;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;

/**
 * Query entity.
 *
 * @author michael
 */
@IgnoreExtraProperties
public class Query {

    private String type;
    private String phrase;
    private ArrayList<Option> options;
    private String requiredPhrase;
    private String incorrectAnswerPhrase;
    private boolean allowedToSkip;

    // Empty constructor required for firebase
    public Query() {}

    public Query(String type, String phrase, ArrayList<Option> options) {
        this.type = type;
        this.phrase = phrase;
        this.options = options;
    }

    public Query(String type, String phrase, ArrayList<Option> options, String requiredPhrase, String incorrectAnswerPhrase) {
        this.type = type;
        this.phrase = phrase;
        this.options = options;
        this.requiredPhrase = requiredPhrase;
        this.incorrectAnswerPhrase = incorrectAnswerPhrase;
    }

    public Query(String type, String phrase, ArrayList<Option> options, boolean allowedToSkip) {
        this.type = type;
        this.phrase = phrase;
        this.options = options;
        this.allowedToSkip = allowedToSkip;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPhrase() {
        return phrase;
    }

    public void setPhrase(String phrase) {
        this.phrase = phrase;
    }

    public ArrayList<Option> getOptions() {
        return options;
    }

    public void setOptions(ArrayList<Option> options) {
        this.options = options;
    }

    public String getRequiredPhrase() {
        return requiredPhrase;
    }

    public void setRequiredPhrase(String requiredPhrase) {
        this.requiredPhrase = requiredPhrase;
    }

    public String getIncorrectAnswerPhrase() {
        return incorrectAnswerPhrase;
    }

    public void setIncorrectAnswerPhrase(String incorrectAnswerPhrase) {
        this.incorrectAnswerPhrase = incorrectAnswerPhrase;
    }

    public boolean isAllowedToSkip() {
        return allowedToSkip;
    }

    public void setAllowedToSkip(boolean allowedToSkip) {
        this.allowedToSkip = allowedToSkip;
    }
}
