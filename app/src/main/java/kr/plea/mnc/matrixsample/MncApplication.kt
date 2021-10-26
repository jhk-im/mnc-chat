package kr.plea.mnc.matrixsample

import android.app.Activity
import android.os.Bundle
import androidx.multidex.MultiDexApplication
import kr.plea.matrixsample.libpmmacore.im.core.PMMACore
import timber.log.Timber

class MncApplication : MultiDexApplication() {
    private val TAG = MncApplication::class.java.simpleName

    companion object {
        @Volatile private var INSTANCE: MncApplication? = null

        @JvmStatic fun getInstance(): MncApplication =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: MncApplication().also {
                    INSTANCE = it
                }
            }
    }

    // Intent request code
    // val REQ_CODE_PERMISSION = 1001
    // val REQ_CODE_AUTH_SMS = 1002
    // val REQ_CODE_FINGER_SET = 1003

    /* TODO instance of PMMACore */
    var mPMMACore: PMMACore? = null

    private val mLifecycleCallbacks: ActivityLifecycleCallbacks =
        object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                Timber.tag(TAG).v("onActivityCreated $activity")

                /* TODO call PMMACore lifecycle control interface */
                mPMMACore?.let {
                    if (activity is AuthCompatActivity) {
                        it.onActivityCreated(activity)
                    }
                }
            }

            override fun onActivityStarted(activity: Activity) {
                Timber.tag(TAG).v("onActivityStarted $activity")

                /* TODO call PMMACore lifecycle control interface */
                mPMMACore?.let {
                    if (activity is AuthCompatActivity) {
                        it.onActivityStarted(activity)
                    }
                }
            }

            override fun onActivityResumed(activity: Activity) {
                Timber.tag(TAG).v("onActivityResumed $activity")

                /* TODO call PMMACore lifecycle control interface */
                mPMMACore?.let {
                    if (activity is AuthCompatActivity) {
                        it.onActivityResumed(activity)
                    }
                }
            }

            override fun onActivityPaused(activity: Activity) {
                Timber.tag(TAG).v("onActivityPaused $activity")

                /* TODO call PMMACore lifecycle control interface */
                mPMMACore?.let {
                    if (activity is AuthCompatActivity) {
                        it.onActivityPaused(activity)
                    }
                }
            }

            override fun onActivityStopped(activity: Activity) {
                Timber.tag(TAG).v("onActivityStopped $activity")

                /* TODO call PMMACore lifecycle control interface */
                mPMMACore?.let {
                    if (activity is AuthCompatActivity) {
                        it.onActivityStopped(activity)
                    }
                }
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
                Timber.tag(TAG).v("onActivitySaveInstanceState $activity")

                /* TODO call PMMACore lifecycle control interface */
                mPMMACore?.let {
                    if (activity is AuthCompatActivity) {
                        it.onActivitySaveInstanceState(activity)
                    }
                }
            }

            override fun onActivityDestroyed(activity: Activity) {
                Timber.tag(TAG).v("onActivityDestroyed $activity")

                /* TODO call PMMACore lifecycle control interface */
                mPMMACore?.let {
                    if (activity is AuthCompatActivity) {
                        it.onActivityDestroyed(activity)
                    }
                }
            }
        }

    override fun onCreate() {
        super.onCreate()
        Timber.tag(TAG).d("onCreate")
        registerActivityLifecycleCallbacks(mLifecycleCallbacks)
    }

    fun createdPMMACore() {
        /* TODO create instance of NIMCore */
        if (mPMMACore == null) {
            Timber.tag(TAG).d("createdPMMACore")
            mPMMACore = PMMACore.getInstance(this@MncApplication.applicationContext)
        }
    }
}