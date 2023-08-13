package pro.devapp.mwallet.feature.sendmoney

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
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import pro.devapp.mwallet.api.qrscanner.QrScannerIntentFactory
import pro.devapp.mwallet.api.qrscanner.QrScannerLauncherFactory
import pro.devapp.mwallet.navigation.NavigationAction

@Composable
fun SendMoney(
    onNavigationAction: (action: NavigationAction) -> Unit
) {

    val qrScannerIntentFactory: QrScannerIntentFactory = koinInject()

    val viewModel: SendMoneyViewModel = koinViewModel()
    val screenState = viewModel.screenState.collectAsState()
    val signInState = viewModel.navigationFlow.collectAsState(null)

    val focusManager = LocalFocusManager.current

    val startForResult = QrScannerLauncherFactory { result: String ->
        viewModel.handleQrResult(result)
    }
    val intent = qrScannerIntentFactory.createIntent(LocalContext.current)

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
        Spacer(modifier = Modifier.padding(4.dp))
        OutlinedButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                startForResult.launch(intent)
            }
        ) {
            Text(text = "Scan QR")
        }
        Spacer(modifier = Modifier.padding(4.dp))
        Text(text = "Account")
        Spacer(modifier = Modifier.padding(4.dp))
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = screenState.value.recipientAccount,
            onValueChange = {
                viewModel.onRecipientAccountChanged(it)
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                autoCorrect = true,
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.moveFocus(FocusDirection.Down)
                }
            )
        )
        Spacer(modifier = Modifier.padding(4.dp))
        if (screenState.value.needPublicKey) {
            Text(text = "Public key")
            Spacer(modifier = Modifier.padding(4.dp))
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = screenState.value.publicKey,
                onValueChange = {},
                keyboardOptions = KeyboardOptions.Default.copy(
                    autoCorrect = true,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.moveFocus(FocusDirection.Down)
                    }
                )
            )
            Spacer(modifier = Modifier.padding(4.dp))
        }
        Text(text = "Amount")
        Spacer(modifier = Modifier.padding(4.dp))
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = screenState.value.amount,
            onValueChange = {
                viewModel.onValueChanged(it)
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                autoCorrect = true,
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                    viewModel.sendMoney()
                }
            )
        )
        Spacer(modifier = Modifier.padding(4.dp))
        screenState.value.recipientAccountBalance?.let {
            Text(text = "Recipient balance: " + screenState.value.recipientAccountBalance.toString() + " PZM")
            Spacer(modifier = Modifier.padding(4.dp))
        }
        OutlinedButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                viewModel.sendMoney()
            }
        ) {
            Text(text = "Send")
        }
    }
}