package com.inventerit.qrcodescanner.Model;

public class Codes {
    private String code;
    private String id;
    private String type;
    private String val;

    public String getCode() {
        return code;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getVal() {
        return val;
    }

    public Codes(String code, String id, String type, String val) {
        this.code = code;
        this.id = id;
        this.type = type;
        this.val = val;
    }
}
