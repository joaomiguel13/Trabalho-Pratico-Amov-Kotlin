package pt.isec.locatewiki.ui.screens.location

import android.view.ViewGroup
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import pt.isec.locatewiki.R
import pt.isec.locatewiki.ui.data.AppDataManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddLocationScreen(
    navController: NavHostController
) {
    val mainState by AppDataManager.state.collectAsState()

    var title by remember { mutableStateOf("") }
    var otherInfo by remember { mutableStateOf("") }

    TopAppBar(
        title = { Text(stringResource(R.string.add_location)) },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
        },
        modifier = Modifier.fillMaxWidth()
    )

    LazyColumn(
        Modifier
            .fillMaxSize()
            .padding(top = 50.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        item {
            Spacer(modifier = Modifier.size(16.dp))
        }

        item {
            TextField(
                value = title,
                onValueChange = { title = it },
                label = { Text(stringResource(R.string.location_title)) },
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        item {
            TextField(
                value = otherInfo,
                onValueChange = { otherInfo = it },
                label = { Text(stringResource(id = R.string.location_description)) },
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        val geoPoint = GeoPoint(mainState.currentLocation.latitude, mainState.currentLocation.longitude)

        item {
            AndroidView(
                factory = { context ->
                    val mapView = MapView(context).apply {
                        setTileSource(TileSourceFactory.MAPNIK)
                        setMultiTouchControls(true)
                        controller.setZoom(17.0)
                        controller.setCenter(geoPoint)

                        clipToOutline = true
                        layoutMode = MapView.LAYOUT_MODE_CLIP_BOUNDS
                    }

                    val marker = Marker(mapView).apply {
                        this.position = geoPoint
                        this.title = title
                        this.snippet = otherInfo
                    }
                    mapView.overlays.add(marker)

                    mapView
                },
                modifier = Modifier
                    .aspectRatio(1f)
                    .padding(25.dp)
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.primary,
                        shape = MaterialTheme.shapes.small
                    )
            )
        }

        item {
            Button(
                onClick = {
                    if (title.isNotBlank() && otherInfo.isNotBlank()) {
                        val newGeoPoint = com.google.firebase.firestore.GeoPoint(
                            mainState.currentLocation.latitude,
                            mainState.currentLocation.longitude
                        )
                        AppDataManager.createLocation(title, newGeoPoint, otherInfo)
                        navController.popBackStack()
                    }
                },
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Text(stringResource(R.string.add_location))
            }
        }
    }
}
