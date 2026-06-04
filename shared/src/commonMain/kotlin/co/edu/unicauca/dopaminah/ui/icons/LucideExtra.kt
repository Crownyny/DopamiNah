package co.edu.unicauca.dopaminah.ui.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val LucideSearch: ImageVector by lazy {
    ImageVector.Builder(name = "Search", defaultWidth = 24.dp, defaultHeight = 24.dp, viewportWidth = 24f, viewportHeight = 24f).apply {
        path(fill = null, stroke = SolidColor(Color.Black), strokeLineWidth = 2f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
            moveTo(10f, 3f); arcTo(7f, 7f, 0f, true, true, 10f, 17f); arcTo(7f, 7f, 0f, false, true, 10f, 3f); close()
            moveTo(21f, 21f); lineTo(15f, 15f)
        }
    }.build()
}

val LucideStar: ImageVector by lazy {
    ImageVector.Builder(name = "Star", defaultWidth = 24.dp, defaultHeight = 24.dp, viewportWidth = 24f, viewportHeight = 24f).apply {
        path(fill = null, stroke = SolidColor(Color.Black), strokeLineWidth = 2f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
            moveTo(12f, 2f); lineTo(15.09f, 8.26f); lineTo(22f, 9.27f); lineTo(17f, 14.14f); lineTo(18.18f, 21.02f); lineTo(12f, 17.77f); lineTo(5.82f, 21.02f); lineTo(7f, 14.14f); lineTo(2f, 9.27f); lineTo(8.91f, 8.26f); close()
        }
    }.build()
}

val LucideClock: ImageVector by lazy {
    ImageVector.Builder(name = "Clock", defaultWidth = 24.dp, defaultHeight = 24.dp, viewportWidth = 24f, viewportHeight = 24f).apply {
        path(fill = null, stroke = SolidColor(Color.Black), strokeLineWidth = 2f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
            moveTo(22f, 12f); curveTo(22f, 17.52f, 17.52f, 22f, 12f, 22f); curveTo(6.48f, 22f, 2f, 17.52f, 2f, 12f); curveTo(2f, 6.48f, 6.48f, 2f, 12f, 2f); curveTo(17.52f, 2f, 22f, 6.48f, 22f, 12f); close()
            moveTo(12f, 6f); lineTo(12f, 12f); lineTo(16f, 14f)
        }
    }.build()
}

val LucideTrendingUp: ImageVector by lazy {
    ImageVector.Builder(name = "TrendingUp", defaultWidth = 24.dp, defaultHeight = 24.dp, viewportWidth = 24f, viewportHeight = 24f).apply {
        path(fill = null, stroke = SolidColor(Color.Black), strokeLineWidth = 2f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
            moveTo(22f, 7f); lineTo(13.5f, 15.5f); lineTo(8.5f, 10.5f); lineTo(2f, 17f)
            moveTo(16f, 7f); horizontalLineTo(22f); verticalLineTo(13f)
        }
    }.build()
}

val LucideChevronDown: ImageVector by lazy {
    ImageVector.Builder(name = "ChevronDown", defaultWidth = 24.dp, defaultHeight = 24.dp, viewportWidth = 24f, viewportHeight = 24f).apply {
        path(fill = null, stroke = SolidColor(Color.Black), strokeLineWidth = 2f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
            moveTo(6f, 9f); lineTo(12f, 15f); lineTo(18f, 9f)
        }
    }.build()
}

val LucideCheck: ImageVector by lazy {
    ImageVector.Builder(name = "Check", defaultWidth = 24.dp, defaultHeight = 24.dp, viewportWidth = 24f, viewportHeight = 24f).apply {
        path(fill = null, stroke = SolidColor(Color.Black), strokeLineWidth = 2f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
            moveTo(20f, 6f); lineTo(9f, 17f); lineTo(4f, 12f)
        }
    }.build()
}

val LucideX: ImageVector by lazy {
    ImageVector.Builder(name = "X", defaultWidth = 24.dp, defaultHeight = 24.dp, viewportWidth = 24f, viewportHeight = 24f).apply {
        path(fill = null, stroke = SolidColor(Color.Black), strokeLineWidth = 2f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
            moveTo(18f, 6f); lineTo(6f, 18f); moveTo(6f, 6f); lineTo(18f, 18f)
        }
    }.build()
}

