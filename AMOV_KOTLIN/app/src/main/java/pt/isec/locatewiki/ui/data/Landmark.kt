package pt.isec.locatewiki.ui.data

import com.google.firebase.firestore.GeoPoint

data class Landmark(
    var id: String? = null,
    val locationId: String,
    val categoryId: String,
    val title: String,
    val geoPoint: GeoPoint,
    val otherInfo: String,
    val beingDeleted: Boolean,
    val usersTrust: List<String>,
    val usersClassification: Map<String, Int>,   // 0 - 3
    val usersComment: Map<String, String>,     // comment
    val numLikes: Int,
    val numDislikes : Int,
    val ownerId: String
) {
    constructor() : this("", "", "", "", GeoPoint(0.0, 0.0), "", false, listOf(), mapOf(), mapOf(), 0, 0, "")
    constructor(
        locationId: String,
        categoryId: String,
        title: String,
        geoPoint: GeoPoint,
        otherInfo: String,
        ownerId: String
    ) : this(
        "",
        locationId,
        categoryId,
        title,
        geoPoint,
        otherInfo,
        false,
        listOf(ownerId),
        mapOf(),
        mapOf(),
        0, 0,
        ownerId
    )
}