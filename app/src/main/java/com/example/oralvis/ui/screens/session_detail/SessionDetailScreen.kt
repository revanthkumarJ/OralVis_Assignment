package com.example.oralvis.ui.screens.session_detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.oralvis.ui.screens.home.KeyValueRow
import com.example.oralvis.R
import com.example.oralvis.R.drawable.ic_launcher_foreground
import com.example.oralvis.ui.screens.utils.toFormattedDate
import org.koin.androidx.compose.koinViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionDetailScreen(
    onBackClick: () -> Unit,
    viewModel: SessionDetailViewModel = koinViewModel()
) {
    val state by viewModel.stateFlow.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Session Details",
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
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
                            .padding(16.dp)
                    ) {
                        KeyValueRow("Session Id :", state.session?.sessionId.toString())
                        KeyValueRow("Session Name :", state.session?.name.toString())
                        KeyValueRow("Age :", state.session?.age.toString())
                        KeyValueRow(
                            "Started At :", state.session?.startedAt?.toFormattedDate() ?: ""
                        )
                        KeyValueRow(
                            "Updated At :", state.session?.updatedAt?.toFormattedDate() ?: "---"
                        )
                        KeyValueRow("Total Photos :", state.session?.totalPhotos.toString())

                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Photos:", style = MaterialTheme.typography.titleMedium)

                        if (state.photos.isEmpty()) {
                            Text("No photos yet")
                        } else {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(3),
                                modifier = Modifier.fillMaxHeight(),
                                verticalArrangement = Arrangement.spacedBy(2.dp),
                                horizontalArrangement = Arrangement.spacedBy(2.dp),
                                contentPadding = PaddingValues(8.dp)
                            ) {
                                items(state.photos) { photo ->
                                    Image(
                                        painter = rememberAsyncImagePainter(
                                            ImageRequest.Builder(LocalContext.current)
                                                .data(photo.contentUri)
                                                .error(R.drawable.ic_launcher_foreground)
                                                .build()
                                        ),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(150.dp)
                                            .padding(4.dp)
                                            .clickable {
                                                viewModel.trySendAction(
                                                    SessionDetailAction.ShowPhotoDialog(photo)
                                                )
                                            },
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }
                        }
                    }
                }
            }

            state.selectedPhoto?.let { photo ->
                AlertDialog(
                    onDismissRequest = {
                        viewModel.trySendAction(SessionDetailAction.DismissPhotoDialog)
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            viewModel.trySendAction(SessionDetailAction.DismissPhotoDialog)
                        }) {
                            Text("Close")
                        }
                    },
                    title = { Text("Photo Preview") },
                    text = {
                        Image(
                            painter = rememberAsyncImagePainter(
                                ImageRequest.Builder(LocalContext.current)
                                    .data(photo.contentUri)
                                    .error(R.drawable.ic_launcher_foreground)
                                    .build()
                            ),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(400.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
                )
            }
        }
    }
}
