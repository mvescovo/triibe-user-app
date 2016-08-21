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

    // Empty constructor required for firebase
    public Query() {}

    public Query(String type, String phrase, ArrayList<Option> options) {
        this.type = type;
        this.phrase = phrase;
        this.options = options;
    }

    /*
    * All setters are getters required by firebase even if not used in the program.
    *
    * Note: getters must be of the form "get<parameter name>".
    * Boolean values cannot use "hasExtraValue" for example.
    * */
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
}
