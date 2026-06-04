package co.edu.unicauca.dopaminah.ui.screens.goals.webgoals

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.unicauca.dopaminah.ui.theme.DangerRed

fun cleanDomain(domain: String): String {
    return domain.removePrefix("www.").lowercase()
}

fun brandColor(domain: String): Color {
    val clean = cleanDomain(domain)
    return when {
        clean.contains("youtube") || clean.contains("youtu.be") -> Color(0xFFFF0000)
        clean.contains("x.com") || clean.contains("twitter") -> Color(0xFF000000)
        clean.contains("reddit") -> Color(0xFFFF4500)
        clean.contains("instagram") -> Color(0xFFE4405F)
        clean.contains("tiktok") -> Color(0xFF000000)
        clean.contains("facebook") || clean.contains("fb.com") -> Color(0xFF1877F2)
        clean.contains("netflix") -> Color(0xFFE50914)
        clean.contains("twitch") -> Color(0xFF9146FF)
        clean.contains("discord") -> Color(0xFF5865F2)
        clean.contains("snapchat") -> Color(0xFFFFFC00)
        clean.contains("whatsapp") -> Color(0xFF25D366)
        clean.contains("pinterest") -> Color(0xFFE60023)
        clean.contains("linkedin") -> Color(0xFF0A66C2)
        clean.contains("github") -> Color(0xFF333333)
        clean.contains("spotify") -> Color(0xFF1DB954)
        clean.contains("telegram") -> Color(0xFF26A5E4)
        clean.contains("amazon") -> Color(0xFFFF9900)
        clean.contains("google") -> Color(0xFF4285F4)
        clean.contains("microsoft") || clean.contains("bing") -> Color(0xFF00A4EF)
        clean.contains("apple") -> Color(0xFF555555)
        clean.contains("medium") -> Color(0xFF000000)
        clean.contains("stackoverflow") -> Color(0xFFF48024)
        else -> {
            val palette = listOf(
                Color(0xFF8B5CF6), Color(0xFF3B82F6), Color(0xFF10B981),
                Color(0xFFF59E0B), Color(0xFFEF4444), Color(0xFFEC4899),
                Color(0xFF6366F1), Color(0xFF14B8A6), Color(0xFFF97316),
                Color(0xFF06B6D4), Color(0xFF84CC16), Color(0xFFD946EF),
            )
            val index = (clean.hashCode() and Int.MAX_VALUE) % palette.size
            palette[index]
        }
    }
}

@Composable
fun BrandAvatar(
    domain: String,
    isBlocked: Boolean,
    size: Dp = 44.dp,
    fontSize: TextUnit = 18.sp
) {
    if (isBlocked) {
        val fgColor = DangerRed
        Box(
            modifier = Modifier
                .size(size)
                .clip(RoundedCornerShape(12.dp))
                .background(fgColor.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(size * 0.48f)) {
                drawBlockedIcon(fgColor)
            }
        }
    } else {
        FaviconAvatar(domain = domain, size = size, fontSize = fontSize)
    }
}

