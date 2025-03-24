package com.example.thigiuaki

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.thigiuaki.viewModels.AuthViewModel
import com.example.thigiuaki.viewModels.NoteViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val authViewModel : AuthViewModel by viewModels()
        val noteViewModel : NoteViewModel by viewModels()
        setContent {
            Scaffold(
            ) { paddingValue ->
                MyAppNavigation(modifier = Modifier.padding(paddingValue) ,authViewModel = authViewModel , noteViewModel=  noteViewModel)
            }
        }
    }
}

