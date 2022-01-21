package com.isport.brandapp.device.f18.model;

/**
 * Created by Admin
 * Date 2022/1/19
 */
public class F18ContactBean {

    private String contactName;

    private String contactNumber;

    public F18ContactBean() {
    }

    public F18ContactBean(String contactName, String contactNumber) {
        this.contactName = contactName;
        this.contactNumber = contactNumber;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    @Override
    public String toString() {
        return "F18ContactBean{" +
                "contactName='" + contactName + '\'' +
                ", contactNumber='" + contactNumber + '\'' +
                '}';
    }
}
