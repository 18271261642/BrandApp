package com.isport.brandapp.wu.bean;

public class DrawRecDataBean {
    String strdate;
    int colors;
    int value;

    private boolean isClick;

    public boolean isClick() {
        return isClick;
    }

    public void setClick(boolean click) {
        isClick = click;
    }

    public String getStrdate() {
        return strdate;
    }

    public void setStrdate(String strdate) {
        this.strdate = strdate;
    }

    public int getColors() {
        return colors;
    }

    public void setColors(int colors) {
        this.colors = colors;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "DrawRecDataBean{" +
                "strdate='" + strdate + '\'' +
                ", colors=" + colors +
                ", value=" + value +
                '}';
    }
}
