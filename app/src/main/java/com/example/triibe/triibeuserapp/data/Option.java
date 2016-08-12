package com.example.triibe.triibeuserapp.data;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Option entity.
 *
 * @author michael
 */
@IgnoreExtraProperties
public class Option {

    String phrase;
    boolean hasExtraInput;
    String extraInput;
    String extraInputType;
    String extraInputHint;

    // Empty constructor required for firebase
    public Option() {}

    public Option(String phrase, boolean hasExtraInput) {
        this.phrase = phrase;
        this.hasExtraInput = hasExtraInput;
    }

    public Option(String phrase, boolean hasExtraInput, String extraInput, String extraInputType, String extraInputHint) {
        this.phrase = phrase;
        this.hasExtraInput = hasExtraInput;
        this.extraInput = extraInput;
        this.extraInputType = extraInputType;
        this.extraInputHint = extraInputHint;
    }

    public String getPhrase() {
        return phrase;
    }

    public void setPhrase(String phrase) {
        this.phrase = phrase;
    }

    public boolean HasExtraInput() {
        return hasExtraInput;
    }

    public void setHasExtraInput(boolean hasExtraInput) {
        this.hasExtraInput = hasExtraInput;
    }

    public String getExtraInput() {
        return extraInput;
    }

    public void setExtraInput(String extraInput) {
        this.extraInput = extraInput;
    }

    public String getExtraInputType() {
        return extraInputType;
    }

    public void setExtraInputType(String extraInputType) {
        this.extraInputType = extraInputType;
    }

    public String getExtraInputHint() {
        return extraInputHint;
    }

    public void setExtraInputHint(String extraInputHint) {
        this.extraInputHint = extraInputHint;
    }
}
