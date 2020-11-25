package com.inventerit.qrcodescanner.Model;

public class HistoryModel {
    private String time;
    private String type;
    private String id;
    private String val;
    private String date;
    private String secSum;
    private String nr;

    public String getNr() {
        return nr;
    }

    public String getType() {
        return type;
    }

    public String getTime() {
        return time;
    }

    public String getResult() {
        return result;
    }

    private String result;

    public String getVal() {
        return val;
    }

    public String getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public String getSecSum() {
        return secSum;
    }

    public HistoryModel(String time, String type, String result, String id, String val, String date, String secSum, String nr) {
        this.result = result;
        this.time = time;
        this.type = type;
        this.id = id;
        this.val = val;
        this.date = date;
        this.secSum = secSum;
        this.nr = nr;
    }
}
