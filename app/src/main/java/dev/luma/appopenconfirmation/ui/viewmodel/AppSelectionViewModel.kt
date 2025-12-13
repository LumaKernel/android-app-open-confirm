package dev.luma.appopenconfirmation.ui.viewmodel

import android.app.Application
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dev.luma.appopenconfirmation.data.MonitoredAppsRepository
import dev.luma.appopenconfirmation.data.dataStore
import dev.luma.appopenconfirmation.model.InstalledApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AppSelectionViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = MonitoredAppsRepository(application.dataStore)
    private val packageManager = application.packageManager

    private val _installedApps = MutableStateFlow<List<InstalledApp>>(emptyList())
    val installedApps: StateFlow<List<InstalledApp>> = _installedApps.asStateFlow()

    private val _showSystemApps = MutableStateFlow(false)
    val showSystemApps: StateFlow<Boolean> = _showSystemApps.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    val monitoredApps: StateFlow<Set<String>> = repository.monitoredApps
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptySet()
        )

    init {
        loadInstalledApps()
    }

    private fun loadInstalledApps() {
        viewModelScope.launch {
            _isLoading.value = true
            val apps = withContext(Dispatchers.IO) {
                val installedApplications = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
                val ownPackageName = getApplication<Application>().packageName

                installedApplications
                    .filter { it.packageName != ownPackageName }
                    .filter { packageManager.getLaunchIntentForPackage(it.packageName) != null }
                    .map { appInfo ->
                        InstalledApp(
                            packageName = appInfo.packageName,
                            appName = packageManager.getApplicationLabel(appInfo).toString(),
                            isSystemApp = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
                        )
                    }
                    .sortedBy { it.appName.lowercase() }
            }
            _installedApps.value = apps
            _isLoading.value = false
        }
    }

    fun toggleShowSystemApps() {
        _showSystemApps.value = !_showSystemApps.value
    }

    fun toggleMonitoredApp(packageName: String) {
        viewModelScope.launch {
            repository.toggleMonitoredApp(packageName)
        }
    }

    fun getFilteredApps(): List<InstalledApp> {
        return if (_showSystemApps.value) {
            _installedApps.value
        } else {
            _installedApps.value.filter { !it.isSystemApp }
        }
    }
}
