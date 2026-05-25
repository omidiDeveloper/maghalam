package com.example.maghalam.ui.features.register

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.maghalam.ui.features.GreetingPreview
import com.example.maghalam.R
import com.example.maghalam.ui.theme.AppShapes
import com.example.maghalam.ui.theme.MaghalamTheme
import com.example.maghalam.utills.NetworkChecker
import com.example.maghalam.utills.RtlLayout
import com.example.maghalam.utills.STR_CHECK_CONNECTION
import com.example.maghalam.utills.STR_EMAIL_TYPE
import com.example.maghalam.utills.STR_EMPTY_FEILDS
import com.example.maghalam.utills.STR_INVALID_CHAR
import com.example.maghalam.utills.STR_PASSWORD_MATCH
import com.example.maghalam.utills.STR_SUCCESS
import com.example.maghalam.utills.Screens
import com.example.maghalam.utills.VALUE_SUCCESS
import org.koin.compose.viewmodel.koinViewModel


//---------------------Preview Screen------------------------
@Composable
@Preview
fun RegisterScreenPreview() {
    GreetingPreview() {
        MaghalamTheme() {
            Surface(
                modifier = Modifier.fillMaxSize()
            ) {
                RegisterScreen(navController = rememberNavController())
            }
        }
    }
}


//---------------------Register Main Screen---------------------
@Composable
fun RegisterScreen(
    navController: NavController
) {
    val lang: String = "Persian"
    val viewModel = koinViewModel<RegisterViewModel>()

    Surface(
        modifier = Modifier.fillMaxSize()
    )
    {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (lang.equals("Persian")) {
                RtlLayout {
                    RegisterScreenView(
                        viewModel,
                        navController
                    )
                }
            } else {
                RegisterScreenView(viewModel, navController)
            }

        }
    }

}

//---------------------Register Design Screen---------------------

