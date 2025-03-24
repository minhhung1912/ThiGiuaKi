package com.example.thigiuaki.pages

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.thigiuaki.R
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
        modifier = Modifier.fillMaxSize().background(Color(0xFF263238)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.note_book),
            contentDescription = "noteBook"
        )

        Text(text = " Sign Up " , fontSize = 32.sp , color = Color(0xFFFFC107))

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
            },
            placeholder = {
                Text(text = "Email", color = Color.White.copy(alpha = 0.5f))
            },
            shape = CircleShape,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp),
            colors = TextFieldDefaults.colors(
                focusedTextColor = Color.White,       // Màu chữ khi đang nhập
                unfocusedTextColor = Color(0xFFFFC107), // Màu chữ khi không nhập
                focusedContainerColor = Color(0xFF263238), // Màu nền khi trỏ vào
                unfocusedContainerColor = Color(0xFF263238), // Màu nền khi không trỏ vào
                focusedIndicatorColor = Color(0xFFFFC107),
                unfocusedIndicatorColor = Color.White
            )
        )
        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
            },
            placeholder = {
                Text(text = "Password" ,color = Color.White.copy(alpha = 0.5f))
            },
            shape = CircleShape,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp),
            colors = TextFieldDefaults.colors(
                focusedTextColor = Color.White,       // Màu chữ khi đang nhập
                unfocusedTextColor = Color(0xFFFFC107), // Màu chữ khi không nhập
                focusedContainerColor = Color(0xFF263238), // Màu nền khi trỏ vào
                unfocusedContainerColor = Color(0xFF263238), // Màu nền khi không trỏ vào
                focusedIndicatorColor = Color(0xFFFFC107),
                unfocusedIndicatorColor = Color.White
            )
        )
        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                authViewModel.signup(email , password)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 40.dp, end = 40.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFFC107), // Màu nền button
                contentColor = Color.White,        // Màu chữ
                disabledContainerColor = Color.Gray,  // Màu nền khi bị vô hiệu hóa
                disabledContentColor = Color.LightGray // Màu chữ khi bị vô hiệu hóa
            )
        ) {
            Text(text = "Create account" , fontSize = 20.sp, modifier = Modifier.padding(8.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ){
            Text(text="Already have an account , " , fontSize = 16.sp,  color = Color.White)
            Text(
                text = "Login",
                fontSize = 16.sp,
                modifier = Modifier.clickable {
                    navController.navigate(Screen.LoginPage.route)
                },
                color = Color(0xFFFFC107)
            )
        }

    }
}