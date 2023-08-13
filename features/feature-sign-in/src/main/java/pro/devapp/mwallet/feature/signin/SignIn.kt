package pro.devapp.mwallet.feature.signin

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import pro.devapp.mwallet.navigation.NavigationAction

@Composable
fun SignIn(
    onNavigationAction: (action: NavigationAction) -> Unit
) {

    val viewModel: SignInViewModel = koinViewModel()
    val password = viewModel.password.collectAsState()
    val signInState = viewModel.navigationFlow.collectAsState(null)
    val focusManager = LocalFocusManager.current

    LaunchedEffect(true) {
        viewModel.init()
    }

    LaunchedEffect(signInState.value) {
        signInState.value?.let {
            onNavigationAction(it)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Enter your password")
        Spacer(modifier = Modifier.padding(4.dp))
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = password.value,
            onValueChange = viewModel::onPasswordChanged,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions.Default.copy(
                autoCorrect = true,
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                    viewModel.onSignInClicked()
                }
            ),
        )
        Spacer(modifier = Modifier.padding(4.dp))
        OutlinedButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = viewModel::onSignInClicked,
            enabled = password.value.isNotEmpty()
        ) {
            Text(text = "Sign In")
        }
    }
}