package com.example.android.perfectperfume.data;

import android.os.Parcel;
import android.os.Parcelable;

public class Perfume implements Parcelable {

    private String brand;
    private String description;
    private String sex;
    private int id;
    private String imageUrl;
    private String name;
    private String price;
    private String size;

    public Perfume() {
        // needed for firebase apparently to automatically create instances
    }

    public Perfume(int id, String brand, String description, String sex, String imageurl,
                   String name, String price, String size) {
        this.brand = brand;
        this.description = description;
        this.sex = sex;
        this.id = id;
        this.imageUrl = imageurl;
        this.name = name;
        this.price = price;
        this.price = size;
    }

    public Perfume(Parcel in) {
        this.brand = in.readString();
        this.description = in.readString();
        this.sex = in.readString();
        this.id = in.readInt();
        this.imageUrl = in.readString();
        this.name = in.readString();
        this.price = in.readString();
        this.size = in.readString();
    }

    public String getBrand() {
        return this.brand;
    }

    public String getDescription() {
        return this.description;
    }

    public String getSex() {
        return this.sex;
    }

    public int getId() {
        return this.id;
    }

    public String getimageurl() {
        return this.imageUrl;
    }

    public String getName() {
        return this.name;
    }

    public String getPrice() {
        return this.price;
    }

    public String getSize() {
        return this.size;
    }

    public void setBrand(String mBrand) {
        this.brand = mBrand;
    }

    public void setDescription(String mDescription) {
        this.description = mDescription;
    }

    public void setSex(String mSex) {
        this.sex = mSex;
    }

    public void setId(int mId) {
        this.id = mId;
    }

    public void setImageurl(String mImageurl) {
        this.imageUrl = mImageurl;
    }

    public void setName(String mName) {
        this.name = mName;
    }

    public void setPrice(Double mPrice) {
        this.price = Double.toString(mPrice);
    }

    public void setSize(String mSize) {
        this.size = mSize;
    }

    // Parcelable methods & creator
    public static final Creator<Perfume> CREATOR = new Creator<Perfume>() {
        @Override
        public Perfume createFromParcel(Parcel in) {
            return new Perfume(in);
        }

        @Override
        public Perfume[] newArray(int size) {
            return new Perfume[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.brand);
        parcel.writeString(this.description);
        parcel.writeString(this.sex);
        parcel.writeInt(this.id);
        parcel.writeString(this.imageUrl);
        parcel.writeString(this.name);
        parcel.writeString(this.price);
        parcel.writeString(this.size);
    }
}