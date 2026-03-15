package co.edu.unicauca.dopaminah.ui.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

// Lucide "house" icon
val LucideHouse: ImageVector by lazy {
    ImageVector.Builder(
        name = "House",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            fill = null,
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 2f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round,
            pathFillType = PathFillType.NonZero
        ) {
            // Roof
            moveTo(3f, 9.5f)
            lineTo(12f, 3f)
            lineTo(21f, 9.5f)
            // Right wall + floor
            lineTo(21f, 21f)
            lineTo(15f, 21f)
            lineTo(15f, 15f)
            // Door
            curveTo(15f, 13.34f, 13.66f, 12f, 12f, 12f)
            curveTo(10.34f, 12f, 9f, 13.34f, 9f, 15f)
            lineTo(9f, 21f)
            lineTo(3f, 21f)
            lineTo(3f, 9.5f)
            close()
        }
    }.build()
}

// Lucide "chart-column" icon
val LucideChartColumn: ImageVector by lazy {
    ImageVector.Builder(
        name = "ChartColumn",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            fill = null,
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 2f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            // Horizontal axis
            moveTo(3f, 3f)
            lineTo(3f, 21f)
            lineTo(21f, 21f)
        }
        // Bar 1
        path(
            fill = null,
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 2f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(7f, 16f)
            lineTo(7f, 11f)
        }
        // Bar 2
        path(
            fill = null,
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 2f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(12f, 16f)
            lineTo(12f, 6f)
        }
        // Bar 3
        path(
            fill = null,
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 2f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(17f, 16f)
            lineTo(17f, 13f)
        }
    }.build()
}

// Lucide "target" icon
val LucideTarget: ImageVector by lazy {
    ImageVector.Builder(
        name = "Target",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        // Outer circle
        path(
            fill = null,
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 2f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(22f, 12f)
            curveTo(22f, 17.52f, 17.52f, 22f, 12f, 22f)
            curveTo(6.48f, 22f, 2f, 17.52f, 2f, 12f)
            curveTo(2f, 6.48f, 6.48f, 2f, 12f, 2f)
            curveTo(17.52f, 2f, 22f, 6.48f, 22f, 12f)
            close()
        }
        // Middle circle
        path(
            fill = null,
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 2f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(18f, 12f)
            curveTo(18f, 15.31f, 15.31f, 18f, 12f, 18f)
            curveTo(8.69f, 18f, 6f, 15.31f, 6f, 12f)
            curveTo(6f, 8.69f, 8.69f, 6f, 12f, 6f)
            curveTo(15.31f, 6f, 18f, 8.69f, 18f, 12f)
            close()
        }
        // Center dot
        path(
            fill = SolidColor(Color.Black),
            stroke = null,
            strokeLineWidth = 2f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(14f, 12f)
            curveTo(14f, 13.1f, 13.1f, 14f, 12f, 14f)
            curveTo(10.9f, 14f, 10f, 13.1f, 10f, 12f)
            curveTo(10f, 10.9f, 10.9f, 10f, 12f, 10f)
            curveTo(13.1f, 10f, 14f, 10.9f, 14f, 12f)
            close()
        }
    }.build()
}

// Lucide "award" icon
val LucideAward: ImageVector by lazy {
    ImageVector.Builder(
        name = "Award",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        // Circle badge
        path(
            fill = null,
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 2f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(19f, 9f)
            curveTo(19f, 13.42f, 15.42f, 17f, 11f, 17f)
            curveTo(6.58f, 17f, 3f, 13.42f, 3f, 9f)
            curveTo(3f, 4.58f, 6.58f, 1f, 11f, 1f)
            curveTo(15.42f, 1f, 19f, 4.58f, 19f, 9f)
            close()
        }
        // Ribbon left
        path(
            fill = null,
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 2f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(6.53f, 16.11f)
            lineTo(4f, 23f)
            lineTo(8f, 21f)
            lineTo(11f, 23f)
            lineTo(13f, 17.5f)
        }
        // Ribbon right
        path(
            fill = null,
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 2f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(15.47f, 16.11f)
            lineTo(18f, 23f)
            lineTo(14f, 21f)
            lineTo(11f, 23f)
            lineTo(9f, 17.5f)
        }
    }.build()
}

