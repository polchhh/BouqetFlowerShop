package com.example.bouqetflowershop;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class BouqetCard implements Parcelable {
    String name;
    String id;
    String price;
    String imageUri;


    public BouqetCard() {}

    public BouqetCard(String name, String price) {
        this.name = name;
        this.price = price;
    }

    public BouqetCard(String name, String price, String imageUri) {
        this.name = name;
        this.price = price;
        this.imageUri = imageUri;
    }

    public BouqetCard(String name, String id, String price, String imageUri) {
        this.name = name;
        this.id = id;
        this.price = price;
        this.imageUri = imageUri;
    }

    protected BouqetCard(Parcel in) {
        name = in.readString();
        id = String.valueOf(in.readInt());
        price = String.valueOf(in.readInt());
        imageUri = String.valueOf(in.readInt());
    }

    public static final Creator<BouqetCard> CREATOR = new Creator<BouqetCard>() {
        @Override
        public BouqetCard createFromParcel(Parcel in) {
            return new BouqetCard(in);
        }

        @Override
        public BouqetCard[] newArray(int size) {
            return new BouqetCard[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(Integer.parseInt(id));
        dest.writeInt(Integer.parseInt(price));
        dest.writeInt(Integer.parseInt(imageUri));
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
}
