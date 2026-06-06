package com.androidtechguru.codelab.android.compose

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp

/**
 * COMPOSE ANIMATIONS — Interview Prep
 *
 * Key concepts:
 * 1. animateXAsState — simple value animation
 * 2. AnimatedVisibility — show/hide with transitions
 * 3. Crossfade — content switching animation
 * 4. animateContentSize — smooth size changes
 * 5. updateTransition — coordinated multi-property animation
 */

@Composable
fun AnimationExamplesScreen() {
    var isExpanded by remember { mutableStateOf(false) }
    var isVisible by remember { mutableStateOf(true) }
    var selectedTab by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // ── 1. animateXAsState ──
        // Animates a single value from current to target
        Text("1. animateAsState", style = MaterialTheme.typography.titleMedium)

        val alpha by animateFloatAsState(
            targetValue = if (isVisible) 1f else 0.3f,
            animationSpec = tween(durationMillis = 500),
            label = "alpha"  // label helps with debugging in Layout Inspector
        )
        val size by animateDpAsState(
            targetValue = if (isExpanded) 120.dp else 60.dp,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            ),
            label = "size"
        )

        Box(
            modifier = Modifier
                .size(size)
                .alpha(alpha)
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            Text("Box", color = MaterialTheme.colorScheme.onPrimary)
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { isExpanded = !isExpanded }) {
                Text(if (isExpanded) "Shrink" else "Expand")
            }
            Button(onClick = { isVisible = !isVisible }) {
                Text(if (isVisible) "Fade" else "Show")
            }
        }

        HorizontalDivider()

        // ── 2. AnimatedVisibility ──
        // Show/hide with enter/exit transitions
        Text("2. AnimatedVisibility", style = MaterialTheme.typography.titleMedium)

        var showCard by remember { mutableStateOf(true) }
        Button(onClick = { showCard = !showCard }) {
            Text(if (showCard) "Hide Card" else "Show Card")
        }

        AnimatedVisibility(
            visible = showCard,
            enter = slideInVertically() + fadeIn(),  // combine transitions with +
            exit = slideOutVertically() + fadeOut()
        ) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Text(
                    "I animate in and out!",
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        HorizontalDivider()

        // ── 3. Crossfade ──
        // Smooth transition between different content
        Text("3. Crossfade", style = MaterialTheme.typography.titleMedium)

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            (0..2).forEach { tab ->
                FilterChip(
                    selected = selectedTab == tab,
                    onClick = { selectedTab = tab },
                    label = { Text("Tab $tab") }
                )
            }
        }

        Crossfade(
            targetState = selectedTab,
            animationSpec = tween(300),
            label = "tab_crossfade"
        ) { tab ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Text(
                    "Content for Tab $tab",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        HorizontalDivider()

        // ── 4. animateContentSize ──
        // Smoothly animates size changes of a composable
        Text("4. animateContentSize", style = MaterialTheme.typography.titleMedium)

        var expanded by remember { mutableStateOf(false) }
        Card(
            onClick = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .animateContentSize(  // just add this modifier!
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioLowBouncy
                        )
                    )
                    .padding(16.dp)
            ) {
                Text("Click to ${if (expanded) "collapse" else "expand"}")
                if (expanded) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("This extra content animates in smoothly!")
                    Text("No manual size tracking needed.")
                    Text("animateContentSize handles it all.")
                }
            }
        }
    }
}

// INTERVIEW TIPS — Compose Animations:
// - animateXAsState: simple property animation (alpha, size, color, offset)
// - AnimatedVisibility: enter/exit transitions for showing/hiding content
// - Crossfade: switching between content with fade animation
// - animateContentSize: auto-animate size changes (expandable cards, etc.)
// - updateTransition: coordinate multiple animations together
// - AnimationSpec types: tween (duration), spring (physics), keyframes
// - spring is the DEFAULT and usually the best choice (feels natural)
