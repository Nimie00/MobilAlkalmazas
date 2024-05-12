package com.nimie.myapplication4;

public class SavedDataItem {
    private String userEmail;
    private String keyOfDownloaded;
    private String dateTimeOfSave;
    private String data;

    public SavedDataItem(String userEmail, String keyOfDownloaded, String dateTimeOfSave, String data) {
        this.userEmail = userEmail;
        this.keyOfDownloaded = keyOfDownloaded;
        this.dateTimeOfSave = dateTimeOfSave;
        this.data = data;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getKeyOfDownloaded() {
        return keyOfDownloaded;
    }

    public void setKeyOfDownloaded(String keyOfDownloaded) {
        this.keyOfDownloaded = keyOfDownloaded;
    }

    public String getDateTimeOfSave() {
        return dateTimeOfSave;
    }

    public void setDateTimeOfSave(String dateTimeOfSave) {
        this.dateTimeOfSave = dateTimeOfSave;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "SavedDataItem{" +
                "userEmail='" + userEmail + '\'' +
                ", keyOfDownloaded='" + keyOfDownloaded + '\'' +
                ", dateTimeOfSave='" + dateTimeOfSave + '\'' +
                ", data='" + data + '\'' +
                '}';
    }
}
