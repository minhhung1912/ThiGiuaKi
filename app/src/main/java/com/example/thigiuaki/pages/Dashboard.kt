package com.example.thigiuaki.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.thigiuaki.Screen
import com.example.thigiuaki.model.Note
import com.example.thigiuaki.ui.theme.ThiGiuaKiTheme
import com.example.thigiuaki.viewModels.AuthState
import com.example.thigiuaki.viewModels.AuthViewModel
import com.example.thigiuaki.viewModels.NoteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashBoard(navController: NavController , noteViewModel: NoteViewModel , authViewModel: AuthViewModel) {

    val notes by noteViewModel.notes.collectAsState()

    val authState by authViewModel.authState.observeAsState()

    var showLogoutDialog by remember { mutableStateOf(false) }

    // Kiểm tra trạng thái đăng xuất
    LaunchedEffect(authState) {
        if (authState is AuthState.Unauthenticated) {
            navController.navigate(Screen.LoginPage.route) {
                popUpTo(0) // Xóa hết màn hình trước đó
            }
        }
    }

    // dialog hiển thị chấp nhận hay không
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false }, // Đóng dialog khi nhấn ra ngoài
            title = { Text("Xác nhận đăng xuất") },
            text = { Text("Bạn có chắc chắn muốn đăng xuất không?") },
            confirmButton = {
                TextButton(onClick = {
                    authViewModel.signout()
                    showLogoutDialog = false
                }) {
                    Text("Đăng xuất", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Hủy")
                }
            }
        )
    }
    //------------------------------

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.AddNote.route) },
                containerColor = Color(0xFFFF9800)
            ) {
                Text("+", fontSize = 24.sp, color = Color.White)
            }
        },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "NOTE APP" , color = Color(0xFFFFC107)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF263238)
                ),
                actions = {
                    IconButton(onClick = {
                        showLogoutDialog = true
                    }) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp, // Biểu tượng Xoa
                            contentDescription = "xoa",
                            tint = Color(0xFFFF070F)
                        )
                    }
                }
            )
        }
    ) {  paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF263238))
                .padding(paddingValues),
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(notes) {note ->
                    ListItems(note = note , navController = navController)
                }
            }
        }
    }
}

@Composable
fun ListItems(note: Note , navController: NavController) {
    Card(
        modifier = Modifier.fillMaxWidth()
            .padding(8.dp)
            .clickable {
                navController.navigate("${Screen.EditNote.route}/${note.nid}")
            },
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Column {
                Text(text = note.title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(text = note.description, fontSize = 14.sp, color = Color.Gray)
            }
            Spacer(modifier = Modifier.width(16.dp))
            AsyncImage(
                model = note.image_url,
                contentDescription = "Note Image",
                modifier = Modifier.fillMaxWidth().height(200.dp)
            )
        }
    }
    
}


@Preview(showBackground = true)
@Composable
fun DashBoardPreview() {
    ThiGiuaKiTheme {

    }
}