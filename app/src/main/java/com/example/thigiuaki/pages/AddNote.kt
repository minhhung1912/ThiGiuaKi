package com.example.thigiuaki.pages

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.thigiuaki.ui.theme.ThiGiuaKiTheme
import com.example.thigiuaki.viewModels.NoteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNote( navController: NavController , noteViewModel: NoteViewModel) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current
    val imagePicker =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
            imageUri = uri
        }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "ADD NOTE" , color = Color(0xFFFFC107)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF263238)
                ),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFFFFC107)
                        )
                    }
                },
            )
        }
    ) { paddingValue ->
        Box(modifier = Modifier.padding(paddingValue)){
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF263238))
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Title", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.White,       // Màu chữ khi đang nhập
                        unfocusedTextColor = Color(0xFFFFC107), // Màu chữ khi không nhập
                        focusedContainerColor = Color(0xFF263238), // Màu nền khi trỏ vào
                        unfocusedContainerColor = Color(0xFF263238), // Màu nền khi không trỏ vào
                        focusedIndicatorColor = Color(0xFFFFC107),
                        unfocusedIndicatorColor = Color.White
                    ),
                    placeholder = {
                        Text(text = "Title" , color = Color.White.copy(alpha = 0.5f) )
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))
                Text("Description", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .size(200.dp),
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.White,       // Màu chữ khi đang nhập
                        unfocusedTextColor = Color(0xFFFFC107), // Màu chữ khi không nhập
                        focusedContainerColor = Color(0xFF263238), // Màu nền khi trỏ vào
                        unfocusedContainerColor = Color(0xFF263238), // Màu nền khi không trỏ vào
                        focusedIndicatorColor = Color(0xFFFFC107),
                        unfocusedIndicatorColor = Color.White
                    ),
                    placeholder = {
                        Text(text = "Description" , color = Color.White.copy(alpha = 0.5f))
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))
                Text("Image", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .size(200.dp)
                        .background(Color.Gray, shape = RoundedCornerShape(8.dp))
                        .clickable { imagePicker.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (imageUri != null) {
                        Image(
                            painter = rememberAsyncImagePainter(imageUri),
                            contentDescription = "Selected Image",
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Text("Tap to select image", color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(50.dp))
                Button(
                    onClick = {
                        noteViewModel.addNote(title , description , imageUri , context) {
                            navController.popBackStack()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800))
                ) {
                    Text("ADD ITEM", color = Color.White, fontSize = 16.sp)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddNotePreview() {
    ThiGiuaKiTheme {

    }
}
