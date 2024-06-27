package pt.isec.locatewiki.ui.screens.category

import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import pt.isec.locatewiki.R
import pt.isec.locatewiki.ui.data.AppDataManager
import pt.isec.locatewiki.ui.data.Category
import pt.isec.locatewiki.ui.screens.Screens
import pt.isec.locatewiki.utils.firebase.FirebaseHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesScreen(
    navController: NavController
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

    TopAppBar(
        title = { Text(stringResource(id = R.string.categories)) },
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
        when {
            mainState.dbCategories.isNotEmpty() -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    items(mainState.dbCategories.toList()) { category ->
                        CategoryCard(category = category, navController = navController)
                    }
                }
            }

            mainState.error != null -> {
                ErrorView(error = mainState.error!!)
            }

            else -> {
                Text(
                    text = stringResource(R.string.no_categories_found),
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
                navController.navigate(Screens.ADD_CATEGORY.route)
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

@Composable
private fun CategoryCard(navController: NavController, category: Category) {
    val user = FirebaseHelper.getCurrentUser()

    val isOwner = user!!.uid == category.ownerId
    val numberOfApprovals = category.usersTrust.size
    val canApprove = !category.usersTrust.contains(user.uid)
    val needsApproval = category.usersTrust.size < 2

    val mainState by AppDataManager.state.collectAsState()
    val canDelete = mainState.dbLandmarks.none { it.categoryId == category.id }

    val contextForToast = LocalContext.current.applicationContext

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant)
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
                Row (
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 8.dp)
                ){
                    Icon(
                        imageVector = category.icon.icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = category.name,
                        style = TextStyle(fontSize = 16.sp),
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }

                if (isOwner) {
                    Text(
                        text = stringResource(R.string.you_are_the_owner_of_this_category),
                        style = TextStyle(fontSize = 13.sp),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                if (needsApproval) {
                    if (canApprove) {
                        Text(
                            text = stringResource(R.string.you_can_approve_this_category),
                            style = TextStyle(fontSize = 13.sp),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    } else {
                        Text(
                            text = stringResource(R.string.you_already_approved_this_category),
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
                        Text(
                            text = stringResource(R.string.needs_approval, numberOfApprovals),
                            style = TextStyle(fontSize = 13.sp),
                            modifier = Modifier
                                .padding(bottom = 8.dp)
                                .weight(1f)
                        )
                    }
                }
            }

            var expanded by remember { mutableStateOf(false) }

            if (isOwner || canApprove) {
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
                        if (needsApproval && canApprove) {
                            DropdownMenuItem(
                                text = { Text(text = stringResource(R.string.approve)) },
                                onClick = {
                                    AppDataManager.approveCategory(category)
                                    Toast.makeText(
                                        contextForToast,
                                        contextForToast.getString(R.string.category_approved),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                })
                        }

                        if (isOwner) {
                            DropdownMenuItem(
                                text = { Text(text = stringResource(R.string.edit)) },
                                onClick = {
                                    navController.navigate(Screens.EDIT_CATEGORY.route + "?categoryId=" + category.id)
                                })

                            if (canDelete) {
                                DropdownMenuItem(
                                    text = { Text(text = stringResource(R.string.delete)) },
                                    onClick = {
                                        AppDataManager.deleteCategory(category)
                                        Toast.makeText(
                                            contextForToast,
                                            contextForToast.getString(R.string.category_deleted),
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
