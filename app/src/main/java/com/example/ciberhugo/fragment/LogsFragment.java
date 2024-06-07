package com.example.ciberhugo.fragment;

public class LogsFragment {
    private int imageResource;
    private String languageId;

    public LogsFragment(int imageResource, String languageId) {
        this.imageResource = imageResource;
        this.languageId = languageId;
    }

    public int getImageResource() {
        return imageResource;
    }

    public String getLanguageId() {
        return languageId;
    }
}
