package net.idik.lib.slimintent.example;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import net.idik.lib.slimintent.R;
import net.idik.lib.slimintent.SlimIntent;
import net.idik.lib.slimintent.annotations.AutoIntent;


@AutoIntent
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startActivity(SlimIntent.toUserCenterActivity(this, 23, "好呀"));
    }
}
