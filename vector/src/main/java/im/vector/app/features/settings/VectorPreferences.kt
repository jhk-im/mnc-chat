package im.vector.app.features.settings

import android.content.Context
import androidx.core.content.edit
import javax.inject.Inject

class VectorPreferences @Inject constructor(private val context: Context) {

    companion object {
        // Security
        const val SETTINGS_SECURITY_USE_FLAG_SECURE = "SETTINGS_SECURITY_USE_FLAG_SECURE"
        const val SETTINGS_SECURITY_USE_PIN_CODE_FLAG = "SETTINGS_SECURITY_USE_PIN_CODE_FLAG"
        private const val SETTINGS_SECURITY_USE_GRACE_PERIOD_FLAG = "SETTINGS_SECURITY_USE_GRACE_PERIOD_FLAG"

        private const val SETTINGS_DEVELOPER_MODE_PREFERENCE_KEY = "SETTINGS_DEVELOPER_MODE_PREFERENCE_KEY"
        const val SETTINGS_LABS_ALLOW_EXTENDED_LOGS = "SETTINGS_LABS_ALLOW_EXTENDED_LOGS"

        // other
        private const val DID_ASK_TO_ENABLE_SESSION_PUSH = "DID_ASK_TO_ENABLE_SESSION_PUSH"
    }

    private val defaultPrefs = DefaultSharedPreferences.getInstance(context)

    fun developerMode(): Boolean {
        return defaultPrefs.getBoolean(SETTINGS_DEVELOPER_MODE_PREFERENCE_KEY, false)
    }

    fun labAllowedExtendedLogging(): Boolean {
        return developerMode() && defaultPrefs.getBoolean(SETTINGS_LABS_ALLOW_EXTENDED_LOGS, false)
    }

    /**
     * The user enable protecting app access with pin code.
     * Currently we use the pin code store to know if the pin is enabled, so this is not used
     */
    fun useFlagPinCode(): Boolean {
        return defaultPrefs.getBoolean(SETTINGS_SECURITY_USE_PIN_CODE_FLAG, false)
    }

    /**
     * The user enable protecting app access with pin code.
     * Currently we use the pin code store to know if the pin is enabled, so this is not used
     */
    fun useGracePeriod(): Boolean {
        return defaultPrefs.getBoolean(SETTINGS_SECURITY_USE_GRACE_PERIOD_FLAG, true)
    }

    fun didAskUserToEnableSessionPush(): Boolean {
        return defaultPrefs.getBoolean(DID_ASK_TO_ENABLE_SESSION_PUSH, false)
    }

    fun setDidAskUserToEnableSessionPush() {
        defaultPrefs.edit {
            putBoolean(DID_ASK_TO_ENABLE_SESSION_PUSH, true)
        }
    }

    /**
     * The user does not allow screenshots of the application
     */
    fun useFlagSecure(): Boolean {
        return defaultPrefs.getBoolean(SETTINGS_SECURITY_USE_FLAG_SECURE, false)
    }
}