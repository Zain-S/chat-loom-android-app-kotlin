package com.saeedtechies.chatloom.presentation.auth

import android.app.Activity.RESULT_OK
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.google.firebase.auth.FirebaseAuth
import com.saeedtechies.chatloom.R
import com.saeedtechies.chatloom.utils.HandleResponse
import com.saeedtechies.chatloom.utils.USER_NOT_FOUND
import kotlinx.serialization.Serializable

@Serializable
object SignIn


@Composable
fun SignInScreen(navController: NavController, authViewModel: AuthViewModel) {
    var isLoggedIn by remember { mutableStateOf(FirebaseAuth.getInstance().currentUser != null) }
    val context = LocalContext.current
    val setCurrentUserResponse by authViewModel.setCurrentUserResponse.collectAsStateWithLifecycle()

    setCurrentUserResponse?.HandleResponse(
        onLoad = {
            if (it) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                )  {
                    CircularProgressIndicator()
                }
            }
        },
        onSuccess = { _ ->
            isLoggedIn = true
            Toast.makeText(context, "Sign in successful!", Toast.LENGTH_SHORT).show()
        },
        onError = { message ->
            if (message == USER_NOT_FOUND)
                navController.navigate(SignUp)
            else
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        },
    )

    if (isLoggedIn) {
        Toast.makeText(context, "User logged in!", Toast.LENGTH_SHORT).show()
        SignOutContent{
            FirebaseAuth.getInstance().signOut()
            isLoggedIn = false
        }
    } else
        LaunchSignIn(onSuccessfulSignIn = {
            authViewModel.setCurrentUser()
        }, onFailedSignIn = {
            Toast.makeText(context, "Sign in failed", Toast.LENGTH_SHORT).show()
        })
}

@Composable
fun LaunchSignIn(onSuccessfulSignIn: () -> Unit, onFailedSignIn: (resultCode: Int) -> Unit) {
    // Choose authentication providers
    val providers = arrayListOf(
        AuthUI.IdpConfig.EmailBuilder().build(),
        AuthUI.IdpConfig.GoogleBuilder().build(),
    )

    // Create a launcher for the sign-in intent
    val signInLauncher = rememberLauncherForActivityResult(FirebaseAuthUIActivityResultContract()) { result ->
        if (result.resultCode == RESULT_OK) {
            onSuccessfulSignIn()
        } else {
            onFailedSignIn(result.resultCode)
        }
    }

    // Create and launch sign-in intent
    val signInIntent = AuthUI.getInstance()
        .createSignInIntentBuilder()
        .setAvailableProviders(providers)
        .setLogo(R.drawable.jetchat_logo) // Set logo drawable
        .setTheme(R.style.Theme_ChatLoom) // Set theme
        .build()

    // Launch the sign-in intent
    SignInContent {
        signInLauncher.launch(signInIntent)
    }
}

@Composable
fun SignInContent(onButtonClick: (() -> Unit)?) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {
            onButtonClick?.invoke()
        }) {
            Text(text = "Sign In")
        }
    }
}

@Composable
fun SignOutContent(onButtonClick: (() -> Unit)?) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {
            onButtonClick?.invoke()
        }) {
            Text(text = "Sign Out")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSignInScreen() {
    SignInContent(null)
}

@Preview(showBackground = true)
@Composable
fun PreviewSignOutScreen() {
    SignOutContent(null)
}