package com.example.thigiuaki.viewModels

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import com.cloudinary.Cloudinary
import com.cloudinary.utils.ObjectUtils
import com.example.thigiuaki.model.Note
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class NoteViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    val notes:StateFlow<List<Note>> = _notes.asStateFlow()

    private val cloudinary = Cloudinary (
        ObjectUtils.asMap(
            "cloud_name", "dzwuvr52e",
            "api_key", "322756169612372",
            "api_secret", "J_gV9KduYlI2SNdFWMrs3sMAlXQ"
        )
    )

    init {
        loadNotes()
    }

    private fun loadNotes() {
        firestore.collection("notes")
            .orderBy("nid" , Query.Direction.DESCENDING)
            .addSnapshotListener { value , error ->
                if(error != null) {
                    Log.e("firestore" , "Error loading notes" , error)
                    return@addSnapshotListener
                }

                val noteList = value?.documents?.mapNotNull { doc ->
                    doc.toObject(Note::class.java)
                } ?: emptyList()

                _notes.value = noteList
            }
    }

    fun addNote(
        title: String,
        description: String,
        imageUri: Uri?,
        context: Context,
        onComplete: () -> Unit
    ){
        if(title.isEmpty() || description.isEmpty()) {
            Log.e("NoteViewModel" , "Title and description cannot be empty")
            return
        }

        if(imageUri == null) {
            saveNoteToFirebase(title , description , "" , onComplete)
        } else {
            uploadImageToCloudinary(imageUri , context) { imageUrl ->
                if(imageUrl.isNotEmpty()){
                    saveNoteToFirebase(title , description , imageUrl , onComplete)
                }else {
                    Log.e("NoteViewModel" , "Image upload failed")
                }
            }
        }
    }

    private fun uploadImageToCloudinary(uri: Uri, context: Context, onResult: (String) -> Unit) {
        val file = uriToFile(context, uri)
        if (file == null || !file.exists()) {
            Log.e("Cloudinary", "File does not exist, cannot upload")
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = cloudinary.uploader().upload(file, ObjectUtils.asMap("secure", true))
                val imageUrl = response["secure_url"] as? String ?: "" // secure_url là lưu ảnh thành https://

                Log.d("Cloudinary", "Image uploaded: $imageUrl")

                withContext(Dispatchers.Main) {
                    onResult(imageUrl)
                }
            } catch (e: Exception) {
                Log.e("Cloudinary", "Upload failed", e)
                withContext(Dispatchers.Main) { onResult("") }
            }
        }
    }

    private fun uriToFile(context : Context , uri: Uri): File? {
        try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            if(inputStream == null) return null

            val file = File(context.cacheDir , "temp_image.jpg")
            val outputStream = FileOutputStream(file)
            inputStream.copyTo(outputStream)
            outputStream.close()
            inputStream.close()

            return file
        } catch (e : Exception) {
            Log.e("Cloudinary" , "Error converting URI to file" , e)
            return null
        }
    }

    private fun saveNoteToFirebase(
        title: String ,
        description: String,
        imageUrl: String,
        onComplete: () -> Unit
    ){
        val noteId = firestore.collection("notes").document().id
        val note = Note(nid = noteId , title = title , description = description , image_url = imageUrl)

        firestore.collection("notes").document(noteId)
            .set(note)
            .addOnSuccessListener {
                Log.e("Firestore" , "Note added successfully")
                onComplete()
            }
            .addOnFailureListener{ e ->
                Log.e("Firestore" , "Note added fail " , e)
            }
    }

    // update //

    fun updateNote(nid: String, title: String, description: String, imageUri: Uri?, oldImageUrl: String, context: Context, onComplete: () -> Unit) {
        if (title.isBlank() || description.isBlank()) {
            Log.e("NoteViewModel", "Title and description cannot be empty")
            return
        }

        if (imageUri == null) {
            // Không thay đổi ảnh, giữ nguyên URL cũ
            saveUpdatedNote(nid, title, description, oldImageUrl, onComplete)
        } else {
            uploadImageToCloudinary(imageUri, context) { newImageUrl ->
                if (newImageUrl.isNotEmpty()) {
                    saveUpdatedNote(nid, title, description, newImageUrl, onComplete)
                } else {
                    Log.e("NoteViewModel", "Image upload failed")
                }
            }
        }
    }

    private fun saveUpdatedNote(nid: String, title: String, description: String, imageUrl: String, onComplete: () -> Unit) {
        val updatedNote = mapOf(
            "title" to title,
            "description" to description,
            "image_url" to imageUrl
        )

        firestore.collection("notes").document(nid)
            .update(updatedNote)
            .addOnSuccessListener {
                Log.d("Firestore", "Note updated successfully")
                onComplete()
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Failed to update note", e)
            }
    }

    // delete //

    fun deleteNote(nid: String, onComplete: () -> Unit) {
        firestore.collection("notes").document(nid)
            .delete()
            .addOnSuccessListener {
                Log.d("Firestore", "Note deleted successfully")
                onComplete()
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Failed to delete note", e)
            }
    }

}