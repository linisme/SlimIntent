package net.idik.lib.slimintent.sampleforkotlin

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import net.idik.lib.slimintent.annotations.AutoIntent
import net.idik.lib.slimintent.annotations.IntentArg

@AutoIntent
public class UserCenterActivity : AppCompatActivity() {

    @field:IntentArg
    @JvmField
    var userId: Int? = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_center)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

    }

}
