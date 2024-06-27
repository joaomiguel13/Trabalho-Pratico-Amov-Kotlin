package pt.isec.locatewiki.ui.screens.category

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.navigation.NavHostController
import pt.isec.locatewiki.R
import pt.isec.locatewiki.ui.data.AppDataManager
import pt.isec.locatewiki.ui.data.CategoryIcon
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditCategoryScreen(
    navController: NavHostController, categoryId: String
) {
    val mainState by AppDataManager.state.collectAsState()
    val foundCategory = mainState.dbCategories.find { it.id == categoryId }

    var title by remember { mutableStateOf(foundCategory!!.name) }
    var selectedIcon by remember { mutableStateOf<CategoryIcon?>(foundCategory!!.icon) }

    TopAppBar(
        title = { Text(stringResource(R.string.edit_category)) },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
        },
        modifier = Modifier.fillMaxWidth()
    )

    Column(
        Modifier
            .fillMaxSize()
            .padding(50.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.size(16.dp))

        TextField(
            value = title,
            onValueChange = { title = it },
            label = { Text(foundCategory!!.name) },
            modifier = Modifier.padding(bottom = 8.dp)
        )

        var expanded by remember { mutableStateOf(false) }

        Column(
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            Text(stringResource(id = R.string.category_icon))

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                CategoryIcon.values().forEach { icon ->
                    DropdownMenuItem(
                        text = { Text(icon.getPresentationName()) },
                        leadingIcon = { Icon(icon.icon, contentDescription = icon.getPresentationName()) },
                        onClick = {
                            selectedIcon = icon
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxHeight()
                ) {
                    if (selectedIcon != null) {
                        Icon(
                            imageVector = selectedIcon!!.icon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = selectedIcon!!.getPresentationName(),
                            style = TextStyle(fontSize = 16.sp),
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    } else {
                        Text(
                            text = stringResource(R.string.select_the_new_icon),
                            style = TextStyle(fontSize = 14.sp)
                        )
                    }
                }
            }
        }

        Button(onClick = {
            if (selectedIcon != null && title.isNotBlank()) {
                AppDataManager.updateCategory(
                    foundCategory!!.id!!,
                    selectedIcon!!,
                    title
                )
                navController.popBackStack()
            }
        }) {
            Text(text = stringResource(R.string.save))
        }
    }
}