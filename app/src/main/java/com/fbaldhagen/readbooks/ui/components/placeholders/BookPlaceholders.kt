package com.fbaldhagen.readbooks.ui.components.placeholders

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.fbaldhagen.readbooks.ui.components.shimmerBackground


@Composable
fun BookCoverPlaceholder(
    modifier: Modifier = Modifier,
    showText: Boolean = true
) {
    Column(modifier) {
        Box(
            modifier = Modifier
                .aspectRatio(0.7f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .shimmerBackground()
        )
        if (showText) {
            Spacer(Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(16.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .shimmerBackground()
            )
            Spacer(Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(14.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .shimmerBackground()
            )
        }
    }
}

@Composable
fun BookCarouselPlaceholder(
    modifier: Modifier = Modifier,
    showTitle: Boolean = true,
    itemCount: Int = 5,
    itemWidth: Dp = 110.dp
) {
    Column(modifier) {
        if (showTitle) {
            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .width(200.dp)
                    .height(24.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .shimmerBackground()
            )
            Spacer(Modifier.height(12.dp))
        }
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            userScrollEnabled = false
        ) {
            items(itemCount) {
                BookCoverPlaceholder(
                    modifier = Modifier.width(itemWidth),
                    showText = false
                )
            }
        }
    }
}