// Lucide "settings" icon — exact SVG path conversion using arcToRelative
val LucideSettings: ImageVector by lazy {
    ImageVector.Builder(
        name = "Settings",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        // Outer gear shape: exact translation of the Lucide settings SVG path
        // SVG source: M12.22 2h-.44a2 2 0 0 0-2 2v.18a2 2 0 0 1-1 1.73l-.43.25a2 2 0 0 1-2 0l-.15-.08
        //             a2 2 0 0 0-2.73.73l-.22.38a2 2 0 0 0 .73 2.73l.15.1a2 2 0 0 1 1 1.72v.51
        //             a2 2 0 0 1-1 1.74l-.15.09a2 2 0 0 0-.73 2.73l.22.38a2 2 0 0 0 2.73.73l.15-.08
        //             a2 2 0 0 1 2 0l.43.25a2 2 0 0 1 1 1.73V20a2 2 0 0 0 2 2h.44a2 2 0 0 0 2-2v-.18
        //             a2 2 0 0 1 1-1.73l.43-.25a2 2 0 0 1 2 0l.15.08a2 2 0 0 0 2.73-.73l.22-.39
        //             a2 2 0 0 0-.73-2.73l-.15-.08a2 2 0 0 1-1-1.74v-.5a2 2 0 0 1 1-1.74l.15-.09
        //             a2 2 0 0 0 .73-2.73l-.22-.38a2 2 0 0 0-2.73-.73l-.15.08a2 2 0 0 1-2 0l-.43-.25
        //             a2 2 0 0 1-1-1.73V4a2 2 0 0 0-2-2z
        path(
            fill = null,
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 2f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(12.22f, 2f)
            horizontalLineToRelative(-0.44f)
            arcToRelative(2f, 2f, 0f, false, false, -2f, 2f)
            verticalLineToRelative(0.18f)
            arcToRelative(2f, 2f, 0f, false, true, -1f, 1.73f)
            lineToRelative(-0.43f, 0.25f)
            arcToRelative(2f, 2f, 0f, false, true, -2f, 0f)
            lineToRelative(-0.15f, -0.08f)
            arcToRelative(2f, 2f, 0f, false, false, -2.73f, 0.73f)
            lineToRelative(-0.22f, 0.38f)
            arcToRelative(2f, 2f, 0f, false, false, 0.73f, 2.73f)
            lineToRelative(0.15f, 0.1f)
            arcToRelative(2f, 2f, 0f, false, true, 1f, 1.72f)
            verticalLineToRelative(0.51f)
            arcToRelative(2f, 2f, 0f, false, true, -1f, 1.74f)
            lineToRelative(-0.15f, 0.09f)
            arcToRelative(2f, 2f, 0f, false, false, -0.73f, 2.73f)
            lineToRelative(0.22f, 0.38f)
            arcToRelative(2f, 2f, 0f, false, false, 2.73f, 0.73f)
            lineToRelative(0.15f, -0.08f)
            arcToRelative(2f, 2f, 0f, false, true, 2f, 0f)
            lineToRelative(0.43f, 0.25f)
            arcToRelative(2f, 2f, 0f, false, true, 1f, 1.73f)
            verticalLineTo(20f)
            arcToRelative(2f, 2f, 0f, false, false, 2f, 2f)
            horizontalLineToRelative(0.44f)
            arcToRelative(2f, 2f, 0f, false, false, 2f, -2f)
            verticalLineToRelative(-0.18f)
            arcToRelative(2f, 2f, 0f, false, true, 1f, -1.73f)
            lineToRelative(0.43f, -0.25f)
            arcToRelative(2f, 2f, 0f, false, true, 2f, 0f)
            lineToRelative(0.15f, 0.08f)
            arcToRelative(2f, 2f, 0f, false, false, 2.73f, -0.73f)
            lineToRelative(0.22f, -0.39f)
            arcToRelative(2f, 2f, 0f, false, false, -0.73f, -2.73f)
            lineToRelative(-0.15f, -0.08f)
            arcToRelative(2f, 2f, 0f, false, true, -1f, -1.74f)
            verticalLineToRelative(-0.5f)
            arcToRelative(2f, 2f, 0f, false, true, 1f, -1.74f)
            lineToRelative(0.15f, -0.09f)
            arcToRelative(2f, 2f, 0f, false, false, 0.73f, -2.73f)
            lineToRelative(-0.22f, -0.38f)
            arcToRelative(2f, 2f, 0f, false, false, -2.73f, -0.73f)
            lineToRelative(-0.15f, 0.08f)
            arcToRelative(2f, 2f, 0f, false, true, -2f, 0f)
            lineToRelative(-0.43f, -0.25f)
            arcToRelative(2f, 2f, 0f, false, true, -1f, -1.73f)
            verticalLineTo(4f)
            arcToRelative(2f, 2f, 0f, false, false, -2f, -2f)
            close()
        }
        // Inner circle: M12 15a3 3 0 1 0 0-6 3 3 0 0 0 0 6z
        path(
            fill = null,
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 2f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(12f, 15f)
            arcToRelative(3f, 3f, 0f, true, false, 0f, -6f)
            close()
        }
    }.build()
}

