package com.serdigital.pataditas.ui.screen.history

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.serdigital.pataditas.ui.components.StatCard
import com.serdigital.pataditas.ui.theme.*
import com.serdigital.pataditas.ui.viewmodel.HistoryViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Pantalla de detalle de una sesión individual.
 * Muestra todos los timestamps de patadas, duración y métricas.
 */
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionDetailScreen(
    sessionId: Long,
    onBack: () -> Unit,
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val session = uiState.sessions.find { it.id == sessionId }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de sesión", style = MaterialTheme.typography.titleMedium) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = Color.Transparent
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(listOf(CieloSuave.copy(0.3f), BlancoRoto))
                )
                .padding(innerPadding)
        ) {
            if (session == null) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = CieloProfundo)
                }
                return@Box
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                // ─── Encabezado ───────────────────────────────────────────
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = SuperficieCard),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(listOf(CieloSuave, LavandaSuave))
                                )
                        ) {
                            Text("👣", fontSize = 32.sp)
                        }

                        Spacer(Modifier.height(12.dp))

                        Text(
                            text = "${session.kickCount} patadas",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextoPrincipal
                        )

                        val dateStr = session.date.format(
                            DateTimeFormatter.ofPattern("EEEE dd 'de' MMMM", Locale("es"))
                        ).replaceFirstChar { it.uppercase() }
                        Text(
                            text = dateStr,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextoSecundario
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                // ─── Métricas ─────────────────────────────────────────────
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        label = "Duración",
                        value = session.durationFormatted,
                        emoji = "⏱",
                        color = CieloSuave,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        label = "Inicio",
                        value = session.startTime.format(DateTimeFormatter.ofPattern("HH:mm")),
                        emoji = "🌅",
                        color = LavandaSuave,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        label = "Fin",
                        value = session.endTime?.format(DateTimeFormatter.ofPattern("HH:mm")) ?: "--",
                        emoji = "🌙",
                        color = RosadoSuave,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Intervalo promedio
                session.averageIntervalSeconds?.let { avg ->
                    Spacer(Modifier.height(10.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = SuperficieElevada),
                        elevation = CardDefaults.cardElevation(0.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "Intervalo promedio entre patadas",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextoSecundario
                            )
                            Text(
                                "%.0fs".format(avg),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = CieloProfundo
                            )
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))

                // ─── Timeline de patadas ──────────────────────────────────
                if (session.kickTimestamps.isNotEmpty()) {
                    Text(
                        text = "Timeline de movimientos",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = TextoPrincipal,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = SuperficieCard),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            session.kickTimestamps.forEachIndexed { index, timestamp ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Número de patada
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(CieloSuave.copy(alpha = 0.5f))
                                    ) {
                                        Text(
                                            "${index + 1}",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = CieloProfundo
                                        )
                                    }

                                    Spacer(Modifier.width(12.dp))

                                    Text(
                                        text = Instant.ofEpochMilli(timestamp)
                                            .atZone(ZoneId.systemDefault())
                                            .format(DateTimeFormatter.ofPattern("HH:mm:ss")),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = TextoPrincipal
                                    )

                                    Spacer(Modifier.weight(1f))

                                    // Intervalo desde anterior
                                    if (index > 0) {
                                        val prevTs = session.kickTimestamps[index - 1]
                                        val intervalSec = (timestamp - prevTs) / 1000
                                        Text(
                                            "+${intervalSec}s",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = GrisMedio
                                        )
                                    }
                                }

                                if (index < session.kickTimestamps.lastIndex) {
                                    Divider(
                                        color = GrisClaro,
                                        modifier = Modifier.padding(start = 40.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(32.dp))
            }
        }
    }
}
