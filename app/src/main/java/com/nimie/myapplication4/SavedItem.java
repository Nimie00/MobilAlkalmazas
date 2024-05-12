package com.nimie.myapplication4;

public class SavedItem {
    private String key;
    private String value;

    private String userEmail;
    private String keyOfDownloaded;
    private String dateTimeOfSave;
    private String data;

    public SavedItem(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public SavedItem(String userEmail, String keyOfDownloaded, String dateTimeOfSave, String data) {
        this.userEmail = userEmail;
        this.keyOfDownloaded = keyOfDownloaded;
        this.dateTimeOfSave = dateTimeOfSave;
        this.data = data;
    }

    public SavedItem() {
        // Üres konstruktor
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setValue(String value) {
        this.value = value;
    }

    // Getterek
    public String getUserEmail() {
        return userEmail;
    }

    public String getKeyOfDownloaded() {
        return keyOfDownloaded;
    }

    public String getDateTimeOfSave() {
        return dateTimeOfSave;
    }

    public String getData() {
        return data;
    }

    // Setterek
    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public void setKeyOfDownloaded(String keyOfDownloaded) {
        this.keyOfDownloaded = keyOfDownloaded;
    }

    public void setDateTimeOfSave(String dateTimeOfSave) {
        this.dateTimeOfSave = dateTimeOfSave;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Key: " + this.keyOfDownloaded + ", Data: " + this.data;
    }
}