package com.example.financetracker;

public class InitialPlanningDataModel {
    private String title;
    private int value;
    private String date;

    public InitialPlanningDataModel(String title, int value, String date) {
        this.title = title;
        this.value = value;
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
