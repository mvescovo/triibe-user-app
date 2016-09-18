package com.example.triibe.triibeuserapp.trackData;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Matthew on 3/09/2016.
 */
@IgnoreExtraProperties
public class Connection {


    private String connectionProtocol;
    private String ipAddrURL;
    private String startConnection;
    private String endConnection;


    // Empty constructor required for firebase???
    public Connection() {}

    public Connection(String connectionProtocol, String ipAddrURL, String startConncetion) {
        this.connectionProtocol = connectionProtocol;
        this.ipAddrURL = ipAddrURL;
        this.startConnection = startConncetion;

    }

    public String getConnectionProtocol() {
        return connectionProtocol;
    }

    public String getEndConnection() {
        return endConnection;
    }

    public String getStartConnection() {
        return startConnection;
    }
    public void setEndConnection(String endConnection) {
        this.endConnection = endConnection;
    }

    public String getIpAddrURL() {
        return ipAddrURL;
    }

    public void setConnectionProtocol(String connectionProtocol) {
        this.connectionProtocol = connectionProtocol;
    }

    public void setIpAddrURL(String ipAddrURL) {
        this.ipAddrURL = ipAddrURL;
    }

    public void setStartConnection(String startConnection) {
        this.startConnection = startConnection;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("Connection Protocol", connectionProtocol);
        result.put("IP Address URL", ipAddrURL);
        result.put("Start Time", startConnection);
        result.put("End Time", endConnection);

        return result;
    }

}
