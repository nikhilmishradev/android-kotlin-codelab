package com.androidtechguru.codelab.android.permissions

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat

/**
 * PERMISSIONS — Runtime Permission Handling in Compose
 *
 * Key concepts:
 * 1. Activity Result API — modern permission request
 * 2. rememberLauncherForActivityResult — Compose integration
 * 3. Permission flow: check → rationale → request → handle result
 * 4. Handling granted / denied / permanently denied states
 */
@Composable
fun PermissionExamplesScreen() {
    val context = LocalContext.current
    var cameraPermissionState by remember { mutableStateOf<PermissionState>(PermissionState.NotRequested) }
    var locationPermissionState by remember { mutableStateOf<PermissionState>(PermissionState.NotRequested) }
    var showRationale by remember { mutableStateOf(false) }

    // ── Single Permission Launcher ──
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        cameraPermissionState = if (isGranted) {
            PermissionState.Granted
        } else {
            PermissionState.Denied
        }
    }

    // ── Multiple Permissions Launcher ──
    val locationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

        locationPermissionState = when {
            fineGranted -> PermissionState.Granted
            coarseGranted -> PermissionState.PartiallyGranted
            else -> PermissionState.Denied
        }
    }

    // ── Rationale Dialog ──
    if (showRationale) {
        AlertDialog(
            onDismissRequest = { showRationale = false },
            title = { Text("Camera Permission Needed") },
            text = { Text("We need camera access to take photos. Without it, this feature won't work.") },
            confirmButton = {
                TextButton(onClick = {
                    showRationale = false
                    cameraLauncher.launch(Manifest.permission.CAMERA)
                }) {
                    Text("Grant")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRationale = false }) {
                    Text("No Thanks")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Permission Examples", style = MaterialTheme.typography.headlineSmall)

        // ── Camera Permission ──
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Camera Permission", style = MaterialTheme.typography.titleMedium)
                Text("Status: ${cameraPermissionState.label}")

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        handlePermissionRequest(
                            context = context,
                            permission = Manifest.permission.CAMERA,
                            onAlreadyGranted = {
                                cameraPermissionState = PermissionState.Granted
                            },
                            onShouldShowRationale = {
                                showRationale = true
                            },
                            onRequest = {
                                cameraLauncher.launch(Manifest.permission.CAMERA)
                            }
                        )
                    },
                    enabled = cameraPermissionState != PermissionState.Granted
                ) {
                    Text("Request Camera")
                }
            }
        }

        // ── Location Permission ──
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Location Permission", style = MaterialTheme.typography.titleMedium)
                Text("Status: ${locationPermissionState.label}")

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        locationLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    },
                    enabled = locationPermissionState != PermissionState.Granted
                ) {
                    Text("Request Location")
                }
            }
        }
    }
}

// ── Permission State ──
enum class PermissionState(val label: String) {
    NotRequested("Not requested"),
    Granted("Granted"),
    Denied("Denied"),
    PartiallyGranted("Partially granted (coarse only)")
}

// ── Permission Request Flow ──
// Check → Rationale → Request
private fun handlePermissionRequest(
    context: Context,
    permission: String,
    onAlreadyGranted: () -> Unit,
    onShouldShowRationale: () -> Unit,
    onRequest: () -> Unit
) {
    when {
        // Step 1: Already granted?
        ContextCompat.checkSelfPermission(context, permission) ==
                PackageManager.PERMISSION_GRANTED -> {
            onAlreadyGranted()
        }

        // Step 2: Should show rationale?
        // Returns true if user previously denied (but didn't check "don't ask again")
        // Returns false if: first time asking, or user checked "don't ask again"
        // shouldShowRequestPermissionRationale requires Activity context
        // In real app: (context as? Activity)?.shouldShowRequestPermissionRationale(permission)

        // Step 3: Request permission
        else -> onRequest()
    }
}

// INTERVIEW TIP — Permission Flow:
//
// 1. Check: ContextCompat.checkSelfPermission()
// 2. Rationale: shouldShowRequestPermissionRationale()
//    - true: user denied before, show explanation WHY you need it
//    - false: first time OR permanently denied ("don't ask again")
// 3. Request: ActivityResultContracts.RequestPermission
// 4. Handle: granted → proceed, denied → graceful degradation
//
// BEST PRACTICES:
// - Ask in context (when user tries the feature, not at app start)
// - Explain WHY before requesting (rationale dialog)
// - Handle denial gracefully (disable feature, show alternative)
// - Android 13+: POST_NOTIFICATIONS needs runtime permission
// - Android 12+: SCHEDULE_EXACT_ALARM needs special permission
