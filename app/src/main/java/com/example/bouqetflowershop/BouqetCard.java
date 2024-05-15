package com.example.bouqetflowershop;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class BouqetCard implements Parcelable {
    String name;
    int id;
    int price;
    int image;
    boolean box;

    BouqetCard(String _describe, int _id, int _price, int _image, boolean _box){
        name = _describe;
        id = _id;
        price = _price;
        image = _image;
        box = _box;
    }

    protected BouqetCard(Parcel in) {
        name = in.readString();
        id = in.readInt();
        price = in.readInt();
        image = in.readInt();
        box = in.readByte() != 0;
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
        dest.writeInt(id);
        dest.writeInt(price);
        dest.writeInt(image);
        dest.writeByte((byte) (box ? 1 : 0));
    }
}
