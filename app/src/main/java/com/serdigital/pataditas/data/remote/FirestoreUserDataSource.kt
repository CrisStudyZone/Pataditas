package com.serdigital.pataditas.data.remote

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirestoreUserDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    suspend fun createUser(email: String) {

        val user = hashMapOf(
            "email" to email,
            "createdAt" to System.currentTimeMillis()
        )

        firestore
            .collection("users")
            .document(email)
            .set(user)
            .await()
    }
}