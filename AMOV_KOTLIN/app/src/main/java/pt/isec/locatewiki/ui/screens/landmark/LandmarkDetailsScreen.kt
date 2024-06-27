package pt.isec.locatewiki.ui.screens.landmark

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import pt.isec.locatewiki.R
import pt.isec.locatewiki.ui.data.AppDataManager
import pt.isec.locatewiki.ui.data.Landmark
import pt.isec.locatewiki.ui.screens.Screens
import pt.isec.locatewiki.utils.firebase.FirebaseHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LandmarkDetailsScreen(
    navController: NavHostController, landmarkId: String
) {
    val mainState by AppDataManager.state.collectAsState()
    val foundLandmark = mainState.dbLandmarks.find { it.id == landmarkId }
    val foundCategory = mainState.dbCategories.find { it.id == foundLandmark!!.categoryId }
    if (foundLandmark == null || foundCategory == null) {
        navController.popBackStack()
        return
    }

    var image by remember { mutableStateOf<Bitmap?>(null) }
    LaunchedEffect(landmarkId) {
        runBlocking(Dispatchers.IO) {
            AppDataManager.getLandmarkImage(foundLandmark) { imageResult ->
                image = imageResult
            }
        }
    }

    TopAppBar(
        title = { Text(stringResource(R.string.landmark_details, foundLandmark.title)) },
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
            if (image != null) {
                Image(
                    bitmap = image!!.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(bottom = 16.dp, top = 16.dp)
                )
            } else {
                Text(
                    text = stringResource(R.string.no_image),
                    style = TextStyle(fontSize = 13.sp),
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
        }

        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Text(
                    text = foundLandmark.title,
                    style = TextStyle(fontSize = 16.sp),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Spacer(modifier = Modifier.size(8.dp))
                Icon(
                    imageVector = foundCategory.icon.icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(25.dp)
                        .padding(bottom = 8.dp)
                )
                Text(
                    text = foundCategory.name,
                    style = TextStyle(fontSize = 13.sp),
                    modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
                )
            }
        }

        item {
            Text(
                text = foundLandmark.otherInfo,
                style = TextStyle(fontSize = 16.sp),
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        val geoPoint = GeoPoint(foundLandmark.geoPoint.latitude, foundLandmark.geoPoint.longitude)

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
                    }
                    mapView.overlays.add(marker)

                    mapView
                },
                modifier = Modifier
                    .aspectRatio(1f)
                    .padding(16.dp)
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.primary,
                        shape = MaterialTheme.shapes.small
                    )
            )
        }

        val users = foundLandmark.usersClassification.keys.filter { foundLandmark.usersComment[it] != null }
        if (users.isNotEmpty()) {
            for (key in users) {
                item {
                    CommentCard(landmark = foundLandmark, userId = key)
                }
            }
        }
        else {
            item {
                Text(
                    text = stringResource(R.string.no_comments_found),
                    style = TextStyle(fontSize = 13.sp),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
        }
    }

    val userId = FirebaseHelper.getCurrentUser()!!.uid
    if (!foundLandmark.usersComment.containsKey(userId) && foundLandmark.ownerId != userId) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            FloatingActionButton(
                onClick = {
                    navController.navigate(Screens.ADD_COMMENT.route + "?landmarkId=" + landmarkId)
                },
                modifier = Modifier
                    .size(56.dp)
                    .background(MaterialTheme.colorScheme.primary, CircleShape)
                    .align(Alignment.BottomEnd),
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
            }
        }
    }
}

@Composable
fun CommentCard(landmark: Landmark, userId: String) {
    val isOwner = FirebaseHelper.getCurrentUser()!!.uid == userId

    val classification = landmark.usersClassification[userId]
    val comment = landmark.usersComment[userId]

    var image by remember { mutableStateOf<Bitmap?>(null) }
    LaunchedEffect(userId) {
        runBlocking(Dispatchers.IO) {
            AppDataManager.getClassificationImage(landmark, userId) { imageResult ->
                image = imageResult
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 8.dp, start = 24.dp)
        ) {
            if (image != null) {
                Image(
                    bitmap = image!!.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .size(100.dp)
                        .padding(bottom = 8.dp)
                )
            } else {
                Text(
                    text = stringResource(R.string.no_image),
                    style = TextStyle(fontSize = 13.sp),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            Spacer(modifier = Modifier.size(16.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Top
            ) {
                Text(
                    text = stringResource(R.string.grade, getGradeQuality(grade = classification!!)),
                    style = TextStyle(fontSize = 13.sp),
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = comment!!,
                    style = TextStyle(fontSize = 13.sp),
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                if (isOwner) {
                    Text(
                        text = stringResource(R.string.you_are_the_owner_of_this_comment),
                        style = TextStyle(fontSize = 13.sp),
                        modifier = Modifier.padding(bottom = 8.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun getGradeQuality(grade: Int): String
{
    return when (grade) {
        0 -> stringResource(id = R.string.very_bad)
        1 -> stringResource(id = R.string.bad)
        2 -> stringResource(id = R.string.good)
        3 -> stringResource(id = R.string.very_good)
        else -> ""
    }
}