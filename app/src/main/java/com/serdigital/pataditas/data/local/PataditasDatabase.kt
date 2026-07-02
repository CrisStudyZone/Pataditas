package com.serdigital.pataditas.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.serdigital.pataditas.data.local.dao.KickSessionDao
import com.serdigital.pataditas.data.local.dao.NoteDao
import com.serdigital.pataditas.data.local.entity.KickSessionEntity
import com.serdigital.pataditas.data.local.entity.NoteEntity

@Database(
    entities = [
        KickSessionEntity::class,
        NoteEntity::class
    ],
    version = 4,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class PataditasDatabase : RoomDatabase() {

    abstract fun kickSessionDao(): KickSessionDao
    abstract fun noteDao(): NoteDao

    companion object {
        const val DATABASE_NAME = "pataditas_db"
    }
}
