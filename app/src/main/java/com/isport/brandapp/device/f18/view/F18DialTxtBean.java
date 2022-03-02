package com.isport.brandapp.device.f18.view;

public class F18DialTxtBean {

    private int imgResource;

    private boolean isChecked;


    public F18DialTxtBean(int imgResource, boolean isChecked) {
        this.imgResource = imgResource;
        this.isChecked = isChecked;
    }

    public int getImgResource() {
        return imgResource;
    }

    public void setImgResource(int imgResource) {
        this.imgResource = imgResource;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
