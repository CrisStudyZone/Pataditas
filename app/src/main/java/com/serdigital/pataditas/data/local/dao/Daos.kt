package com.serdigital.pataditas.data.local.dao

import androidx.room.*
import com.serdigital.pataditas.data.local.entity.ContractionEntity
import com.serdigital.pataditas.data.local.entity.KickSessionEntity
import com.serdigital.pataditas.data.local.entity.NoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface KickSessionDao {

    @Query("SELECT * FROM kick_sessions ORDER BY date DESC")
    fun getAllSessions(): Flow<List<KickSessionEntity>>

    @Query("SELECT * FROM kick_sessions WHERE id = :id")
    suspend fun getSessionById(id: Long): KickSessionEntity?

    @Query("""
        SELECT * FROM kick_sessions 
        WHERE date >= :startOfDay AND date <= :endOfDay
        ORDER BY startTime ASC
    """)
    fun getSessionsByDay(startOfDay: Long, endOfDay: Long): Flow<List<KickSessionEntity>>

    @Query("""
        SELECT * FROM kick_sessions 
        WHERE date >= :fromDate
        ORDER BY startTime ASC
    """)
    fun getSessionsFromDate(fromDate: Long): Flow<List<KickSessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: KickSessionEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllSessions(sessions: List<KickSessionEntity>)

    @Update
    suspend fun updateSession(session: KickSessionEntity)

    @Delete
    suspend fun deleteSession(session: KickSessionEntity)

    @Query("DELETE FROM kick_sessions WHERE id = :id")
    suspend fun deleteSessionById(id: Long)

    @Query("SELECT COUNT(*) FROM kick_sessions")
    suspend fun getTotalSessionCount(): Int

    @Query("SELECT * FROM kick_sessions WHERE isSynced = 0")
    suspend fun getUnsyncedSessions(): List<KickSessionEntity>

    @Query("UPDATE kick_sessions SET isSynced = 1, remoteId = :remoteId WHERE id = :localId")
    suspend fun markAsSynced(localId: Long, remoteId: String)

    @Query("DELETE FROM kick_sessions")
    suspend fun deleteAllSessions()
}

@Dao
interface NoteDao {

    @Query("SELECT * FROM notes ORDER BY updatedAt DESC")
    fun getAllNotes(): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE id = :id")
    suspend fun getNoteById(id: Long): NoteEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NoteEntity): Long

    @Update
    suspend fun updateNote(note: NoteEntity)

    @Delete
    suspend fun deleteNote(note: NoteEntity)

    @Query("DELETE FROM notes WHERE id = :id")
    suspend fun deleteNoteById(id: Long)

    @Query("SELECT * FROM notes WHERE isSynced = 0")
    suspend fun getUnsyncedNotes(): List<NoteEntity>
}

@Dao
interface ContractionDao {

    @Query("SELECT * FROM contractions WHERE sessionId = :sessionId ORDER BY startTime ASC")
    fun getContractionsBySession(sessionId: String): Flow<List<ContractionEntity>>

    @Query("SELECT * FROM contractions ORDER BY startTime DESC")
    fun getAllContractions(): Flow<List<ContractionEntity>>

    @Query("SELECT DISTINCT sessionId FROM contractions ORDER BY startTime DESC")
    fun getAllSessionIds(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContraction(contraction: ContractionEntity): Long

    @Update
    suspend fun updateContraction(contraction: ContractionEntity)

    @Query("DELETE FROM contractions WHERE sessionId = :sessionId")
    suspend fun deleteSession(sessionId: String)

    @Query("SELECT * FROM contractions WHERE sessionId = :sessionId ORDER BY startTime DESC LIMIT 1")
    suspend fun getLastContractionInSession(sessionId: String): ContractionEntity?

    @Query("SELECT * FROM contractions WHERE id = :id")
    suspend fun getContractionById(id: Long): ContractionEntity?
}
