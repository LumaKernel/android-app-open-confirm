package dev.luma.appopenconfirmation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dev.luma.appopenconfirmation.ui.navigation.AppNavigation
import dev.luma.appopenconfirmation.ui.theme.AppOpenConfirmationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppOpenConfirmationTheme {
                AppNavigation()
            }
        }
    }
}
