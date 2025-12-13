package dev.luma.appopenconfirmation.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import dev.luma.appopenconfirmation.confirmation.ConfirmationActivity
import dev.luma.appopenconfirmation.data.PreferencesKeys
import dev.luma.appopenconfirmation.data.dataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class AppOpenMonitorService : AccessibilityService() {

    private var monitoredApps: Set<String> = emptySet()
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var lastTriggeredPackage: String? = null
    private var lastTriggeredTime: Long = 0

    override fun onServiceConnected() {
        super.onServiceConnected()
        scope.launch {
            applicationContext.dataStore.data
                .map { it[PreferencesKeys.MONITORED_APPS] ?: emptySet() }
                .collect { apps -> monitoredApps = apps }
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return

        val packageName = event.packageName?.toString() ?: return

        // Skip our own app and system UI
        if (packageName == this.packageName) return
        if (packageName == "com.android.systemui") return
        if (packageName == "com.android.launcher3") return

        // Skip if this is the confirmation activity launching
        val className = event.className?.toString() ?: ""
        if (className.contains("ConfirmationActivity")) return

        // Debounce: avoid triggering multiple times for the same app within 2 seconds
        val currentTime = System.currentTimeMillis()
        if (packageName == lastTriggeredPackage && currentTime - lastTriggeredTime < 2000) {
            return
        }

        // Check if this app is monitored
        if (packageName in monitoredApps) {
            lastTriggeredPackage = packageName
            lastTriggeredTime = currentTime

            // Go back to home
            performGlobalAction(GLOBAL_ACTION_HOME)

            // Show confirmation dialog
            showConfirmationDialog(packageName)
        }
    }

    private fun showConfirmationDialog(packageName: String) {
        val intent = Intent(this, ConfirmationActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra(ConfirmationActivity.EXTRA_PACKAGE_NAME, packageName)
        }
        startActivity(intent)
    }

    override fun onInterrupt() {
        // Required override
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}