// Lucide "sunrise" icon
val LucideSunrise: ImageVector by lazy {
    ImageVector.Builder(
        name = "Sunrise",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            fill = null, stroke = SolidColor(Color.Black), strokeLineWidth = 2f,
            strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(12f, 2f)
            verticalLineTo(6f)
            moveTo(4.93f, 10.93f)
            lineTo(7.76f, 13.76f)
            moveTo(2f, 18f)
            horizontalLineTo(22f)
            moveTo(19.07f, 10.93f)
            lineTo(16.24f, 13.76f)
            moveTo(22f, 22f)
            horizontalLineTo(2f)
            moveTo(8f, 6f)
            lineTo(12f, 2f)
            lineTo(16f, 6f)
            moveTo(16f, 18f)
            arcToRelative(4f, 4f, 0f, false, false, -8f, 0f)
        }
    }.build()
}

// Lucide "timer" icon
val LucideTimer: ImageVector by lazy {
    ImageVector.Builder(
        name = "Timer",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            fill = null, stroke = SolidColor(Color.Black), strokeLineWidth = 2f,
            strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(10f, 2f)
            horizontalLineTo(14f)
            moveTo(12f, 14f)
            lineTo(15f, 11f)
        }
        path(
            fill = null, stroke = SolidColor(Color.Black), strokeLineWidth = 2f,
            strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(12f, 22f)
            arcToRelative(8f, 8f, 0f, true, false, 0f, -16f)
            arcToRelative(8f, 8f, 0f, false, false, 0f, 16f)
            close()
        }
    }.build()
}

// Lucide "smartphone" icon
val LucideSmartphone: ImageVector by lazy {
    ImageVector.Builder(
        name = "Smartphone",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            fill = null, stroke = SolidColor(Color.Black), strokeLineWidth = 2f,
            strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(5f, 2f)
            lineTo(19f, 2f)
            arcToRelative(2f, 2f, 0f, false, true, 2f, 2f)
            lineTo(21f, 20f)
            arcToRelative(2f, 2f, 0f, false, true, -2f, 2f)
            lineTo(5f, 22f)
            arcToRelative(2f, 2f, 0f, false, true, -2f, -2f)
            lineTo(3f, 4f)
            arcToRelative(2f, 2f, 0f, false, true, 2f, -2f)
            close()
        }
        path(
            fill = null, stroke = SolidColor(Color.Black), strokeLineWidth = 2f,
            strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(12f, 18f)
            horizontalLineToRelative(0.01f)
        }
    }.build()
}

// Lucide "chevron-left" icon
val LucideChevronLeft: ImageVector by lazy {
    ImageVector.Builder(
        name = "ChevronLeft",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            fill = null, stroke = SolidColor(Color.Black), strokeLineWidth = 2f,
            strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(15f, 18f)
            lineTo(9f, 12f)
            lineTo(15f, 6f)
        }
    }.build()
}

// Lucide "chevron-right" icon
val LucideChevronRight: ImageVector by lazy {
    ImageVector.Builder(
        name = "ChevronRight",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            fill = null, stroke = SolidColor(Color.Black), strokeLineWidth = 2f,
            strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(9f, 18f)
            lineTo(15f, 12f)
            lineTo(9f, 6f)
        }
    }.build()
}