@Composable
fun DomainFallbackIcon(
    cleanDomain: String,
    color: Color,
    size: Dp,
    fontSize: TextUnit
) {
    when {
        cleanDomain.contains("youtube") || cleanDomain.contains("youtu.be") -> {
            Canvas(modifier = Modifier.size(size * 0.5f)) {
                drawYoutubeIcon(color)
            }
        }
        cleanDomain == "x.com" || cleanDomain.contains("twitter") -> {
            Canvas(modifier = Modifier.size(size * 0.45f)) {
                drawXIcon(color)
            }
        }
        cleanDomain.contains("reddit") -> {
            Canvas(modifier = Modifier.size(size * 0.55f)) {
                drawRedditIcon(color)
            }
        }
        cleanDomain.contains("instagram") -> {
            Canvas(modifier = Modifier.size(size * 0.5f)) {
                drawInstagramIcon(color)
            }
        }
        cleanDomain.contains("tiktok") -> {
            Canvas(modifier = Modifier.size(size * 0.42f)) {
                drawTikTokIcon(color)
            }
        }
        cleanDomain.contains("facebook") || cleanDomain.contains("fb.com") -> {
            Canvas(modifier = Modifier.size(size * 0.4f)) {
                drawFacebookIcon(color)
            }
        }
        cleanDomain.contains("discord") -> {
            Canvas(modifier = Modifier.size(size * 0.48f)) {
                drawDiscordIcon(color)
            }
        }
        cleanDomain.contains("twitch") -> {
            Canvas(modifier = Modifier.size(size * 0.42f)) {
                drawTwitchIcon(color)
            }
        }
        cleanDomain.contains("snapchat") -> {
            Canvas(modifier = Modifier.size(size * 0.42f)) {
                drawSnapchatIcon(color)
            }
        }
        cleanDomain.contains("github") -> {
            Canvas(modifier = Modifier.size(size * 0.48f)) {
                drawGithubIcon(color)
            }
        }
        cleanDomain.contains("spotify") -> {
            Canvas(modifier = Modifier.size(size * 0.42f)) {
                drawSpotifyIcon(color)
            }
        }
        cleanDomain.contains("netflix") -> {
            Canvas(modifier = Modifier.size(size * 0.38f)) {
                drawNetflixIcon(color)
            }
        }
        cleanDomain.contains("whatsapp") -> {
            Canvas(modifier = Modifier.size(size * 0.45f)) {
                drawWhatsAppIcon(color)
            }
        }
        else -> {
            val letter = cleanDomain.first().uppercase()
            androidx.compose.material3.Text(
                text = letter,
                fontSize = fontSize,
                fontWeight = FontWeight.Bold,
                color = color,
                textAlign = TextAlign.Center
            )
        }
    }
}

private fun DrawScope.drawYoutubeIcon(color: Color) {
    val w = size.width
    val h = size.height
    val path = Path().apply {
        moveTo(w * 0.28f, h * 0.2f)
        lineTo(w * 0.28f, h * 0.8f)
        lineTo(w * 0.82f, h * 0.5f)
        close()
    }
    drawPath(path, color, style = Fill)
}

private fun DrawScope.drawXIcon(color: Color) {
    val s = size.width * 0.15f
    val cx = size.width / 2
    val cy = size.height / 2
    drawLine(color, Offset(cx - s, cy - s), Offset(cx + s, cy + s), strokeWidth = size.width * 0.14f, cap = androidx.compose.ui.graphics.StrokeCap.Round)
    drawLine(color, Offset(cx + s, cy - s), Offset(cx - s, cy + s), strokeWidth = size.width * 0.14f, cap = androidx.compose.ui.graphics.StrokeCap.Round)
}

private fun DrawScope.drawRedditIcon(color: Color) {
    val cx = size.width / 2
    val cy = size.height * 0.45f
    val r = size.width * 0.3f
    drawCircle(color, r, Offset(cx, cy))
    drawCircle(color, size.width * 0.04f, Offset(cx - r * 0.45f, cy - r * 0.2f))
    drawCircle(color, size.width * 0.04f, Offset(cx + r * 0.45f, cy - r * 0.2f))
    val smilePath = Path().apply {
        moveTo(cx - r * 0.3f, cy + r * 0.1f)
        cubicTo(cx - r * 0.15f, cy + r * 0.35f, cx + r * 0.15f, cy + r * 0.35f, cx + r * 0.3f, cy + r * 0.1f)
    }
    drawPath(smilePath, color, style = Stroke(width = size.width * 0.05f, cap = androidx.compose.ui.graphics.StrokeCap.Round))
    val antennaPath = Path().apply {
        moveTo(cx, cy - r * 0.6f)
        lineTo(cx + r * 0.4f, cy - r * 1.0f)
        lineTo(cx + r * 0.5f, cy - r * 0.8f)
    }
    drawPath(antennaPath, color, style = Stroke(width = size.width * 0.05f, cap = androidx.compose.ui.graphics.StrokeCap.Round))
}

