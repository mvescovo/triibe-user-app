package com.example.triibe.triibeuserapp.data;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * QuestionDetails entity.
 *
 * @author michael.
 */
@IgnoreExtraProperties
public class QuestionDetails {

    private String mSurveyId;
    private String mId;
    private String mType;
    private String mImageUrl;
    private String mTitle;
    private String mIntro;
    private String mIntroLinkKey;
    private String mIntroLinkUrl;
    private String mPhrase;
    private String mRequiredPhrase;
    private String mIncorrectAnswerPhrase;

    // Empty constructor required for firebase
    public QuestionDetails() {}

    public QuestionDetails(String surveyId, String id, String type) {
        mSurveyId = surveyId;
        mId = id;
        mType = type;
    }

    public QuestionDetails(String surveyId, String id, String type, String imageUrl, String title,
                           String intro, String phrase, String introLinkKey, String introLinkUrl,
                           String requiredPhrase, String incorrectAnswerPhrase) {
        mSurveyId = surveyId;
        mId = id;
        mType = type;
        mImageUrl = imageUrl;
        mTitle = title;
        mIntro = intro;
        mPhrase = phrase;
        mIntroLinkKey = introLinkKey;
        mIntroLinkUrl = introLinkUrl;
        mRequiredPhrase = requiredPhrase;
        mIncorrectAnswerPhrase = incorrectAnswerPhrase;
    }

    /*
    * All setters are getters required by firebase even if not used in the program.
    *
    * Note: getters must be of the form "get<parameter name>".
    * Boolean values cannot use "hasExtraValue" for example.
    * */
    public String getSurveyId() {
        return mSurveyId;
    }

    public void setSurveyId(String surveyId) {
        mSurveyId = surveyId;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        mType = type;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        mImageUrl = imageUrl;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getIntro() {
        return mIntro;
    }

    public void setIntro(String intro) {
        mIntro = intro;
    }

    public String getPhrase() {
        return mPhrase;
    }

    public void setPhrase(String phrase) {
        mPhrase = phrase;
    }

    public String getIntroLinkKey() {
        return mIntroLinkKey;
    }

    public void setIntroLinkKey(String introLinkKey) {
        mIntroLinkKey = introLinkKey;
    }

    public String getIntroLinkUrl() {
        return mIntroLinkUrl;
    }

    public void setIntroLinkUrl(String introLinkUrl) {
        mIntroLinkUrl = introLinkUrl;
    }

    public String getRequiredPhrase() {
        return mRequiredPhrase;
    }

    public void setRequiredPhrase(String requiredPhrase) {
        mRequiredPhrase = requiredPhrase;
    }

    public String getIncorrectAnswerPhrase() {
        return mIncorrectAnswerPhrase;
    }

    public void setIncorrectAnswerPhrase(String incorrectAnswerPhrase) {
        mIncorrectAnswerPhrase = incorrectAnswerPhrase;
    }

    // For firebase map
    @Exclude
    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();
        result.put("surveyId", mSurveyId);
        result.put("id", mId);
        result.put("type", mType);
        result.put("imageUrl", mImageUrl);
        result.put("title", mTitle);
        result.put("intro", mIntro);
        result.put("phrase", mPhrase);
        result.put("introLinkKey", mIntroLinkKey);
        result.put("introLinkUrl", mIntroLinkUrl);
        result.put("requiredPhrase", mRequiredPhrase);
        result.put("incorrectAnswerPhrase", mIncorrectAnswerPhrase);
        return result;
    }
}
