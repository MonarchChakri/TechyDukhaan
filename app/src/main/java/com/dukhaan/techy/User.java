package com.dukhaan.techy;

import java.io.Serializable;

public class User implements Serializable {

    private String name;
    private String cllg;
    private String lat;
    private String lng;
    private String addr;
    private String phNum;
    private String cllg_lat;
    private String cllg_lng;
    private String mail;
    private String uid;

    public User(String name, String cllg, String cllg_lat, String cllg_lng,
                String addr, String lat, String lng, String phNum, String mail, String uid) {
        this.name = name;
        this.cllg = cllg;
        this.lat = lat;
        this.lng = lng;
        this.addr = addr;
        this.phNum = phNum;
        this.mail = mail;
        this.uid = uid;
        this.cllg_lat = cllg_lat;
        this.cllg_lng = cllg_lng;
    }

    public User() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCllg() {
        return cllg;
    }

    public void setCllg(String cllg) {
        this.cllg = cllg;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getPhNum() {
        return phNum;
    }

    public void setPhNum(String phNum) {
        this.phNum = phNum;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCllg_lat() {
        return cllg_lat;
    }

    public void setCllg_lat(String cllg_lat) {
        this.cllg_lat = cllg_lat;
    }

    public String getCllg_lng() {
        return cllg_lng;
    }

    public void setCllg_lng(String cllg_lng) {
        this.cllg_lng = cllg_lng;
    }

}