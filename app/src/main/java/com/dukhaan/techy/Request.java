package com.dukhaan.techy;


import java.io.Serializable;

public class Request implements Serializable{

    String bid;
    String pid;
    String sid;
    String status;

    public Request(String bid, String pid, String sid, String status) {
        this.bid = bid;
        this.pid = pid;
        this.sid = sid;
        this.status = status;
    }

    public Request() {
    }

    public String getBid() {
        return bid;
    }

    public void setBid(String bid) {
        this.bid = bid;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}