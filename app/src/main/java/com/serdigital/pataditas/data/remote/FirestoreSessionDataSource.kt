package com.serdigital.pataditas.data.remote.firestore

import com.google.firebase.firestore.FirebaseFirestore
import com.serdigital.pataditas.data.local.entity.KickSessionEntity
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirestoreSessionDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    suspend fun saveSession(
        email: String,
        session: KickSessionEntity
    ) {

        firestore
            .collection("users")
            .document(email)
            .collection("sessions")
            .document(session.id.toString())
            .set(session)
            .await()

    }

    suspend fun getSessions(email: String): List<KickSessionEntity> {

        return firestore
            .collection("users")
            .document(email)
            .collection("sessions")
            .get()
            .await()
            .documents
            .mapNotNull { it.toObject(KickSessionEntity::class.java) }
    }
}