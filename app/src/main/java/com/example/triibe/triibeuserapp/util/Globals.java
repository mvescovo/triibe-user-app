package com.example.triibe.triibeuserapp.util;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Singleton for storing global variables.
 *
 * @author michael
 */
public class Globals {

    private volatile static Globals uniqueInstance;
    private boolean firebasePersistenceSet;

    private Globals() {}

    public static Globals getInstance() {
        if (uniqueInstance == null) {
            synchronized (Globals.class) {
                if (uniqueInstance == null) {
                    uniqueInstance = new Globals();
                }
            }
        }
        return uniqueInstance;
    }

    public boolean isFirebasePersistenceSet() {
        return firebasePersistenceSet;
    }

    public void setFirebasePersistenceEnabled() {
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        this.firebasePersistenceSet = true;
    }
}
