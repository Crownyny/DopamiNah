package co.edu.unicauca.dopaminah.ui.screens.settings.components

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import co.edu.unicauca.dopaminah.BuildConfig
import co.edu.unicauca.dopaminah.ui.theme.extendedColors
import kotlinx.coroutines.launch
import java.security.SecureRandom
import java.math.BigInteger

private const val TAG = "GoogleSignInDialog"

@Composable
fun GoogleSignInDialog(
    isLoading: Boolean,
    errorMessage: String?,
    onSignInSuccess: (idToken: String) -> Unit,
    onSignInError: (String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Dialog(
        onDismissRequest = { if (!isLoading) onDismiss() },
        properties = DialogProperties(
            dismissOnBackPress = !isLoading,
            dismissOnClickOutside = !isLoading
        )
    ) {
        Card(
            modifier = modifier
                .fillMaxWidth(0.9f)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Desbloquear Premium",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    if (!isLoading) {
                        IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Close",
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                Text(
                    text = "Inicia sesión con tu cuenta de Google para acceder a Premium",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )

                Spacer(Modifier.height(24.dp))

                if (errorMessage != null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFFEBEE)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = errorMessage,
                            fontSize = 12.sp,
                            color = Color(0xFFC62828),
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                Button(
                    onClick = {
                        scope.launch {
                            launchCredentialManager(context, onSignInSuccess, onSignInError)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.extendedColors.brandOrange,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Iniciar sesión con Google", fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(Modifier.height(12.dp))

                if (!isLoading) {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Cancelar", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

private suspend fun launchCredentialManager(
    context: Context,
    onSignInSuccess: (idToken: String) -> Unit,
    onSignInError: (String) -> Unit
) {
    try {
        val credentialManager = CredentialManager.create(context)
        val webClientId = BuildConfig.googleWebClientId
        
        if (webClientId.isEmpty()) {
            Log.e(TAG, "Web Client ID is empty or 'null'. Check local.properties.")
            onSignInError("Web Client ID no configurado. Verifica tu archivo local.properties.")
            return
        }

        // Generate a nonce for security
        val nonce = BigInteger(130, SecureRandom()).toString(32)

        // Use GetSignInWithGoogleOption for the Google Sign-In button flow
        val googleIdOption = GetSignInWithGoogleOption.Builder(webClientId)
            .setNonce(nonce)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        val activityContext = context.findActivity()
        if (activityContext == null) {
            Log.e(TAG, "Activity context not found")
            onSignInError("Error de contexto: No se pudo encontrar la actividad.")
            return
        }

        Log.d(TAG, "Launching Credential Manager with Web Client ID: $webClientId")
        val result = credentialManager.getCredential(
            context = activityContext,
            request = request
        )

        handleSignInResult(result.credential, onSignInSuccess, onSignInError)

    } catch (e: NoCredentialException) {
        Log.e(TAG, "NoCredentialException: No accounts found or configuration mismatch", e)
        onSignInError("No se encontraron cuentas de Google. Verifica que: \n1. Tengas una cuenta de Google en el dispositivo.\n2. El Web Client ID sea correcto.\n3. Tu SHA-1 esté registrado en la Consola de Google.")
    } catch (e: GetCredentialCancellationException) {
        Log.e(TAG, "GetCredentialCancellationException: ${e.message}", e)
        if (e.message?.contains("16") == true || e.message?.contains("reauth") == true) {
            onSignInError("Error [16]: Problema de configuración (SHA-1 o Client ID incorrecto).")
        } else {
            onSignInError("Inicio de sesión cancelado.")
        }
    } catch (e: GetCredentialException) {
        Log.e(TAG, "GetCredentialException: ${e.message}", e)
        onSignInError("Error de autenticación: ${e.message}")
    } catch (e: Exception) {
        Log.e(TAG, "Unexpected exception: ${e.message}", e)
        onSignInError("Error inesperado: ${e.message}")
    }
}

private fun handleSignInResult(
    credential: androidx.credentials.Credential,
    onSignInSuccess: (idToken: String) -> Unit,
    onSignInError: (String) -> Unit
) {
    try {
        if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
            onSignInSuccess(googleIdTokenCredential.idToken)
        } else {
            onSignInError("Tipo de credencial no válido: ${credential.type}")
        }
    } catch (e: Exception) {
        onSignInError("Error al procesar credencial: ${e.message}")
    }
}

fun Context.findActivity(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    return null
}
