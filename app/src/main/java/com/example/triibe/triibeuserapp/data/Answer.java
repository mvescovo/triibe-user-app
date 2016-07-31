package com.example.triibe.triibeuserapp.data;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Map;

/**
 * Answer entity.
 * 
 * @author michael
 */
@IgnoreExtraProperties
public class Answer {

    private String formatType;
    private Map<String, Object> answer;

    public Answer() {

    }

    public Answer(String formatType, Map<String, Object> answer) {
        this.formatType = formatType;
        this.answer = answer;
    }

    public String getFormatType() {
        return formatType;
    }

    public void setFormatType(String formatType) {
        this.formatType = formatType;
    }

    public Map<String, Object> getAnswer() {
        return answer;
    }

    public void setAnswer(Map<String, Object> answer) {
        this.answer = answer;
    }
}
