package pt.isec.locatewiki.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
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
import pt.isec.locatewiki.utils.firebase.FirebaseHelper

@Composable
fun MainScreen(
    navController: NavHostController?,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
    ) {
        item {
            Spacer(modifier = Modifier.padding(16.dp))
        }

        item {
            Text(
                text = stringResource(R.string.welcome, FirebaseHelper.getCurrentUser()?.email!!),
                style = MaterialTheme.typography.headlineSmall,
                fontFamily = FontFamily(Font(R.font.font)),
                fontSize = 30.sp,
                modifier = Modifier.padding(16.dp)
            )
        }

        item {
            Text(
                text = stringResource(R.string.please_select_an_option),
                style = MaterialTheme.typography.headlineSmall,
                fontFamily = FontFamily(Font(R.font.font)),
                fontSize = 24.sp,
                modifier = Modifier.padding(16.dp)
            )
        }

        item {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Button(
                    onClick = { navController?.navigate(Screens.LOCATIONS.route) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(3, 187, 133)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.locations),
                        fontFamily = FontFamily(Font(R.font.font)),
                        fontSize = 30.sp,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                Button(
                    onClick = { navController?.navigate(Screens.CATEGORIES.route) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(3, 187, 133)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.categories),
                        fontFamily = FontFamily(Font(R.font.font)),
                        fontSize = 30.sp,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                Button(
                    onClick = {
                        FirebaseHelper.signOut()
                        navController?.navigate(Screens.MENU.route)
                        {
                            popUpTo(Screens.MAIN.route) {
                                inclusive = true
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(255, 0, 0)
                    ),
                    modifier = Modifier
                        .wrapContentWidth()
                        .padding(8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.logout),
                        fontFamily = FontFamily(Font(R.font.font)),
                        fontSize = 18.sp,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }
}