@Composable
fun RegisterScreenView(viewModel: RegisterViewModel, navController: NavController) {

//    val name = viewModel.name.observeAsState("")
//    val userName = viewModel.userName.observeAsState("")
//    val email = viewModel.email.observeAsState("")
//    val password = viewModel.password.observeAsState("")
//    val rePassword = viewModel.password.observeAsState("")
//    val context = LocalContext.current

    val name = remember { mutableStateOf("") }
    val userName = remember { mutableStateOf("") }
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val rePassword = remember { mutableStateOf("") }
    val context = LocalContext.current

    Surface(
        modifier = Modifier.fillMaxSize()
    )
    {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

//                HandwriteWord(
//                    duration = 2000,
//                    strokeWidth = 4.dp,
//                    color = MaterialTheme.colorScheme.primary,
//                    pathData = R.drawable.maghalam_icon_background,
//                )

            Text(
                stringResource(R.string.registersc_btn_register),
                textAlign = TextAlign.Center,
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
                    modifier = Modifier.background(color = MaterialTheme.colorScheme.background)

                ) {

                    //Name and Family box =>
                    MainEditText(
                        name.value,
                        R.string.registersc_name_family_txt,
                        R.drawable.outline_person_2_24
                    ) {
                        name.value = it
                    }

                    //UserName box=>
                    MainEditText(
                        userName.value,
                        R.string.registersc_user_txt,
                        R.drawable.outline_person_2_24
                    ) {
                        userName.value = it
                    }

                    //Email Box =>
                    MainEditText(
                        email.value,
                        R.string.registersc_email_txt,
                        R.drawable.baseline_email_24
                    ) {
                        email.value = it
                    }

                    //Password Box =>
                    PasswordEditText(
                        hint = stringResource(R.string.registersc_password),
                        edtValue = password.value,
                        R.drawable.baseline_password_24,
                    ) {
                        password.value = it
                    }
                    //RePassword Box =>
                    PasswordEditText(
                        hint = stringResource(R.string.registersc_re_password),
                        edtValue = rePassword.value,
                        R.drawable.baseline_password_24
                    ) {
                        rePassword.value = it
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
                            if (NetworkChecker(context).isInternetConnected || NetworkChecker(
                                    context
                                ).isWifiConnected
                            ) {
                                if (name.value.isNotEmpty() && email.value.isNotEmpty() && password.value.isNotEmpty() && rePassword.value.isNotEmpty()) {
                                    if (Patterns.EMAIL_ADDRESS.matcher(email.value).matches()) {
                                        if (password.value.length >= 8) {
                                            if (password.value == rePassword.value) {
//                                                viewModel.registerUser {
//                                                    if (it == VALUE_SUCCESS) {

                                                Toast.makeText(
                                                    context,
                                                    STR_SUCCESS,
                                                    Toast.LENGTH_SHORT
                                                )
                                                    .show()

                                                Log.v(
                                                    "SignUped",
                                                    "${name.value} ${userName.value} ${email.value}   ${password.value}"
                                                )

                                                navController.navigate(Screens.AiScreen.rute)

//                                                    } else {
//                                                        Toast.makeText(
//                                                            context,
//                                                            it,
//                                                            Toast.LENGTH_SHORT
//                                                        ).show()
//


                                            } else {
                                                Toast.makeText(
                                                    context,
                                                    STR_PASSWORD_MATCH,
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        } else {
                                            Toast.makeText(
                                                context,
                                                STR_INVALID_CHAR,
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    } else {
                                        Toast.makeText(context, STR_EMAIL_TYPE, Toast.LENGTH_SHORT)
                                            .show()
                                    }
                                } else {
                                    Toast.makeText(
                                        context,
                                        STR_EMPTY_FEILDS,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                Toast.makeText(
                                    context,
                                    STR_CHECK_CONNECTION,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    ) {
                        Text(
                            text = stringResource(R.string.registersc_btn_register),
                            style = MaterialTheme.typography.labelMedium
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Text(
                            stringResource(R.string.registersc_have_account),
                            style = MaterialTheme.typography.bodyMedium
                        )

                        TextButton(
                            onClick = {
                                navController.navigate(Screens.LoginScreen.rute)
                            }
                        ) {
                            Text(
                                stringResource(R.string.loginsc_btn_login_txt)
                            )
                        }
                    }
                }
            }

        }
    }
}

//---------------------Password Box----------------------

@Composable
fun PasswordEditText(
    hint: String,
    edtValue: String,
    icon: Int,
    onValueChanged: (String) -> Unit
) {


    val passwordVisible = remember { mutableStateOf(false) }
//------------------------------------------------------------------------------------
    OutlinedTextField(
        label = { Text(hint, style = MaterialTheme.typography.labelMedium) },
        value = edtValue,
        singleLine = true,
        onValueChange = onValueChanged,
        placeholder = { Text(hint) },
        textStyle = MaterialTheme.typography.labelMedium,
        modifier = Modifier
            .fillMaxWidth(.9f)
            .padding(6.dp),
        visualTransformation = if (passwordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        leadingIcon = {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null
            )
        },
        trailingIcon = {
            val image =
                if (passwordVisible.value) painterResource(R.drawable.ic_visibility_off) else painterResource(
                    R.drawable.ic_visibility
                )
//------------------------------------------------------------------------------------
            Icon(
                painter = image,
                contentDescription = null,
                modifier = Modifier
                    .padding(end = 12.dp)
                    .clickable { passwordVisible.value = !passwordVisible.value }
            )
        }


    )

}


//---------------------Main EditText---------------------

@Composable
fun MainEditText(
    value: String,
    label: Int,
    icon: Int,
    onValueChange: (String) -> Unit
) {


    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        label = {
            Text(
                text = stringResource(id = label),
                style = MaterialTheme.typography.labelMedium
            )
        },
        leadingIcon = {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null
            )
        },
        modifier = Modifier
            .fillMaxWidth(.9f)
            .padding(6.dp)

    )

}
