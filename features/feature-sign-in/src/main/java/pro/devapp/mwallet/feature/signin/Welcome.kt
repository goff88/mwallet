package pro.devapp.mwallet.feature.signin

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import pro.devapp.mwallet.navigation.NavigationAction

@Composable
fun Welcome(
    onNavigationAction: (action: NavigationAction) -> Unit
) {
    val viewModel: WelcomeViewModel = koinViewModel()
    val signInState = viewModel.navigationFlow.collectAsState(null)

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
        Text(text = "Sign in if you have already have account")
        Spacer(modifier = Modifier.padding(4.dp))
        OutlinedButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                onNavigationAction(NavigationAction.SignIn)
            }
        ) {
            Text(text = "Sign in")
        }
        Spacer(modifier = Modifier.padding(4.dp))
        Text(text = "Or create new account")
        Spacer(modifier = Modifier.padding(4.dp))
        OutlinedButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                onNavigationAction(NavigationAction.CreateAccount)
            }
        ) {
            Text(text = "Create")
        }
    }
}