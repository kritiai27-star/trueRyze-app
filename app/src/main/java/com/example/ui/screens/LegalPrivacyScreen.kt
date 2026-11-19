package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.ui.components.BulletRow
import com.example.ui.components.EyebrowText
import com.example.ui.components.GlassCard
import com.example.ui.theme.CobaltIce
import com.example.ui.theme.InkPrimary
import com.example.ui.theme.InkSilver

@Composable
fun LegalPrivacyScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 600.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("legal_back_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = stringResource(R.string.cd_back),
                            tint = InkPrimary
                        )
                    }
                    EyebrowText("Privacy & Terms")
                }

                Text(
                    text = "Data handling & terms",
                    style = MaterialTheme.typography.displayMedium,
                    color = InkPrimary
                )

                GlassCard {
                    Text(
                        text = "What this Android app collects and stores",
                        style = MaterialTheme.typography.titleLarge,
                        color = InkPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    BulletRow("Google Sign-In (Credential Manager & Firebase Auth) verifies your identity before creating or reading cloud records.")
                    BulletRow("Enquiries, career applications, client access requests, support tickets, and portal messages you submit are stored in your private user-scoped Cloud Firestore records.")
                    BulletRow("Your AI scoping questions inside the Client Portal are processed through Firebase AI Logic (protected by Firebase App Check) with your own account context.")
                    BulletRow("This app does not request broad device storage, contacts, camera, or location permissions, and uses bundled local fonts without advertising cookies.", iconTint = CobaltIce)
                }

                GlassCard {
                    Text(
                        text = "Service terms & honesty pledge",
                        style = MaterialTheme.typography.titleLarge,
                        color = InkPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    BulletRow("Only Website Development and Website Maintenance & Support are active services today; coming-soon capabilities are clearly marked as unavailable.", iconTint = CobaltIce)
                    BulletRow("All project deliverables, timelines, and final quotes are confirmed in writing before any paid work begins.", iconTint = CobaltIce)
                    BulletRow("You can archive enquiries, close support tickets, or sign out and disconnect your Google session from the Portal at any time.", iconTint = CobaltIce)
                }
            }
        }
    }
}
