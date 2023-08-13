package pro.devapp.mwallet.screen.create

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
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
fun CreateAccount(
    onNavigationAction: (action: NavigationAction) -> Unit
) {
    val viewModel: CreateAccountViewModel = koinViewModel()
    val screenState = viewModel.screenState.collectAsState()

    LaunchedEffect(true) {
        viewModel.onInit()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Save this pass phrase in safe place. Tap on text to copy it.")
        Spacer(modifier = Modifier.padding(4.dp))
        Text(
            modifier = Modifier.clickable {
                viewModel.onCopyClicked()
            },
            text = screenState.value.passPhrase,
            style = MaterialTheme.typography.subtitle2
        )
        Spacer(modifier = Modifier.padding(4.dp))
        OutlinedButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                onNavigationAction(NavigationAction.Wallet)
            }
        ) {
            Text(text = "Ok, I saved it")
        }
    }
}