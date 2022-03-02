package com.isport.blelibrary.db.table.f18;

/**
 * Created by Admin
 * Date 2022/2/21
 */
public class F18StepBean {

    private int step;

    private float distance;

    private float kcal;

    public F18StepBean() {
    }

    public F18StepBean(int step, float distance, float kcal) {
        this.step = step;
        this.distance = distance;
        this.kcal = kcal;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public float getKcal() {
        return kcal;
    }

    public void setKcal(float kcal) {
        this.kcal = kcal;
    }

    @Override
    public String toString() {
        return "F18StepBean{" +
                "step=" + step +
                ", distance=" + distance +
                ", kcal=" + kcal +
                '}';
    }
}
