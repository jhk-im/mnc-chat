package im.vector.app

import android.app.Application
import android.content.Context
import android.os.Handler
import android.os.HandlerThread
import android.os.StrictMode
import androidx.core.provider.FontRequest
import androidx.core.provider.FontsContractCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import im.vector.app.core.di.ActiveSessionHolder
import im.vector.app.core.di.DaggerVectorComponent
import im.vector.app.core.di.HasVectorInjector
import im.vector.app.core.di.VectorComponent
import im.vector.app.features.lifecycle.VectorActivityLifecycleCallbacks
import im.vector.app.features.popup.PopupAlertManager
import im.vector.app.features.settings.VectorPreferences
import org.jitsi.meet.sdk.log.JitsiMeetDefaultLogHandler
import timber.log.Timber
import javax.inject.Inject

class VectorApplication :
        Application(),
        HasVectorInjector {

    lateinit var appContext: Context
    lateinit var vectorComponent: VectorComponent

    @Inject lateinit var emojiCompatFontProvider: EmojiCompatFontProvider
    @Inject lateinit var emojiCompatWrapper: EmojiCompatWrapper
    @Inject lateinit var activeSessionHolder: ActiveSessionHolder
    @Inject lateinit var vectorPreferences: VectorPreferences
    @Inject lateinit var popupAlertManager: PopupAlertManager

    // font thread handler
    private var fontThreadHandler: Handler? = null

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

        registerActivityLifecycleCallbacks(VectorActivityLifecycleCallbacks(popupAlertManager))

        // font
        val fontRequest = FontRequest(
            "com.google.android.gms.fonts",
            "com.google.android.gms",
            "Noto Color Emoji Compat",
            R.array.com_google_android_gms_fonts_certs
        )
        FontsContractCompat.requestFont(this, fontRequest, emojiCompatFontProvider, getFontThreadHandler())
        emojiCompatWrapper.init(fontRequest)

        if (/*authenticationService.hasAuthenticatedSessions() && */!activeSessionHolder.hasActiveSession()) {
            //val lastAuthenticatedSession = authenticationService.getLastAuthenticatedSession()!!
            //activeSessionHolder.setActiveSession(lastAuthenticatedSession)
            //lastAuthenticatedSession.configureAndStart(applicationContext)
        }
        ProcessLifecycleOwner.get().lifecycle.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
            fun entersForeground() {
                Timber.i("App entered foreground")
                //FcmHelper.onEnterForeground(appContext, activeSessionHolder)
                activeSessionHolder.getSafeActiveSession()?.also {
                    it.stopAnyBackgroundSync()
                }
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
            fun entersBackground() {
                Timber.i("App entered background") // call persistInfo
                //notificationDrawerManager.persistInfo()
                //FcmHelper.onEnterBackground(appContext, vectorPreferences, activeSessionHolder)
            }
        })
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

    /**
     * Font
     **/
    private fun getFontThreadHandler(): Handler {
        return fontThreadHandler ?: createFontThreadHandler().also {
            fontThreadHandler = it
        }
    }

    private fun createFontThreadHandler(): Handler {
        val handlerThread = HandlerThread("fonts")
        handlerThread.start()
        return Handler(handlerThread.looper)
    }
}