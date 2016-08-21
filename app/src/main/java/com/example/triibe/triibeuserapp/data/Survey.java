package com.example.triibe.triibeuserapp.data;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;

/**
 * Survey entity. Used for Firebase realtime database so an entire survey can be
 * retrieved in one call.
 *
 * @author michael
 */
@IgnoreExtraProperties
public class Survey {

    private String description;
    private String version;
    private ArrayList<Question> questions;

    // Empty constructor required for firebase
    public Survey() {}

    public Survey(String description, String version, ArrayList<Question> questions) {
        this.description = description;
        this.version = version;
        this.questions = questions;
    }

    /*
    * All setters are getters required by firebase even if not used in the program.
    *
    * Note: getters must be of the form "get<parameter name>".
    * Boolean values cannot use "hasExtraValue" for example.
    * */
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public ArrayList<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(ArrayList<Question> questions) {
        this.questions = questions;
    }
}
