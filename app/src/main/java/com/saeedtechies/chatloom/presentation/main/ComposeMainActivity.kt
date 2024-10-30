package com.saeedtechies.chatloom.presentation.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.saeedtechies.chatloom.presentation.auth.AuthViewModel
import com.saeedtechies.chatloom.presentation.auth.SignIn
import com.saeedtechies.chatloom.presentation.auth.SignInScreen
import com.saeedtechies.chatloom.presentation.auth.SignUp
import com.saeedtechies.chatloom.presentation.auth.SignUpScreen
import com.saeedtechies.chatloom.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ComposeMainActivity : ComponentActivity() {

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = SignIn
                ) {
                    composable<SignIn> {
                        SignInScreen(navController, authViewModel)
                    }
                    composable<SignUp> {
                        SignUpScreen(navController, authViewModel)
                    }
                }
            }
        }
    }
}