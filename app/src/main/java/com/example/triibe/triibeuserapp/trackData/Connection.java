package com.example.triibe.triibeuserapp.trackData;

import java.util.Date;

/**
 * Created by Matthew on 3/09/2016.
 */
public class Connection {

    private String connectionProtocol;
    private String ipAddrURL;
    private Date startConncetion;
    private Date endConnection;


    // Empty constructor required for firebase???
    public Connection() {}

    public Connection(String connectionProtocol, String ipAddrURL, Date startConncetion) {
        this.connectionProtocol = connectionProtocol;
        this.ipAddrURL = ipAddrURL;
        this.startConncetion = startConncetion;

    }
    public void setConnectionProtocol(String connectionProtocol) {
        this.connectionProtocol = connectionProtocol;
    }

    public void setIpAddrURL(String ipAddrURL) {
        this.ipAddrURL = ipAddrURL;
    }

    public void setStartConncetion(Date startConncetion) {
        this.startConncetion = startConncetion;
    }

    public void setEndConnection(Date endConnection) {
        this.endConnection = endConnection;
    }

    public String getIpAddrURL() {
        return ipAddrURL;
    }
}
