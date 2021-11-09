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
 *
 */

package im.vector.app.core.di

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.ViewModelProvider
import dagger.BindsInstance
import dagger.Component
import im.vector.app.core.dialogs.UnrecognizedCertificateDialog
import im.vector.app.core.error.ErrorFormatter
import im.vector.app.features.MainActivity
import im.vector.app.features.home.HomeActivity
import im.vector.app.features.login.LoginActivity
import im.vector.app.features.navigation.Navigator
import kotlinx.coroutines.CoroutineScope

@Component(
    dependencies = [
        VectorComponent::class
    ],
    modules = [
        ViewModelModule::class,
        FragmentModule::class,
        ScreenModule::class
    ]
)
@ScreenScope
interface ScreenComponent {

    /* ==========================================================================================
     * Shortcut to VectorComponent elements
     * ========================================================================================== */
    fun fragmentFactory(): FragmentFactory
    fun viewModelFactory(): ViewModelProvider.Factory
    fun activeSessionHolder(): ActiveSessionHolder
    fun navigator(): Navigator
    fun errorFormatter(): ErrorFormatter
    fun unrecognizedCertificateDialog(): UnrecognizedCertificateDialog
    //fun autoAcceptInvites(): AutoAcceptInvites
    //fun appCoroutineScope(): CoroutineScope

    /* ==========================================================================================
     * Activities
     * ========================================================================================== */
    fun inject(activity: HomeActivity)
    fun inject(activity: MainActivity)
    fun inject(activity: LoginActivity)

    /* ==========================================================================================
     * Others
     * ========================================================================================== */
    //

    /* ==========================================================================================
     * Factory
     * ========================================================================================== */
    @Component.Factory
    interface Factory {
        fun create(vectorComponent: VectorComponent,
                   @BindsInstance context: AppCompatActivity
        ): ScreenComponent
    }
}