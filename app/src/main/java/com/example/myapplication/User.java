package com.example.myapplication;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class User {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String parentName;
    private String parentPhone;
    private String parentRelation;
    private String childName;
    private String childAge;
    private String childDevice;
    private String passwordHash;
    private String salt;

    public User(String parentName, String parentPhone, String parentRelation,
                String childName, String childAge, String childDevice,
                String passwordHash, String salt) {
        this.parentName = parentName;
        this.parentPhone = parentPhone;
        this.parentRelation = parentRelation;
        this.childName = childName;
        this.childAge = childAge;
        this.childDevice = childDevice;
        this.passwordHash = passwordHash;
        this.salt = salt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public String getParentPhone() {
        return parentPhone;
    }

    public void setParentPhone(String parentPhone) {
        this.parentPhone = parentPhone;
    }

    public String getParentRelation() {
        return parentRelation;
    }

    public void setParentRelation(String parentRelation) {
        this.parentRelation = parentRelation;
    }

    public String getChildName() {
        return childName;
    }

    public void setChildName(String childName) {
        this.childName = childName;
    }

    public String getChildAge() {
        return childAge;
    }

    public void setChildAge(String childAge) {
        this.childAge = childAge;
    }

    public String getChildDevice() {
        return childDevice;
    }

    public void setChildDevice(String childDevice) {
        this.childDevice = childDevice;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }
}
