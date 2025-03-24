//package com.example.thigiuaki.ui.layout
//
//import android.content.Context
//import android.net.Uri
//import android.util.Log
//import androidx.activity.compose.rememberLauncherForActivityResult
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.annotation.StringRes
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.padding
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.unit.dp
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.material3.Button
//import androidx.compose.material3.Text
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.layout.ContentScale
//import coil.compose.rememberAsyncImagePainter
//import com.cloudinary.Cloudinary
//import com.cloudinary.utils.ObjectUtils
//import com.google.firebase.firestore.FirebaseFirestore
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.withContext
//import java.io.File
//import java.io.FileOutputStream
//import java.io.InputStream
//
//@Composable
//fun UploadImageToCloudinaryScreen() {
//    val context = LocalContext.current
//    var imageUri by remember { mutableStateOf<Uri?>(null) }
//    var uploadedImageUrl by remember { mutableStateOf<String?>(null) }
//    var isUploading by remember { mutableStateOf(false) }
//
//    val imagePickerLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.GetContent(),
//        onResult = { uri ->
//            uri?.let { imageUri = it }
//        }
//    )
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp),
//        verticalArrangement = Arrangement.Center
//    ) {
//        imageUri?.let {
//            Image(
//                painter = rememberAsyncImagePainter(it),
//                contentDescription = "Selected Image",
//                modifier = Modifier
//                    .size(150.dp)
//                    .clickable { imagePickerLauncher.launch("image/*") },
//                contentScale = ContentScale.Crop
//            )
//        } ?: Box(
//            modifier = Modifier
//                .size(150.dp)
//                .clickable { imagePickerLauncher.launch("image/*") }
//                .background(Color.Gray, CircleShape),
//        )
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        Button(
//            onClick = {
//                imageUri?.let { uri ->
//                    Log.d("Cloudinary", "Uploading image: $uri") // Kiểm tra Uri ảnh trước khi upload
//                    isUploading = true
//                    uploadImageToCloudinary(context, uri) { url ->
//                        isUploading = false
//                        uploadedImageUrl = url
//
//                        if (url.isNotEmpty()) {
//                            Log.d("Cloudinary", "Image uploaded, saving to Firestore: $url")
//                            saveImageUrlToFirestore(url)
//                        } else {
//                            Log.e("Cloudinary", "Upload failed, not saving to Firestore")
//                        }
//                    }
//                }
//            },
//            enabled = imageUri != null && !isUploading
//        ) {
//            Text(if (isUploading) "Uploading..." else "Upload to Cloudinary")
//        }
//
//        uploadedImageUrl?.let {
//            Spacer(modifier = Modifier.height(16.dp))
//            Text(text = "Image URL: $it", color = Color.Blue)
//        }
//    }
//}
//
///**
// * Chuyển đổi Uri thành File
// */
//fun uriToFile(context: Context, uri: Uri): File? {
//    try {
//        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
//        if (inputStream == null) {
//            Log.e("Cloudinary", "InputStream is null")
//            return null
//        }
//
//        val file = File(context.cacheDir, "temp_image.jpg") // Đổi sang .jpg
//        val outputStream = FileOutputStream(file)
//        inputStream.copyTo(outputStream)
//        outputStream.close()
//        inputStream.close()
//
//        Log.d("Cloudinary", "File saved at: ${file.absolutePath}")
//        return file
//    } catch (e: Exception) {
//        Log.e("Cloudinary", "Error converting URI to file", e)
//        return null
//    }
//}
//
//
///**
// * Upload ảnh lên Cloudinary
// */
//fun uploadImageToCloudinary(context: Context, uri: Uri, onResult: (String) -> Unit) {
//    val cloudinary = Cloudinary(
//        ObjectUtils.asMap(
//            "cloud_name", "dzwuvr52e",
//            "api_key", "322756169612372",
//            "api_secret", "J_gV9KduYlI2SNdFWMrs3sMAlXQ"
//        )
//    )
//
//    val file = uriToFile(context, uri)
//    if (file == null || !file.exists()) {
//        Log.e("Cloudinary", "File does not exist, cannot upload")
//        return
//    }
//
//    CoroutineScope(Dispatchers.IO).launch {
//        try {
//            Log.d("Cloudinary", "Uploading file: ${file.absolutePath}")
//            val response = cloudinary.uploader().upload(file, ObjectUtils.emptyMap())
//            val imageUrl = response["url"] as? String ?: ""
//
//            withContext(Dispatchers.Main) {
//                if (imageUrl.isNotEmpty()) {
//                    Log.d("Cloudinary", "Upload successful: $imageUrl")
//                    onResult(imageUrl)
//                } else {
//                    Log.e("Cloudinary", "Upload failed, empty URL")
//                    onResult("")
//                }
//            }
//        } catch (e: Exception) {
//            Log.e("Cloudinary", "Upload failed", e)
//            withContext(Dispatchers.Main) { onResult("") }
//        }
//    }
//}
//
//
///**
// * Lưu đường link ảnh vào Firebase Firestore
// */
//fun saveImageUrlToFirestore(imageUrl: String) {
//
//    Log.d("Firestore", "Saving image URL: $imageUrl") // Kiểm tra link trước khi lưu vào Firestore
//
//    val firestore = FirebaseFirestore.getInstance()
//    val imageMap = hashMapOf("imageUrl" to imageUrl)
//
//    firestore.collection("images")
//        .add(imageMap)
//        .addOnSuccessListener { Log.d("Firestore", "Image URL saved successfully") }
//        .addOnFailureListener { e -> Log.e("Firestore", "Error saving image URL", e) }
//}