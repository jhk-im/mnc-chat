package im.vector.app

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import android.os.StrictMode
import androidx.lifecycle.ProcessLifecycleOwner
import im.vector.app.core.di.DaggerVectorComponent
import im.vector.app.core.di.HasVectorInjector
import im.vector.app.core.di.VectorComponent
import im.vector.app.features.pin.PinLocker
import im.vector.app.features.settings.VectorPreferences
import org.jitsi.meet.sdk.log.JitsiMeetDefaultLogHandler
import timber.log.Timber
import javax.inject.Inject

class VectorApplication :
        Application(),
        HasVectorInjector {

    lateinit var appContext: Context
    lateinit var vectorComponent: VectorComponent

    // font thread handler
    // private var fontThreadHandler: Handler? = null

    @Inject lateinit var vectorPreferences: VectorPreferences

    /*
    @Inject lateinit var pinLocker: PinLocker
    private val powerKeyReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            if (intent.action == Intent.ACTION_SCREEN_OFF &&
                vectorPreferences.useFlagPinCode()) {
                pinLocker.screenIsOff()
            }
        }
    }
    */

    override fun onCreate() {
        enableStrictModeIfNeeded()
        super.onCreate()
        appContext = this
        vectorComponent = DaggerVectorComponent.factory().create(this)
        vectorComponent.inject(this)

        // Remove Log handler statically added by Jitsi
        Timber.forest()
            .filterIsInstance(JitsiMeetDefaultLogHandler::class.java)
            .forEach { Timber.uproot(it) }

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        Timber.plant(vectorComponent.vectorFileLogger())

        /*
        ProcessLifecycleOwner.get().lifecycle.addObserver(pinLocker)
        applicationContext.registerReceiver(powerKeyReceiver, IntentFilter().apply {
            // Looks like i cannot receive OFF, if i don't have both ON and OFF
            addAction(Intent.ACTION_SCREEN_OFF)
            addAction(Intent.ACTION_SCREEN_ON)
        })
        */
    }

    override fun injector(): VectorComponent {
        return vectorComponent
    }

    /**
     * 진저브레드에서 추가된 개발툴
     * 개발자의 실수를 감지하고 해결할 수 있도록 돕는 모드
     **/
    private fun enableStrictModeIfNeeded() {
        //enableStrictModeIfNeeded()
        if (BuildConfig.ENABLE_STRICT_MODE_LOGS) {
            StrictMode.setThreadPolicy(
                StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build())
        }
    }
}