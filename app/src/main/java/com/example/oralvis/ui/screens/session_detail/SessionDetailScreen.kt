package com.example.oralvis.ui.screens.session_detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.oralvis.ui.screens.home.KeyValueRow
import com.example.oralvis.ui.screens.utils.toFormattedDate
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionDetailScreen(
    viewModel: SessionDetailViewModel = koinViewModel()
) {
    val state by viewModel.stateFlow.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Session Details") })
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                state.loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) { CircularProgressIndicator() }
                }
                state.session == null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) { Text("Session not found") }
                }
                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
                    ) {
                        KeyValueRow("Session Id :", state.session?.sessionId.toString())
                        KeyValueRow("Session Name :", state.session?.name.toString())
                        KeyValueRow("Age :", state.session?.age.toString())
                        KeyValueRow("Started At :", state.session?.startedAt?.toFormattedDate()?:"")
                        KeyValueRow("Updated At :", state.session?.updatedAt?.toFormattedDate() ?: "---")
                        KeyValueRow("Total Photos :", state.session?.totalPhotos.toString())

                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Photos:", style = MaterialTheme.typography.titleMedium)

                        if (state.photos.isEmpty()) {
                            Text("No photos yet")
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                contentPadding = PaddingValues(top = 8.dp)
                            ) {
                                items(state.photos) { photo ->
//                                    Image(
//                                        painter = rememberAsyncImagePainter(
//                                            ImageRequest.Builder(LocalContext.current)
//                                                .data(photo.contentUri)
//                                                .build()
//                                        ),
//                                        contentDescription = null,
//                                        modifier = Modifier
//                                            .fillMaxWidth()
//                                            .height(200.dp),
//                                        contentScale = ContentScale.Crop
//                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
