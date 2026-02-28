package co.edu.unicauca.DopamiNah.ui.screens.onboarding

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import co.edu.unicauca.DopamiNah.R
import co.edu.unicauca.DopamiNah.ui.screens.dashboard.viewmodel.DashboardViewModel
import co.edu.unicauca.DopamiNah.ui.theme.DopaminahPurple
import co.edu.unicauca.DopamiNah.ui.theme.DopaminahPurpleDark

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingPermissionScreen(
    onPermissionGranted: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val hasUsagePermission by viewModel.hasUsagePermission.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current
    
    // We have 3 steps now
    val pagerState = rememberPagerState(pageCount = { 3 })
    val coroutineScope = rememberCoroutineScope()

    // Notification permission launcher (Android 13+)
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            coroutineScope.launch { 
                if (pagerState.currentPage < 2) pagerState.animateScrollToPage(pagerState.currentPage + 1)
            }
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.refreshStats()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // Auto-proceed if on last step and got permission
    if (hasUsagePermission && pagerState.currentPage == 2) {
        onPermissionGranted()
    }

    Scaffold(
        containerColor = Color.White
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header with Stepper
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Configuración",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = DopaminahPurpleDark
                )
                
                // Indicators
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    repeat(3) { index ->
                        Box(
                            modifier = Modifier
                                .width(if (pagerState.currentPage == index) 24.dp else 24.dp)
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(if (pagerState.currentPage == index) DopaminahPurple else Color.LightGray.copy(alpha=0.5f))
                        )
                    }
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f),
                userScrollEnabled = false // Force users to use buttons
            ) { page ->
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.weight(1f))

                    // Content based on page
                    when(page) {
                        0 -> {
                            // Step 1: Apps Usage
                            PermissionPageContent(
                                icon = Icons.Default.Smartphone,
                                iconTint = Color(0xFF4285F4),
                                title = stringResource(R.string.onboarding_apps_title),
                                description = stringResource(R.string.onboarding_apps_desc)
                            )
                        }
                        1 -> {
                            // Step 2: Notifications
                            PermissionPageContent(
                                icon = Icons.Default.Notifications,
                                iconTint = DopaminahPurple,
                                title = stringResource(R.string.onboarding_notif_title),
                                description = stringResource(R.string.onboarding_notif_desc)
                            )
                        }
                        2 -> {
                            // Step 3: Accessibility (Usage Stats)
                            PermissionPageContent(
                                icon = Icons.Default.Visibility,
                                iconTint = Color(0xFFE91E63),
                                title = stringResource(R.string.onboarding_permission_title),
                                description = stringResource(R.string.onboarding_permission_desc)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))
                }
            }
            
            // Action Button
            Button(
                onClick = {
                    when (pagerState.currentPage) {
                        0 -> {
                            // Move to notifications
                            coroutineScope.launch { pagerState.animateScrollToPage(1) }
                        }
                        1 -> {
                            // Request notifications if needed, then move to next
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            } else {
                                coroutineScope.launch { pagerState.animateScrollToPage(2) }
                            }
                        }
                        2 -> {
                            // Request Usage Stats
                            val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            context.startActivity(intent)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DopaminahPurple)
            ) {
                Text(
                    text = if (pagerState.currentPage == 2) stringResource(R.string.onboarding_permission_button) else stringResource(R.string.onboarding_next_button),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun PermissionPageContent(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    title: String,
    description: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // Main Icon
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(DopaminahPurple.copy(alpha = 0.2f)), // Adjusted matching the unified purple background circle
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(48.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Title
        Text(
            text = title,
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            color = DopaminahPurpleDark,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Description
        Text(
            text = description,
            fontSize = 16.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Security Badge
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(DopaminahPurple.copy(alpha = 0.05f))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Border Accent
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(48.dp)
                    .background(DopaminahPurple)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Icon(
                imageVector = Icons.Outlined.Shield,
                contentDescription = "Shield",
                tint = DopaminahPurple,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Text(
                text = stringResource(R.string.onboarding_permission_badge),
                fontSize = 12.sp,
                color = DopaminahPurpleDark,
                lineHeight = 16.sp
            )
        }
    }
}
