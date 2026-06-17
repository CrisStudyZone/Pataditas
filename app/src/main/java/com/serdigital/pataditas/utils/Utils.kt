package com.serdigital.pataditas.utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
object DateUtils {

    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    private val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale("es"))
    private val dayFormatter = DateTimeFormatter.ofPattern("EEEE dd 'de' MMMM", Locale("es"))

    fun LocalDateTime.toTimeString(): String = format(timeFormatter)

    fun LocalDateTime.toDateString(): String = format(dateFormatter)

    fun LocalDate.toDayString(): String {
        val today = LocalDate.now()
        return when (this) {
            today -> "Hoy"
            today.minusDays(1) -> "Ayer"
            else -> format(dayFormatter).replaceFirstChar { it.uppercase() }
        }
    }

    fun Long.toElapsedString(): String {
        val minutes = this / 60
        val seconds = this % 60
        return "%02d:%02d".format(minutes, seconds)
    }
}

object HapticUtils {
    /**
     * Preparado para feedback háptico al registrar patada.
     * Usar con LocalView.current.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
     */
    const val KICK_FEEDBACK = "KICK_FEEDBACK"
    const val SESSION_START_FEEDBACK = "SESSION_START"
    const val SESSION_END_FEEDBACK = "SESSION_END"
}
