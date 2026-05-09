package com.pos.portablebilling

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pos.portablebilling.ui.screens.DashboardScreen
import com.pos.portablebilling.ui.screens.ManageItemsScreen
import com.pos.portablebilling.ui.viewmodel.BillingViewModel
import com.pos.portablebilling.ui.viewmodel.BillingViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(
                colorScheme = androidx.compose.material3.dynamicLightColorScheme(this)
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PortableBillingApp()
                }
            }
        }
    }
}

@Composable
fun PortableBillingApp() {
    val navController = rememberNavController()
    
    // Get application context correctly
    val context = androidx.compose.ui.platform.LocalContext.current
    val application = context.applicationContext as PortableBillingApplication
    
    // Instantiate ViewModel via Factory
    val viewModel: BillingViewModel = viewModel(
        factory = BillingViewModelFactory(application, application.useCases)
    )

    NavHost(navController = navController, startDestination = "dashboard") {
        composable("dashboard") {
            DashboardScreen(
                viewModel = viewModel,
                onNavigateToSettings = { navController.navigate("manage_items") }
            )
        }
        composable("manage_items") {
            ManageItemsScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
