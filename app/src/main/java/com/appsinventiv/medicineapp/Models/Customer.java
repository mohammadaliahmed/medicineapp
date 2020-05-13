package com.appsinventiv.medicineapp.Models;

/**
 * Created by AliAh on 20/06/2018.
 */

public class Customer {
    String id,name, password, phone,address,fcmKey;
    long time;



    public Customer() {
    }

    public Customer(String id, String name, String password, String phone, String address, String fcmKey, long time) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.phone = phone;
        this.address = address;
        this.fcmKey = fcmKey;
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getFcmKey() {
        return fcmKey;
    }

    public void setFcmKey(String fcmKey) {
        this.fcmKey = fcmKey;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
