package com.isport.blelibrary.entry;

import com.htsmart.wristband2.bean.WristbandContacts;

import java.util.List;

/**
 * Created by Admin
 * Date 2022/1/19
 */
public interface F18ContactListener {

    void onContactAllData(List<WristbandContacts> list);
}
