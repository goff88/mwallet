package pro.devapp.mwallet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import pro.devapp.mwallet.feature.account.Wallet
import pro.devapp.mwallet.feature.createaccount.CreateAccount
import pro.devapp.mwallet.feature.myqr.MyQr
import pro.devapp.mwallet.feature.pinpad.PinPad
import pro.devapp.mwallet.navigation.NavigationAction
import pro.devapp.mwallet.navigation.NavigationHandler
import pro.devapp.mwallet.screen.sendmoney.SendMoney
import pro.devapp.mwallet.screen.signin.SignIn
import pro.devapp.mwallet.screen.welcome.Welcome
import pro.devapp.mwallet.uikit.MWalletTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val navigation = NavigationHandler(navController)
            MWalletTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = NavigationAction.Pin.route
                    ) {
                        composable(NavigationAction.Pin.route) {
                            PinPad(
                                onNavigationAction = navigation::navigate
                            )
                        }
                        composable(NavigationAction.Welcome.route) {
                            Welcome(
                                onNavigationAction = navigation::navigate
                            )
                        }
                        composable(NavigationAction.CreateAccount.route) {
                            CreateAccount(
                                onNavigationAction = navigation::navigate
                            )
                        }
                        composable(NavigationAction.SignIn.route) {
                            SignIn(
                                onNavigationAction = navigation::navigate
                            )
                        }
                        composable(NavigationAction.Wallet.route) {
                            Wallet(
                                onNavigationAction = navigation::navigate
                            )
                        }
                        composable(NavigationAction.MyQr.route) {
                            MyQr(
                                onNavigationAction = navigation::navigate
                            )
                        }
                        composable(NavigationAction.SendMoney.route) {
                            SendMoney(
                                onNavigationAction = navigation::navigate
                            )
                        }
                    }
                }
            }
        }
    }
}