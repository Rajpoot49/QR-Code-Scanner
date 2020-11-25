package com.inventerit.qrcodescanner.Model;

public class PendingDocModel {
    private String name;
    private String email;
    private String pendingState;
    private String id;
    private String authority;
    private String requestId;
    private String result;
    private String authorityId;
    private String date;
    private String type;
    private String val;

    public void setVal(String val){
        this.val = val;
    }
    public void setType(String type){
        this.type = type;
    }

    public String getType(){
        return type;
    }

    public String getVal(){
        return val;
    }

    public String getDate(){
        return date;
    }

    public String getAuthorityId() {
        return authorityId;
    }

    public String getResult() {
        return result;
    }

    public PendingDocModel(String name, String email, String pendingState, String id, String authority, String requestId, String result, String authorityId, String date) {
        this.name = name;
        this.email = email;
        this.pendingState = pendingState;
        this.id = id;
        this.authority = authority;
        this.requestId = requestId;
        this.result = result;
        this.authorityId = authorityId;
        this.date = date;
    }

    public String getRequestId() {
        return requestId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPendingState() {
        return pendingState;
    }

    public String getId() {
        return id;
    }

    public String getAuthority() {
        return authority;
    }

}
