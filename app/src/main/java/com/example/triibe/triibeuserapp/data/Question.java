package com.example.triibe.triibeuserapp.data;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Question entity.
 *
 * @author michael
 */
@IgnoreExtraProperties
public class Question {

    private String id;
    private String imageUrl;
    private String title;
    private String intro;
    private Query query;
    private String introLinkKey;
    private String introLinkUrl;

    // Empty constructor required for firebase
    public Question() {}

    public Question(String id, String imageUrl, String title, String intro, Query query) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.title = title;
        this.intro = intro;
        this.query = query;
    }

    /*
    * All setters are getters required by firebase even if not used in the program.
    *
    * Note: getters must be of the form "get<parameter name>".
    * Boolean values cannot use "hasExtraValue" for example.
    * */
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public Query getQuery() {
        return query;
    }

    public void setQuery(Query query) {
        this.query = query;
    }

    public String getIntroLinkKey() {
        return introLinkKey;
    }

    public void setIntroLinkKey(String introLinkKey) {
        this.introLinkKey = introLinkKey;
    }

    public String getIntroLinkUrl() {
        return introLinkUrl;
    }

    public void setIntroLinkUrl(String introLinkUrl) {
        this.introLinkUrl = introLinkUrl;
    }
}
