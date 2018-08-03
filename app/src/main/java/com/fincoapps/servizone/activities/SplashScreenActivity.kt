package com.fincoapps.servizone.activities

import android.Manifest
import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.fincoapps.servizone.MainActivity
import com.fincoapps.servizone.R
import com.fincoapps.servizone.utils.AppConstants
import com.tbruyelle.rxpermissions2.RxPermissions
import java.util.concurrent.TimeUnit

class SplashScreenActivity : BaseActivity() {
    val TAG = "SplashScreen";
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
    }

    override fun onResume() {
        super.onResume()
        Thread.sleep(TimeUnit.SECONDS.toMillis(5).toInt().toLong())
        AppConstants.log(TAG, "Sleep Finished")
        var rxx  = RxPermissions(this)
        rxx
                .request(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.RECEIVE_SMS, Manifest.permission.SEND_SMS)
                .subscribe { granted ->
                    if (granted) {

                    } else {

                    }
                }
        if(app.user == null)
            startActivity(Intent(this@SplashScreenActivity, SignInActivity::class.java))
        else
            startActivity(Intent(this@SplashScreenActivity, MainActivity::class.java))
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out)
        finish()
    }
}
