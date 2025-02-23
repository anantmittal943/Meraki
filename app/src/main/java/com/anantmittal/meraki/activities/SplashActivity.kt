package com.anantmittal.meraki.activities

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.os.Looper
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.anantmittal.meraki.R

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_splash)

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, LoginActivity::class.java)
            val options = ActivityOptions.makeCustomAnimation(this, R.anim.fade_in, R.anim.fade_out)
            startActivity(intent, options.toBundle())
//            overridePendingTransition(R.anim.fade_in,R.anim.fade_out)
            finish()
        }, 2000)
    }

}