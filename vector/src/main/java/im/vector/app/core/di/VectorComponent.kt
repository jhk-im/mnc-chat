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

package im.vector.app.core.di

import android.content.Context
import android.content.res.Resources
import dagger.BindsInstance
import dagger.Component
import im.vector.app.*
import im.vector.app.core.dialogs.UnrecognizedCertificateDialog
import im.vector.app.core.error.ErrorFormatter
import im.vector.app.features.navigation.Navigator
import im.vector.app.features.popup.PopupAlertManager
import im.vector.app.features.regeshake.VectorFileLogger
import im.vector.app.features.session.SessionListener
import im.vector.app.features.settings.VectorPreferences
import im.vector.app.features.ui.UiStateRepository
import org.matrix.android.sdk.api.auth.AuthenticationService
import org.matrix.android.sdk.api.auth.HomeServerHistoryService
import org.matrix.android.sdk.api.session.Session
import javax.inject.Singleton

@Component(modules = [VectorModule::class])
@Singleton
interface VectorComponent {

    fun inject(vectorApplication: VectorApplication)

    fun sessionListener(): SessionListener

    fun currentSession(): Session

    fun appContext(): Context

    fun resources(): Resources

    fun authenticationService(): AuthenticationService

    fun homeServerHistoryService(): HomeServerHistoryService

    fun vectorFileLogger(): VectorFileLogger

    fun vectorPreferences(): VectorPreferences

    fun uiStateRepository(): UiStateRepository

    fun activeSessionHolder(): ActiveSessionHolder

    fun navigator(): Navigator

    fun errorFormatter(): ErrorFormatter

    fun appStateHandler(): AppStateHandler

    fun activeSessionObservableStore(): ActiveSessionDataSource

    fun alertManager(): PopupAlertManager

    fun unrecognizedCertificateDialog(): UnrecognizedCertificateDialog

    fun emojiCompatFontProvider(): EmojiCompatFontProvider

    fun emojiCompatWrapper(): EmojiCompatWrapper

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): VectorComponent
    }
}
