package com.example.bouqetflowershop;

import android.os.Parcel;

public class BouqetCard {
    String name;
    String id;
    String price;
    String imageUri;
    String type;
    Integer number;

    public BouqetCard() {
    }

    public BouqetCard(String name, String id, String price, String imageUri, String type, Integer number) {
        this.name = name;
        this.id = id;
        this.price = price;
        this.imageUri = imageUri;
        this.type = type;
        this.number = number;
    }

    public BouqetCard(String name, String price, String imageUri) {
        this.name = name;
        this.price = price;
        this.imageUri = imageUri;
    }

    public BouqetCard(String name, String id, String price, String imageUri, String type) {
        this.name = name;
        this.id = id;
        this.price = price;
        this.imageUri = imageUri;
        this.type = type;
    }

    protected BouqetCard(Parcel in) {
        name = in.readString();
        id = String.valueOf(in.readInt());
        price = String.valueOf(in.readInt());
        imageUri = String.valueOf(in.readInt());
    }


    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getPrice() {
        return price;
    }

    public String getImageUri() {
        return imageUri;
    }

    public String getType() {
        return type;
    }


    public Integer getNumber() {
        return number;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }
}
