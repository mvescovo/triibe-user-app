package com.example.triibe.triibeuserapp.data;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Option entity.
 *
 * @author michael
 */
@IgnoreExtraProperties
public class Option {

    private static final String TAG = "Option";

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

    public String getPhrase() {
        return phrase;
    }

    public void setPhrase(String phrase) {
        this.phrase = phrase;
    }

//    public Boolean HasExtraInput() {
//        if (hasExtraInput == null) {
//            Log.d(TAG, "HasExtraInput: IS NULL");
//        } else {
//            Log.i(TAG, "HasExtraInput: IS NOT NULL");
//        }
//        return hasExtraInput != null && hasExtraInput.contentEquals("true");
//    }


    /*
    * All setters are getters requried by firebase even if not used in the program.
    *
    * Note: getters must be of the form "get<parameter name>".
    * Boolean values cannot use "hasExtraValue" for example.
    * */
    public boolean getHasExtraInput() {
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
