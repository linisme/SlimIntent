package net.idik.lib.slimintent.example;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import net.idik.lib.slimintent.R;
import net.idik.lib.slimintent.SlimIntent;
import net.idik.lib.slimintent.annotations.IntentArg;
import net.idik.lib.slimintent.annotations.AutoIntent;

@AutoIntent
public class UserCenterActivity extends AppCompatActivity {


    @IntentArg
    int userId;

    @IntentArg
    float a;

    @IntentArg
    double b;

    @IntentArg
    long d;

    @IntentArg
    Integer e;

    @IntentArg
    Float f;

    @IntentArg
    Double g;

    @IntentArg
    Long l;

    @IntentArg
    String i;

    @IntentArg
    CharSequence j;

    @IntentArg
    char h;

    @IntentArg
    Book book;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_center);
        SlimIntent.bind(this);
        ((TextView) findViewById(R.id.textView)).setText(this.toString());
    }

    @Override
    public String toString() {
        return "UserCenterActivity{" +
                "userId=" + userId +
                ", a=" + a +
                ", b=" + b +
                ", d=" + d +
                ", e=" + e +
                ", f=" + f +
                ", g=" + g +
                ", l=" + l +
                ", i='" + i + '\'' +
                ", j=" + j +
                ", h=" + h +
                ", book='" + book + '\'' +
                '}';
    }

    public static class Book implements Parcelable {

        String name;
        float price;

        public Book(String name, float price) {
            this.name = name;
            this.price = price;
        }

        public Book() {
        }

        @Override
        public String toString() {
            return "Book{" +
                    "name='" + name + '\'' +
                    ", price=" + price +
                    '}';
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.name);
            dest.writeFloat(this.price);
        }

        protected Book(Parcel in) {
            this.name = in.readString();
            this.price = in.readFloat();
        }

        public static final Creator<Book> CREATOR = new Creator<Book>() {
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
