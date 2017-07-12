package net.idik.lib.slimintent.example;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import net.idik.lib.slimintent.R;
import net.idik.lib.slimintent.annotations.AutoIntent;

@AutoIntent
public class SecondActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
    }
}
