package co.edu.unicauca.dopaminah.ui.screens.goals.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import co.edu.unicauca.dopaminah.domain.model.AppInfo
import co.edu.unicauca.dopaminah.ui.icons.LucideTarget
import co.edu.unicauca.dopaminah.ui.screens.dashboard.components.AppIconImage
import co.edu.unicauca.dopaminah.ui.theme.DopaminahPurple
import co.edu.unicauca.dopaminah.ui.theme.DopaminahPurpleDark
import co.edu.unicauca.dopaminah.ui.theme.DopaminahPurpleLight

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CreateGoalDialog(
    installedApps: List<AppInfo>,
    onDismiss: () -> Unit,
    onSave: (type: String, appNames: List<String>, limitMinutes: Int) -> Unit
) {
    var expandedType by remember { mutableStateOf(false) }

    val goalTypes = listOf("Tiempo Total Diario", "Limite de Aplicacion", "Limite Desbloqueos")
    var selectedGoalType by remember { mutableStateOf(goalTypes[0]) }

    var selectedApps by remember { mutableStateOf(setOf<String>()) }
    var limitInput by remember { mutableStateOf("120") }
    var searchQuery by remember { mutableStateOf("") }
    var useGridView by remember { mutableStateOf(true) }

    val filteredApps = remember(installedApps, searchQuery) {
        if (searchQuery.isBlank()) installedApps
        else installedApps.filter { it.displayName.contains(searchQuery, ignoreCase = true) }
    }

    val isAppLimitType = selectedGoalType == "Limite de Aplicacion"
    val canSave = (!isAppLimitType || selectedApps.isNotEmpty()) && (limitInput.toIntOrNull() ?: 0) > 0

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 640.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .background(
                            Brush.horizontalGradient(listOf(DopaminahPurple, DopaminahPurpleDark))
                        )
                )

                Column(modifier = Modifier.padding(24.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(DopaminahPurpleLight.copy(alpha = 0.35f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = LucideTarget,
                                contentDescription = null,
                                tint = DopaminahPurple,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = "Nueva Meta",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Define tus límites de uso diario",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(22.dp))

                    Text(
                        text = "Tipo",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = DopaminahPurple,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    ExposedDropdownMenuBox(
                        expanded = expandedType,
                        onExpandedChange = {
                            expandedType = !expandedType
                            if (!expandedType) searchQuery = ""
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = selectedGoalType,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedType)
                            },
                            modifier = Modifier.fillMaxWidth().menuAnchor(),
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                                focusedBorderColor = DopaminahPurple,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f),
                                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)
                            ),
                            textStyle = MaterialTheme.typography.bodyMedium
                        )
                        ExposedDropdownMenu(
                            expanded = expandedType,
                            onDismissRequest = { expandedType = false },
                            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                        ) {
                            goalTypes.forEach { selectionOption ->
                                DropdownMenuItem(
                                    text = { Text(selectionOption) },
                                    onClick = {
                                        selectedGoalType = selectionOption
                                        expandedType = false
                                        selectedApps = emptySet()
                                    }
                                )
                            }
                        }
                    }

                    if (isAppLimitType) {
                        Spacer(modifier = Modifier.height(22.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Aplicaciones",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = DopaminahPurple,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Surface(
                                shape = RoundedCornerShape(9.dp),
                                color = DopaminahPurpleLight.copy(alpha = 0.25f)
                            ) {
                                Row(modifier = Modifier.padding(3.dp)) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(7.dp))
                                            .background(
                                                if (!useGridView) Color.White
                                                else Color.Transparent
                                            )
                                            .clickable { useGridView = false }
                                            .padding(horizontal = 9.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = "☰",
                                            fontSize = 14.sp,
                                            color = if (!useGridView) DopaminahPurple
                                            else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(7.dp))
                                            .background(
                                                if (useGridView) Color.White
                                                else Color.Transparent
                                            )
                                            .clickable { useGridView = true }
                                            .padding(horizontal = 9.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = "▦",
                                            fontSize = 14.sp,
                                            color = if (useGridView) DopaminahPurple
                                            else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                            if (selectedApps.isNotEmpty()) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(DopaminahPurple),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${selectedApps.size}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Buscar aplicación...", fontSize = 13.sp) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Buscar",
                                    tint = DopaminahPurple.copy(alpha = 0.6f),
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = Color.Transparent,
                                focusedBorderColor = DopaminahPurple,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f),
                                focusedContainerColor = DopaminahPurpleLight.copy(alpha = 0.15f)
                            ),
                            textStyle = MaterialTheme.typography.bodySmall
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        if (useGridView) {
                            AppGridView(
                                apps = filteredApps,
                                selectedApps = selectedApps,
                                onToggle = { name ->
                                    selectedApps = if (name in selectedApps) selectedApps - name
                                    else selectedApps + name
                                }
                            )
                        } else {
                            AppListView(
                                apps = filteredApps,
                                selectedApps = selectedApps,
                                onToggle = { name ->
                                    selectedApps = if (name in selectedApps) selectedApps - name
                                    else selectedApps + name
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(22.dp))

                    Text(
                        text = if (selectedGoalType == "Limite Desbloqueos") "Limite (cantidad)" else "Tiempo maximo",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = DopaminahPurple,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = limitInput,
                        onValueChange = { newValue ->
                            if (newValue.isEmpty() || newValue.all { it.isDigit() }) {
                                limitInput = newValue
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                            focusedBorderColor = DopaminahPurple,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f),
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)
                        ),
                        textStyle = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = {
                                val limit = limitInput.toIntOrNull() ?: 0
                                onSave(selectedGoalType, selectedApps.toList(), limit)
                            },
                            enabled = canSave,
                            modifier = Modifier.weight(1f).height(50.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                disabledContainerColor = Color.Transparent
                            ),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                        val bgMod = if (canSave) {
                            Modifier.background(
                                Brush.horizontalGradient(listOf(DopaminahPurple, DopaminahPurpleDark)),
                                RoundedCornerShape(14.dp)
                            )
                        } else {
                            Modifier.background(
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                RoundedCornerShape(14.dp)
                            )
                        }
                        Box(
                            modifier = Modifier.fillMaxSize().then(bgMod),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "Guardar",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (canSave) Color.White
                                    else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Button(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f).height(50.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = DopaminahPurpleLight.copy(alpha = 0.25f),
                                contentColor = DopaminahPurple
                            )
                        ) {
                            Text("Cancelar", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AppGridView(
    apps: List<AppInfo>,
    selectedApps: Set<String>,
    onToggle: (String) -> Unit
) {
    val surfaceBg = DopaminahPurpleLight.copy(alpha = 0.07f)
    Surface(
        modifier = Modifier.fillMaxWidth().heightIn(max = 300.dp),
        shape = RoundedCornerShape(16.dp),
        color = surfaceBg
    ) {
        if (apps.isEmpty()) {
            Text(
                text = "Sin resultados",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(24.dp)
            )
        } else {
            Box(Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(14.dp)
                        .padding(bottom = 4.dp)
                ) {
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        apps.forEach { app ->
                            val isSelected = app.displayName in selectedApps
                            Column(
                                modifier = Modifier
                                    .width(84.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(
                                        if (isSelected) DopaminahPurpleLight
                                        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                    )
                                    .clickable { onToggle(app.displayName) }
                                    .padding(horizontal = 4.dp, vertical = 10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                AppIconImage(
                                    iconBytes = app.iconBytes,
                                    appName = app.displayName,
                                    size = 36.dp
                                )
                                Spacer(modifier = Modifier.height(5.dp))
                                Text(
                                    text = app.displayName.take(10),
                                    fontSize = 11.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    textAlign = TextAlign.Center,
                                    color = if (isSelected) DopaminahPurpleDark
                                    else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(24.dp)
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, surfaceBg)
                            )
                        )
                )
            }
        }
    }
}

@Composable
private fun AppListView(
    apps: List<AppInfo>,
    selectedApps: Set<String>,
    onToggle: (String) -> Unit
) {
    val surfaceBg = DopaminahPurpleLight.copy(alpha = 0.07f)
    Surface(
        modifier = Modifier.fillMaxWidth().heightIn(max = 300.dp),
        shape = RoundedCornerShape(16.dp),
        color = surfaceBg
    ) {
        if (apps.isEmpty()) {
            Text(
                text = "Sin resultados",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(24.dp)
            )
        } else {
            Box(Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(bottom = 4.dp)
                ) {
                    apps.forEach { app ->
                        val isSelected = app.displayName in selectedApps
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onToggle(app.displayName) }
                                .background(
                                    if (isSelected) DopaminahPurpleLight.copy(alpha = 0.3f)
                                    else Color.Transparent
                                )
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AppIconImage(
                                iconBytes = app.iconBytes,
                                appName = app.displayName,
                                size = 28.dp
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = app.displayName,
                                fontSize = 14.sp,
                                fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )
                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clip(CircleShape)
                                        .background(DopaminahPurple),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "✓",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                        if (app != apps.last()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(0.5.dp)
                                    .padding(start = 54.dp)
                                    .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f))
                            )
                        }
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(24.dp)
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, surfaceBg)
                            )
                        )
                )
            }
        }
    }
}
