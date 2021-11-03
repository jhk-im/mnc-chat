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
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import im.vector.app.R
import im.vector.app.core.utils.toast

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