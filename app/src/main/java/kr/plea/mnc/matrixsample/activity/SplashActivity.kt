package kr.plea.mnc.matrixsample.activity

import android.os.Bundle
import kr.plea.mnc.matrixsample.AuthCompatActivity
import kr.plea.mnc.matrixsample.R

class SplashActivity : AuthCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
    }
}