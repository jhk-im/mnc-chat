/*
 * Copyright 2019 New Vector Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package im.vector.app.features

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import im.vector.app.core.di.ScreenComponent
import im.vector.app.core.platform.VectorBaseActivity
import im.vector.app.databinding.ActivityMainBinding
import im.vector.app.features.pin.UnlockedActivity
import im.vector.app.features.themes.ActivityOtherThemes
import im.vector.app.features.ui.UiStateRepository
import kotlinx.parcelize.Parcelize
import timber.log.Timber
import javax.inject.Inject

@Parcelize
data class MainActivityArgs(
    val clearCache: Boolean = false,
    val clearCredentials: Boolean = false,
    val isUserLoggedOut: Boolean = false,
    val isAccountDeactivated: Boolean = false,
    val isSoftLogout: Boolean = false
) : Parcelable

/**
 * This is the entry point of Element Android
 * This Activity, when started with argument, is also doing some cleanup when user signs out,
 * clears cache, is logged out, or is soft logged out
 */
class MainActivity : VectorBaseActivity<ActivityMainBinding>(), UnlockedActivity {

    companion object {
        private const val EXTRA_ARGS = "EXTRA_ARGS"

        // Special action to clear cache and/or clear credentials
        fun restartApp(activity: Activity, args: MainActivityArgs) {
            val intent = Intent(activity, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)

            intent.putExtra(EXTRA_ARGS, args)
            activity.startActivity(intent)
        }
    }

    override fun injectWith(injector: ScreenComponent) {
        injector.inject(this)
    }
    override fun getBinding() = ActivityMainBinding.inflate(layoutInflater)

    override fun getOtherThemes() = ActivityOtherThemes.Launcher

    private lateinit var args: MainActivityArgs

    //@Inject
    //lateinit var uiStateRepository: UiStateRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        args = parseArgs()
        if (args.clearCredentials || args.isUserLoggedOut || args.clearCache) {
            //clearNotifications()
        }
        // Handle some wanted cleanup
        if (args.clearCache || args.clearCredentials) {
            //doCleanUp()
        } else {
            //startNextActivityAndFinish()
        }
    }

    private fun parseArgs(): MainActivityArgs {
        val argsFromIntent: MainActivityArgs? = intent.getParcelableExtra(EXTRA_ARGS)
        Timber.w("Starting MainActivity with $argsFromIntent")

        return MainActivityArgs(
            clearCache = argsFromIntent?.clearCache ?: false,
            clearCredentials = argsFromIntent?.clearCredentials ?: false,
            isUserLoggedOut = argsFromIntent?.isUserLoggedOut ?: false,
            isAccountDeactivated = argsFromIntent?.isAccountDeactivated ?: false,
            isSoftLogout = argsFromIntent?.isSoftLogout ?: false
        )
    }
}