// Lucide "calendar" icon
val LucideCalendar: ImageVector by lazy {
    ImageVector.Builder(
        name = "Calendar",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            fill = null, stroke = SolidColor(Color.Black), strokeLineWidth = 2f,
            strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(8f, 2f)
            verticalLineToRelative(4f)
            moveTo(16f, 2f)
            verticalLineToRelative(4f)
            moveTo(3f, 10f)
            horizontalLineToRelative(18f)
        }
        path(
            fill = null, stroke = SolidColor(Color.Black), strokeLineWidth = 2f,
            strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(19f, 4f)
            lineTo(5f, 4f)
            arcToRelative(2f, 2f, 0f, false, false, -2f, 2f)
            lineTo(3f, 20f)
            arcToRelative(2f, 2f, 0f, false, false, 2f, 2f)
            lineTo(19f, 22f)
            arcToRelative(2f, 2f, 0f, false, false, 2f, -2f)
            lineTo(21f, 6f)
            arcToRelative(2f, 2f, 0f, false, false, -2f, -2f)
            close()
        }
    }.build()
}

// Lucide "trash" icon
val LucideTrash: ImageVector by lazy {
    ImageVector.Builder(
        name = "Trash",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = null, stroke = SolidColor(Color.Black), strokeLineWidth = 2f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
            moveTo(3f, 6f)
            horizontalLineToRelative(18f)
        }
        path(fill = null, stroke = SolidColor(Color.Black), strokeLineWidth = 2f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
            moveTo(19f, 6f)
            verticalLineToRelative(14f)
            arcToRelative(2f, 2f, 0f, false, true, -2f, 2f)
            horizontalLineTo(7f)
            arcToRelative(2f, 2f, 0f, false, true, -2f, -2f)
            verticalLineTo(6f)
        }
        path(fill = null, stroke = SolidColor(Color.Black), strokeLineWidth = 2f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
            moveTo(8f, 6f)
            verticalLineTo(4f)
            arcToRelative(2f, 2f, 0f, false, true, 2f, -2f)
            horizontalLineToRelative(4f)
            arcToRelative(2f, 2f, 0f, false, true, 2f, 2f)
            verticalLineToRelative(2f)
        }
    }.build()
}

// Lucide "lock" icon
val LucideLock: ImageVector by lazy {
    ImageVector.Builder(
        name = "Lock",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = null, stroke = SolidColor(Color.Black), strokeLineWidth = 2f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
            moveTo(5f, 11f)
            horizontalLineToRelative(14f)
            arcToRelative(2f, 2f, 0f, false, true, 2f, 2f)
            verticalLineToRelative(7f)
            arcToRelative(2f, 2f, 0f, false, true, -2f, 2f)
            horizontalLineTo(5f)
            arcToRelative(2f, 2f, 0f, false, true, -2f, -2f)
            verticalLineToRelative(-7f)
            arcToRelative(2f, 2f, 0f, false, true, 2f, -2f)
            close()
        }
        path(fill = null, stroke = SolidColor(Color.Black), strokeLineWidth = 2f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
            moveTo(7f, 11f)
            verticalLineTo(7f)
            arcToRelative(5f, 5f, 0f, false, true, 10f, 0f)
            verticalLineToRelative(4f)
        }
    }.build()
}

// Lucide "triangle-alert" icon
val LucideTriangleAlert: ImageVector by lazy {
    ImageVector.Builder(
        name = "TriangleAlert",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = null, stroke = SolidColor(Color.Black), strokeLineWidth = 2f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
            moveTo(21.73f, 18f)
            lineToRelative(-8f, -14f)
            arcToRelative(2f, 2f, 0f, false, false, -3.48f, 0f)
            lineToRelative(-8f, 14f)
            arcToRelative(2f, 2f, 0f, false, false, 1.74f, 3f)
            horizontalLineToRelative(16f)
            arcToRelative(2f, 2f, 0f, false, false, 1.74f, -3f)
            close()
        }
        path(fill = null, stroke = SolidColor(Color.Black), strokeLineWidth = 2f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
            moveTo(12f, 9f)
            verticalLineToRelative(4f)
        }
        path(fill = null, stroke = SolidColor(Color.Black), strokeLineWidth = 2f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
            moveTo(12f, 17f)
            horizontalLineToRelative(0.01f)
        }
    }.build()
}