private fun DrawScope.drawInstagramIcon(color: Color) {
    val w = size.width
    val h = size.height
    val inset = w * 0.08f
    drawRoundRect(
        color = color,
        topLeft = Offset(inset, inset),
        size = Size(w - inset * 2, h - inset * 2),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(w * 0.2f),
        style = Stroke(width = w * 0.09f)
    )
    val circleR = w * 0.18f
    drawCircle(color, circleR, Offset(w / 2, h / 2), style = Stroke(width = w * 0.09f))
    drawCircle(color, w * 0.04f, Offset(w * 0.72f, h * 0.28f))
}

private fun DrawScope.drawTikTokIcon(color: Color) {
    val w = size.width
    val h = size.height
    val notePath = Path().apply {
        moveTo(w * 0.35f, h * 0.25f)
        lineTo(w * 0.35f, h * 0.78f)
        lineTo(w * 0.72f, h * 0.7f)
        lineTo(w * 0.72f, h * 0.25f)
        close()
    }
    drawPath(notePath, color, style = Fill)
    val stemPath = Path().apply {
        moveTo(w * 0.35f, h * 0.25f)
        lineTo(w * 0.35f, h * 0.15f)
        lineTo(w * 0.72f, h * 0.05f)
        lineTo(w * 0.72f, h * 0.25f)
    }
    drawPath(stemPath, color, style = Fill)
    drawCircle(color, w * 0.1f, Offset(w * 0.25f, h * 0.78f))
    drawCircle(color, w * 0.1f, Offset(w * 0.75f, h * 0.75f))
}

private fun DrawScope.drawFacebookIcon(color: Color) {
    val cx = size.width / 2
    val cy = size.height / 2
    val s = size.width * 0.35f
    val fPath = Path().apply {
        moveTo(cx + s * 0.15f, cy - s)
        lineTo(cx + s * 0.15f, cy)
        lineTo(cx + s * 0.5f, cy)
        lineTo(cx + s * 0.5f, cy + s * 0.25f)
        lineTo(cx + s * 0.15f, cy + s * 0.25f)
        lineTo(cx + s * 0.15f, cy + s)
        lineTo(cx - s * 0.15f, cy + s)
        lineTo(cx - s * 0.15f, cy + s * 0.25f)
        lineTo(cx - s * 0.4f, cy + s * 0.25f)
        lineTo(cx - s * 0.4f, cy)
        lineTo(cx - s * 0.15f, cy)
        lineTo(cx - s * 0.15f, cy - s)
        close()
    }
    drawPath(fPath, color, style = Fill)
}

