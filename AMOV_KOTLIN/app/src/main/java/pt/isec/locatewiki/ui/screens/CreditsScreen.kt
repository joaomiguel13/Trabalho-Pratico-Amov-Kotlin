package pt.isec.locatewiki.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import pt.isec.locatewiki.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreditsScreen(
    navController: NavController
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(color = Color.White)
    ) {
        TopAppBar(
            title = { Text(stringResource(id = R.string.credits)) },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val additionalInfo = listOf(
                stringResource(id = R.string.course),
                stringResource(id = R.string.degree)
            )

            additionalInfo.forEach { info ->
                ListItemText(info, fontSize = 24.sp)
            }

            val developers = listOf(
                "Ângelo Galvão - 2019138402",
                "João Duarte - 2020122715",
                "Mateus Oliveira - 2021136689"
            )

            developers.forEach { developer ->
                ListItemText(developer, fontSize = 24.sp)
            }
        }
    }
}

@Composable
fun ListItemText(text: String, fontSize: TextUnit) {
    Text(
        text = text,
        fontSize = fontSize,
        fontFamily = FontFamily(Font(R.font.font)),
        modifier = Modifier.padding(16.dp)
    )
}