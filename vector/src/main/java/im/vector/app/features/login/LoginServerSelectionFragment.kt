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

package im.vector.app.features.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import im.vector.app.R
import im.vector.app.core.utils.openUrlInChromeCustomTab
import im.vector.app.databinding.FragmentLoginServerSelectionBinding
import me.gujun.android.span.span
import javax.inject.Inject

/**
 * In this screen, the user will choose between matrix.org, modular or other type of homeserver
 */
class LoginServerSelectionFragment @Inject constructor() : AbstractLoginFragment<FragmentLoginServerSelectionBinding>() {

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentLoginServerSelectionBinding {
        return FragmentLoginServerSelectionBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        initTextViews()
    }

    private fun initViews() {
        views.loginServerChoiceMatrixOrg.setOnClickListener { selectMatrixOrg() }
        views.loginServerChoiceMNC.setOnClickListener { selectMNCServer() }
        views.loginServerIKnowMyIdSubmit.setOnClickListener { loginWithMatrixId() }
    }

    private fun updateSelectedChoice(state: LoginViewState) {
        views.loginServerChoiceMatrixOrg.isChecked = state.serverType == ServerType.MatrixOrg
    }

    private fun initTextViews() {
        //
    }

    private fun learnMore() {
        //openUrlInChromeCustomTab(requireActivity(), null, EMS_LINK)
    }

    private fun selectMatrixOrg() {
        loginViewModel.handle(LoginAction.UpdateServerType(ServerType.MatrixOrg))
    }

    private fun selectMNCServer() {
        loginViewModel.handle(LoginAction.UpdateServerType(ServerType.MNCServer))
    }

    private fun loginWithMatrixId() {
        loginViewModel.handle(LoginAction.UpdateSignMode(SignMode.SignInWithMatrixId))
    }

    override fun resetViewModel() {
        loginViewModel.handle(LoginAction.ResetHomeServerType)
    }

    override fun updateWithState(state: LoginViewState) {
        updateSelectedChoice(state)

        if (state.loginMode != LoginMode.Unknown) {
            // LoginFlow for matrix.org has been retrieved
            loginViewModel.handle(LoginAction.PostViewEvent(LoginViewEvents.OnLoginFlowRetrieved))
        }
    }
}
