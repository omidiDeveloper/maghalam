package com.example.maghalam.ui.features.intro

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.maghalam.R
import com.example.maghalam.ui.features.GreetingPreview
import com.example.maghalam.ui.theme.MaghalamTheme

@Composable
fun IntroScreen() {

    Surface(
        modifier = Modifier.fillMaxSize()
    )
    {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                Intro()
            }
        }
    }

}

@Composable
@Preview
fun IntroScreenPreview() {
    GreetingPreview() {
        MaghalamTheme() {
            Surface(
                modifier = Modifier.fillMaxSize()
            ) {
                Intro()
            }
        }
    }
}

@Composable
fun Intro() {

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {

        Column(
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Column(
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.introsc_welcome_txt1) + " " + stringResource(R.string.app_name) + " " + stringResource(
                        R.string.introsc_welcome_txt2
                    ),
                    style = MaterialTheme.typography.headlineLarge
                )
                Text(
                    modifier = Modifier.padding(all = 10.dp),
                    text = stringResource(R.string.introsc_welcome_body),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Button(
                onClick = {}
            ) {
                Text(
                    text = stringResource(R.string.introsc_btn_lets_go),
                    style = MaterialTheme.typography.titleLarge
                )
            }

        }
    }
}

