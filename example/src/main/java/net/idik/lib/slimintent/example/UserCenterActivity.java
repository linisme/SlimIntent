package net.idik.lib.slimintent.example;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import net.idik.lib.slimintent.R;
import net.idik.lib.slimintent.SlimIntent;
import net.idik.lib.slimintent.annotations.IntentArg;
import net.idik.lib.slimintent.annotations.AutoIntent;

@AutoIntent
public class UserCenterActivity extends AppCompatActivity {

    @IntentArg
    int userId;

    @IntentArg
    String book;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_center);
        SlimIntent.bind(this);
        System.out.println(userId + "   ::::   " + book);
    }

    public static class Book implements Parcelable {

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
        }

        public Book() {
        }

        protected Book(Parcel in) {
        }

        public static final Parcelable.Creator<Book> CREATOR = new Parcelable.Creator<Book>() {
            @Override
            public Book createFromParcel(Parcel source) {
                return new Book(source);
            }

            @Override
            public Book[] newArray(int size) {
                return new Book[size];
            }
        };
    }

}
