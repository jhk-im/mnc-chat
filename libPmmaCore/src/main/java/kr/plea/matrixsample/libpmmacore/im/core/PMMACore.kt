package kr.plea.matrixsample.libpmmacore.im.core

import android.app.Activity
import android.content.Context
import timber.log.Timber

/**
 * The main application injection point
 */
class PMMACore private constructor(context: Context) {
    private val TAG = PMMACore::class.java.simpleName

    /** Action **/
    val ACTION_INTRO: String = "kr.plea.mnc.matrixsample.action.INTRO"
    val ACTION_PERMISSION: String = "kr.plea.mnc.matrixsample.action.PERMISSION"
    val ACTION_LOGIN: String = "kr.plea.mnc.matrixsample.action.LOGIN"
    val ACTION_WEBVIEW: String = "kr.plea.mnc.matrixsample.action.WEBVIEW"

    companion object {
        @Volatile private var INSTANCE: PMMACore? = null

        @JvmStatic fun getInstance(context: Context): PMMACore =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: PMMACore(context).also {
                    INSTANCE = it
                }
            }
    }

    fun onActivityCreated(activity: Activity) = Timber.tag(TAG).v("onActivityCreated $activity")

    fun onActivityStarted(activity: Activity) = Timber.tag(TAG).v("onActivityStarted $activity")

    fun onActivityStopped(activity: Activity) = Timber.tag(TAG).v("onActivityStopped $activity")

    fun onActivitySaveInstanceState(activity: Activity) = Timber.tag(TAG).v("onActivitySaveInstanceState $activity")

    fun onActivityResumed(activity: Activity) {
        Timber.tag(TAG).d("onActivityResumed $activity")

        /* TODO */
    }

    fun onActivityPaused(activity: Activity) {
        Timber.tag(TAG).d("onActivityPaused $activity")

        /* TODO */
    }

    fun onActivityDestroyed(activity: Activity) {
        Timber.tag(TAG).d("onActivityDestroyed $activity")

        /* TODO */
    }
}