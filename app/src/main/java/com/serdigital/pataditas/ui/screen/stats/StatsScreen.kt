package com.serdigital.pataditas.ui.screen.stats

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.serdigital.pataditas.domain.model.DailyStats
import com.serdigital.pataditas.domain.model.HourlyActivity
import com.serdigital.pataditas.ui.components.SectionHeader
import com.serdigital.pataditas.ui.components.StatCard
import com.serdigital.pataditas.ui.theme.*
import com.serdigital.pataditas.ui.viewmodel.StatsViewModel
import java.time.format.DateTimeFormatter
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun StatsScreen(
    viewModel: StatsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(LavandaSuave.copy(0.3f), BlancoRoto)
                )
            )
    ) {
        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = CieloProfundo)
            }
            return@Box
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(40.dp))

            SectionHeader(
                title = "Estadísticas",
                subtitle = "Últimos 5 días de actividad"
            )

            Spacer(Modifier.height(24.dp))

            if (uiState.dailyStats.isEmpty()) {
                EmptyStatsState()
            } else {
                // ─── Resumen global ───────────────────────────────────────
                val totalKicks = uiState.dailyStats.sumOf { it.totalKicks }
                val totalSessions = uiState.dailyStats.sumOf { it.sessionCount }
                val avgPerSession = if (totalSessions > 0) totalKicks / totalSessions else 0

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        label = "Total patadas",
                        value = "$totalKicks",
                        emoji = "👣",
                        color = CieloSuave,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        label = "Sesiones",
                        value = "$totalSessions",
                        emoji = "📋",
                        color = LavandaSuave,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        label = "Promedio",
                        value = "$avgPerSession",
                        emoji = "⚡",
                        color = RosadoSuave,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(Modifier.height(28.dp))

                // ─── Actividad por día ────────────────────────────────────
                DailyActivitySection(dailyStats = uiState.dailyStats)

                Spacer(Modifier.height(28.dp))

                // ─── Mapa de calor horario ────────────────────────────────
                HourlyActivitySection(hourlyActivity = uiState.hourlyActivity)

                Spacer(Modifier.height(28.dp))

                // ─── Comparación primera vs última ────────────────────────
                FirstVsLastSection(dailyStats = uiState.dailyStats)

                Spacer(Modifier.height(32.dp))
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun DailyActivitySection(dailyStats: List<DailyStats>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SuperficieCard),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Patadas por día",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Últimos ${dailyStats.size} días",
                style = MaterialTheme.typography.bodySmall,
                color = TextoSecundario
            )
            Spacer(Modifier.height(16.dp))

            val maxKicks = dailyStats.maxOf { it.totalKicks }.coerceAtLeast(1)
            val dayColors = listOf(
                CieloProfundo, LavandaMedio, RosadoMedio, Verde, Ambar
            )

            dailyStats.take(5).reversed().forEachIndexed { index, stats ->
                val dayLabel = stats.date.format(
                    DateTimeFormatter.ofPattern("EEE dd", Locale("es"))
                ).replaceFirstChar { it.uppercase() }
                val barFraction = stats.totalKicks.toFloat() / maxKicks
                val color = dayColors[index % dayColors.size]

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = dayLabel,
                        style = MaterialTheme.typography.labelMedium,
                        color = TextoSecundario,
                        modifier = Modifier.width(52.dp)
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(20.dp)
                            .background(GrisClaro, RoundedCornerShape(10.dp))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(barFraction.coerceAtLeast(0.02f))
                                .fillMaxHeight()
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(color.copy(alpha = 0.7f), color)
                                    ),
                                    RoundedCornerShape(10.dp)
                                )
                        )
                    }

                    Text(
                        text = "${stats.totalKicks}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = color,
                        modifier = Modifier
                            .width(36.dp)
                            .padding(start = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun HourlyActivitySection(hourlyActivity: List<HourlyActivity>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SuperficieCard),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Horarios de mayor actividad",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Últimos 30 días",
                style = MaterialTheme.typography.bodySmall,
                color = TextoSecundario
            )
            Spacer(Modifier.height(16.dp))

            // Top 3 horas más activas
            val topHours = hourlyActivity
                .filter { it.totalKicks > 0 }
                .sortedByDescending { it.totalKicks }
                .take(3)

            if (topHours.isEmpty()) {
                Text(
                    "Sin datos suficientes",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextoSecundario
                )
            } else {
                topHours.forEachIndexed { index, activity ->
                    val medal = listOf("🥇", "🥈", "🥉")[index]
                    val hourLabel = "%02d:00 - %02d:00".format(activity.hour, (activity.hour + 1) % 24)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(medal, fontSize = 20.sp)
                            Spacer(Modifier.width(8.dp))
                            Text(
                                hourLabel,
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextoPrincipal
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = CieloSuave.copy(0.5f)
                        ) {
                            Text(
                                text = "${activity.totalKicks} patadas",
                                style = MaterialTheme.typography.labelMedium,
                                color = CieloProfundo,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // Mapa de calor visual (24h)
            Text(
                text = "Distribución 24 horas",
                style = MaterialTheme.typography.labelSmall,
                color = TextoSecundario
            )
            Spacer(Modifier.height(8.dp))

            val maxActivity = hourlyActivity.maxOf { it.totalKicks }.coerceAtLeast(1)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                hourlyActivity.forEach { activity ->
                    val intensity = activity.totalKicks.toFloat() / maxActivity
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(32.dp)
                            .background(
                                CieloProfundo.copy(alpha = intensity.coerceAtLeast(0.05f)),
                                RoundedCornerShape(4.dp)
                            )
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("0h", style = MaterialTheme.typography.labelSmall, color = TextoSecundario)
                Text("12h", style = MaterialTheme.typography.labelSmall, color = TextoSecundario)
                Text("23h", style = MaterialTheme.typography.labelSmall, color = TextoSecundario)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun FirstVsLastSection(dailyStats: List<DailyStats>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SuperficieCard),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Primera vs última sesión",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Comparación diaria",
                style = MaterialTheme.typography.bodySmall,
                color = TextoSecundario
            )
            Spacer(Modifier.height(16.dp))

            val daysFmt = DateTimeFormatter.ofPattern("dd/MM", Locale("es"))
            val daysWithBoth = dailyStats.filter {
                it.firstSession != null && it.lastSession != null
            }

            if (daysWithBoth.isEmpty()) {
                Text(
                    "Necesitás al menos 2 sesiones por día\npara ver esta comparación",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextoSecundario
                )
            } else {
                // Cabecera
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "Día",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextoSecundario,
                        modifier = Modifier.width(52.dp)
                    )
                    Text(
                        "Primera 🌅",
                        style = MaterialTheme.typography.labelSmall,
                        color = CieloProfundo,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        "Última 🌙",
                        style = MaterialTheme.typography.labelSmall,
                        color = LavandaMedio,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(Modifier.height(8.dp))
                Divider(color = GrisClaro)
                Spacer(Modifier.height(8.dp))

                daysWithBoth.take(5).forEach { stats ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            stats.date.format(daysFmt),
                            style = MaterialTheme.typography.bodySmall,
                            color = TextoSecundario,
                            modifier = Modifier.width(52.dp)
                        )

                        val firstKicks = stats.firstSession?.kickCount ?: 0
                        val lastKicks = stats.lastSession?.kickCount ?: 0
                        val maxForDay = maxOf(firstKicks, lastKicks).coerceAtLeast(1)

                        Column(modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(firstKicks.toFloat() / maxForDay)
                                    .height(12.dp)
                                    .background(CieloProfundo.copy(0.6f), RoundedCornerShape(6.dp))
                            )
                            Text(
                                "$firstKicks",
                                style = MaterialTheme.typography.labelSmall,
                                color = CieloProfundo
                            )
                        }

                        Spacer(Modifier.width(8.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(lastKicks.toFloat() / maxForDay)
                                    .height(12.dp)
                                    .background(LavandaMedio.copy(0.6f), RoundedCornerShape(6.dp))
                            )
                            Text(
                                "$lastKicks",
                                style = MaterialTheme.typography.labelSmall,
                                color = LavandaMedio
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyStatsState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 64.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("📊", fontSize = 64.sp)
        Spacer(Modifier.height(16.dp))
        Text(
            "Sin datos todavía",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Las estadísticas aparecerán\ncuando completes tus primeras sesiones",
            style = MaterialTheme.typography.bodyMedium,
            color = TextoSecundario,
            textAlign = TextAlign.Center
        )
    }
}
