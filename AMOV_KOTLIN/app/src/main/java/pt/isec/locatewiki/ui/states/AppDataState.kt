package pt.isec.locatewiki.ui.states

import pt.isec.locatewiki.ui.data.Category
import pt.isec.locatewiki.ui.data.Landmark
import pt.isec.locatewiki.ui.data.Location

data class AppDataState(
    val dbLocations: MutableSet<Location> = mutableSetOf(),
    val dbLandmarks: MutableSet<Landmark> = mutableSetOf(),
    val dbCategories: MutableSet<Category> = mutableSetOf(),
    var error: Throwable? = null,
    var currentLocation: android.location.Location = android.location.Location(null),
)
