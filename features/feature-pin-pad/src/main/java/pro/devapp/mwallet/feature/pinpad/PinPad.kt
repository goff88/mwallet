package pro.devapp.mwallet.feature.pinpad

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
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
fun PinPad(
    onNavigationAction: (action: NavigationAction) -> Unit
) {

    val viewModel: PinPadViewModel = koinViewModel()
    val screenState = viewModel.screenState.collectAsState()

    LaunchedEffect(true) {
        viewModel.init()
    }

    val signInState = viewModel.navigationFlow.collectAsState(null)

    LaunchedEffect(signInState.value) {
        signInState.value?.let {
            onNavigationAction(it)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = screenState.value.title,
            style = MaterialTheme.typography.h6
        )
        if (screenState.value.error?.isNullOrBlank() == false) {
            Text(
                modifier = Modifier.padding(horizontal = 16.dp),
                text = screenState.value.error.orEmpty(),
                color = MaterialTheme.colors.error
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            for (i in 0 until screenState.value.pin.length) {
                PinItem(isEntered = true)
            }
            for (i in screenState.value.pin.length..7) {
                PinItem(isEntered = false)
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            for (i in 1..3) {
                PinPadButton(text = i.toString()) {
                    viewModel.onPinChanged(i.toString())
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            for (i in 4..6) {
                PinPadButton(text = i.toString()) {
                    viewModel.onPinChanged(i.toString())
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            for (i in 7..9) {
                PinPadButton(text = i.toString()) {
                    viewModel.onPinChanged(i.toString())
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp)
            )
            PinPadButton(text = "0") {
                viewModel.onPinChanged("0")
            }
            PinPadButton(text = "Del") {
                viewModel.onPinChanged("Del")
            }
        }
    }
}

@Composable
private fun RowScope.PinPadButton(
    text: String,
    onClick: (String) -> Unit
) {
    OutlinedButton(
        modifier = Modifier
            .weight(1f)
            .padding(horizontal = 4.dp),
        onClick = {
            onClick(text)
        }
    ) {
        Text(text = text)
    }
}

@Composable
fun PinItem(
    isEntered: Boolean = false
) {
    Text(
        text = " ● ",
        style = MaterialTheme.typography.h6.copy(
            color = if (isEntered) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface
        )
    )
}