package com.fbaldhagen.readbooks.ui.profile

import androidx.compose.ui.graphics.vector.ImageVector

data class ProfileItem(
    val title: String,
    val onClick: () -> Unit,
    val icon: ImageVector? = null
)