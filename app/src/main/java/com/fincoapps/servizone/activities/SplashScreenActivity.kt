package com.fincoapps.servizone.activities

import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.fincoapps.servizone.MainActivity
import com.fincoapps.servizone.R
import com.fincoapps.servizone.utils.AppConstants
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
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out)
        if(app.user == null)
            startActivity(Intent(this@SplashScreenActivity, SignInActivity::class.java))
        else
            startActivity(Intent(this@SplashScreenActivity, MainActivity::class.java))
        finish()
    }
}
