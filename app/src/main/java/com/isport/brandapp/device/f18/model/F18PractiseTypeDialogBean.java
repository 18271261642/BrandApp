package com.isport.brandapp.device.f18.model;

/**
 * Created by Admin
 * Date 2022/2/13
 */
public class F18PractiseTypeDialogBean {

    private int type;

    private String desc;

    private boolean isChecked;

    public F18PractiseTypeDialogBean() {
    }

    public F18PractiseTypeDialogBean(int type, String desc, boolean isChecked) {
        this.type = type;
        this.desc = desc;
        this.isChecked = isChecked;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    @Override
    public String toString() {
        return "F18PractiseTypeDialogBean{" +
                "type=" + type +
                ", desc='" + desc + '\'' +
                ", isChecked=" + isChecked +
                '}';
    }
}
