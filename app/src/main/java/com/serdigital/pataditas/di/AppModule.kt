package com.serdigital.pataditas.di

import android.content.Context
import androidx.room.Room
import com.serdigital.pataditas.data.local.PataditasDatabase
import com.serdigital.pataditas.data.local.dao.KickSessionDao
import com.serdigital.pataditas.data.local.dao.NoteDao
import com.serdigital.pataditas.data.repository.KickSessionRepositoryImpl
import com.serdigital.pataditas.data.repository.NoteRepositoryImpl
import com.serdigital.pataditas.data.repository.StatsRepositoryImpl
import com.serdigital.pataditas.data.repository.FirebaseAuthRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import com.serdigital.pataditas.data.repository.ConfigRepositoryImpl
import com.serdigital.pataditas.domain.repository.AuthRepository
import com.serdigital.pataditas.domain.repository.ConfigRepository
import com.serdigital.pataditas.domain.repository.KickSessionRepository
import com.serdigital.pataditas.domain.repository.NoteRepository
import com.serdigital.pataditas.domain.repository.StatsRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): PataditasDatabase =
        Room.databaseBuilder(
            context,
            PataditasDatabase::class.java,
            PataditasDatabase.DATABASE_NAME
        ).build()

    @Provides
    fun provideKickSessionDao(db: PataditasDatabase): KickSessionDao = db.kickSessionDao()

    @Provides
    fun provideNoteDao(db: PataditasDatabase): NoteDao = db.noteDao()

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth =
        FirebaseAuth.getInstance()
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindKickSessionRepository(impl: KickSessionRepositoryImpl): KickSessionRepository

    @Binds
    @Singleton
    abstract fun bindNoteRepository(impl: NoteRepositoryImpl): NoteRepository

    @Binds
    @Singleton
    abstract fun bindStatsRepository(impl: StatsRepositoryImpl): StatsRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        impl: FirebaseAuthRepositoryImpl
    ): AuthRepository
}

//Modulo de inyeccion para RemoteConfig
@Module
@InstallIn(SingletonComponent::class)
object ConfigModule {

    @Provides
    @Singleton
    fun provideConfigRepository(): ConfigRepository {
        return ConfigRepositoryImpl()
    }
}
