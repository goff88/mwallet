package pro.devapp.mwallet.feature.account

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
fun Wallet(
    onNavigationAction: (action: NavigationAction) -> Unit
) {

    val viewModel: WalletViewModel = koinViewModel()
    val screenState = viewModel.screenState.collectAsState()

    LaunchedEffect(true) {
        viewModel.onInit()
    }

    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = screenState.value.account)
        Spacer(modifier = Modifier.padding(4.dp))
        Text(
            text = screenState.value.balance.toString() + " PZM",
            style = MaterialTheme.typography.h3
        )
        if (screenState.value.balanceRUB > 0) {
            Text(text = "(" + screenState.value.balanceRUB.toString() + " RUB)")
        }
        Spacer(modifier = Modifier.padding(4.dp))
        OutlinedButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                onNavigationAction(NavigationAction.SendMoney)
            }
        ) {
            Text(text = "Send")
        }
        Spacer(modifier = Modifier.padding(4.dp))
        OutlinedButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                onNavigationAction(NavigationAction.MyQr)
            }
        ) {
            Text(text = "My QR")
        }
    }
}