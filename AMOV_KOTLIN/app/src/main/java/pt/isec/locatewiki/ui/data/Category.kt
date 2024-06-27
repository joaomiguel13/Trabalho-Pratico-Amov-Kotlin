package pt.isec.locatewiki.ui.data

import android.os.Parcel
import android.os.Parcelable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.twotone.Place
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import pt.isec.locatewiki.R

enum class CategoryIcon(val icon: ImageVector) : Parcelable {
    MUSEUM(Icons.Default.Place),
    MONUMENT(Icons.TwoTone.Place),
    GARDEN(Icons.Default.Star),
    VIEWPOINT(Icons.Default.AccountCircle),
    RESTAURANT(Icons.Default.ShoppingCart),
    ACCOMMODATION(Icons.Default.Home),
    OTHER(Icons.Outlined.Place);

    @Composable
    fun getPresentationName(): String {
        return when (this) {
            MUSEUM -> stringResource(R.string.museum)
            MONUMENT -> stringResource(R.string.monument)
            GARDEN -> stringResource(R.string.garden)
            VIEWPOINT -> stringResource(R.string.viewpoint)
            RESTAURANT -> stringResource(R.string.restaurant)
            ACCOMMODATION -> stringResource(R.string.accommodation)
            else -> stringResource(R.string.other)
        }
    }

    override fun toString(): String {
        return when (this) {
            MUSEUM -> "MUSEUM"
            MONUMENT -> "MONUMENT"
            GARDEN -> "GARDEN"
            VIEWPOINT -> "VIEWPOINT"
            RESTAURANT -> "RESTAURANT"
            ACCOMMODATION -> "ACCOMMODATION"
            else -> "OTHER"
        }
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(toString())
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CategoryIcon> {
        override fun createFromParcel(parcel: Parcel): CategoryIcon {
            return when (parcel.readString()) {
                "MUSEUM" -> MUSEUM
                "MONUMENT" -> MONUMENT
                "GARDEN" -> GARDEN
                "VIEWPOINT" -> VIEWPOINT
                "RESTAURANT" -> RESTAURANT
                "ACCOMMODATION" -> ACCOMMODATION
                else -> OTHER
            }
        }

        override fun newArray(size: Int): Array<CategoryIcon?> {
            return arrayOfNulls(size)
        }
    }
}

data class Category(
    var id: String? = null,
    val name: String,
    val icon: CategoryIcon,
    val usersTrust: List<String>,
    val ownerId: String
) {
    constructor() : this("", "", CategoryIcon.OTHER, listOf(), "")
    constructor(name: String, icon: CategoryIcon, ownerId: String) : this(
        "",
        name,
        icon,
        listOf(ownerId),
        ownerId
    )
}
