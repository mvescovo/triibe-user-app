package com.example.triibe.triibeuserapp.util;

import com.example.triibe.triibeuserapp.data.TriibeRepository;
import com.example.triibe.triibeuserapp.data.TriibeRepositoryImpl;
import com.example.triibe.triibeuserapp.data.TriibeServiceApiImpl;
import com.example.triibe.triibeuserapp.data.User;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Singleton for storing global variables.
 *
 * @author michael
 */
public class Globals {

    private volatile static Globals sUniqueInstance;
    private static TriibeRepository sTriibeRepository;
    private static boolean sFirebasePersistenceSet;
    private static volatile User sUser;
//    public static GoogleMap mMap;

    private Globals() {}

    public static Globals getInstance() {
        if (sUniqueInstance == null) {
            synchronized (Globals.class) {
                if (sUniqueInstance == null) {
                    sUniqueInstance = new Globals();
                }
            }
        }
        return sUniqueInstance;
    }

    public synchronized TriibeRepository getTriibeRepository() {
        if (sTriibeRepository == null) {
            sTriibeRepository = new TriibeRepositoryImpl(new TriibeServiceApiImpl());
        }
        return sTriibeRepository;
    }

    public boolean isFirebasePersistenceSet() {
        return sFirebasePersistenceSet;
    }

    public void setFirebasePersistenceEnabled() {
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        this.sFirebasePersistenceSet = true;
    }

    public User getUser() {
        return sUser;
    }

    public void setUser(User user) {
        sUser = user;
    }
}
