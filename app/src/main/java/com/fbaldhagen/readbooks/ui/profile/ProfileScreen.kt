package com.fbaldhagen.readbooks.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.fbaldhagen.readbooks.R
import com.fbaldhagen.readbooks.ui.common.TopBarBackground
import com.fbaldhagen.readbooks.ui.common.TopBarState

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    contentPadding: PaddingValues,
    onConfigureTopBar: (TopBarState) -> Unit,
    onNavigateToDebug: () -> Unit,
    onNavigateToProgress: () -> Unit,
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        onConfigureTopBar(
            TopBarState.Standard(
                title = "",
                background = TopBarBackground.Scrim
            )
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        ProfileContent(
            state = state,
            onNavigateToDebug = onNavigateToDebug,
            onNavigateToProgress = onNavigateToProgress,
            contentPadding = contentPadding
        )
    }
}

@Composable
fun ProfileContent(
    state: ProfileState,
    onNavigateToProgress: () -> Unit,
    onNavigateToDebug: () -> Unit,
    contentPadding: PaddingValues
) {
    var clickCount by remember { mutableIntStateOf(0) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .verticalScroll(rememberScrollState())
    ) {
        UserHeader(
            userName = "F. Baldhagen",
            avatarUrl = null,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp)
        )

        ProfileListSection(
            title = "Progress",
            items = listOf(
                ProfileItem(title = "My Stats & Achievements", onClick = onNavigateToProgress)
            )
        )

        ProfileListSection(
            title = "Settings",
            items = listOf(
                ProfileItem("Reader Preferences", onClick = { }),
                ProfileItem("Appearance", onClick = { }),
                ProfileItem("Notifications", onClick = { })
            )
        )

        ProfileListSection(
            title = "Support",
            items = listOf(
                ProfileItem("Help & Feedback", onClick = { }),
                ProfileItem("About", onClick = { })
            ),
            modifier = Modifier.clickable {
                clickCount++
                if (clickCount >= 7) {
                    clickCount = 0
                    onNavigateToDebug()
                }
            }
        )
    }
}

@Composable
fun UserHeader(
    userName: String,
    avatarUrl: String?,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        AsyncImage(
            model = avatarUrl,
            contentDescription = "User Avatar",
            placeholder = painterResource(id = R.drawable.ic_default_avatar),
            error = painterResource(id = R.drawable.ic_default_avatar),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        )
        Text(
            text = userName,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun ProfileListSection(
    title: String,
    items: List<ProfileItem>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        items.forEach { item ->
            ListItem(
                headlineContent = { Text(item.title) },
                leadingContent = item.icon?.let {
                    { Icon(imageVector = it, contentDescription = null) }
                },
                modifier = Modifier.clickable(onClick = item.onClick),
                trailingContent = {
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            )
        }
    }
}