package com.huynhtinh.android.findhp;

/**
 * Created by TINH HUYNH on 3/7/2018.
 */

public enum PlaceType {
    DENTIST, DOCTOR, GYM, HOSPITAL, PHARMARCY, SPA, UNKNOWN;

    public static PlaceType toPlaceType(String s) {
        switch (s) {
            case "dentist":
                return DENTIST;
            case "doctor":
                return DOCTOR;
            case "gym":
                return GYM;
            case "hospital":
                return HOSPITAL;
            case "pharmacy":
                return PHARMARCY;
            case "spa":
                return SPA;
            default:
                return UNKNOWN;
        }
    }

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
            default:
                return "unknown";
        }
    }
}