val LucideInfo: ImageVector by lazy {
    ImageVector.Builder(name = "Info", defaultWidth = 24.dp, defaultHeight = 24.dp, viewportWidth = 24f, viewportHeight = 24f).apply {
        path(fill = null, stroke = SolidColor(Color.Black), strokeLineWidth = 2f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
            moveTo(12f, 22f); curveTo(17.52f, 22f, 22f, 17.52f, 22f, 12f); curveTo(22f, 6.48f, 17.52f, 2f, 12f, 2f); curveTo(6.48f, 2f, 2f, 6.48f, 2f, 12f); curveTo(2f, 17.52f, 6.48f, 22f, 12f, 22f); close()
            moveTo(12f, 16f); verticalLineTo(12f); moveTo(12f, 8f); horizontalLineToRelative(0.01f)
        }
    }.build()
}

val LucideMoon: ImageVector by lazy {
    ImageVector.Builder(name = "Moon", defaultWidth = 24.dp, defaultHeight = 24.dp, viewportWidth = 24f, viewportHeight = 24f).apply {
        path(fill = null, stroke = SolidColor(Color.Black), strokeLineWidth = 2f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
            moveTo(12f, 3f); arcTo(9f, 9f, 0f, true, false, 21f, 12f); curveTo(18f, 14f, 14f, 14f, 12f, 12f); curveTo(10f, 10f, 10f, 6f, 12f, 3f); close()
        }
    }.build()
}

val LucideBell: ImageVector by lazy {
    ImageVector.Builder(name = "Bell", defaultWidth = 24.dp, defaultHeight = 24.dp, viewportWidth = 24f, viewportHeight = 24f).apply {
        path(fill = null, stroke = SolidColor(Color.Black), strokeLineWidth = 2f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
            moveTo(18f, 8f); arcTo(6f, 6f, 0f, false, false, 6f, 8f); curveTo(6f, 15f, 3f, 17f, 3f, 17f); horizontalLineTo(21f); curveTo(21f, 17f, 18f, 15f, 18f, 8f); close()
            moveTo(13.73f, 21f); arcTo(2f, 2f, 0f, false, true, 10.27f, 21f)
        }
    }.build()
}

val LucideShield: ImageVector by lazy {
    ImageVector.Builder(name = "Shield", defaultWidth = 24.dp, defaultHeight = 24.dp, viewportWidth = 24f, viewportHeight = 24f).apply {
        path(fill = null, stroke = SolidColor(Color.Black), strokeLineWidth = 2f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
            moveTo(12f, 22f); lineTo(3f, 18f); lineTo(3f, 7f); lineTo(12f, 2f); lineTo(21f, 7f); lineTo(21f, 18f); close()
        }
    }.build()
}

val LucideHelpCircle: ImageVector by lazy {
    ImageVector.Builder(name = "HelpCircle", defaultWidth = 24.dp, defaultHeight = 24.dp, viewportWidth = 24f, viewportHeight = 24f).apply {
        path(fill = null, stroke = SolidColor(Color.Black), strokeLineWidth = 2f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
            moveTo(12f, 22f); curveTo(17.52f, 22f, 22f, 17.52f, 22f, 12f); curveTo(22f, 6.48f, 17.52f, 2f, 12f, 2f); curveTo(6.48f, 2f, 2f, 6.48f, 2f, 12f); curveTo(2f, 17.52f, 6.48f, 22f, 12f, 22f); close()
            moveTo(9.09f, 9f); curveTo(9.58f, 7.83f, 10.69f, 7f, 12f, 7f); curveTo(13.66f, 7f, 15f, 8.34f, 15f, 10f); curveTo(15f, 12f, 12f, 13f, 12f, 15f); moveTo(12f, 17f); horizontalLineToRelative(0.01f)
        }
    }.build()
}

val LucideLogOut: ImageVector by lazy {
    ImageVector.Builder(name = "LogOut", defaultWidth = 24.dp, defaultHeight = 24.dp, viewportWidth = 24f, viewportHeight = 24f).apply {
        path(fill = null, stroke = SolidColor(Color.Black), strokeLineWidth = 2f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
            moveTo(9f, 21f); horizontalLineTo(5f); arcTo(2f, 2f, 0f, false, true, 3f, 19f); lineTo(3f, 5f); arcTo(2f, 2f, 0f, false, true, 5f, 3f); horizontalLineTo(9f)
            moveTo(16f, 17f); lineTo(21f, 12f); lineTo(16f, 7f); moveTo(21f, 12f); lineTo(9f, 12f)
        }
    }.build()
}

