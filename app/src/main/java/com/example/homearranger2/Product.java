package com.example.homearranger2;

import java.io.Serializable;
import java.util.Date;


public class Product implements Serializable {

    private String imageResource;
    private String name;
    private int amount;
    private String location;
    private String date;
    private String isFavourite;



    public Product() {

    }

    public Product(String imageResource,String name, int amount, String location, String date,String isFavourite) {
        setName(name);
        setDate(date);
        setAmount(amount);
        setLocation(location);
        setImageResource(imageResource);
        setFavourite(isFavourite);

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
    public String getImageResource() {
        return imageResource;
    }

    public void setImageResource(String imageResource) {
        this.imageResource = imageResource;
    }

    public String GetIsFavourite() {
        return isFavourite;
    }

    public void setFavourite(String isFavourite) {
        this.isFavourite = isFavourite;
    }


}
