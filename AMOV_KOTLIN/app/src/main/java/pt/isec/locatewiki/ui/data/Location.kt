package pt.isec.locatewiki.ui.data

import com.google.firebase.firestore.GeoPoint

data class Location(
    var id: String? = null,
    val title: String,
    val geoPoint: GeoPoint,
    val otherInfo: String,
    val usersTrust: List<String>,
    val numLikes: Int,
    val numDislikes : Int,
    val ownerId: String
) {
    constructor() : this("", "", GeoPoint(0.0, 0.0), "", listOf(), 0, 0, "")
    constructor(title: String, geoPoint: GeoPoint, otherInfo: String, ownerId: String) : this(
        "",
        title,
        geoPoint,
        otherInfo,
        listOf(ownerId),
        0, 0,
        ownerId
    )
}