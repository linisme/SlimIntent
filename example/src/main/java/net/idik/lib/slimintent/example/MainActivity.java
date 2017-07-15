package net.idik.lib.slimintent.example;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import net.idik.lib.slimintent.R;
import net.idik.lib.slimintent.SlimIntent;
import net.idik.lib.slimintent.annotations.AutoIntent;


@AutoIntent
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.clickButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SlimIntent.toUserCenterActivity(MainActivity.this, 123, 32.3f, 32.3, 12, 1, 32.3f, 32.3, 2331L, "kdfj", "kdjfkdsf", '2', new UserCenterActivity.Book("鲁宾", 12.3f)).start(MainActivity.this);
            }
        });
    }
}
