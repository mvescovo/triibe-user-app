package com.example.triibe.triibeuserapp.data;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Map;

/**
 * Survey entity. Used for Firebase realtime database so an entire survey can be
 * retrieved in one call.
 *
 * @author michael
 */
@IgnoreExtraProperties
public class Survey {

    private String description;
    private Map<String, Question> questions;

    public Survey() {

    }

    public Survey(String description, Map<String, Question> questions) {
        this.description = description;
        this.questions = questions;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, Question> getQuestions() {
        return questions;
    }

    public void setQuestions(Map<String, Question> questions) {
        this.questions = questions;
    }
}
