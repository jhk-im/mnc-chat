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

package im.vector.app.features.navigation

import android.content.Context
import android.content.Intent
import androidx.core.app.TaskStackBuilder
import im.vector.app.AppStateHandler
import im.vector.app.core.di.ActiveSessionHolder
import im.vector.app.features.login.LoginActivity
import im.vector.app.features.login.LoginConfig
import im.vector.app.features.settings.VectorPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultNavigator @Inject constructor(
        private val sessionHolder: ActiveSessionHolder,
        private val vectorPreferences: VectorPreferences,
        private val appStateHandler: AppStateHandler
) : Navigator {

    override fun openLogin(context: Context, loginConfig: LoginConfig?, flags: Int) {
        /*val intent = if (context.resources.getBoolean(R.bool.useLoginV2)) {
            LoginActivity2.newIntent(context, loginConfig)
        } else {
            LoginActivity.newIntent(context, loginConfig)
        }*/
        val intent = LoginActivity.newIntent(context, loginConfig)
        intent.addFlags(flags)
        context.startActivity(intent)
    }

    private fun startActivity(context: Context, intent: Intent, buildTask: Boolean) {
        if (buildTask) {
            val stackBuilder = TaskStackBuilder.create(context)
            stackBuilder.addNextIntentWithParentStack(intent)
            stackBuilder.startActivities()
        } else {
            context.startActivity(intent)
        }
    }
}
