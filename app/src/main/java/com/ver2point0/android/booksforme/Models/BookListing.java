package com.ver2point0.android.booksforme.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class BookListing implements Parcelable {

    private String mBookTitle;
    private String mAuthor;

    public BookListing(String bookTitle, String author) {
        this.mBookTitle = bookTitle;
        this.mAuthor = author;
    }

    protected BookListing(Parcel in) {
        mBookTitle = in.readString();
        mAuthor = in.readString();
    }

    public static final Creator<BookListing> CREATOR = new Creator<BookListing>() {

        @Override
        public BookListing createFromParcel(Parcel in) {
            return new BookListing(in);
        }

        @Override
        public BookListing[] newArray(int size) {
            return new BookListing[size];
        }
    };

    public String getBookTitle() {
        return mBookTitle;
    }

    public String getAuthor() {
        return mAuthor;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mBookTitle);
        parcel.writeString(mAuthor);
    }
}

