package pt.isec.locatewiki.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import pt.isec.locatewiki.R
import pt.isec.locatewiki.utils.firebase.FirebaseHelper
import pt.isec.locatewiki.utils.firebase.FirebaseResponseHandler

@Composable
fun LoginScreen(
    navController: NavController
) {
    val emailValue = remember { mutableStateOf("") }
    val passwordValue = remember { mutableStateOf("") }
    val passwordVisibility = remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        item {
            Spacer(modifier = Modifier.padding(16.dp))
        }

        item {
            Image(
                painter = painterResource(R.drawable.ic_locatewiki),
                contentDescription = "logo",
                contentScale = ContentScale.FillHeight,
            )
        }

        item {
            Spacer(modifier = Modifier.padding(16.dp))
        }

        item {
            Card(
                modifier = Modifier
                    .padding(12.dp),
                shape = RoundedCornerShape(30.dp)
            ) {
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(32.dp)
                ) {
                    Text(
                        text = stringResource(R.string.title),
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily(Font(R.font.font)),
                        fontSize = 30.sp,
                        modifier = Modifier
                            .padding()
                            .fillMaxWidth()
                            .wrapContentSize(Alignment.Center)
                    )
                    Column(
                        Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {

                        OutlinedTextField(
                            value = emailValue.value,
                            onValueChange = { emailValue.value = it },
                            label = { Text(text = stringResource(R.string.email_address)) },
                            placeholder = { Text(text = stringResource(R.string.email_address)) },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth(1f)
                        )

                        OutlinedTextField(
                            value = passwordValue.value,
                            onValueChange = { passwordValue.value = it },
                            label = { Text(stringResource(R.string.password)) },
                            placeholder = { Text(text = stringResource(R.string.password)) },
                            singleLine = true,
                            visualTransformation = if (passwordVisibility.value) VisualTransformation.None
                            else PasswordVisualTransformation(),
                            modifier = Modifier
                                .fillMaxWidth(1f)
                        )

                        Spacer(modifier = Modifier.padding(10.dp))

                        Button(
                            onClick = {
                                FirebaseHelper.signIn(
                                    emailValue.value,
                                    passwordValue.value,
                                    FirebaseResponseHandler(
                                        successCallback = {
                                            navController.navigate(Screens.MAIN.route)
                                            {
                                                popUpTo(Screens.MENU.route) {
                                                    inclusive = true
                                                }
                                            }
                                        },
                                        failureCallback = {
                                            Toast.makeText(
                                                navController.context,
                                                "Login failed: ${it.message}",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    )
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth(1f)
                                .height(60.dp)
                        )
                        {
                            Text(
                                text = stringResource(R.string.login),
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily(Font(R.font.font)),
                                fontSize = 20.sp
                            )
                        }

                        Spacer(modifier = Modifier.padding(10.dp))

                        Button(
                            onClick = {
                                navController.popBackStack()
                            },
                            modifier = Modifier
                                .fillMaxWidth(1f)
                                .height(60.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.back_to_main_menu),
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily(Font(R.font.font)),
                                fontSize = 20.sp
                            )
                        }
                    }
                }
            }
        }
    }
}