package com.example.maghalam.ui.features.login

import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.maghalam.R
import com.example.maghalam.ui.features.GreetingPreview
import com.example.maghalam.ui.features.register.MainEditText
import com.example.maghalam.ui.features.register.PasswordEditText
import com.example.maghalam.ui.theme.AppShapes
import com.example.maghalam.ui.theme.MaghalamTheme
import com.example.maghalam.utills.NetworkChecker
import com.example.maghalam.utills.STR_CHECK_CONNECTION
import com.example.maghalam.utills.STR_EMAIL_TYPE
import com.example.maghalam.utills.STR_EMPTY_FEILDS
import com.example.maghalam.utills.STR_SUCCESS
import com.example.maghalam.utills.Screens
import dev.burnoo.cokoin.navigation.getNavController
import kotlin.math.log

@Composable
fun LoginScreen(navController: NavController) {

    Surface(
        modifier = Modifier.fillMaxSize()
    )
    {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                LoginScreenView(navController)
            }
        }
    }

}

@Composable
@Preview
fun LoginScreenPreview() {
    GreetingPreview() {
        MaghalamTheme() {
            Surface(
                modifier = Modifier.fillMaxSize()
            ) {
                LoginScreenView(rememberNavController())
            }
        }
    }
}

@Composable
fun LoginScreenView(navController: NavController) {

    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val context = LocalContext.current


    Surface(
        modifier = Modifier.fillMaxSize()
    )
    {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {


            Text(
                stringResource(R.string.loginsc_welcome_txt),
                style = MaterialTheme.typography.headlineLarge
            )

            Card(
                modifier = Modifier
                    .wrapContentSize()
                    .padding(top = 22.dp, start = 12.dp, end = 12.dp),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .background(color = MaterialTheme.colorScheme.background)
                        .padding(top = 32.dp, bottom = 32.dp)

                ) {

                    //User or Email Box =>
                    MainEditText(
                        email.value,
                        R.string.loginsc_user_email_txt,
                        R.drawable.baseline_email_24
                    ) {
                        email.value = it
                    }

                    //Password Box =>
                    PasswordEditText(
                        stringResource(R.string.loginsc_password_txt),
                        icon = R.drawable.baseline_password_24,
                        edtValue = password.value
                    ) {
                        password.value = it
                    }

                    //Confirm Button =>
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .padding(horizontal = 24.dp, vertical = 12.dp),
                        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
                        shape = AppShapes.medium,
                        onClick = {
                            //check internet connection =>
                            if (NetworkChecker(context).isInternetConnected || NetworkChecker(
                                    context
                                ).isWifiConnected
                            ) {

                                //check fields are not empty =>
                                if (email.value.isNotEmpty() && password.value.isNotEmpty()) {
                                    //check exist values =>
                                    //TODO email and password exists

                                    Toast.makeText(context, STR_SUCCESS, Toast.LENGTH_SHORT).show()
                                    Log.v("LoginValues", "${email.value} ${password.value}")

                                    navController.navigate(Screens.AiScreen.rute)

                                } else {
                                    Toast.makeText(context, STR_EMPTY_FEILDS, Toast.LENGTH_SHORT)
                                        .show()
                                }
                            } else {
                                Toast.makeText(context, STR_CHECK_CONNECTION, Toast.LENGTH_SHORT)
                                    .show()
                            }

                        }) {
                        Text(
                            text = stringResource(R.string.loginsc_btn_login_txt),
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Text(
                            stringResource(R.string.loginsc_no_account_txt),
                            style = MaterialTheme.typography.bodyMedium
                        )

                        TextButton(
                            onClick = {
                                navController.navigate(Screens.RegisterScreen.rute)
                            }
                        ) {
                            Text(
                                stringResource(R.string.registersc_btn_register)
                            )
                        }
                    }


                }
            }
        }

    }
}


