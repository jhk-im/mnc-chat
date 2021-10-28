package im.vector.app

import android.app.Application
import android.content.Context
import android.os.StrictMode

class VectorApplication : Application() {

    lateinit var appContext: Context

    override fun onCreate() {
        enableStrictModeIfNeeded()
        super.onCreate()
        appContext = this

    }

    /**
     * 진저브레드에서 추가된 개발툴
     * 개발자의 실수를 감지하고 해결할 수 있도록 돕는 모드
     **/
    private fun enableStrictModeIfNeeded() {
        enableStrictModeIfNeeded()
        if (BuildConfig.ENABLE_STRICT_MODE_LOGS) {
            StrictMode.setThreadPolicy(
                StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build())
        }
    }
}