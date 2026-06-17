package com.serdigital.pataditas.ui.screen.history

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.serdigital.pataditas.ui.components.SectionHeader
import com.serdigital.pataditas.ui.components.SessionCard
import com.serdigital.pataditas.ui.theme.*
import com.serdigital.pataditas.ui.viewmodel.HistoryViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HistoryScreen(
    onSessionClick: (Long) -> Unit = {},
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(CieloSuave.copy(0.2f), BlancoRoto)
                )
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 24.dp)
            ) {
                Spacer(Modifier.height(16.dp))
                SectionHeader(
                    title = "Historial",
                    subtitle = "${uiState.sessions.size} sesiones registradas"
                )
            }

            when {
                uiState.isLoading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = CieloProfundo)
                    }
                }

                uiState.sessions.isEmpty() -> {
                    EmptyHistoryState()
                }

                else -> {
                    val grouped = uiState.sessions.groupBy {
                        it.date.toLocalDate()
                    }.toSortedMap(reverseOrder())

                    LazyColumn(
                        contentPadding = PaddingValues(
                            start = 16.dp,
                            end = 16.dp,
                            bottom = 24.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        grouped.forEach { (date, sessions) ->
                            item(key = date.toString()) {
                                Text(
                                    text = formatDate(date),
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextoSecundario,
                                    modifier = Modifier.padding(
                                        start = 8.dp,
                                        top = 16.dp,
                                        bottom = 4.dp
                                    )
                                )
                            }

                            items(
                                items = sessions,
                                key = { it.id }
                            ) { session ->
                                AnimatedVisibility(
                                    visible = true,
                                    enter = slideInHorizontally() + fadeIn()
                                ) {
                                    SessionCard(
                                        kickCount = session.kickCount,
                                        timeLabel = session.startTime.format(
                                            DateTimeFormatter.ofPattern("HH:mm")
                                        ),
                                        duration = session.durationFormatted,
                                        dateLabel = date.format(
                                            DateTimeFormatter.ofPattern("dd/MM")
                                        ),
                                        onDelete = { viewModel.deleteSession(session.id) },
                                        onClick = { onSessionClick(session.id) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyHistoryState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "👣", fontSize = 64.sp)
        Spacer(Modifier.height(16.dp))
        Text(
            text = "Sin sesiones aún",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = TextoPrincipal
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Empezá a contar las patadas de tu bebé\ndesde la pantalla de inicio",
            style = MaterialTheme.typography.bodyMedium,
            color = TextoSecundario,
            textAlign = TextAlign.Center
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun formatDate(date: LocalDate): String {
    val today = LocalDate.now()
    return when (date) {
        today -> "Hoy"
        today.minusDays(1) -> "Ayer"
        else -> date.format(DateTimeFormatter.ofPattern("EEEE dd 'de' MMMM", Locale("es")))
            .replaceFirstChar { it.uppercase() }
    }
}
