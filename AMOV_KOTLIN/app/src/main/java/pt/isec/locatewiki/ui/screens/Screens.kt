package pt.isec.locatewiki.ui.screens

enum class Screens(val display: String, val showAppBar: Boolean) {
    MENU("Menu", false),
    CREDITS("Credits", true),

    LOGIN("Login", true),
    REGISTER("Register", true),

    MAIN("Main", true),
    MAP("Map", true),

    LOCATIONS("Locations", true),
    ADD_LOCATION("AddLocation", true),
    EDIT_LOCATION("EditLocation", true),

    LANDMARKS("Landmarks", true),
    ADD_LANDMARK("AddLandmark", true),
    EDIT_LANDMARK("EditLandmark", true),
    LANDMARK_DETAILS("LandmarkDetails", true),
    ADD_COMMENT("AddComment", true),

    CATEGORIES("Categories", true),
    ADD_CATEGORY("AddCategory", true),
    EDIT_CATEGORY("EditCategory", true);

    val route: String
        get() = this.toString()
}