package com.serdigital.pataditas.ui.components

import android.app.Activity
import android.view.ViewGroup
import android.widget.Button
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.serdigital.pataditas.ui.theme.*

/**
 * Botón circular principal del contador.
 * Soporta tap (registrar patada) y long press (finalizar sesión).
 */
@Composable
fun KickButton(
    modifier: Modifier = Modifier,
    isActive: Boolean,
    kickCount: Int,
    onTap: () -> Unit,
    onLongPress: () -> Unit,
    size: Dp = 200.dp
) {
    val scale by animateFloatAsState(
        targetValue = if (isActive) 1f else 0.97f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )

    val pressScale = remember { Animatable(1f) }

    val gradientColors = if (isActive) {
        listOf(CieloProfundo, CieloMedio, LavandaMedio)
    } else {
        listOf(CieloSuave, LavandaSuave)
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(size)
            .scale(scale * pressScale.value)
            .shadow(
                elevation = if (isActive) 16.dp else 8.dp,
                shape = CircleShape,
                ambientColor = CieloProfundo.copy(alpha = 0.3f),
                spotColor = CieloProfundo.copy(alpha = 0.3f)
            )
            .clip(CircleShape)
            .background(
                Brush.radialGradient(gradientColors)
            )
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        onTap()
                    },
                    onLongPress = {
                        onLongPress()
                    },
                    onPress = {
                        pressScale.animateTo(
                            0.93f,
                            spring(stiffness = Spring.StiffnessHigh)
                        )
                        tryAwaitRelease()
                        pressScale.animateTo(
                            1f,
                            spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMedium
                            )
                        )
                    }
                )
            }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "👣",
                fontSize = 42.sp
            )
            Spacer(Modifier.height(4.dp))
            AnimatedContent(
                targetState = kickCount,
                transitionSpec = {
                    (slideInVertically { -it } + fadeIn()) togetherWith
                            (slideOutVertically { it } + fadeOut())
                },
                label = "kick_count"
            ) { count ->
                Text(
                    text = count.toString(),
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            Text(
                text = if (count > 1 || kickCount == 0) "patadas" else "patada",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.9f)
            )
        }
    }
}

private val count = 0 // truco de compilación, ver uso real en KickButton

/**
 * Chip de estado de la sesión.
 */
@Composable
fun SessionStatusChip(
    status: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50),
        color = color.copy(alpha = 0.15f)
    ) {
        Text(
            text = status,
            style = MaterialTheme.typography.labelMedium,
            color = color,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
        )
    }
}

/**
 * Tarjeta de sesión del historial.
 */
@Composable
fun SessionCard(
    kickCount: Int,
    timeLabel: String,
    duration: String,
    dateLabel: String,
    onDelete: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDeleteConfirm by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SuperficieCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Ícono circular
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(listOf(CieloSuave, LavandaSuave))
                    )
            ) {
                Text(text = "👣", fontSize = 24.sp)
            }

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "$kickCount patadas",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = TextoPrincipal
                    )
                }
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "$dateLabel · $timeLabel",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextoSecundario
                )
                Text(
                    text = "Duración: $duration",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextoSecundario
                )
            }

            IconButton(onClick = { showDeleteConfirm = true }) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = "Eliminar",
                    tint = GrisMedio
                )
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Eliminar sesión") },
            text = { Text("¿Eliminás esta sesión? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = { onDelete(); showDeleteConfirm = false }) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

/**
 * Header de sección reutilizable.
 */
@Composable
fun SectionHeader(
    title: String,
    subtitle: String? = null,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            color = TextoPrincipal
        )
        if (subtitle != null) {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = TextoSecundario
            )
        }
    }
}

/**
 * Tarjeta de estadística simple.
 */
@Composable
fun StatCard(
    label: String,
    value: String,
    emoji: String,
    modifier: Modifier = Modifier,
    color: Color = CieloSuave
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.3f)),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = emoji, fontSize = 24.sp)
            Spacer(Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TextoPrincipal
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = TextoSecundario
            )
        }
    }
}

@Composable
fun ButtonCrash(
    modifier: Modifier = Modifier
) {
    FloatingActionButton(
        onClick = {
            throw RuntimeException("Test Crash") // Fuerza el crash para Firebase
        },
        modifier = modifier,
        containerColor = Color.Red, // Rojo para alertar que es un botón de prueba/crash
        contentColor = Color.White,
        shape = CircleShape
    ) {
        // Usamos un ícono de advertencia, pero puedes cambiarlo por el que gustes
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = "Forzar Crash"
        )
    }
}
