package com.inventerit.qrcodescanner.Model;


public class UserSumTableModel {
    private String type;
    private String val;
    private String nr;

    public String getType() {
        return type;
    }

    public String getVal() {
        return val;
    }

    public String getNr() {
        return nr;
    }

    public UserSumTableModel(String type, String val, String nr) {
        this.type = type;
        this.val = val;
        this.nr = nr;
    }
}
