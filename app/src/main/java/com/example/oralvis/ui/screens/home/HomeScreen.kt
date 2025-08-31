package com.example.oralvis.ui.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.oralvis.data.db.SessionEntity
import com.example.oralvis.ui.screens.utils.EventsEffect
import com.example.oralvis.ui.screens.utils.toFormattedDate
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel(),
    onStartCamera: () -> Unit,
    onOpenSession: (Long) -> Unit
) {
    val state by viewModel.stateFlow.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.trySendAction(HomeAction.RefreshSessions)
    }

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            is HomeEvent.NavigateToCamera -> onStartCamera()
            is HomeEvent.NavigateToSession -> onOpenSession(event.sessionId)
        }
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("OralVis") },
                    colors = topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
                    )
                )
                OutlinedTextField(
                    value = state.query,
                    onValueChange = { viewModel.trySendAction(HomeAction.UpdateQuery(it)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    label = { Text("Search by Id") }
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.trySendAction(HomeAction.StartCamera) }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Start Session")
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (state.loading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                if (state.sessions.isEmpty()) {
                    Text(
                        text="No sessions yet. Click the + button to start one.",
                        modifier = Modifier.padding(16.dp)
                    )
                } else {
                    val filteredData=state.sessions.filter {
                        it.sessionId?.contains(state.query, ignoreCase = true) ?: false
                    }
                    if(filteredData.isEmpty()){
                        Text(
                            text="No sessions found for given search",
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                    else{
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(
                                filteredData
                            ) { session ->
                                SessionItem(session) {
                                    viewModel.trySendAction(HomeAction.OpenSession(session.id))
                                }
                            }
                        }
                    }

                }
            }
        }
    }
}

@Composable
fun SessionItem(session: SessionEntity, onClick: () -> Unit) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            KeyValueRow("Session Id :", session.sessionId.toString())
            KeyValueRow("Session Name :", session.name.toString())
            KeyValueRow("Age :", session.age.toString())
            KeyValueRow("Started At :", session.startedAt.toFormattedDate())
            KeyValueRow("Updated At :", session.updatedAt?.toFormattedDate() ?: "---")
            KeyValueRow("Total Photos :", session.totalPhotos.toString())
        }
    }
}

@Composable
fun KeyValueRow(key: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(key, style = MaterialTheme.typography.bodySmall)
        Text(value, style = MaterialTheme.typography.bodySmall)
    }
}
