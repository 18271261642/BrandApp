package com.isport.brandapp.device.bracelet.bean;

public class StateBean {

    public boolean isHrState;
    public boolean isCall;
    public boolean isMessage;
    public String tempUnitl;

    public boolean isHrState() {
        return isHrState;
    }

    public void setHrState(boolean hrState) {
        isHrState = hrState;
    }

    public boolean isCall() {
        return isCall;
    }

    public void setCall(boolean call) {
        isCall = call;
    }

    public boolean isMessage() {
        return isMessage;
    }

    public void setMessage(boolean message) {
        isMessage = message;
    }

    public String getTempUnitl() {
        return tempUnitl;
    }

    public void setTempUnitl(String tempUnitl) {
        this.tempUnitl = tempUnitl;
    }

    @Override
    public String toString() {
        return "StateBean{" +
                "isHrState=" + isHrState +
                ", isCall=" + isCall +
                ", isMessage=" + isMessage +
                ", isTempUnitl=" + tempUnitl +
                '}';
    }
}
