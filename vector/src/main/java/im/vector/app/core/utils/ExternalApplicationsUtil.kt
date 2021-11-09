/*
 * Copyright 2019 New Vector Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package im.vector.app.core.utils

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.browser.customtabs.CustomTabsSession
import im.vector.app.R
import im.vector.app.core.utils.toast
import im.vector.app.features.themes.ThemeUtils

/**
 * Ask the user to select a location and a file name to write in
 */
fun selectTxtFileToWrite(
        activity: Activity,
        activityResultLauncher: ActivityResultLauncher<Intent>,
        defaultFileName: String,
        chooserHint: String
) {
    val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
    intent.addCategory(Intent.CATEGORY_OPENABLE)
    intent.type = "text/plain"
    intent.putExtra(Intent.EXTRA_TITLE, defaultFileName)

    try {
        val chooserIntent = Intent.createChooser(intent, chooserHint)
        activityResultLauncher.launch(chooserIntent)
    } catch (activityNotFoundException: ActivityNotFoundException) {
        activity.toast(R.string.error_no_external_application_found)
    }
}

/**
 * Open url in custom tab or, if not available, in the default browser
 * If several compatible browsers are installed, the user will be proposed to choose one.
 * Ref: https://developer.chrome.com/multidevice/android/customtabs
 */
fun openUrlInChromeCustomTab(context: Context,
                             session: CustomTabsSession?,
                             url: String) {
    try {
        CustomTabsIntent.Builder()
            .setDefaultColorSchemeParams(
                CustomTabColorSchemeParams.Builder()
                    .setToolbarColor(ThemeUtils.getColor(context, android.R.attr.colorBackground))
                    .setNavigationBarColor(ThemeUtils.getColor(context, android.R.attr.colorBackground))
                    .build()
            )
            .setColorScheme(
                when {
                    ThemeUtils.isSystemTheme(context) -> CustomTabsIntent.COLOR_SCHEME_SYSTEM
                    ThemeUtils.isLightTheme(context)  -> CustomTabsIntent.COLOR_SCHEME_LIGHT
                    else                              -> CustomTabsIntent.COLOR_SCHEME_DARK
                }
            )
            // Note: setting close button icon does not work
            .setCloseButtonIcon(BitmapFactory.decodeResource(context.resources, R.drawable.ic_back_24dp))
            .setStartAnimations(context, R.anim.enter_fade_in, R.anim.exit_fade_out)
            .setExitAnimations(context, R.anim.enter_fade_in, R.anim.exit_fade_out)
            .apply { session?.let { setSession(it) } }
            .build()
            .launchUrl(context, Uri.parse(url))
    } catch (activityNotFoundException: ActivityNotFoundException) {
        context.toast(R.string.error_no_external_application_found)
    }
}