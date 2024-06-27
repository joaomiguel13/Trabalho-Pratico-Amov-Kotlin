package pt.isec.locatewiki.ui.screens.landmark

import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import pt.isec.locatewiki.R
import pt.isec.locatewiki.ui.data.AppDataManager
import pt.isec.locatewiki.ui.data.Landmark
import pt.isec.locatewiki.ui.data.Location
import pt.isec.locatewiki.ui.data.Order
import pt.isec.locatewiki.ui.screens.Screens
import pt.isec.locatewiki.utils.firebase.FirebaseHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LandmarksScreen(
    navController: NavController,
    location: Location
) {
    val user = FirebaseHelper.getCurrentUser()
    if (user == null) {
        navController.navigate(Screens.MENU.route)
        {
            popUpTo(Screens.MAIN.route) {
                inclusive = true
            }
        }
    }

    val mainState by AppDataManager.state.collectAsState()

    var orderType by remember { mutableStateOf(Order.DISTANCE) }
    var searchTerm by remember { mutableStateOf(TextFieldValue("")) }

    TopAppBar(
        title = { Text(stringResource(R.string.landmarks)) },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
        },
        modifier = Modifier.fillMaxWidth()
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 50.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
            ,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { orderType = Order.NAME },
                modifier = Modifier.weight(1f)
            ) {
                Text(text = stringResource(R.string.order_by_name))
            }
            Button(
                onClick = { orderType = Order.DISTANCE },
                modifier = Modifier.weight(1f)
            ) {
                Text(text = stringResource(R.string.order_by_distance))
            }
            Button(
                onClick = { orderType = Order.CATEGORY },
                modifier = Modifier.weight(1f)
            ) {
                Text(text = stringResource(id = R.string.order_by_category))
            }
        }
        TextField(
            modifier = Modifier.padding(top = 15.dp),
            value = searchTerm,
            onValueChange = { searchTerm = it },
            label = { Text(stringResource(R.string.search)) },
        )

        var filteredLandmarks = mainState.dbLandmarks.filter {
            it.locationId == location.id && it.title.contains(searchTerm.text, ignoreCase = true)
        }.toList()

        filteredLandmarks = when (orderType) {
            Order.NAME -> {
                filteredLandmarks.sortedBy { it.title }
            }

            Order.DISTANCE -> {
                filteredLandmarks.sortedBy {
                    calculateDistance(
                        it.geoPoint,
                        mainState.currentLocation
                    )
                }
            }

            Order.CATEGORY -> {
                filteredLandmarks.sortedBy { it.categoryId }
            }
        }

        when {

            filteredLandmarks.isNotEmpty() -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    items(filteredLandmarks) { landmark ->
                        LandmarkCard(navController, landmark)
                    }
                }
            }

            mainState.error != null -> {
                ErrorView(mainState.error!!)
            }

            else -> {
                Text(
                    text = stringResource(R.string.no_landmarks_found),
                    style = TextStyle(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        FloatingActionButton(
            onClick = {
                navController.navigate(Screens.ADD_LANDMARK.route + "?locationId=" + location.id)
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

private fun calculateDistance(point: GeoPoint, currentLocation: android.location.Location): Double {
    val location = android.location.Location(null)
    location.latitude = point.latitude
    location.longitude = point.longitude
    return currentLocation.distanceTo(location).toDouble()
}

@Composable
private fun LandmarkCard(navController: NavController, landmark: Landmark) {
    val user = FirebaseHelper.getCurrentUser()

    val isOwner = user!!.uid == landmark.ownerId
    val numberOfApprovals = landmark.usersTrust.size
    val canApprove = !landmark.usersTrust.contains(user.uid)
    val needsApproval = (landmark.beingDeleted && numberOfApprovals < 3) || (!landmark.beingDeleted && numberOfApprovals < 2)

    val category = AppDataManager.getCategoryById(landmark.categoryId)
    val contextForToast = LocalContext.current.applicationContext
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable { navController.navigate(Screens.LANDMARK_DETAILS.route + "?landmarkId=" + landmark.id) }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Left Column
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 16.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Text(
                        text = landmark.title,
                        style = TextStyle(fontSize = 16.sp),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Icon(
                        imageVector = category!!.icon.icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .size(25.dp)
                            .padding(bottom = 8.dp)
                    )
                    Text(
                        text = category.name,
                        style = TextStyle(fontSize = 13.sp),
                        modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
                    )
                }
                Text(
                    text = landmark.otherInfo,
                    style = TextStyle(fontSize = 13.sp),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "${landmark.geoPoint.latitude}, ${landmark.geoPoint.longitude}",
                    style = TextStyle(fontSize = 13.sp),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                if (isOwner) {
                    Text(
                        text = stringResource(R.string.you_are_the_owner_of_this_landmark),
                        style = TextStyle(fontSize = 13.sp),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                if (needsApproval) {
                    if (canApprove) {
                        var textId = R.string.you_can_approve_this_landmark
                        if (landmark.beingDeleted)
                            textId = R.string.delete_you_can_approve_this_landmark
                        Text(
                            text = stringResource(textId),
                            style = TextStyle(fontSize = 13.sp),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    } else {
                        var textId = R.string.you_already_approved_this_landmark
                        if (landmark.beingDeleted)
                            textId = R.string.delete_you_already_approved_this_landmark
                        Text(
                            text = stringResource(textId),
                            style = TextStyle(fontSize = 13.sp),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    Row {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        var textId = R.string.needs_approval
                        if (landmark.beingDeleted)
                            textId = R.string.delete_needs_approval
                        Text(
                            text = stringResource(textId, numberOfApprovals),
                            style = TextStyle(fontSize = 13.sp),
                            modifier = Modifier
                                .padding(bottom = 8.dp)
                                .weight(1f)
                        )
                    }
                }
            }

            // Right Column
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier
                    .weight(0.1f)
                    .padding(start = 16.dp)
            ) {
                IconButton(onClick = {
                    expanded = true
                }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = stringResource(R.string.open_options)
                    )
                }

                // Dropdown menu
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = {
                        expanded = false
                    }
                ) {
                    DropdownMenuItem(
                        text = { Text(text = stringResource(R.string.view_map)) },
                        onClick = {
                            navController.navigate(Screens.MAP.route + "?landmarkId=" + landmark.id)
                        })

                    if (needsApproval && canApprove) {
                        DropdownMenuItem(
                            text = { Text(text = stringResource(R.string.approve)) },
                            onClick = {
                                AppDataManager.approveLandmark(landmark)
                                Toast.makeText(
                                    contextForToast,
                                    contextForToast.getText(R.string.landmark_approved),
                                    Toast.LENGTH_SHORT
                                ).show()
                            })
                    }

                    if (isOwner) {
                        DropdownMenuItem(
                            text = { Text(text = stringResource(R.string.edit)) },
                            onClick = {
                                navController.navigate(Screens.EDIT_LANDMARK.route + "?landmarkId=" + landmark.id)
                            })

                        if (!landmark.beingDeleted) {
                            DropdownMenuItem(
                                text = { Text(text = stringResource(R.string.delete)) },
                                onClick = {
                                    AppDataManager.deleteLandmark(landmark)
                                    Toast.makeText(
                                        contextForToast,
                                        contextForToast.getText(R.string.landmark_deleted),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                })
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ErrorView(error: Throwable) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.error)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.Center)
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onError
            )
            Text(
                text = "Error: ${error.message}",
                style = LocalTextStyle.current.copy(color = MaterialTheme.colorScheme.onError),
                modifier = Modifier.padding(top = 8.dp)
            )
            Button(
                onClick = { /* Handle reset */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(top = 8.dp)
            ) {
                Text(text = "Reset")
            }
        }
    }
}
