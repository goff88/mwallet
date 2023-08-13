package pro.devapp.mwallet.screen.myqr

import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import pro.devapp.mwallet.navigation.NavigationAction

@Composable
fun MyQr(
    onNavigationAction: (action: NavigationAction) -> Unit
) {

    val viewModel: MyQrViewModel = koinViewModel()
    val screenState = viewModel.screenState.collectAsState()

    LaunchedEffect(true) {
        viewModel.init()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Sign in if you have already have account")
        Spacer(modifier = Modifier.padding(4.dp))
        screenState.value.img?.let {
            Image(bitmap = it.asImageBitmap(), contentDescription = "")
        }
        Spacer(modifier = Modifier.padding(4.dp))
        OutlinedButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                onNavigationAction(NavigationAction.Back)
            }
        ) {
            Text(text = "Close")
        }
    }
}