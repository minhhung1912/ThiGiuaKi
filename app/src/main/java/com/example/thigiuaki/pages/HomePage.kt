package com.example.thigiuaki.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.thigiuaki.Screen
import com.example.thigiuaki.viewModels.AuthState
import com.example.thigiuaki.viewModels.AuthViewModel

@Composable
fun HomePage(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel
) {
    val authState = authViewModel.authState.observeAsState()

    LaunchedEffect(authState.value) {
        if (authState.value is AuthState.Unauthenticated) {
            navController.navigate(Screen.LoginPage.route) {
                popUpTo(Screen.HomePage.route) { inclusive = true }
            }
        }
    }

    val user = (authState.value as? AuthState.Authenticated)?.user

    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Home Page", fontSize = 32.sp)
        Text(text = "Welcome, ${user?.email ?: "Guest"}")

        if (user?.role == "Admin") {
            Text(text = "You are an Admin", color = Color.Red, fontSize = 20.sp)
        } else {
            Text(text = "You are a User", color = Color.Blue, fontSize = 20.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = { authViewModel.signout() }
        ) {
            Text(text = "Sign out")
        }
    }
}
