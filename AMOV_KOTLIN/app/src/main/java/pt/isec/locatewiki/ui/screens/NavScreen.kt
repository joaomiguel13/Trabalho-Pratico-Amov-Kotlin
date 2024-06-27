package pt.isec.locatewiki.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import org.osmdroid.util.GeoPoint
import pt.isec.locatewiki.ui.data.AppDataManager
import pt.isec.locatewiki.ui.screens.category.AddCategoryScreen
import pt.isec.locatewiki.ui.screens.category.CategoriesScreen
import pt.isec.locatewiki.ui.screens.category.EditCategoryScreen
import pt.isec.locatewiki.ui.screens.landmark.AddCommentScreen
import pt.isec.locatewiki.ui.screens.landmark.AddLandmarkScreen
import pt.isec.locatewiki.ui.screens.landmark.EditLandmarkScreen
import pt.isec.locatewiki.ui.screens.landmark.LandmarkDetailsScreen
import pt.isec.locatewiki.ui.screens.landmark.LandmarksScreen
import pt.isec.locatewiki.ui.screens.location.AddLocationScreen
import pt.isec.locatewiki.ui.screens.location.EditLocationScreen
import pt.isec.locatewiki.ui.screens.location.LocationsScreen
import pt.isec.locatewiki.utils.firebase.FirebaseHelper

@Composable
fun NavScreen(navController: NavHostController = rememberNavController()) {
    val user = FirebaseHelper.getCurrentUser()

    var showNextAction by remember { mutableStateOf(false) }

    navController.addOnDestinationChangedListener { _, destination, _ ->
        showNextAction = (destination.route in
                arrayOf(Screens.LOGIN.route, Screens.REGISTER.route, Screens.CREDITS.route))
    }
    Scaffold {
        NavHost(
            navController = navController,
            startDestination = user?.let { Screens.MAIN.route } ?: Screens.MENU.route,
            modifier = Modifier.padding(it)
        ) {
            composable(Screens.MENU.route) {
                MenuScreen(navController)
            }
            composable(Screens.LOGIN.route) {
                LoginScreen(navController)
            }
            composable(Screens.REGISTER.route) {
                RegisterScreen(navController)
            }
            composable(Screens.MAIN.route) {
                MainScreen(navController)
            }
            composable(Screens.CREDITS.route) {
                CreditsScreen(navController)
            }
            composable(
                Screens.MAP.route + "?locationId={locationId}&landmarkId={landmarkId}",
                arguments = listOf(
                    navArgument("locationId") {
                        type = NavType.StringType
                        defaultValue = ""
                    },
                    navArgument("landmarkId") {
                        type = NavType.StringType
                        defaultValue = ""
                    }
                )
            ) { backStackEntry ->
                val locationId = backStackEntry.arguments?.getString("locationId") ?: ""
                val landmarkId = backStackEntry.arguments?.getString("landmarkId") ?: ""
                if (locationId.isEmpty() && landmarkId.isEmpty()) {
                    navController.popBackStack()
                } else if (locationId.isNotEmpty() && landmarkId.isNotEmpty()) {
                    navController.popBackStack()
                } else if (locationId.isNotEmpty()) {
                    val location = AppDataManager.getLocationById(locationId)
                    if (location != null) {
                        val geoPoint =
                            GeoPoint(location.geoPoint.latitude, location.geoPoint.longitude)
                        MapScreen(navController, location.title, location.otherInfo, geoPoint)
                    }
                } else if (landmarkId.isNotEmpty()) {
                    val landmark = AppDataManager.getLandmarkById(landmarkId)
                    if (landmark != null) {
                        val geoPoint =
                            GeoPoint(landmark.geoPoint.latitude, landmark.geoPoint.longitude)
                        MapScreen(navController, landmark.title, landmark.otherInfo, geoPoint)
                    }
                }
            }
            composable(Screens.LOCATIONS.route) {
                LocationsScreen(navController)
            }
            composable(Screens.ADD_LOCATION.route) {
                AddLocationScreen(navController)
            }
            composable(Screens.EDIT_LOCATION.route + "?locationId={locationId}",
                arguments = listOf(
                    navArgument("locationId") {
                        type = NavType.StringType
                        defaultValue = ""
                    }
                )
            ) { backStackEntry ->
                val locationId = backStackEntry.arguments?.getString("locationId")
                if (locationId != null) {
                    EditLocationScreen(navController, locationId)
                }
            }
            composable(Screens.CATEGORIES.route) {
                CategoriesScreen(navController)
            }
            composable(Screens.ADD_CATEGORY.route) {
                AddCategoryScreen(navController)
            }
            composable(
                Screens.LANDMARKS.route + "?locationId={locationId}",
                arguments = listOf(
                    navArgument("locationId") {
                        type = NavType.StringType
                        defaultValue = ""
                    }
                )
            ) { backStackEntry ->
                val locationId = backStackEntry.arguments?.getString("locationId") ?: ""
                val location = AppDataManager.getLocationById(locationId)
                if (location != null) {
                    LandmarksScreen(navController, location)
                }
            }
            composable(Screens.ADD_LANDMARK.route + "?locationId={locationId}",
                arguments = listOf(
                    navArgument("locationId") {
                        type = NavType.StringType
                        defaultValue = ""
                    }
                )
            ) { backStackEntry ->
                val locationId = backStackEntry.arguments?.getString("locationId")
                if (locationId != null) {
                    AddLandmarkScreen(navController, locationId)
                }
            }
            composable(Screens.EDIT_LANDMARK.route + "?landmarkId={landmarkId}",
                arguments = listOf(
                    navArgument("landmarkId") {
                        type = NavType.StringType
                        defaultValue = ""
                    }
                )
            ) { backStackEntry ->
                val landmarkId = backStackEntry.arguments?.getString("landmarkId")
                if (landmarkId != null) {
                    EditLandmarkScreen(navController, landmarkId)
                }
            }
            composable(Screens.LANDMARK_DETAILS.route + "?landmarkId={landmarkId}",
                arguments = listOf(
                    navArgument("landmarkId") {
                        type = NavType.StringType
                        defaultValue = ""
                    }
                )
            ) { backStackEntry ->
                val landmarkId = backStackEntry.arguments?.getString("landmarkId")
                if (landmarkId != null) {
                    LandmarkDetailsScreen(navController, landmarkId)
                }
            }
            composable(Screens.EDIT_CATEGORY.route + "?categoryId={categoryId}",
                arguments = listOf(
                    navArgument("categoryId") {
                        type = NavType.StringType
                        defaultValue = ""
                    }
                )
            ) { backStackEntry ->
                val categoryId = backStackEntry.arguments?.getString("categoryId")
                if (categoryId != null) {
                    EditCategoryScreen(navController, categoryId)
                }
            }
            composable(Screens.ADD_COMMENT.route + "?landmarkId={landmarkId}",
                arguments = listOf(
                    navArgument("landmarkId") {
                        type = NavType.StringType
                        defaultValue = ""
                    }
                )
            ) { backStackEntry ->
                val landmarkId = backStackEntry.arguments?.getString("landmarkId")
                if (landmarkId != null) {
                    AddCommentScreen(navController, landmarkId)
                }
            }
        }
    }
}