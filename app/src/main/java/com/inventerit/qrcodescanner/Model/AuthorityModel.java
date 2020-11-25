package com.inventerit.qrcodescanner.Model;

public class AuthorityModel {
    public String getAuthority() {
        return authority;
    }

    public String getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public String getPending() {
        return pending;
    }

    public String getRequest() {
        return request;
    }

    public String getResult() {
        return result;
    }

    public String getTime() {
        return time;
    }

    private String authority;
    private String name;
    private String type;
    private String id;
    private String pending;
    private String request;
    private String result;
    private String time;

    public AuthorityModel(String authority, String type, String id, String pending, String request, String result, String time, String name) {
        this.authority = authority;
        this.type = type;
        this.id = id;
        this.pending = pending;
        this.request = request;
        this.result = result;
        this.time = time;
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
