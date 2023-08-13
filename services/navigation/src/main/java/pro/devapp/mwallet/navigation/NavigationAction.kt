package pro.devapp.mwallet.navigation

sealed class NavigationAction(val route: String) {

    object Wallet : NavigationAction("wallet")

    object Welcome : NavigationAction("welcome")
    object SendMoney : NavigationAction("send_money")
    object MyQr : NavigationAction("my_qr")
    object CreateAccount : NavigationAction("create_account")
    object SignIn : NavigationAction("sign_in")
    object Back : NavigationAction("back")

    object Pin : NavigationAction("pin")

    object QrScanner : NavigationAction("qr_scanner")
}