package dev.luma.appopenconfirmation.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.luma.appopenconfirmation.ui.component.AppListItem
import dev.luma.appopenconfirmation.ui.viewmodel.AppSelectionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppSelectionScreen(
    onNavigateBack: () -> Unit,
    viewModel: AppSelectionViewModel = viewModel()
) {
    val context = LocalContext.current
    val packageManager = context.packageManager
    val installedApps by viewModel.installedApps.collectAsState()
    val monitoredApps by viewModel.monitoredApps.collectAsState()
    val showSystemApps by viewModel.showSystemApps.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val filteredApps = if (showSystemApps) {
        installedApps
    } else {
        installedApps.filter { !it.isSystemApp }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Select Apps") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // System Apps Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Show System Apps",
                    modifier = Modifier.weight(1f)
                )
                Switch(
                    checked = showSystemApps,
                    onCheckedChange = { viewModel.toggleShowSystemApps() }
                )
            }

            HorizontalDivider()

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(
                        items = filteredApps,
                        key = { it.packageName }
                    ) { app ->
                        val appIcon = try {
                            packageManager.getApplicationIcon(app.packageName)
                        } catch (_: Exception) {
                            null
                        }

                        AppListItem(
                            appName = app.appName,
                            packageName = app.packageName,
                            appIcon = appIcon,
                            isChecked = app.packageName in monitoredApps,
                            onCheckedChange = { viewModel.toggleMonitoredApp(app.packageName) }
                        )
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}
