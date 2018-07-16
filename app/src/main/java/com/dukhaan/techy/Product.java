package com.dukhaan.techy;


import java.io.Serializable;

public class Product implements Serializable {

    private String img;
    private String age;
    private String cat;
    private String name;
    private String desc;
    private String pid;
    private String uid;
    private String price;

    public Product(String img, String age, String cat, String name, String desc, String pid, String uid, String price) {
        this.img = img;
        this.age = age;
        this.cat = cat;
        this.name = name;
        this.desc = desc;
        this.pid = pid;
        this.uid = uid;
        this.price = price;
    }

    public Product() {
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getCat() {
        return cat;
    }

    public void setCat(String cat) {
        this.cat = cat;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}