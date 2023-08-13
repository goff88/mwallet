package pro.devapp.mwallet.navigation

import androidx.navigation.NavHostController

class NavigationHandler(
    private val controller: NavHostController
) {

    fun navigate(action: NavigationAction) {
        if (action == NavigationAction.Back) {
            controller.popBackStack()
        } else {
            controller.navigate(action.route)
        }
    }
}