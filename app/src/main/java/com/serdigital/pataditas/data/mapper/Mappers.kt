package com.serdigital.pataditas.data.mapper

import android.os.Build
import androidx.annotation.RequiresApi
import com.serdigital.pataditas.data.local.entity.ContractionEntity
import com.serdigital.pataditas.data.local.entity.KickSessionEntity
import com.serdigital.pataditas.data.local.entity.NoteEntity
import com.serdigital.pataditas.domain.model.Contraction
import com.serdigital.pataditas.domain.model.KickSession
import com.serdigital.pataditas.domain.model.Note
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

@RequiresApi(Build.VERSION_CODES.O)
fun Long.toLocalDateTime(): LocalDateTime =
    LocalDateTime.ofInstant(Instant.ofEpochMilli(this), ZoneId.systemDefault())

@RequiresApi(Build.VERSION_CODES.O)
fun LocalDateTime.toEpochMilli(): Long =
    this.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

// ─── KickSession ─────────────────────────────────────────────────────────────

@RequiresApi(Build.VERSION_CODES.O)
fun KickSessionEntity.toDomain(): KickSession = KickSession(
    id = id,
    remoteId = remoteId,
    startTime = startTime.toLocalDateTime(),
    endTime = endTime?.toLocalDateTime(),
    kickCount = kickCount,
    kickTimestamps = kickTimestamps,
    date = date.toLocalDateTime(),
    durationSeconds = durationSeconds,
    notes = notes,
    isSynced = isSynced
)

@RequiresApi(Build.VERSION_CODES.O)
fun KickSession.toEntity(): KickSessionEntity = KickSessionEntity(
    id = id,
    remoteId = remoteId,
    startTime = startTime.toEpochMilli(),
    endTime = endTime?.toEpochMilli(),
    kickCount = kickCount,
    kickTimestamps = kickTimestamps,
    date = date.toEpochMilli(),
    durationSeconds = durationSeconds,
    notes = notes,
    isSynced = isSynced
)

// ─── Note ────────────────────────────────────────────────────────────────────

@RequiresApi(Build.VERSION_CODES.O)
fun NoteEntity.toDomain(): Note = Note(
    id = id,
    remoteId = remoteId,
    title = title,
    content = content,
    createdAt = createdAt.toLocalDateTime(),
    updatedAt = updatedAt.toLocalDateTime(),
    isSynced = isSynced
)

@RequiresApi(Build.VERSION_CODES.O)
fun Note.toEntity(): NoteEntity = NoteEntity(
    id = id,
    remoteId = remoteId,
    title = title,
    content = content,
    createdAt = createdAt.toEpochMilli(),
    updatedAt = updatedAt.toEpochMilli(),
    isSynced = isSynced
)

@RequiresApi(Build.VERSION_CODES.O)
fun ContractionEntity.toDomain(): Contraction = Contraction(
    id = id,
    startTime = startTime.toLocalDateTime(),
    endTime = endTime?.toLocalDateTime(),
    durationSeconds = durationSeconds,
    intervalFromPreviousSeconds = intervalFromPreviousSeconds,
    sessionId = sessionId,
    notes = notes
)

@RequiresApi(Build.VERSION_CODES.O)
fun Contraction.toEntity(): ContractionEntity = ContractionEntity(
    id = id,
    startTime = startTime.toEpochMilli(),
    endTime = endTime?.toEpochMilli(),
    durationSeconds = durationSeconds,
    intervalFromPreviousSeconds = intervalFromPreviousSeconds,
    sessionId = sessionId,
    notes = notes
)
