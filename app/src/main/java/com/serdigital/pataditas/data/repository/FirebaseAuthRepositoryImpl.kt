package com.serdigital.pataditas.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.serdigital.pataditas.domain.repository.AuthRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton



@Singleton
class FirebaseAuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {

    override val isLoggedIn: Boolean
        get() = firebaseAuth.currentUser != null

    override val currentUserId: String?
        get() = firebaseAuth.currentUser?.uid

    override suspend fun signInWithEmail(
        email: String,
        password: String
    ): Result<Unit> {
        return try {
            firebaseAuth
                .signInWithEmailAndPassword(email, password)
                .await()

            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signUpWithEmail(
        email: String,
        password: String
    ): Result<Unit> {
        return try {
            firebaseAuth
                .createUserWithEmailAndPassword(email, password)
                .await()

            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun sendPasswordReset(
        email: String
    ): Result<Unit> {
        return try {
            firebaseAuth
                .sendPasswordResetEmail(email)
                .await()

            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signOut() {
        firebaseAuth.signOut()
    }
}