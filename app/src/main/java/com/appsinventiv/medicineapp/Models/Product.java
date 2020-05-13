package com.appsinventiv.medicineapp.Models;

/**
 * Created by AliAh on 20/06/2018.
 */

public class Product {
    String id, title, subtitle, isActive;
    int price;
    long time;

    public Product(String id, String title, String subtitle, String isActive, int price, long time) {
        this.id = id;
        this.title = title;
        this.subtitle = subtitle;
        this.isActive = isActive;
        this.price = price;
        this.time = time;
    }

    public Product() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
