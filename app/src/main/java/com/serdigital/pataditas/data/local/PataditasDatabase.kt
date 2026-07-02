package com.serdigital.pataditas.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.serdigital.pataditas.data.local.dao.ContractionDao
import com.serdigital.pataditas.data.local.dao.KickSessionDao
import com.serdigital.pataditas.data.local.dao.NoteDao
import com.serdigital.pataditas.data.local.entity.ContractionEntity
import com.serdigital.pataditas.data.local.entity.KickSessionEntity
import com.serdigital.pataditas.data.local.entity.NoteEntity

@Database(
    entities = [
        KickSessionEntity::class,
        NoteEntity::class,
        ContractionEntity::class
    ],
    version = 4,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class PataditasDatabase : RoomDatabase() {

    abstract fun kickSessionDao(): KickSessionDao
    abstract fun noteDao(): NoteDao
    abstract fun contractionDao(): ContractionDao

    companion object {
        const val DATABASE_NAME = "pataditas_db"
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
            CREATE TABLE IF NOT EXISTS contractions (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                startTime INTEGER NOT NULL,
                endTime INTEGER,
                durationSeconds INTEGER,
                intervalFromPreviousSeconds INTEGER,
                sessionId TEXT NOT NULL,
                notes TEXT,
                createdAt INTEGER NOT NULL DEFAULT 0
            )
        """)
            }
        }
    }
}
