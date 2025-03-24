package com.example.thigiuaki.pages

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.thigiuaki.Screen
import com.example.thigiuaki.viewModels.AuthState
import com.example.thigiuaki.viewModels.AuthViewModel

@Composable
fun SignUpPage(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val authState = authViewModel.authState.observeAsState()
    val context = LocalContext.current

    LaunchedEffect(authState.value) {
        when(authState.value) {
            is AuthState.Authenticated -> navController.navigate(Screen.HomePage.route)
            is AuthState.Error -> Toast.makeText(context,
                (authState.value as AuthState.Error).message, Toast.LENGTH_LONG ).show()
            else -> Unit
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Sign Up Page" , fontSize = 32.sp)

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
            },
            placeholder = {
                Text(text = "Email")
            },
            shape = CircleShape,
            modifier = Modifier.fillMaxWidth().padding(start = 20.dp , end = 20.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
            },
            placeholder = {
                Text(text = "Password")
            },
            shape = CircleShape,
            modifier = Modifier.fillMaxWidth().padding(start = 20.dp , end = 20.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                authViewModel.signup(email , password)
            },
            modifier = Modifier
                .fillMaxWidth().padding(start = 45.dp , end = 45.dp)
        ) {
            Text(text = "Create account")
        }

        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ){
            Text(text="Already have an account , ")
            Text(
                text = "Login",

                modifier = Modifier.clickable {
                    navController.navigate(Screen.LoginPage.route)
                },
                color = MaterialTheme.colorScheme.primary
            )
        }

    }
}