private fun DrawScope.drawDiscordIcon(color: Color) {
    val w = size.width
    val h = size.height
    val body = Path().apply {
        moveTo(w * 0.2f, h * 0.25f)
        cubicTo(w * 0.15f, h * 0.25f, w * 0.1f, h * 0.3f, w * 0.1f, h * 0.38f)
        lineTo(w * 0.1f, h * 0.78f)
        cubicTo(w * 0.1f, h * 0.85f, w * 0.15f, h * 0.9f, w * 0.2f, h * 0.9f)
        lineTo(w * 0.3f, h * 0.9f)
        lineTo(w * 0.25f, h * 0.7f)
        lineTo(w * 0.35f, h * 0.8f)
        lineTo(w * 0.45f, h * 0.8f)
        lineTo(w * 0.55f, h * 0.8f)
        lineTo(w * 0.65f, h * 0.8f)
        lineTo(w * 0.75f, h * 0.7f)
        lineTo(w * 0.7f, h * 0.9f)
        lineTo(w * 0.8f, h * 0.9f)
        cubicTo(w * 0.85f, h * 0.9f, w * 0.9f, h * 0.85f, w * 0.9f, h * 0.78f)
        lineTo(w * 0.9f, h * 0.38f)
        cubicTo(w * 0.9f, h * 0.3f, w * 0.85f, h * 0.25f, w * 0.8f, h * 0.25f)
        close()
    }
    drawPath(body, color, style = Fill)
    drawCircle(color, w * 0.07f, Offset(w * 0.3f, h * 0.5f))
    drawCircle(color, w * 0.07f, Offset(w * 0.7f, h * 0.5f))
    val eyeL = Path().apply {
        moveTo(w * 0.3f - w * 0.03f, h * 0.5f)
        lineTo(w * 0.3f + w * 0.03f, h * 0.5f)
    }
    val eyeR = Path().apply {
        moveTo(w * 0.7f - w * 0.03f, h * 0.5f)
        lineTo(w * 0.7f + w * 0.03f, h * 0.5f)
    }
    drawPath(eyeL, color, style = Stroke(width = w * 0.04f, cap = androidx.compose.ui.graphics.StrokeCap.Round))
    drawPath(eyeR, color, style = Stroke(width = w * 0.04f, cap = androidx.compose.ui.graphics.StrokeCap.Round))
}

private fun DrawScope.drawTwitchIcon(color: Color) {
    val w = size.width
    val h = size.height
    val chatPath = Path().apply {
        moveTo(w * 0.2f, h * 0.1f)
        lineTo(w * 0.2f, h * 0.7f)
        lineTo(w * 0.4f, h * 0.7f)
        lineTo(w * 0.5f, h * 0.85f)
        lineTo(w * 0.5f, h * 0.7f)
        lineTo(w * 0.8f, h * 0.7f)
        lineTo(w * 0.8f, h * 0.1f)
        close()
    }
    drawPath(chatPath, color, style = Fill)
}

private fun DrawScope.drawSnapchatIcon(color: Color) {
    val cx = size.width / 2
    val cy = size.height * 0.45f
    val r = size.width * 0.3f
    val ghostPath = Path().apply {
        moveTo(cx, cy - r)
        cubicTo(cx + r * 0.7f, cy - r, cx + r, cy - r * 0.2f, cx + r, cy + r * 0.2f)
        cubicTo(cx + r, cy + r * 0.5f, cx + r * 0.6f, cy + r * 0.6f, cx, cy + r)
        cubicTo(cx - r * 0.6f, cy + r * 0.6f, cx - r, cy + r * 0.5f, cx - r, cy + r * 0.2f)
        cubicTo(cx - r, cy - r * 0.2f, cx - r * 0.7f, cy - r, cx, cy - r)
        close()
    }
    drawPath(ghostPath, color, style = Fill)
}

private fun DrawScope.drawGithubIcon(color: Color) {
    val cx = size.width / 2
    val cy = size.height * 0.45f
    val r = size.width * 0.32f
    drawCircle(color, r, Offset(cx, cy), style = Stroke(width = size.width * 0.12f))
    val tailPath = Path().apply {
        moveTo(cx + r * 0.6f, cy + r * 0.4f)
        cubicTo(cx + r * 0.8f, cy + r * 0.6f, cx + r * 0.5f, cy + r * 0.8f, cx, cy + r * 0.5f)
    }
    drawPath(tailPath, color, style = Stroke(width = size.width * 0.08f, cap = androidx.compose.ui.graphics.StrokeCap.Round))
}

