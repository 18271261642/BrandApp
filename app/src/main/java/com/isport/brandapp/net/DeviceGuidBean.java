package com.isport.brandapp.net;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Admin
 * Date 2022/5/11
 */

public class DeviceGuidBean implements Serializable {


    /**
     * deviceTypeIds : [7018,812,813,814]
     * guideUrl : https://test.api.mini-banana.com/bonlala-h5/#/guide?deviceTypeId=
     */

    private String guideUrl;
    private List<Integer> deviceTypeIds;


    public String getGuideUrl() {
        return guideUrl;
    }

    public void setGuideUrl(String guideUrl) {
        this.guideUrl = guideUrl;
    }

    public List<Integer> getDeviceTypeIds() {
        return deviceTypeIds;
    }

    public void setDeviceTypeIds(List<Integer> deviceTypeIds) {
        this.deviceTypeIds = deviceTypeIds;
    }
}
