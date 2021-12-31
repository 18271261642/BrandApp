package com.isport.brandapp.util;

/**
 * Created by Admin
 * Date 2021/10/21
 */
public class SmsInfo {

    private int id;

    private String address;

    //联系人姓名
    private String person;

    private long date;

    private int type;

    private String body;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }

    @Override
    public String toString() {
        return "SmsInfo{" +
                "id=" + id +
                ", address='" + address + '\'' +
                ", person='" + person + '\'' +
                ", date=" + date +
                ", type=" + type +
                ", body='" + body + '\'' +
                '}';
    }
}
