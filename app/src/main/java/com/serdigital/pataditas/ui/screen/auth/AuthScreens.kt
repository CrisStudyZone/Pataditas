package com.serdigital.pataditas.ui.screen.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.serdigital.pataditas.ui.theme.*

/**
 * Pantalla de perfil / autenticación.
 * Placeholder preparado para integrar Firebase Authentication.
 *
 * Para activar Firebase:
 * 1. Agregar google-services.json
 * 2. Agregar plugins de Firebase en Gradle
 * 3. Reemplazar StubAuthRepositoryImpl por FirebaseAuthRepositoryImpl
 * 4. Implementar las pantallas Login, Register, ForgotPassword
 */
@Composable
fun ProfileScreen(
    onNavigateToLogin: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(LavandaSuave.copy(0.4f), BlancoRoto, CieloSuave.copy(0.2f))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Avatar placeholder
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(listOf(CieloSuave, LavandaSuave))
                    )
            ) {
                Text("👶", fontSize = 48.sp)
            }

            Spacer(Modifier.height(20.dp))

            Text(
                text = "Tu espacio personal",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                color = TextoPrincipal
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Próximamente podrás crear una cuenta\npara sincronizar tus datos\nen todos tus dispositivos",
                style = MaterialTheme.typography.bodyMedium,
                color = TextoSecundario,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(40.dp))

            // Features coming soon
            val features = listOf(
                "☁️" to "Sincronización en la nube",
                "📱" to "Acceso desde cualquier dispositivo",
                "🔒" to "Datos seguros con Firebase",
                "👨‍👩‍👧" to "Compartir con tu pareja o médico"
            )

            features.forEach { (emoji, text) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(SuperficieCard)
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(emoji, fontSize = 22.sp)
                    Spacer(Modifier.width(14.dp))
                    Text(
                        text = text,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextoPrincipal
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            // Botón deshabilitado (se habilitará con Firebase)
            Button(
                onClick = { /* Firebase Auth: navegar a Login */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CieloProfundo.copy(alpha = 0.4f)
                ),
                enabled = false
            ) {
                Text(
                    "Iniciar sesión · Próximamente",
                    style = MaterialTheme.typography.titleSmall
                )
            }

            Spacer(Modifier.height(12.dp))

            Text(
                text = "Tus datos se guardan localmente en tu dispositivo",
                style = MaterialTheme.typography.labelSmall,
                color = GrisMedio,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Pantalla de login — Placeholder para Firebase Authentication.
 *
 * TODO: implementar cuando se integre Firebase:
 * - signInWithEmailAndPassword
 * - Google Sign-In
 * - Mostrar errores de auth
 */
@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit = {},
    onNavigateToForgotPassword: () -> Unit = {},
    onLoginSuccess: () -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BlancoRoto)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("👶", fontSize = 56.sp)
        Spacer(Modifier.height(12.dp))
        Text(
            "Bienvenida de nuevo",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(32.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            singleLine = true
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            singleLine = true
        )
        Spacer(Modifier.height(8.dp))

        TextButton(onClick = onNavigateToForgotPassword) {
            Text("Olvidé mi contraseña", color = CieloProfundo)
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = { /* TODO: Firebase signIn */ },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = CieloProfundo),
            enabled = false // Habilitar con Firebase
        ) {
            Text("Iniciar sesión")
        }

        Spacer(Modifier.height(12.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("¿No tenés cuenta? ", color = TextoSecundario)
            TextButton(onClick = onNavigateToRegister) {
                Text("Registrarte", color = CieloProfundo)
            }
        }
    }
}
