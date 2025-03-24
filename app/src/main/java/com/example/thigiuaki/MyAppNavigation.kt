package com.example.thigiuaki

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.thigiuaki.model.Note
import com.example.thigiuaki.pages.AddNote
import com.example.thigiuaki.pages.DashBoard
import com.example.thigiuaki.pages.EditNote
import com.example.thigiuaki.pages.HomePage
import com.example.thigiuaki.pages.LoginPage
import com.example.thigiuaki.pages.SignUpPage
import com.example.thigiuaki.viewModels.AuthViewModel
import com.example.thigiuaki.viewModels.NoteViewModel

@Composable
fun MyAppNavigation(modifier: Modifier = Modifier, authViewModel: AuthViewModel , noteViewModel: NoteViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController , startDestination = Screen.LoginPage.route , builder = {
        composable(Screen.LoginPage.route) {
            LoginPage(modifier , navController , authViewModel)
        }
        composable(Screen.SignUpPage.route) {
            SignUpPage(modifier , navController , authViewModel)
        }
        composable(Screen.HomePage.route) {
            HomePage(modifier , navController , authViewModel)
        }
        composable(Screen.AddNote.route) {
            AddNote(navController , noteViewModel)
        }
        composable(Screen.DashBoard.route) {
            DashBoard(navController , noteViewModel , authViewModel)
        }
        composable(
            route = "${Screen.EditNote.route}/{nid}",
            arguments = listOf(navArgument("nid") { type = NavType.StringType })
        ) { backStackEntry ->
            val nid = backStackEntry.arguments?.getString("nid") ?: return@composable
            val notes by noteViewModel.notes.collectAsState()
            val note = notes.find { it.nid == nid }

            note?.let {
                EditNote(it, noteViewModel, navController)
            }
        }
    })
}