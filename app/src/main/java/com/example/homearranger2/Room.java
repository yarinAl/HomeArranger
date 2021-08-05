package com.example.homearranger2;

import java.util.ArrayList;

public class Room {
    private int ImageResource;
    private String TextHeader;
    private String TextDescription;
    private ArrayList<Product> ProductList;

    public Room(int imageResource, String textHeader, String textDescription, ArrayList<Product> productList) {
        ImageResource = imageResource;
        TextHeader = textHeader;
        TextDescription = textDescription;
        ProductList = productList;
    }

    public int getImageResource() {
        return ImageResource;
    }

    public void setImageResource(int imageResource) {
        ImageResource = imageResource;
    }

    public String getTextHeader() {
        return TextHeader;
    }

    public void setTextHeader(String textHeader) {
        TextHeader = textHeader;
    }

    public String getTextDescription() {
        return TextDescription;
    }

    public void setTextDescription(String textDescription) {
        TextDescription = textDescription;
    }

    public ArrayList<Product> getProductList() {
        return ProductList;
    }

    public void setProductList(ArrayList<Product> productList) {
        ProductList = productList;
    }

//    הקודם למחוק כשמסיימים לשנות
//    public Room(int imageResource, String text1, String text2,ArrayList<Product> arrayList) {
//        mImageResource = imageResource;
//        mTextHeader = text1;
//        mTextDescription = text2;
//        this.arrayList = arrayList;
//    }
//
//    public ArrayList<Product> getArrayList() {
//        return arrayList;
//    }
//
//    public void setArrayList(ArrayList<Product> arrayList) {
//        this.arrayList = arrayList;
//    }
//
//    public void changeText1(String text) {
//        mTextHeader = text;
//    }
//
//    public int getImageResource() {
//        return mImageResource;
//    }
//
//    public String getText1() {
//        return mTextHeader;
//    }
//
//    public String getText2() {
//        return mTextDescription;
//    }
}