// Lucide "plus" icon
val LucidePlus: ImageVector by lazy {
    ImageVector.Builder(
        name = "Plus",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = null, stroke = SolidColor(Color.Black), strokeLineWidth = 2f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
            moveTo(5f, 12f)
            horizontalLineToRelative(14f)
        }
        path(fill = null, stroke = SolidColor(Color.Black), strokeLineWidth = 2f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
            moveTo(12f, 5f)
            verticalLineToRelative(14f)
        }
    }.build()
}

// Lucide "lightbulb" icon
val LucideLightbulb: ImageVector by lazy {
    ImageVector.Builder(
        name = "Lightbulb",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = null, stroke = SolidColor(Color.Black), strokeLineWidth = 2f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
            moveTo(15f, 14f)
            curveToRelative(0.2f, -1f, 0.7f, -1.7f, 1.5f, -2.5f)
            curveToRelative(1f, -0.9f, 1.5f, -2.2f, 1.5f, -3.5f)
            arcTo(6f, 6f, 0f, false, false, 6f, 8f)
            curveToRelative(0f, 1.3f, 0.5f, 2.6f, 1.5f, 3.5f)
            curveToRelative(0.8f, 0.8f, 1.3f, 1.5f, 1.5f, 2.5f)
        }
        path(fill = null, stroke = SolidColor(Color.Black), strokeLineWidth = 2f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
            moveTo(9f, 18f)
            horizontalLineToRelative(6f)
        }
        path(fill = null, stroke = SolidColor(Color.Black), strokeLineWidth = 2f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
            moveTo(10f, 22f)
            horizontalLineToRelative(4f)
        }
    }.build()
}

val LucidePencil: ImageVector by lazy {
    ImageVector.Builder(
        name = "Pencil",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = null, stroke = SolidColor(Color.Black), strokeLineWidth = 2f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
            moveTo(17f, 3f)
            arcToRelative(2.85f, 2.83f, 0f, true, true, 4f, 4f)
            lineTo(7.5f, 20.5f)
            lineTo(2f, 22f)
            lineToRelative(1.5f, -5.5f)
            close()
        }
        path(fill = null, stroke = SolidColor(Color.Black), strokeLineWidth = 2f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
            moveTo(15f, 5f)
            lineToRelative(4f, 4f)
        }
    }.build()
}

val LucideCalendarClock: ImageVector by lazy {
    ImageVector.Builder(
        name = "CalendarClock",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = null, stroke = SolidColor(Color.Black), strokeLineWidth = 2f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
            moveTo(21f, 7.5f)
            verticalLineTo(6f)
            arcToRelative(2f, 2f, 0f, false, false, -2f, -2f)
            horizontalLineTo(5f)
            arcToRelative(2f, 2f, 0f, false, false, -2f, 2f)
            verticalLineToRelative(14f)
            arcToRelative(2f, 2f, 0f, false, false, 2f, 2f)
            horizontalLineToRelative(3.5f)
        }
        path(fill = null, stroke = SolidColor(Color.Black), strokeLineWidth = 2f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
            moveTo(16f, 2f)
            verticalLineToRelative(4f)
        }
        path(fill = null, stroke = SolidColor(Color.Black), strokeLineWidth = 2f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
            moveTo(8f, 2f)
            verticalLineToRelative(4f)
        }
        path(fill = null, stroke = SolidColor(Color.Black), strokeLineWidth = 2f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
            moveTo(3f, 10f)
            horizontalLineToRelative(5f)
        }
        // Clock circle
        path(fill = null, stroke = SolidColor(Color.Black), strokeLineWidth = 2f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
            moveTo(17f, 13f)
            arcToRelative(4f, 4f, 0f, true, true, 0f, 8f)
            arcToRelative(4f, 4f, 0f, false, true, 0f, -8f)
            close()
        }
        path(fill = null, stroke = SolidColor(Color.Black), strokeLineWidth = 2f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
            moveTo(17f, 15f)
            verticalLineToRelative(2f)
            lineToRelative(1f, 1f)
        }
    }.build()
}
