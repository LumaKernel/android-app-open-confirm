package dev.luma.appopenconfirmation.model

data class InstalledApp(
    val packageName: String,
    val appName: String,
    val isSystemApp: Boolean
)
