package com.kuro.yemaadmin.data.model;

public class User {
    private String uid;
    private String managedCountryCode;

    public User() {

    }

    public User(String userId, String managedCountry) {
        this.uid = userId;
        this.managedCountryCode = managedCountry;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getManagedCountryCode() {
        return managedCountryCode;
    }

    public void setManagedCountryCode(String managedCountryCode) {
        this.managedCountryCode = managedCountryCode;
    }
}
