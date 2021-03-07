package com.employersapps.employersapp.config;

public class Config {
    //Server address
    //public final static String IP_ADDRESS = "nl1-ss19.a2hosting.com:45000";
    //Local address
    public final static String IP_ADDRESS = "192.168.88.211:8080";
    public final static String API_ADDRESS = "http://" + IP_ADDRESS;
    public final static String WEB_SOCKET_ADDRESS = "ws://" + IP_ADDRESS;
    public final static String SHARED_PREFS_NAME_COMMON = "employersAppPrefs";
}
