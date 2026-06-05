package co.edu.unicauca.dopaminah.ui.screens.goals.webgoals.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import co.edu.unicauca.dopaminah.ui.icons.LucideGlobe
import co.edu.unicauca.dopaminah.ui.icons.LucideTimer
import co.edu.unicauca.dopaminah.ui.screens.goals.webgoals.BrandAvatar
import co.edu.unicauca.dopaminah.ui.screens.goals.webgoals.DomainSuggestion
import co.edu.unicauca.dopaminah.ui.screens.goals.webgoals.domainSuggestions
import co.edu.unicauca.dopaminah.ui.theme.DopaminahOrange
import co.edu.unicauca.dopaminah.ui.theme.DopaminahPurple
import co.edu.unicauca.dopaminah.ui.theme.DopaminahPurpleDark
import co.edu.unicauca.dopaminah.ui.theme.DopaminahPurpleLight

private val timePresets = listOf(
    "15 min" to 15,
    "30 min" to 30,
    "1 hora" to 60,
    "2 horas" to 120,
    "3 horas" to 180,
    "Inmediato" to 0,
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CreateWebGoalDialog(
    onDismiss: () -> Unit,
    onSave: (url: String, timeLimitMinutes: Int) -> Unit
) {
    var urlInput by remember { mutableStateOf("") }
    var selectedMinutes by remember { mutableStateOf(30) }
    var isCustom by remember { mutableStateOf(false) }
    var customMinutes by remember { mutableStateOf("") }
    var urlError by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 480.dp)
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(28.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(DopaminahPurpleLight.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = LucideGlobe,
                            contentDescription = null,
                            tint = DopaminahPurple,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = "Nuevo limite web",
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Establece un limite diario para este sitio",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Sitios sugeridos",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(10.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    domainSuggestions.forEach { suggestion ->
                        DomainSuggestionChip(
                            suggestion = suggestion,
                            selected = urlInput == suggestion.domain,
                            onClick = { urlInput = suggestion.domain; urlError = false }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "O escribe un dominio",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = urlInput,
                    onValueChange = {
                        urlInput = it
                        urlError = false
                    },
                    placeholder = { Text("ej. youtube.com", fontSize = 14.sp) },
                    leadingIcon = {
                        Icon(
                            imageVector = LucideGlobe,
                            contentDescription = null,
                            tint = DopaminahPurple,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    isError = urlError,
                    supportingText = if (urlError) {
                        { Text("Ingresa un dominio valido", color = MaterialTheme.colorScheme.error, fontSize = 12.sp) }
                    } else null,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                        focusedBorderColor = DopaminahPurple,
                        cursorColor = DopaminahPurple
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Tiempo maximo diario",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(10.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    timePresets.forEach { (label, minutes) ->
                        val selected = !isCustom && selectedMinutes == minutes
                        TimeChip(
                            label = label,
                            selected = selected,
                            onClick = {
                                isCustom = false
                                selectedMinutes = minutes
                            }
                        )
                    }
                    TimeChip(
                        label = "Personalizado",
                        selected = isCustom,
                        onClick = { isCustom = true }
                    )
                }

                if (isCustom) {
                    Spacer(modifier = Modifier.height(14.dp))
                    OutlinedTextField(
                        value = customMinutes,
                        onValueChange = { newVal ->
                            if (newVal.isEmpty() || newVal.all { it.isDigit() }) {
                                customMinutes = newVal
                                selectedMinutes = newVal.toIntOrNull() ?: 0
                            }
                        },
                        placeholder = { Text("Minutos", fontSize = 14.sp) },
                        leadingIcon = {
                            Icon(
                                imageVector = LucideTimer,
                                contentDescription = null,
                                tint = DopaminahOrange,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                            focusedBorderColor = DopaminahOrange,
                            cursorColor = DopaminahOrange
                        ),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = {
                            val domain = urlInput.trim()
                                .removePrefix("https://")
                                .removePrefix("http://")
                                .removePrefix("www.")
                                .trimEnd('/')
                            if (domain.isBlank() || !domain.contains(".")) {
                                urlError = true
                                return@Button
                            }
                            val limit = if (isCustom) (customMinutes.toIntOrNull() ?: 30) else selectedMinutes
                            onSave(domain, limit.coerceAtLeast(0))
                        },
                        modifier = Modifier.weight(1f).height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent
                        ),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(DopaminahPurple, DopaminahPurpleDark)
                                    ),
                                    RoundedCornerShape(12.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Guardar limite",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f).height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        )
                    ) {
                        Text("Cancelar", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

@Composable
private fun TimeChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(
                if (selected) DopaminahPurpleLight
                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
            )
            .then(
                if (selected) Modifier.border(
                    1.5.dp, DopaminahPurple, RoundedCornerShape(10.dp)
                ) else Modifier
            )
            .clickable { onClick() }
            .padding(horizontal = 18.dp, vertical = 10.dp)
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = if (selected) DopaminahPurpleDark else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun DomainSuggestionChip(
    suggestion: DomainSuggestion,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(
                if (selected) DopaminahPurpleLight
                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
            )
            .then(
                if (selected) Modifier.border(
                    1.5.dp, DopaminahPurple, RoundedCornerShape(10.dp)
                ) else Modifier
            )
            .clickable { onClick() }
            .padding(start = 6.dp, end = 14.dp, top = 4.dp, bottom = 4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            BrandAvatar(
                domain = suggestion.domain,
                isBlocked = false,
                size = 26.dp,
                fontSize = 11.sp
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = suggestion.label,
                fontSize = 13.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                color = if (selected) DopaminahPurpleDark else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
