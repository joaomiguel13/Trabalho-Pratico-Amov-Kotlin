package pt.isec.locatewiki.ui.screens.landmark

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
fun EditLandmarkScreen(
    navController: NavHostController, landmarkId: String
) {
    val mainState by AppDataManager.state.collectAsState()
    val foundLandmark = mainState.dbLandmarks.find { it.id == landmarkId }
    val foundCategory = mainState.dbCategories.find { it.id == foundLandmark!!.categoryId }

    val menuItemsCategory = mainState.dbCategories

    var title by remember { mutableStateOf(foundLandmark!!.title) }
    var otherInfo by remember { mutableStateOf(foundLandmark!!.otherInfo) }
    var selectedCategory by remember { mutableStateOf(foundCategory) }

    TopAppBar(
        title = { Text(stringResource(R.string.edit_landmark)) },
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
                label = { Text(stringResource(R.string.landmark_title)) },
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        item {
            TextField(
                value = otherInfo,
                onValueChange = { otherInfo = it },
                label = { Text(stringResource(R.string.landmark_description)) },
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        item {
            var expanded by remember { mutableStateOf(false) }

            Column(
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Text(stringResource(R.string.category))

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    menuItemsCategory.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category.name) },
                            onClick = {
                                selectedCategory = category
                                expanded = false
                            }
                        )
                    }
                }

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(50.dp)
                        .padding(8.dp)
                        .border(
                            1.dp,
                            MaterialTheme.colorScheme.primary,
                            shape = MaterialTheme.shapes.small
                        )
                        .clickable { expanded = !expanded }
                ) {
                    if (selectedCategory != null)
                    {
                        Row (
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 8.dp)
                        ){
                            Icon(
                                imageVector = selectedCategory!!.icon.icon,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = selectedCategory!!.name,
                                style = TextStyle(fontSize = 16.sp),
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }
                    }
                    else
                    {
                        Text(stringResource(R.string.select_the_new_icon))
                    }
                }
            }
        }

        val geoPoint = GeoPoint(foundLandmark!!.geoPoint.latitude, foundLandmark.geoPoint.longitude)

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
                    if (title.isNotBlank() && selectedCategory?.id != null && otherInfo.isNotBlank()) {
                        AppDataManager.updateLandmark(
                            landmarkId,
                            selectedCategory?.id!!,
                            title,
                            otherInfo
                        )
                        navController.popBackStack()
                    }
                },
                modifier = Modifier.padding(top = 15.dp, bottom = 16.dp)
            ) {
                Text(stringResource(R.string.edit_landmark))
            }
        }
    }
}
