//package com.example.triibe.triibeuserapp.data;
//
//import com.google.firebase.database.IgnoreExtraProperties;
//
//import java.util.ArrayList;
//
///**
// * Query entity.
// *
// * @author michael
// */
//@IgnoreExtraProperties
//public class Query {
//
//    private String mType;
//    private String mPhrase;
//    private ArrayList<Option> mOptions;
//    private String mRequiredPhrase;
//    private String mIncorrectAnswerPhrase;
//
//    // Empty constructor required for firebase
//    public Query() {}
//
//    public Query(String type, String phrase, ArrayList<Option> options) {
//        mType = type;
//        mPhrase = phrase;
//        mOptions = options;
//    }
//
//    /*
//    * All setters are getters required by firebase even if not used in the program.
//    *
//    * Note: getters must be of the form "get<parameter name>".
//    * Boolean values cannot use "hasExtraValue" for example.
//    * */
//    public String getType() {
//        return mType;
//    }
//
//    public void setType(String type) {
//        mType = type;
//    }
//
//    public String getPhrase() {
//        return mPhrase;
//    }
//
//    public void setPhrase(String phrase) {
//        mPhrase = phrase;
//    }
//
//    public ArrayList<Option> getOptions() {
//        return mOptions;
//    }
//
//    public void setOptions(ArrayList<Option> options) {
//        mOptions = options;
//    }
//
//    public String getRequiredPhrase() {
//        return mRequiredPhrase;
//    }
//
//    public void setRequiredPhrase(String requiredPhrase) {
//        mRequiredPhrase = requiredPhrase;
//    }
//
//    public String getIncorrectAnswerPhrase() {
//        return mIncorrectAnswerPhrase;
//    }
//
//    public void setIncorrectAnswerPhrase(String incorrectAnswerPhrase) {
//        mIncorrectAnswerPhrase = incorrectAnswerPhrase;
//    }
//}
