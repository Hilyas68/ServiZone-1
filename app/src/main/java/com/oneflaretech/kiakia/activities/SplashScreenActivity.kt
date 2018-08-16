package com.oneflaretech.kiakia.activities

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.oneflaretech.kiakia.R
import com.oneflaretech.kiakia.utils.AppConstants
import com.oneflaretech.kiakia.utils.AppSettings
import com.tbruyelle.rxpermissions2.RxPermissions

class SplashScreenActivity : AppCompatActivity() {
    val TAG = "SplashScreen"
    var app : AppSettings? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        app = AppSettings(this)
    }

    override fun onResume() {
        super.onResume()
        RxPermissions(this)
                .request(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                .subscribe { granted ->
                    if (granted) {
                        AppConstants.log(TAG, "Permission Accepted")
                        moveOn()
                    } else {
                        AppConstants.log(TAG, "Permission Rejected")
                        moveOn()
                    }
                }
    }

    fun moveOn(){
        if(app!!.user == null)
            startActivity(Intent(this@SplashScreenActivity, SignInActivity::class.java))
        else
            startActivity(Intent(this@SplashScreenActivity, MainActivity::class.java))
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out)
        finish()
    }
}
