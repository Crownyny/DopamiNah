package co.edu.unicauca.DopamiNah.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.unicauca.DopamiNah.ui.theme.*

@Composable
fun DashboardScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // App Bar / Header region
        HeaderSection()
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Main content area
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                DailyUnlocksCard()
                Spacer(modifier = Modifier.height(24.dp))
                MostUsedAppsSection()
            }
        }
    }
}

@Composable
fun HeaderSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
            .background(DopaminahPurpleDark)
            .padding(top = 48.dp, bottom = 24.dp, start = 20.dp, end = 20.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "DopamiNah",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Text(
                        text = "Tu asistente de bienestar digital",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
                
                // Brain icon placeholder
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🧠", fontSize = 24.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Streak Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DopaminahOrange),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🔥", fontSize = 40.sp, modifier = Modifier.padding(end = 12.dp))
                            Column {
                                Text(
                                    "RACHA ACTUAL",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Row(verticalAlignment = Alignment.Bottom) {
                                    Text(
                                        "7",
                                        color = Color.White,
                                        fontSize = 36.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                    Text(
                                        " días consecutivos",
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        modifier = Modifier.padding(bottom = 6.dp, start = 4.dp)
                                    )
                                }
                            }
                        }
                        
                        // Level Badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(DopaminahOrangeLight)
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Nivel", color = Color.White, fontSize = 12.sp)
                                Text("5", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    
                    HorizontalDivider(color = Color.White.copy(alpha = 0.3f), modifier = Modifier.padding(vertical = 12.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = WarningYellow,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "¡Increíble! Mantén el impulso",
                            color = Color.White,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DailyUnlocksCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DopaminahRedDark),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Desbloqueos de Hoy",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = DopaminahRedText
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    "70",
                    color = DopaminahRedText,
                    fontSize = 64.sp,
                    fontWeight = FontWeight.ExtraBold,
                    lineHeight = 64.sp
                )
                Text(
                    " veces",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp, start = 8.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White.copy(alpha = 0.1f))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    "↗ +24 vs ayer",
                    color = DopaminahRedText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun MostUsedAppsSection() {
    var searchQuery by remember { mutableStateOf("") }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(DopaminahPurpleLight),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Star, // Target icon abstraction
                        contentDescription = null,
                        tint = DopaminahPurple,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Apps Más Usadas",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Buscar app...", color = TextSecondary) },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = DopaminahPurple)
                },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = DopaminahPurpleLight,
                    focusedBorderColor = DopaminahPurple,
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent
                ),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { /*TODO*/ },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = DopaminahPurple),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("↓ Más usado", fontWeight = FontWeight.Bold)
                }
                
                Button(
                    onClick = { /*TODO*/ },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BackgroundLight,
                        contentColor = TextSecondary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                ) {
                    Text("↑ Menos usado", fontWeight = FontWeight.Bold)
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Mock List Items
            AppUsageItemMock("Twitter", "1h 31m", Color(0xFF1DA1F2))
            AppUsageItemMock("WhatsApp", "1h 17m", Color(0xFF25D366))
            AppUsageItemMock("YouTube", "1h 5m", Color(0xFFFF0000))
        }
    }
}

@Composable
fun AppUsageItemMock(name: String, time: String, iconColor: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(BackgroundLight)
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(iconColor)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = time,
                    fontSize = 14.sp,
                    color = TextSecondary,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
