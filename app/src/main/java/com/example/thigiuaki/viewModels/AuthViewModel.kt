package com.example.thigiuaki.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.thigiuaki.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AuthViewModel: ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    init {
        checkAuthStatus()
    }

    fun checkAuthStatus() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            _authState.value = AuthState.Unauthenticated
        } else {
            firestore.collection("users").document(currentUser.uid).get()
                .addOnSuccessListener { document ->
                    val user = document.toObject(User::class.java)
                    if (user != null) {
                        _authState.value = AuthState.Authenticated(user)
                    } else {
                        _authState.value = AuthState.Error("User not found in Firestore")
                    }
                }
                .addOnFailureListener {
                    _authState.value = AuthState.Error("Failed to fetch user data")
                }
        }
    }

    fun login(email: String , password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            _authState.value = AuthState.Error("Email and Password can't be empty")
            return
        }

        _authState.value = AuthState.Loading
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    checkAuthStatus() // Lấy user từ Firestore sau khi đăng nhập
                } else {
                    _authState.value = AuthState.Error(task.exception?.message ?: "Something went wrong")
                }
            }
    }

    fun signup(email: String, password: String, isAdmin: Boolean = false) {
        if (email.isEmpty() || password.isEmpty()) {
            _authState.value = AuthState.Error("Email and Password can't be empty")
            return
        }

        _authState.value = AuthState.Loading
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid ?: return@addOnCompleteListener
                    val role = if (isAdmin) "Admin" else "User"
                    val newUser = User(uid, email, role)

                    // Lưu user vào Firestore
                    firestore.collection("users").document(uid).set(newUser)
                        .addOnSuccessListener {
                            _authState.value = AuthState.Authenticated(newUser)
                        }
                        .addOnFailureListener {
                            _authState.value = AuthState.Error("Failed to save user data")
                        }
                } else {
                    _authState.value = AuthState.Error(task.exception?.message ?: "Something went wrong")
                }
            }
    }

    fun signout() {
        auth.signOut()
        _authState.value = AuthState.Unauthenticated
    }

}

sealed class AuthState {
    data class Authenticated(val user: User) : AuthState()
    object Unauthenticated : AuthState()
    object Loading : AuthState()
    data class Error(val message: String) : AuthState()
}