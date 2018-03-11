package com.huynhtinh.android.findhp;

/**
 * Created by TINH HUYNH on 3/7/2018.
 */

public enum PlaceType {
    DENTIST, DOCTOR, GYM, HOSPITAL, PHARMARCY, SPA;

    @Override
    public String toString() {
        switch (this) {
            case DENTIST:
                return "dentist";
            case DOCTOR:
                return "doctor";
            case GYM:
                return "gym";
            case HOSPITAL:
                return "hospital";
            case PHARMARCY:
                return "pharmacy";
            case SPA:
                return "spa";
        }
        return super.toString();
    }
}
