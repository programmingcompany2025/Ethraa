package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.*
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.AppViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val viewModel: AppViewModel = viewModel()
                var currentTab by remember { mutableIntStateOf(0) }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        NavigationBar(
                            modifier = Modifier.testTag("bottom_nav_bar")
                        ) {
                            NavigationBarItem(
                                selected = currentTab == 0,
                                onClick = { currentTab = 0 },
                                icon = { Icon(imageVector = Icons.Default.Dashboard, contentDescription = "الرئيسية") },
                                label = { Text("الرئيسية", style = MaterialTheme.typography.labelSmall) }
                            )
                            NavigationBarItem(
                                selected = currentTab == 1,
                                onClick = { currentTab = 1 },
                                icon = { Icon(imageVector = Icons.Default.BusinessCenter, contentDescription = "العمليات") },
                                label = { Text("العمليات", style = MaterialTheme.typography.labelSmall) }
                            )
                            NavigationBarItem(
                                selected = currentTab == 2,
                                onClick = { currentTab = 2 },
                                icon = { Icon(imageVector = Icons.Default.Analytics, contentDescription = "التحليلات") },
                                label = { Text("التحليلات", style = MaterialTheme.typography.labelSmall) }
                            )
                            NavigationBarItem(
                                selected = currentTab == 3,
                                onClick = { currentTab = 3 },
                                icon = { Icon(imageVector = Icons.Default.Psychology, contentDescription = "الذكاء الاصطناعي") },
                                label = { Text("الذكاء الاصطناعي", style = MaterialTheme.typography.labelSmall) }
                            )
                            NavigationBarItem(
                                selected = currentTab == 4,
                                onClick = { currentTab = 4 },
                                icon = { Icon(imageVector = Icons.Default.ManageAccounts, contentDescription = "الإدارة") },
                                label = { Text("الإدارة", style = MaterialTheme.typography.labelSmall) }
                            )
                        }
                    }
                ) { innerPadding ->
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        when (currentTab) {
                            0 -> DashboardScreen(viewModel = viewModel, onNavigateToTab = { currentTab = it })
                            1 -> OperationsScreen(viewModel = viewModel)
                            2 -> InsightsScreen(viewModel = viewModel)
                            3 -> AiScreen(viewModel = viewModel)
                            4 -> AdminScreen(viewModel = viewModel)
                        }
                    }
                }
            }
        }
    }
}
