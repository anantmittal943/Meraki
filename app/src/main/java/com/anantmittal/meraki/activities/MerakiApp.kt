package com.anantmittal.meraki.activities

import android.app.Application
import android.util.Log
import com.anantmittal.meraki.BuildConfig
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions

class MerakiApp : Application() {
    override fun onCreate() {
        super.onCreate()

//        Log.d(TAG, "onCreate: ${BuildConfig.current_key}")

        if (FirebaseApp.getApps(this).isNotEmpty()) {
            FirebaseApp.getInstance().delete()
            Log.d(TAG, "onCreate: Firebase instance deleted")
        }

        val options = FirebaseOptions.Builder()
            .setApplicationId("1:402848379816:android:aa1e5470630ab7e118239d")
            .setApiKey(BuildConfig.current_key)
            .setDatabaseUrl("https://meraki-1b27e-default-rtdb.europe-west1.firebasedatabase.app/")
            .build()

        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseApp.initializeApp(this, options)
            Log.d(TAG, "onCreate: firebase initialized successfully")
        } else {
            Log.d(TAG, "onCreate: not")
        }
    }
}