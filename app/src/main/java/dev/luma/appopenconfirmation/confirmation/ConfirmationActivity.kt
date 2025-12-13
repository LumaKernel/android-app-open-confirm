package dev.luma.appopenconfirmation.confirmation

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import dev.luma.appopenconfirmation.ui.theme.AppOpenConfirmationTheme

class ConfirmationActivity : ComponentActivity() {

    companion object {
        const val EXTRA_PACKAGE_NAME = "extra_package_name"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val targetPackageName = intent.getStringExtra(EXTRA_PACKAGE_NAME) ?: run {
            finish()
            return
        }

        val appName = getAppName(targetPackageName)

        setContent {
            AppOpenConfirmationTheme {
                ConfirmationDialog(
                    appName = appName,
                    onConfirm = {
                        launchApp(targetPackageName)
                        finish()
                    },
                    onCancel = {
                        finish()
                    }
                )
            }
        }
    }

    private fun launchApp(packageName: String) {
        val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
        launchIntent?.let { startActivity(it) }
    }

    private fun getAppName(packageName: String): String {
        return try {
            val appInfo = packageManager.getApplicationInfo(packageName, 0)
            packageManager.getApplicationLabel(appInfo).toString()
        } catch (_: PackageManager.NameNotFoundException) {
            packageName
        }
    }
}

@Composable
fun ConfirmationDialog(
    appName: String,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCancel,
        title = { Text("Open App?") },
        text = { Text("Do you want to open $appName?") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Open")
            }
        },
        dismissButton = {
            TextButton(onClick = onCancel) {
                Text("Cancel")
            }
        }
    )
}