private fun DrawScope.drawSpotifyIcon(color: Color) {
    val cx = size.width / 2
    val cy = size.height / 2
    val r = size.width * 0.4f
    drawCircle(color, r, Offset(cx, cy))
    val wavePath = Path().apply {
        moveTo(cx - r * 0.5f, cy - r * 0.5f)
        cubicTo(cx, cy - r * 0.2f, cx, cy + r * 0.2f, cx - r * 0.5f, cy + r * 0.5f)
        moveTo(cx - r * 0.3f, cy - r * 0.3f)
        cubicTo(cx, cy - r * 0.1f, cx, cy + r * 0.1f, cx - r * 0.3f, cy + r * 0.3f)
    }
    drawPath(wavePath, color, style = Stroke(width = size.width * 0.08f, cap = androidx.compose.ui.graphics.StrokeCap.Round))
    drawLine(
        color, Offset(cx + r * 0.1f, cy - r * 0.6f), Offset(cx + r * 0.1f, cy + r * 0.6f),
        strokeWidth = size.width * 0.08f, cap = androidx.compose.ui.graphics.StrokeCap.Round
    )
}

private fun DrawScope.drawNetflixIcon(color: Color) {
    val w = size.width
    val h = size.height
    val nPath = Path().apply {
        moveTo(w * 0.15f, h * 0.1f)
        lineTo(w * 0.15f, h * 0.9f)
        lineTo(w * 0.5f, h * 0.4f)
        lineTo(w * 0.5f, h * 0.9f)
        lineTo(w * 0.85f, h * 0.9f)
        lineTo(w * 0.85f, h * 0.1f)
        lineTo(w * 0.5f, h * 0.6f)
        lineTo(w * 0.5f, h * 0.1f)
        close()
    }
    drawPath(nPath, color, style = Fill)
}

private fun DrawScope.drawWhatsAppIcon(color: Color) {
    val cx = size.width / 2
    val cy = size.height / 2
    val r = size.width * 0.4f
    drawCircle(color, r, Offset(cx, cy))
    val phonePath = Path().apply {
        moveTo(cx - r * 0.35f, cy - r * 0.25f)
        lineTo(cx - r * 0.25f, cy - r * 0.35f)
        cubicTo(cx - r * 0.1f, cy - r * 0.25f, cx - r * 0.1f, cy - r * 0.25f, cx, cy - r * 0.15f)
        lineTo(cx - r * 0.1f, cy - r * 0.05f)
        cubicTo(cx - r * 0.2f, cy + r * 0.05f, cx - r * 0.3f, cy + r * 0.15f, cx - r * 0.4f, cy + r * 0.05f)
        close()
    }
    drawPath(phonePath, color, style = Fill)
}

private fun DrawScope.drawBlockedIcon(color: Color) {
    val cx = size.width / 2
    val cy = size.height / 2
    val r = size.width * 0.4f
    drawCircle(color, r, Offset(cx, cy))
    drawLine(Color.White, Offset(cx - r * 0.3f, cy), Offset(cx + r * 0.3f, cy), strokeWidth = size.width * 0.12f, cap = androidx.compose.ui.graphics.StrokeCap.Round)
}

data class DomainSuggestion(
    val domain: String,
    val label: String
)

val domainSuggestions = listOf(
    DomainSuggestion("youtube.com", "YouTube"),
    DomainSuggestion("reddit.com", "Reddit"),
    DomainSuggestion("x.com", "X / Twitter"),
    DomainSuggestion("instagram.com", "Instagram"),
    DomainSuggestion("tiktok.com", "TikTok"),
    DomainSuggestion("facebook.com", "Facebook"),
    DomainSuggestion("netflix.com", "Netflix"),
    DomainSuggestion("twitch.tv", "Twitch"),
    DomainSuggestion("discord.com", "Discord"),
    DomainSuggestion("whatsapp.com", "WhatsApp"),
    DomainSuggestion("snapchat.com", "Snapchat"),
    DomainSuggestion("pinterest.com", "Pinterest"),
    DomainSuggestion("linkedin.com", "LinkedIn"),
    DomainSuggestion("github.com", "GitHub"),
    DomainSuggestion("spotify.com", "Spotify"),
    DomainSuggestion("telegram.org", "Telegram"),
    DomainSuggestion("amazon.com", "Amazon"),
    DomainSuggestion("medium.com", "Medium"),
)
