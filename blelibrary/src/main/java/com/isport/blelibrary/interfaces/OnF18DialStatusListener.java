package com.isport.blelibrary.interfaces;

public interface OnF18DialStatusListener {

    void startDial();

    void onError(int errorType, int errorCode);

    void onStateChanged(int state, boolean cancelable);

    void onProgressChanged(int progress);

    void onSuccess();
}
