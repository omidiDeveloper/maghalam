package com.example.maghalam.ui.features.login

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
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
import com.example.maghalam.utills.STR_EMPTY_FEILDS
import com.example.maghalam.utills.STR_SUCCESS
import com.example.maghalam.utills.Screens
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LoginScreen(
    navController: NavController,
    onLoginSuccess: () -> Unit = {
        navController.navigate(Screens.AiScreen.rute)
    }
) {
    val viewModel = koinViewModel<LoginViewModel>()

    Surface(
        modifier = Modifier.fillMaxSize()
    )
    {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                LoginScreenView(navController, viewModel, onLoginSuccess)
            }
        }
    }

}

@Composable
@Preview
fun LoginScreenPreview() {
    GreetingPreview {
        MaghalamTheme {
            Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            LoginScreen(rememberNavController())
        }
    }
}
}

@Composable
fun LoginScreenView(
    navController: NavController,
    viewModel: LoginViewModel,
    onLoginSuccess: () -> Unit
) {
    val email = viewModel.email.observeAsState("")
    val password = viewModel.password.observeAsState("")
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
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .padding(top = 28.dp, bottom = 24.dp)

                ) {

                    //User or Email Box =>
                    MainEditText(
                        email.value.toString(),
                        R.string.loginsc_user_email_txt,
                        R.drawable.baseline_email_24
                    ) {
                        viewModel.email.value = it
                    }

                    //Password Box =>
                    PasswordEditText(
                        stringResource(R.string.loginsc_password_txt),
                        icon = R.drawable.baseline_password_24,
                        edtValue = password.value.orEmpty()
                    ) {
                        viewModel.password.value = it
                    }

                    //Confirm Button =>
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                            .padding(horizontal = 24.dp, vertical = 12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = MaterialTheme.shapes.medium,
                        onClick = {
                            //check internet connection =>
                            if (NetworkChecker(context).isInternetConnected || NetworkChecker(
                                    context
                                ).isWifiConnected
                            ) {

                                //check fields are not empty =>
                                if (email.value.isNotEmpty() && password.value.isNotEmpty()) {
                                    viewModel.login(
                                        onSuccess = {
                                            Toast.makeText(context, STR_SUCCESS, Toast.LENGTH_SHORT).show()
                                            Log.v("LoginValues", "${viewModel.email.value}")
                                            onLoginSuccess()
                                        },
                                        onError = { message ->
                                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                        }
                                    )
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
                    TextButton(
                        onClick = { navController.navigate(Screens.ForgotPasswordScreen.rute) }
                    ) {
                        Text("رمز عبور را فراموش کرده‌اید؟")
                    }
                }
            }
        }

    }
}


