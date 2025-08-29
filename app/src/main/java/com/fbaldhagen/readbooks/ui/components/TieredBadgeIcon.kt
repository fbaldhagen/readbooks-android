package com.fbaldhagen.readbooks.ui.components

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun TieredBadgeIcon(
    primaryColor: Color,
    accentColor: Color,
    modifier: Modifier = Modifier,
    contentDescription: String? = null
) {
    val badgeImageVector = remember(primaryColor, accentColor) {
        ImageVector.Builder(
            name = "TieredBadge",
            defaultWidth = 64.dp,
            defaultHeight = 64.dp,
            viewportWidth = 64f,
            viewportHeight = 64f
        ).apply {

            path(fill = SolidColor(primaryColor)) {
                moveTo(35.8f, 23.7f)
                horizontalLineToRelative(17.9f)
                curveTo(53.3f, 14.9f, 50.4f, 9f, 50.4f, 9f)
                reflectiveCurveToRelative(-5.6f, 0.8f, -9.5f, 0f)
                curveToRelative(-1.8f, -0.4f, -3.6f, -1.2f, -5.1f, -2.1f)
                verticalLineToRelative(16.8f)
            }

            path(fill = SolidColor(primaryColor)) {
                moveTo(28.2f, 23.7f)
                verticalLineTo(6.9f)
                curveToRelative(-1.4f, 0.9f, -3.2f, 1.7f, -5f, 2.1f)
                curveToRelative(-4f, 0.8f, -9.5f, 0f, -9.5f, 0f)
                reflectiveCurveToRelative(-3f, 5.9f, -3.3f, 14.7f)
                horizontalLineToRelative(17.8f)
            }

            path(fill = SolidColor(primaryColor)) {
                moveTo(28.2f, 31.1f)
                horizontalLineTo(10.6f)
                curveToRelative(0.1f, 0.4f, 0.1f, 0.8f, 0.2f, 1.2f)
                curveTo(12.6f, 43.6f, 22.7f, 53f, 28.2f, 57.4f)
                verticalLineTo(31.1f)
            }

            path(fill = SolidColor(primaryColor)) {
                moveTo(35.8f, 31.1f)
                verticalLineToRelative(26.3f)
                curveToRelative(5.6f, -4.4f, 15.6f, -13.8f, 17.4f, -25.1f)
                curveToRelative(0.1f, -0.4f, 0.1f, -0.8f, 0.2f, -1.1f)
                lineToRelative(-17.6f, -0.1f)
            }

            path(fill = SolidColor(accentColor)) {
                moveTo(36.5f, 22.7f)
                horizontalLineToRelative(17.9f)
                curveTo(54.1f, 13.9f, 51.2f, 8f, 51.2f, 8f)
                reflectiveCurveToRelative(-5.6f, 0.8f, -9.5f, 0f)
                curveToRelative(-1.9f, -0.3f, -3.7f, -1.2f, -5.2f, -2f)
                verticalLineToRelative(16.7f)
            }

            path(fill = SolidColor(accentColor)) {
                moveTo(27.5f, 22.7f)
                verticalLineTo(6f)
                curveToRelative(-1.5f, 0.8f, -3.3f, 1.7f, -5.1f, 2f)
                curveToRelative(-4f, 0.8f, -9.5f, 0f, -9.5f, 0f)
                reflectiveCurveToRelative(-3f, 5.9f, -3.3f, 14.7f)
                horizontalLineToRelative(17.9f)
            }

            path(fill = SolidColor(accentColor)) {
                moveTo(27.5f, 31.8f)
                horizontalLineTo(9.8f)
                curveToRelative(0.1f, 0.4f, 0.1f, 0.8f, 0.2f, 1.1f)
                curveTo(11.9f, 44.2f, 21.9f, 53.6f, 27.5f, 58f)
                verticalLineTo(31.8f)
            }

            path(fill = SolidColor(accentColor)) {
                moveTo(36.5f, 31.8f)
                verticalLineTo(58f)
                curveToRelative(5.6f, -4.4f, 15.6f, -13.8f, 17.4f, -25.1f)
                curveToRelative(0.1f, -0.4f, 0.1f, -0.8f, 0.2f, -1.1f)
                horizontalLineTo(36.5f)
            }

            path(fill = SolidColor(accentColor)) {
                moveTo(29f, 3.8f)
                horizontalLineToRelative(6f)
                verticalLineTo(61f)
                horizontalLineToRelative(-6f)
            }

            path(fill = SolidColor(accentColor)) {
                moveTo(8.8f, 24.3f)
                horizontalLineToRelative(46.4f)
                verticalLineToRelative(6f)
                horizontalLineTo(8.8f)
            }

            path(fill = SolidColor(accentColor)) {
                moveTo(32f, 64f)
                curveToRelative(-0.3f, 0f, -0.7f, -0.1f, -1f, -0.3f)
                curveToRelative(-0.8f, -0.6f, -20.6f, -13.7f, -23.4f, -31.4f)
                curveToRelative(-2.4f, -14.7f, 3f, -25.8f, 3.2f, -26.2f)
                curveToRelative(0.4f, -0.7f, 1.1f, -1.1f, 1.9f, -1f)
                curveToRelative(0f, 0f, 2.6f, 0.4f, 5.4f, 0.4f)
                curveToRelative(1.6f, 0f, 3f, -0.1f, 4.2f, -0.3f)
                curveToRelative(4.2f, -0.8f, 8.5f, -4.6f, 8.6f, -4.6f)
                curveToRelative(0.7f, -0.6f, 1.7f, -0.6f, 2.4f, 0f)
                curveToRelative(0f, 0f, 4.4f, 3.8f, 8.6f, 4.6f)
                curveToRelative(1.1f, 0.2f, 2.5f, 0.3f, 4.2f, 0.3f)
                curveToRelative(2.8f, 0f, 5.4f, -0.4f, 5.4f, -0.4f)
                curveToRelative(0.8f, -0.1f, 1.5f, 0.3f, 1.9f, 1f)
                curveToRelative(0.2f, 0.5f, 5.6f, 11.5f, 3.2f, 26.2f)
                curveTo(53.6f, 50f, 33.8f, 63.2f, 33f, 63.7f)
                curveToRelative(-0.3f, 0.2f, -0.7f, 0.3f, -1f, 0.3f)
                moveTo(13.5f, 8.9f)
                curveToRelative(-1.2f, 3.1f, -4.1f, 11.9f, -2.4f, 22.8f)
                curveTo(13.4f, 45.6f, 28f, 57.1f, 32f, 59.9f)
                curveToRelative(4f, -2.9f, 18.6f, -14.3f, 20.8f, -28.2f)
                curveToRelative(1.7f, -10.9f, -1.1f, -19.8f, -2.4f, -22.8f)
                curveToRelative(-1.1f, 0.1f, -2.7f, 0.2f, -4.5f, 0.2f)
                curveToRelative(-1.9f, 0f, -3.5f, -0.1f, -4.9f, -0.4f)
                curveToRelative(-3.7f, -0.7f, -7.3f, -3.2f, -9.1f, -4.5f)
                curveToRelative(-1.8f, 1.4f, -5.4f, 3.8f, -9.1f, 4.5f)
                curveToRelative(-1.4f, 0.3f, -3f, 0.4f, -4.9f, 0.4f)
                curveToRelative(-1.6f, 0f, -3.3f, -0.1f, -4.4f, -0.2f)
            }

            path(fill = SolidColor(accentColor)) {
                moveTo(32f, 61.7f)
                curveToRelative(-0.3f, 0f, -0.6f, -0.1f, -0.9f, -0.3f)
                curveToRelative(-0.8f, -0.5f, -19.1f, -12.7f, -21.7f, -29.1f)
                curveToRelative(-2.2f, -13.7f, 2.8f, -23.9f, 3f, -24.3f)
                curveToRelative(0.3f, -0.7f, 1f, -1f, 1.7f, -0.9f)
                curveToRelative(0f, 0f, 2.4f, 0.3f, 5f, 0.3f)
                curveToRelative(1.5f, 0f, 2.8f, -0.1f, 3.9f, -0.3f)
                curveToRelative(3.9f, -0.8f, 7.9f, -4.3f, 8f, -4.3f)
                curveToRelative(0.6f, -0.5f, 1.6f, -0.5f, 2.2f, 0f)
                curveToRelative(0f, 0f, 4.1f, 3.5f, 8f, 4.3f)
                curveToRelative(1.1f, 0.2f, 2.3f, 0.3f, 3.9f, 0.3f)
                curveToRelative(2.6f, 0f, 5f, -0.3f, 5f, -0.3f)
                curveToRelative(0.7f, -0.1f, 1.4f, 0.3f, 1.7f, 0.9f)
                curveToRelative(0.2f, 0.4f, 5.2f, 10.7f, 3f, 24.3f)
                curveTo(52f, 48.7f, 33.7f, 60.9f, 32.9f, 61.4f)
                curveToRelative(-0.3f, 0.2f, -0.6f, 0.3f, -0.9f, 0.3f)
                moveTo(14.9f, 10.6f)
                curveToRelative(-1.1f, 2.9f, -3.8f, 11.1f, -2.2f, 21.2f)
                curveTo(14.8f, 44.6f, 28.3f, 55.2f, 32f, 57.9f)
                curveToRelative(3.7f, -2.7f, 17.2f, -13.2f, 19.3f, -26.2f)
                curveToRelative(1.6f, -10.1f, -1.1f, -18.3f, -2.2f, -21.2f)
                curveToRelative(-1f, 0.1f, -2.5f, 0.2f, -4.2f, 0.2f)
                curveToRelative(-1.7f, 0f, -3.3f, -0.1f, -4.5f, -0.4f)
                curveToRelative(-3.4f, -0.6f, -6.7f, -2.8f, -8.4f, -4.1f)
                curveToRelative(-1.7f, 1.3f, -5f, 3.5f, -8.4f, 4.2f)
                curveToRelative(-1.3f, 0.3f, -2.8f, 0.4f, -4.5f, 0.4f)
                curveToRelative(-1.7f, 0f, -3.2f, -0.1f, -4.2f, -0.2f)
            }

            path(fill = SolidColor(accentColor)) {
                moveTo(32f, 3.1f)
                moveToRelative(-1.4f, 0f)
                arcToRelative(1.4f, 1.4f, 0f,
                    isMoreThanHalf = true,
                    isPositiveArc = true,
                    dx1 = 2.8f,
                    dy1 = 0f
                )
                arcToRelative(1.4f, 1.4f, 0f,
                    isMoreThanHalf = true,
                    isPositiveArc = true,
                    dx1 = -2.8f,
                    dy1 = 0f
                )
            }

            path(fill = SolidColor(accentColor)) {
                moveTo(50.9f, 8f)
                moveToRelative(-1.4f, 0f)
                arcToRelative(1.4f, 1.4f, 0f,
                    isMoreThanHalf = true,
                    isPositiveArc = true,
                    dx1 = 2.8f,
                    dy1 = 0f
                )
                arcToRelative(1.4f, 1.4f, 0f,
                    isMoreThanHalf = true,
                    isPositiveArc = true,
                    dx1 = -2.8f,
                    dy1 = 0f
                )
            }

            path(fill = SolidColor(accentColor)) {
                moveTo(54.3f, 21.2f)
                moveToRelative(-1.4f, 0f)
                arcToRelative(1.4f, 1.4f, 0f,
                    isMoreThanHalf = true,
                    isPositiveArc = true,
                    dx1 = 2.8f,
                    dy1 = 0f
                )
                arcToRelative(1.4f, 1.4f, 0f,
                    isMoreThanHalf = true,
                    isPositiveArc = true,
                    dx1 = -2.8f,
                    dy1 = 0f
                )
            }

            path(fill = SolidColor(accentColor)) {
                moveTo(52.3f, 37.7f)
                moveToRelative(-1.4f, 0f)
                arcToRelative(1.4f, 1.4f, 0f,
                    isMoreThanHalf = true,
                    isPositiveArc = true,
                    dx1 = 2.8f,
                    dy1 = 0f
                )
                arcToRelative(1.4f, 1.4f, 0f,
                    isMoreThanHalf = true,
                    isPositiveArc = true,
                    dx1 = -2.8f,
                    dy1 = 0f
                )
            }

            path(fill = SolidColor(accentColor)) {
                moveTo(44f, 50.5f)
                moveToRelative(-1.4f, 0f)
                arcToRelative(1.4f, 1.4f, 0f,
                    isMoreThanHalf = true,
                    isPositiveArc = true,
                    dx1 = 2.8f,
                    dy1 = 0f
                )
                arcToRelative(1.4f, 1.4f, 0f,
                    isMoreThanHalf = true,
                    isPositiveArc = true,
                    dx1 = -2.8f,
                    dy1 = 0f
                )
            }

            path(fill = SolidColor(accentColor)) {
                moveTo(32f, 60.7f)
                moveToRelative(-1.4f, 0f)
                arcToRelative(1.4f, 1.4f, 0f,
                    isMoreThanHalf = true,
                    isPositiveArc = true,
                    dx1 = 2.8f,
                    dy1 = 0f
                )
                arcToRelative(1.4f, 1.4f, 0f,
                    isMoreThanHalf = true,
                    isPositiveArc = true,
                    dx1 = -2.8f,
                    dy1 = 0f
                )
            }

            path(fill = SolidColor(accentColor)) {
                moveTo(20f, 50.5f)
                moveToRelative(-1.4f, 0f)
                arcToRelative(1.4f, 1.4f, 0f,
                    isMoreThanHalf = true,
                    isPositiveArc = true,
                    dx1 = 2.8f,
                    dy1 = 0f
                )
                arcToRelative(1.4f, 1.4f, 0f,
                    isMoreThanHalf = true,
                    isPositiveArc = true,
                    dx1 = -2.8f,
                    dy1 = 0f
                )
            }

            path(fill = SolidColor(accentColor)) {
                moveTo(11.7f, 37.7f)
                moveToRelative(-1.4f, 0f)
                arcToRelative(1.4f, 1.4f, 0f,
                    isMoreThanHalf = true,
                    isPositiveArc = true,
                    dx1 = 2.8f,
                    dy1 = 0f
                )
                arcToRelative(1.4f, 1.4f, 0f,
                    isMoreThanHalf = true,
                    isPositiveArc = true,
                    dx1 = -2.8f,
                    dy1 = 0f
                )
            }

            path(fill = SolidColor(accentColor)) {
                moveTo(9.7f, 21.2f)
                moveToRelative(-1.4f, 0f)
                arcToRelative(1.4f, 1.4f, 0f,
                    isMoreThanHalf = true,
                    isPositiveArc = true,
                    dx1 = 2.8f,
                    dy1 = 0f
                )
                arcToRelative(1.4f, 1.4f, 0f,
                    isMoreThanHalf = true,
                    isPositiveArc = true,
                    dx1 = -2.8f,
                    dy1 = 0f
                )
            }

            path(fill = SolidColor(accentColor)) {
                moveTo(13.1f, 8f)
                moveToRelative(-1.4f, 0f)
                arcToRelative(1.4f, 1.4f, 0f,
                    isMoreThanHalf = true,
                    isPositiveArc = true,
                    dx1 = 2.8f,
                    dy1 = 0f
                )
                arcToRelative(1.4f, 1.4f, 0f,
                    isMoreThanHalf = true,
                    isPositiveArc = true,
                    dx1 = -2.8f,
                    dy1 = 0f
                )
            }

            path(fill = SolidColor(Color.White)) {
                moveTo(28f, 3.8f)
                horizontalLineToRelative(8f)
                verticalLineTo(61f)
                horizontalLineToRelative(-8f)
            }

            path(fill = SolidColor(Color.White)) {
                moveTo(8.8f, 23.3f)
                horizontalLineToRelative(46.4f)
                verticalLineToRelative(8f)
                horizontalLineTo(8.8f)
            }

            path(fill = SolidColor(primaryColor)) {
                moveTo(32f, 3.7f)
                curveToRelative(-0.4f, 0f, -0.8f, -0.2f, -1.1f, -0.5f)
                curveToRelative(0.1f, 0.6f, 0.5f, 1f, 1.1f, 1f)
                reflectiveCurveToRelative(1f, -0.4f, 1.1f, -1f)
                curveToRelative(-0.3f, 0.3f, -0.7f, 0.5f, -1.1f, 0.5f)
            }

            path(fill = SolidColor(primaryColor)) {
                moveTo(50.9f, 8.6f)
                curveToRelative(-0.4f, 0f, -0.8f, -0.2f, -1.1f, -0.5f)
                curveToRelative(0.1f, 0.6f, 0.5f, 1f, 1.1f, 1f)
                curveToRelative(0.6f, 0f, 1f, -0.4f, 1.1f, -1f)
                curveToRelative(-0.3f, 0.3f, -0.7f, 0.5f, -1.1f, 0.5f)
            }

            path(fill = SolidColor(primaryColor)) {
                moveTo(54.3f, 21.8f)
                curveToRelative(-0.4f, 0f, -0.8f, -0.2f, -1.1f, -0.5f)
                curveToRelative(0.1f, 0.6f, 0.5f, 1f, 1.1f, 1f)
                curveToRelative(0.6f, 0f, 1f, -0.4f, 1.1f, -1f)
                curveToRelative(-0.3f, 0.3f, -0.7f, 0.5f, -1.1f, 0.5f)
            }

            path(fill = SolidColor(primaryColor)) {
                moveTo(52.3f, 38.2f)
                curveToRelative(-0.4f, 0f, -0.8f, -0.2f, -1.1f, -0.5f)
                curveToRelative(0.1f, 0.6f, 0.5f, 1f, 1.1f, 1f)
                reflectiveCurveToRelative(1f, -0.4f, 1.1f, -1f)
                curveToRelative(-0.3f, 0.3f, -0.7f, 0.5f, -1.1f, 0.5f)
            }

            path(fill = SolidColor(primaryColor)) {
                moveTo(44f, 51f)
                curveToRelative(-0.4f, 0f, -0.8f, -0.2f, -1.1f, -0.5f)
                curveToRelative(0.1f, 0.6f, 0.5f, 1f, 1.1f, 1f)
                reflectiveCurveToRelative(1f, -0.4f, 1.1f, -1f)
                curveToRelative(-0.3f, 0.4f, -0.7f, 0.5f, -1.1f, 0.5f)
            }

            path(fill = SolidColor(primaryColor)) {
                moveTo(32f, 61.3f)
                curveToRelative(-0.4f, 0f, -0.8f, -0.2f, -1.1f, -0.5f)
                curveToRelative(0.1f, 0.6f, 0.5f, 1f, 1.1f, 1f)
                curveToRelative(0.6f, 0f, 1f, -0.4f, 1.1f, -1f)
                curveToRelative(-0.3f, 0.3f, -0.7f, 0.5f, -1.1f, 0.5f)
            }

            path(fill = SolidColor(primaryColor)) {
                moveTo(20f, 51f)
                curveToRelative(-0.4f, 0f, -0.8f, -0.2f, -1.1f, -0.5f)
                curveToRelative(0.1f, 0.6f, 0.5f, 1f, 1.1f, 1f)
                curveToRelative(0.6f, 0f, 1f, -0.4f, 1.1f, -1f)
                curveToRelative(-0.3f, 0.4f, -0.7f, 0.5f, -1.1f, 0.5f)
            }

            path(fill = SolidColor(primaryColor)) {
                moveTo(11.7f, 38.2f)
                curveToRelative(-0.4f, 0f, -0.8f, -0.2f, -1.1f, -0.5f)
                curveToRelative(0.1f, 0.6f, 0.5f, 1f, 1.1f, 1f)
                curveToRelative(0.6f, 0f, 1f, -0.4f, 1.1f, -1f)
                curveToRelative(-0.3f, 0.3f, -0.7f, 0.5f, -1.1f, 0.5f)
            }

            path(fill = SolidColor(primaryColor)) {
                moveTo(9.7f, 21.8f)
                curveToRelative(-0.4f, 0f, -0.8f, -0.2f, -1.1f, -0.5f)
                curveToRelative(0.1f, 0.6f, 0.5f, 1f, 1.1f, 1f)
                curveToRelative(0.6f, 0f, 1f, -0.4f, 1.1f, -1f)
                curveToRelative(-0.3f, 0.3f, -0.7f, 0.5f, -1.1f, 0.5f)
            }

            path(fill = SolidColor(primaryColor)) {
                moveTo(13.1f, 8.6f)
                curveToRelative(-0.4f, 0f, -0.8f, -0.2f, -1.1f, -0.5f)
                curveToRelative(0.1f, 0.6f, 0.5f, 1f, 1.1f, 1f)
                curveToRelative(0.6f, 0f, 1f, -0.4f, 1.1f, -1f)
                curveToRelative(-0.3f, 0.3f, -0.7f, 0.5f, -1.1f, 0.5f)
            }

            path(fill = SolidColor(primaryColor)) {
                moveTo(32f, 60.8f)
                curveToRelative(-0.3f, 0f, -0.6f, -0.1f, -0.9f, -0.3f)
                curveToRelative(-0.7f, -0.5f, -18.5f, -12.3f, -21.1f, -28.2f)
                curveTo(7.9f, 19f, 12.7f, 9.1f, 12.9f, 8.6f)
                curveToRelative(0.3f, -0.6f, 1f, -1f, 1.7f, -0.9f)
                curveToRelative(0f, 0f, 2.3f, 0.3f, 4.9f, 0.3f)
                curveToRelative(1.5f, 0f, 2.7f, -0.1f, 3.7f, -0.3f)
                curveToRelative(3.8f, -0.7f, 7.7f, -4.1f, 7.7f, -4.1f)
                curveToRelative(0.6f, -0.5f, 1.5f, -0.5f, 2.1f, 0f)
                curveToRelative(0f, 0f, 4f, 3.4f, 7.7f, 4.2f)
                curveToRelative(1f, 0.2f, 2.3f, 0.3f, 3.7f, 0.3f)
                curveToRelative(2.6f, 0f, 4.8f, -0.3f, 4.9f, -0.3f)
                curveToRelative(0.7f, -0.1f, 1.4f, 0.3f, 1.7f, 0.9f)
                curveToRelative(0.2f, 0.4f, 5f, 10.4f, 2.9f, 23.6f)
                curveToRelative(-2.5f, 15.9f, -20.3f, 27.7f, -21f, 28.2f)
                curveToRelative(-0.3f, 0.2f, -0.6f, 0.3f, -0.9f, 0.3f)
                moveTo(15.4f, 11.2f)
                curveTo(14.3f, 14f, 11.7f, 22f, 13.3f, 31.8f)
                curveToRelative(2f, 12.5f, 15.2f, 22.8f, 18.7f, 25.4f)
                curveToRelative(3.6f, -2.6f, 16.7f, -12.9f, 18.7f, -25.4f)
                curveToRelative(1.6f, -9.8f, -1f, -17.8f, -2.1f, -20.6f)
                curveToRelative(-1f, 0.1f, -2.5f, 0.2f, -4.1f, 0.2f)
                curveToRelative(-1.7f, 0f, -3.2f, -0.1f, -4.4f, -0.4f)
                curveToRelative(-3.3f, -0.7f, -6.5f, -2.8f, -8.1f, -4f)
                curveToRelative(-1.6f, 1.2f, -4.8f, 3.4f, -8.1f, 4.1f)
                curveToRelative(-1.2f, 0.2f, -2.7f, 0.4f, -4.4f, 0.4f)
                curveToRelative(-1.6f, -0.1f, -3.1f, -0.2f, -4.1f, -0.3f)
            }

            path(fill = SolidColor(Color.White)) {
                moveTo(32f, 62.9f)
                curveToRelative(-0.3f, 0f, -0.7f, -0.1f, -1f, -0.3f)
                curveToRelative(-0.8f, -0.5f, -19.9f, -13.3f, -22.6f, -30.4f)
                curveTo(6.1f, 18f, 11.3f, 7.4f, 11.5f, 6.9f)
                curveToRelative(0.3f, -0.7f, 1.1f, -1.1f, 1.8f, -1f)
                curveToRelative(0f, 0f, 2.5f, 0.4f, 5.2f, 0.4f)
                curveToRelative(1.6f, 0f, 2.9f, -0.1f, 4f, -0.3f)
                curveToRelative(4f, -0.8f, 8.3f, -4.4f, 8.3f, -4.5f)
                curveToRelative(0.7f, -0.6f, 1.6f, -0.6f, 2.3f, 0f)
                curveToRelative(0f, 0f, 4.3f, 3.7f, 8.3f, 4.5f)
                curveToRelative(1.1f, 0.2f, 2.4f, 0.3f, 4f, 0.3f)
                curveToRelative(2.7f, 0f, 5.2f, -0.3f, 5.2f, -0.4f)
                curveToRelative(0.8f, -0.1f, 1.5f, 0.3f, 1.8f, 1f)
                curveToRelative(0.2f, 0.4f, 5.4f, 11.1f, 3.1f, 25.4f)
                curveTo(52.8f, 49.4f, 33.8f, 62.1f, 33f, 62.7f)
                curveToRelative(-0.3f, 0.1f, -0.7f, 0.2f, -1f, 0.2f)
                moveTo(14.2f, 9.6f)
                curveToRelative(-1.2f, 3f, -4f, 11.6f, -2.3f, 22.1f)
                curveTo(14f, 45.2f, 28.2f, 56.2f, 32f, 59f)
                curveToRelative(3.8f, -2.8f, 18f, -13.8f, 20.1f, -27.3f)
                curveToRelative(1.7f, -10.5f, -1.1f, -19.1f, -2.3f, -22.1f)
                curveToRelative(-1.1f, 0.1f, -2.7f, 0.2f, -4.4f, 0.2f)
                curveToRelative(-1.8f, 0f, -3.4f, -0.1f, -4.7f, -0.4f)
                curveToRelative(-3.6f, -0.7f, -7f, -3f, -8.8f, -4.4f)
                curveToRelative(-1.7f, 1.3f, -5.2f, 3.6f, -8.8f, 4.4f)
                curveToRelative(-1.3f, 0.3f, -2.9f, 0.4f, -4.7f, 0.4f)
                curveToRelative(-1.6f, 0.1f, -3.2f, -0.1f, -4.2f, -0.2f)
            }

            path(fill = SolidColor(primaryColor)) {
                moveTo(32f, 3.1f)
                moveToRelative(-1.1f, 0f)
                arcToRelative(1.1f, 1.1f, 0f,
                    isMoreThanHalf = true,
                    isPositiveArc = true,
                    dx1 = 2.2f,
                    dy1 = 0f
                )
                arcToRelative(1.1f, 1.1f, 0f,
                    isMoreThanHalf = true,
                    isPositiveArc = true,
                    dx1 = -2.2f,
                    dy1 = 0f
                )
            }

            path(fill = SolidColor(primaryColor)) {
                moveTo(50.9f, 8f)
                moveToRelative(-1.1f, 0f)
                arcToRelative(1.1f, 1.1f, 0f,
                    isMoreThanHalf = true,
                    isPositiveArc = true,
                    dx1 = 2.2f,
                    dy1 = 0f
                )
                arcToRelative(1.1f, 1.1f, 0f,
                    isMoreThanHalf = true,
                    isPositiveArc = true,
                    dx1 = -2.2f,
                    dy1 = 0f
                )
            }

            path(fill = SolidColor(primaryColor)) {
                moveTo(54.3f, 21.2f)
                moveToRelative(-1.1f, 0f)
                arcToRelative(1.1f, 1.1f, 0f,
                    isMoreThanHalf = true,
                    isPositiveArc = true,
                    dx1 = 2.2f,
                    dy1 = 0f
                )
                arcToRelative(1.1f, 1.1f, 0f,
                    isMoreThanHalf = true,
                    isPositiveArc = true,
                    dx1 = -2.2f,
                    dy1 = 0f
                )
            }

            path(fill = SolidColor(primaryColor)) {
                moveTo(52.3f, 37.7f)
                moveToRelative(-1.1f, 0f)
                arcToRelative(1.1f, 1.1f, 0f,
                    isMoreThanHalf = true,
                    isPositiveArc = true,
                    dx1 = 2.2f,
                    dy1 = 0f
                )
                arcToRelative(1.1f, 1.1f, 0f,
                    isMoreThanHalf = true,
                    isPositiveArc = true,
                    dx1 = -2.2f,
                    dy1 = 0f
                )
            }

            path(fill = SolidColor(primaryColor)) {
                moveTo(44f, 50.5f)
                moveToRelative(-1.1f, 0f)
                arcToRelative(1.1f, 1.1f, 0f,
                    isMoreThanHalf = true,
                    isPositiveArc = true,
                    dx1 = 2.2f,
                    dy1 = 0f
                )
                arcToRelative(1.1f, 1.1f, 0f,
                    isMoreThanHalf = true,
                    isPositiveArc = true,
                    dx1 = -2.2f,
                    dy1 = 0f
                )
            }

            path(fill = SolidColor(primaryColor)) {
                moveTo(32f, 60.7f)
                moveToRelative(-1.1f, 0f)
                arcToRelative(1.1f, 1.1f, 0f,
                    isMoreThanHalf = true,
                    isPositiveArc = true,
                    dx1 = 2.2f,
                    dy1 = 0f
                )
                arcToRelative(1.1f, 1.1f, 0f,
                    isMoreThanHalf = true,
                    isPositiveArc = true,
                    dx1 = -2.2f,
                    dy1 = 0f
                )
            }

            path(fill = SolidColor(primaryColor)) {
                moveTo(20f, 50.5f)
                moveToRelative(-1.1f, 0f)
                arcToRelative(1.1f, 1.1f, 0f,
                    isMoreThanHalf = true,
                    isPositiveArc = true,
                    dx1 = 2.2f,
                    dy1 = 0f
                )
                arcToRelative(1.1f, 1.1f, 0f,
                    isMoreThanHalf = true,
                    isPositiveArc = true,
                    dx1 = -2.2f,
                    dy1 = 0f
                )
            }

            path(fill = SolidColor(primaryColor)) {
                moveTo(11.7f, 37.7f)
                moveToRelative(-1.1f, 0f)
                arcToRelative(1.1f, 1.1f, 0f,
                    isMoreThanHalf = true,
                    isPositiveArc = true,
                    dx1 = 2.2f,
                    dy1 = 0f
                )
                arcToRelative(1.1f, 1.1f, 0f,
                    isMoreThanHalf = true,
                    isPositiveArc = true,
                    dx1 = -2.2f,
                    dy1 = 0f
                )
            }

            path(fill = SolidColor(primaryColor)) {
                moveTo(9.7f, 21.2f)
                moveToRelative(-1.1f, 0f)
                arcToRelative(1.1f, 1.1f, 0f,
                    isMoreThanHalf = true,
                    isPositiveArc = true,
                    dx1 = 2.2f,
                    dy1 = 0f
                )
                arcToRelative(1.1f, 1.1f, 0f,
                    isMoreThanHalf = true,
                    isPositiveArc = true,
                    dx1 = -2.2f,
                    dy1 = 0f
                )
            }

            path(fill = SolidColor(primaryColor)) {
                moveTo(13.1f, 8f)
                moveToRelative(-1.1f, 0f)
                arcToRelative(1.1f, 1.1f, 0f,
                    isMoreThanHalf = true,
                    isPositiveArc = true,
                    dx1 = 2.2f,
                    dy1 = 0f
                )
                arcToRelative(1.1f, 1.1f, 0f,
                    isMoreThanHalf = true,
                    isPositiveArc = true,
                    dx1 = -2.2f,
                    dy1 = 0f
                )
            }
        }.build()
    }

    Image(
        imageVector = badgeImageVector,
        contentDescription = contentDescription,
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
private fun TieredBadgeIconPreview() {
    TieredBadgeIcon(
        primaryColor = Color(0xFFC0C0C0),
        accentColor = Color(0x4D8E8E8E)
    )
}