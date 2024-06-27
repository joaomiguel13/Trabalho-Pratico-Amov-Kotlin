package pt.isec.locatewiki.ui.screens.landmark

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import pt.isec.locatewiki.R
import pt.isec.locatewiki.ui.data.AppDataManager
import pt.isec.locatewiki.ui.data.Category

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCommentScreen(
    navController: NavHostController,
    landmarkId: String
) {
    val context = LocalContext.current
    val mainState by AppDataManager.state.collectAsState()
    val foundLandmark = mainState.dbLandmarks.find { it.id == landmarkId }

    var grade by remember { mutableIntStateOf(2) }
    var comment by remember { mutableStateOf("") }
    var selectedImage by remember { mutableStateOf<Uri?>(null) }

    TopAppBar(
        title = { Text(stringResource(id = R.string.add_comment)) },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
        },
        modifier = Modifier.fillMaxWidth()
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 50.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        item {
            Spacer(modifier = Modifier.size(16.dp))
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(
                    text = stringResource(R.string.grade, getGradeQuality(grade = grade)),
                    style = TextStyle(fontSize = 20.sp),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Slider(
                    value = grade.toFloat(),
                    onValueChange = { grade = it.toInt() },
                    valueRange = 0f..3f,
                    steps = 1
                )
            }
        }

        item {
            TextField(
                value = comment,
                onValueChange = { comment = it },
                label = { Text(stringResource(R.string.comment)) },
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        item {
            val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.PickVisualMedia(),
                onResult = { uri -> selectedImage = uri }
            )

            Button(
                onClick = {
                    singlePhotoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Text(stringResource(R.string.pick_image))
            }

            ImageView(selectedImage)
        }

        item {
            Button(
                onClick = {
                    if (comment.isNotBlank() && selectedImage != null) {
                        AppDataManager.createClassification(
                            context,
                            foundLandmark!!,
                            grade,
                            comment,
                            selectedImage!!
                        )
                        navController.popBackStack()
                    }
                },
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Text(stringResource(R.string.add_comment))
            }
        }
    }
}

@Composable
private fun ImageView(selectedImage: Uri?) {
    if (selectedImage != null) {
        AsyncImage(
            model = selectedImage,
            contentDescription = null,
            modifier = Modifier
                .padding(8.dp)
                .size(100.dp),
            contentScale = ContentScale.Fit
        )
    }
    else {
        Box(
            modifier = Modifier
                .padding(8.dp)
                .size(100.dp)
                .border(
                    1.dp,
                    MaterialTheme.colorScheme.primary,
                    shape = MaterialTheme.shapes.small
                )
        ) {
            Text(stringResource(R.string.no_image))
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