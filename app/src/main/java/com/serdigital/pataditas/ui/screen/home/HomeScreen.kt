package com.serdigital.pataditas.ui.screen.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent
import com.serdigital.pataditas.ui.components.KickButton
import com.serdigital.pataditas.ui.components.SessionStatusChip
import com.serdigital.pataditas.ui.components.StatCard
import com.serdigital.pataditas.ui.theme.BlancoRoto
import com.serdigital.pataditas.ui.theme.CieloProfundo
import com.serdigital.pataditas.ui.theme.CieloSuave
import com.serdigital.pataditas.ui.theme.GrisClaro
import com.serdigital.pataditas.ui.theme.GrisMedio
import com.serdigital.pataditas.ui.theme.LavandaSuave
import com.serdigital.pataditas.ui.theme.RosadoSuave
import com.serdigital.pataditas.ui.theme.SuperficieCard
import com.serdigital.pataditas.ui.theme.TextoPrincipal
import com.serdigital.pataditas.ui.theme.TextoSecundario
import com.serdigital.pataditas.ui.theme.Verde
import com.serdigital.pataditas.ui.viewmodel.CounterState
import com.serdigital.pataditas.ui.viewmodel.HomeViewModel
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        CieloSuave.copy(alpha = 0.4f),
                        BlancoRoto,
                        LavandaSuave.copy(alpha = 0.2f)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(48.dp))

            // ─── Encabezado ───────────────────────────────────────────────
            Text(
                text = "Pataditas",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = TextoPrincipal
            )
            Text(
                text = "Seguimiento de movimientos del bebé",
                style = MaterialTheme.typography.bodyMedium,
                color = TextoSecundario,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(32.dp))

            // ─── Chip de estado ───────────────────────────────────────────
            val (statusText, statusColor) = when (val state = uiState.counterState) {
                is CounterState.Waiting -> "Esperando" to GrisMedio
                is CounterState.Counting -> "Contando..." to CieloProfundo
                is CounterState.Finished -> "Sesión finalizada ✓" to Verde
            }

            SessionStatusChip(status = statusText, color = statusColor)

            Spacer(Modifier.height(16.dp))

            // ─── Timer ────────────────────────────────────────────────────
            AnimatedContent(
                targetState = uiState.counterState,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "timer"
            ) { state ->
                when (state) {
                    is CounterState.Counting -> {
                        val seconds = state.activeSession.elapsedSeconds
                        val mins = seconds / 60
                        val secs = seconds % 60
                        Text(
                            text = "%02d:%02d".format(mins, secs),
                            style = MaterialTheme.typography.displayMedium,
                            color = TextoSecundario,
                            fontWeight = FontWeight.Light
                        )
                    }
                    is CounterState.Finished -> {
                        Text(
                            text = state.session.durationFormatted,
                            style = MaterialTheme.typography.displayMedium,
                            color = Verde,
                            fontWeight = FontWeight.Light
                        )
                    }
                    else -> {
                        Text(
                            text = "--:--",
                            style = MaterialTheme.typography.displayMedium,
                            color = GrisClaro,
                            fontWeight = FontWeight.Light
                        )
                    }
                }
            }

            Spacer(Modifier.height(32.dp))
            //Evento analytics
            val context = LocalContext.current
            val analytics = FirebaseAnalytics.getInstance(context)

            // ─── Botón principal ──────────────────────────────────────────
            val isActive = uiState.counterState is CounterState.Counting
            val kickCount = when (val state = uiState.counterState) {
                is CounterState.Counting -> state.activeSession.kickCount
                is CounterState.Finished -> state.session.kickCount
                else -> 0
            }

            //1. Boton de pataditas original
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                KickButton(
                    isActive = isActive,
                    kickCount = kickCount,
                    onTap = {

                        // 1. Ejecutamos la lógica existente del ViewModel
                        viewModel.onMainButtonTap()

                        // 📊 Evento 1: Registro de toque en el botón (Iniciar conteo o Registrar patada)
                        analytics.logEvent("kick_button_tap") {
                            param("is_active_session", isActive.toString())
                            param("current_kick_count", kickCount.toLong())
                            param("screen_name", "HomeScreen")
                        }
                    },
                    onLongPress = {

                        // 1. Ejecutamos la lógica existente del ViewModel
                        viewModel.onMainButtonLongPress()

                        // 📊 Evento 2: Registro de presión larga (Finalizar sesión)
                        analytics.logEvent("kick_button_long_press") {
                            param("total_kicks_registered", kickCount.toLong())
                            param("screen_name", "HomeScreen")
                        }
                    },
                    size = 200.dp
                )

                // 2. Si el JSON dice que la campaña está activa, cargamos la imagen de internet
                if (uiState.campaignTheme.activeCampaign != "NONE" && uiState.campaignTheme.imageUrl.isNotEmpty()) {
                    // Configuramos sutilmente el diseño según el tipo de accesorio
                    val rotation = if (uiState.campaignTheme.activeCampaign == "CHRISTMAS") 13f else 20f
                    val offsetAdjustmentX = if (uiState.campaignTheme.activeCampaign == "CHRISTMAS") (85).dp else (55).dp
                    val offsetAdjustmentY = if (uiState.campaignTheme.activeCampaign == "CHRISTMAS") (-85).dp else (-105).dp
                    val size = if (uiState.campaignTheme.activeCampaign == "CHRISTMAS") 185.dp else 205.dp

                    AsyncImage(
                        model = uiState.campaignTheme.imageUrl,
                        contentDescription = "Decoración de Campaña Estacional",
                        modifier = Modifier
                            .size(size) // Ajustamos el tamaño
                            .offset(x = offsetAdjustmentX, y = offsetAdjustmentY) // lo ubicamos dodne queda bien
                            .rotate(rotation) // Lo rotamos para que se apoye sobre el boton
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // ─── Instrucción contextual ───────────────────────────────────
            AnimatedContent(
                targetState = uiState.counterState,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "hint"
            ) { state ->
                Text(
                    text = when (state) {
                        is CounterState.Waiting -> "Tocá para comenzar a contar"
                        is CounterState.Counting -> "Tocá por cada movimiento · Mantenés presionado para finalizar"
                        is CounterState.Finished -> "Sesión guardada 💙 Tocá para empezar una nueva"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = TextoSecundario,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
            }

            Spacer(Modifier.height(40.dp))

            // ─── Stats del día ────────────────────────────────────────────
            if (uiState.todaySessions.isNotEmpty() || uiState.counterState is CounterState.Finished) {
                Text(
                    text = "Hoy",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = TextoPrincipal,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        label = "Patadas hoy",
                        value = "${uiState.totalKicksToday}",
                        emoji = "👣",
                        color = CieloSuave,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        label = "Sesiones",
                        value = "${uiState.todaySessions.size}",
                        emoji = "📊",
                        color = LavandaSuave,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        label = "Promedio",
                        value = if (uiState.todaySessions.isNotEmpty())
                            "${uiState.totalKicksToday / uiState.todaySessions.size}"
                        else "—",
                        emoji = "✨",
                        color = RosadoSuave,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(Modifier.height(24.dp))

                // Última sesión del día
                uiState.todaySessions.firstOrNull()?.let { last ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = SuperficieCard),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(20.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "Última sesión",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = TextoSecundario
                                )
                                Text(
                                    "${last.kickCount} patadas · ${last.durationFormatted}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    last.startTime.format(
                                        DateTimeFormatter.ofPattern("HH:mm")
                                    ),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextoSecundario
                                )
                            }
                            Text(text = "💙", fontSize = 28.sp)
                        }
                    }
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}
