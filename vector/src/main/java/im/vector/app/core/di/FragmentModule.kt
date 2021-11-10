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

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import im.vector.app.features.login.LoginSignUpSignInSelectionFragment
import im.vector.app.features.login.LoginSplashFragment

@Module
interface FragmentModule {

    /**
     * Fragments with @IntoMap will be injected by this factory
     */
    @Binds
    fun bindFragmentFactory(factory: VectorFragmentFactory): FragmentFactory

    @Binds
    @IntoMap
    @FragmentKey(LoginSplashFragment::class)
    fun bindLoginSplashFragment(fragment: LoginSplashFragment): Fragment

    @Binds
    @IntoMap
    @FragmentKey(LoginSignUpSignInSelectionFragment::class)
    fun bindLoginSignUpSignInSelectionFragment(fragment: LoginSignUpSignInSelectionFragment): Fragment
}