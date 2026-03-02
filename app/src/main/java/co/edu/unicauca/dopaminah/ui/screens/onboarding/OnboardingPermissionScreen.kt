package co.edu.unicauca.dopaminah.ui.screens.onboarding

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import co.edu.unicauca.dopaminah.ui.screens.dashboard.viewmodel.DashboardViewModel
import co.edu.unicauca.dopaminah.ui.theme.DopaminahPurple
import co.edu.unicauca.dopaminah.R

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

import co.edu.unicauca.dopaminah.ui.screens.onboarding.components.OnboardingActionButton
import co.edu.unicauca.dopaminah.ui.screens.onboarding.components.OnboardingHeader
import co.edu.unicauca.dopaminah.ui.screens.onboarding.components.PermissionPageContent

import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import android.app.Activity


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingPermissionScreen(
    onPermissionGranted: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val hasUsagePermission by viewModel.hasUsagePermission.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }
    
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
            // Modularized Header with Stepper
            OnboardingHeader(
                currentPage = pagerState.currentPage,
                totalPages = 3
            )

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

                    // Content based on page using Modular Component
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
            
            // Modularized Action Button
            OnboardingActionButton(
                text = if (pagerState.currentPage == 2) stringResource(R.string.onboarding_permission_button) else stringResource(R.string.onboarding_next_button),
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
                }
            )
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
