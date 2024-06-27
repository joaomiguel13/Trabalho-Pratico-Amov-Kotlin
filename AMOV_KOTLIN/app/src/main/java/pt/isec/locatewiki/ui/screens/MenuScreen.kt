package pt.isec.locatewiki.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import pt.isec.locatewiki.R

@Composable
fun MenuScreen(
    navController: NavHostController?
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {
        item {
            Spacer(modifier = Modifier.padding(16.dp))
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(R.string.title),
                    fontSize = 48.sp,
                    fontFamily = FontFamily(Font(R.font.font)),
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        item {
            Spacer(modifier = Modifier.padding(16.dp))
        }

        item {
            Button(
                onClick = { navController?.navigate(Screens.LOGIN.route) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(3, 187, 133)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.login),
                    fontFamily = FontFamily(Font(R.font.font)),
                    fontSize = 24.sp,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        item {
            Button(
                onClick = { navController?.navigate(Screens.REGISTER.route) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(3, 187, 133)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.register),
                    fontFamily = FontFamily(Font(R.font.font)),
                    fontSize = 24.sp,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        item {
            Button(
                onClick = { navController?.navigate(Screens.CREDITS.route) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(3, 187, 133)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.credits),
                    fontFamily = FontFamily(Font(R.font.font)),
                    fontSize = 24.sp,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}
