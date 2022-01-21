package com.isport.blelibrary.entry;

import java.util.List;

public class WristbandWeather {
    private WristbandData condition;
    private List<WristbandForecast> forecast15Days;

    public WristbandData getCondition() {
        return condition;
    }

    public void setCondition(WristbandData condition) {
        this.condition = condition;
    }

    public List<WristbandForecast> getForecast15Days() {
        return forecast15Days;
    }

    public void setForecast15Days(List<WristbandForecast> forecast15Days) {
        this.forecast15Days = forecast15Days;
    }


}
