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

    // Empty constructor required for firebase
    public Question() {}

    public Question(String id, String imageUrl, String title, String intro, Query query) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.title = title;
        this.intro = intro;
        this.query = query;
    }

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
}