val LucideMail: ImageVector by lazy {
    ImageVector.Builder(name = "Mail", defaultWidth = 24.dp, defaultHeight = 24.dp, viewportWidth = 24f, viewportHeight = 24f).apply {
        path(fill = null, stroke = SolidColor(Color.Black), strokeLineWidth = 2f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
            moveTo(4f, 4f); horizontalLineTo(20f); arcTo(2f, 2f, 0f, false, true, 22f, 6f); lineTo(22f, 18f); arcTo(2f, 2f, 0f, false, true, 20f, 20f); lineTo(4f, 20f); arcTo(2f, 2f, 0f, false, true, 2f, 18f); lineTo(2f, 6f); arcTo(2f, 2f, 0f, false, true, 4f, 4f); close()
            moveTo(22f, 6f); lineTo(12f, 13f); lineTo(2f, 6f)
        }
    }.build()
}

val LucideShieldAlert: ImageVector by lazy {
    ImageVector.Builder(name = "ShieldAlert", defaultWidth = 24.dp, defaultHeight = 24.dp, viewportWidth = 24f, viewportHeight = 24f).apply {
        path(fill = null, stroke = SolidColor(Color.Black), strokeLineWidth = 2f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
            moveTo(12f, 22f); lineTo(3f, 18f); lineTo(3f, 7f); lineTo(12f, 2f); lineTo(21f, 7f); lineTo(21f, 18f); close()
            moveTo(12f, 8f); verticalLineTo(12f); moveTo(12f, 16f); horizontalLineToRelative(0.01f)
        }
    }.build()
}

val LucideSun: ImageVector by lazy {
    ImageVector.Builder(name = "Sun", defaultWidth = 24.dp, defaultHeight = 24.dp, viewportWidth = 24f, viewportHeight = 24f).apply {
        path(fill = null, stroke = SolidColor(Color.Black), strokeLineWidth = 2f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
            moveTo(12f, 2f); verticalLineTo(4f)
            moveTo(12f, 20f); verticalLineTo(22f)
            moveTo(4.93f, 4.93f); lineTo(6.34f, 6.34f)
            moveTo(17.66f, 17.66f); lineTo(19.07f, 19.07f)
            moveTo(2f, 12f); horizontalLineTo(4f)
            moveTo(20f, 12f); horizontalLineTo(22f)
            moveTo(6.34f, 17.66f); lineTo(4.93f, 19.07f)
            moveTo(19.07f, 4.93f); lineTo(17.66f, 6.34f)
        }
        path(fill = null, stroke = SolidColor(Color.Black), strokeLineWidth = 2f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
            moveTo(12f, 17f); arcTo(5f, 5f, 0f, true, false, 12f, 7f); arcTo(5f, 5f, 0f, false, false, 12f, 17f); close()
        }
    }.build()
}

val LucideZap: ImageVector by lazy {
    ImageVector.Builder(name = "Zap", defaultWidth = 24.dp, defaultHeight = 24.dp, viewportWidth = 24f, viewportHeight = 24f).apply {
        path(fill = null, stroke = SolidColor(Color.Black), strokeLineWidth = 2f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
            moveTo(13f, 2f); lineTo(4f, 14f); horizontalLineTo(12f); lineTo(11f, 22f); lineTo(20f, 10f); horizontalLineTo(12f); close()
        }
    }.build()
}

val LucideGlobe: ImageVector by lazy {
    ImageVector.Builder(name = "Globe", defaultWidth = 24.dp, defaultHeight = 24.dp, viewportWidth = 24f, viewportHeight = 24f).apply {
        path(fill = null, stroke = SolidColor(Color.Black), strokeLineWidth = 2f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
            moveTo(12f, 2f); curveTo(6.48f, 2f, 2f, 6.48f, 2f, 12f); curveTo(2f, 17.52f, 6.48f, 22f, 12f, 22f); curveTo(17.52f, 22f, 22f, 17.52f, 22f, 12f); curveTo(22f, 6.48f, 17.52f, 2f, 12f, 2f); close()
            moveTo(2f, 12f); horizontalLineTo(22f)
            moveTo(12f, 2f); curveTo(14.5f, 4.92f, 16f, 8.32f, 16f, 12f); curveTo(16f, 15.68f, 14.5f, 19.08f, 12f, 22f); curveTo(9.5f, 19.08f, 8f, 15.68f, 8f, 12f); curveTo(8f, 8.32f, 9.5f, 4.92f, 12f, 2f); close()
        }
    }.build()
}
