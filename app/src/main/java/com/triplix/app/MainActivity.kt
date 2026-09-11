package com.triplix.app

import android.os.Bundle
import android.view.HapticFeedbackConstants
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { TriplixApp() }
    }
}

private val Bg = Color(0xFF080A12)
private val Panel = Color(0xFF111522)
private val TextMain = Color(0xFFF7F9FF)
private val TextMuted = Color(0xFF9EA7BF)
private val Accent = Color(0xFF7C5CFF)
private val TileColors = listOf(
    Color(0xFFFF6B7A), Color(0xFFFFC857), Color(0xFF43D6A4),
    Color(0xFF4DB6FF), Color(0xFFBC79FF), Color(0xFFFF8E4D)
)
private val TileIcons = listOf("◆", "●", "✦", "■", "▲", "⬟")

data class Particle(val color: Color, val angle: Float, val distance: Float, val size: Float)

@Composable
fun TriplixApp() {
    var started by rememberSaveable { mutableStateOf(false) }
    MaterialTheme(colorScheme = darkColorScheme()) {
        Surface(Modifier.fillMaxSize(), color = Bg) {
            if (started) GameScreen { started = false } else HomeScreen { started = true }
        }
    }
}

@Composable
private fun HomeScreen(onStart: () -> Unit) {
    val infinite = rememberInfiniteTransition(label = "logo")
    val pulse by infinite.animateFloat(
        0.92f, 1.05f,
        infiniteRepeatable(tween(900, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "pulse"
    )
    Column(
        Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            Modifier.size(104.dp).scale(pulse).clip(RoundedCornerShape(30.dp))
                .background(Brush.linearGradient(listOf(Accent, Color(0xFFB56CFF))))
                .border(2.dp, Color.White.copy(alpha = .18f), RoundedCornerShape(30.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text("3", color = Color.White, fontSize = 52.sp, fontWeight = FontWeight.Black)
        }
        Spacer(Modifier.height(20.dp))
        Text("TRIPLIX", color = TextMain, fontSize = 44.sp, fontWeight = FontWeight.Black)
        Text("Triple Match Puzzle", color = TextMuted, fontSize = 15.sp)
        Spacer(Modifier.height(40.dp))
        Button(
            onClick = onStart,
            modifier = Modifier.fillMaxWidth().height(60.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Accent)
        ) { Text("OYNA", fontSize = 19.sp, fontWeight = FontWeight.Black) }
        Spacer(Modifier.height(14.dp))
        Text("V1 • bol efektli prototip", color = TextMuted, fontSize = 12.sp)
    }
}

private data class Tile(val id: Int, val type: Int)

@Composable
private fun GameScreen(onBack: () -> Unit) {
    val view = LocalView.current
    var score by rememberSaveable { mutableIntStateOf(0) }
    var coins by rememberSaveable { mutableIntStateOf(350) }
    var lives by rememberSaveable { mutableIntStateOf(3) }
    var combo by rememberSaveable { mutableIntStateOf(0) }
    var removed by remember { mutableStateOf(setOf<Int>()) }
    var selected by remember { mutableStateOf(setOf<Int>()) }
    var tray by remember { mutableStateOf(listOf<Int>()) }
    var particles by remember { mutableStateOf(listOf<Particle>()) }
    var banner by remember { mutableStateOf<String?>(null) }
    var gameOver by remember { mutableStateOf(false) }
    val tiles = remember { (0 until 36).map { Tile(it, (it / 2) % TileIcons.size) }.shuffled() }

    Box(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize().padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                TextButton(onClick = onBack) { Text("‹", color = TextMuted, fontSize = 30.sp) }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("LEVEL 1", color = TextMain, fontWeight = FontWeight.Black)
                    Text("TRIPLIX", color = Accent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                    SmallPill("🪙", coins.toString())
                    SmallPill("❤️", lives.toString())
                }
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("⭐ " + score, color = TextMain, fontWeight = FontWeight.Bold)
                Text("🔥 x" + combo, color = Color(0xFFFFB55B), fontWeight = FontWeight.Black)
            }
            Spacer(Modifier.height(12.dp))

            Box(
                Modifier.fillMaxWidth().weight(1f).clip(RoundedCornerShape(28.dp))
                    .background(Brush.verticalGradient(listOf(Color(0xFF171C2C), Color(0xFF0D111D))))
                    .border(1.dp, Color.White.copy(alpha = .07f), RoundedCornerShape(28.dp))
                    .padding(12.dp)
            ) {
                Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    Text("AYNI 3 TAŞI BİRLEŞTİR", color = TextMuted, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(10.dp))
                    tiles.chunked(6).forEach { row ->
                        Row(horizontalArrangement = Arrangement.spacedBy(7.dp), modifier = Modifier.padding(3.dp)) {
                            row.forEach { tile ->
                                AnimatedVisibility(
                                    visible = !removed.contains(tile.id),
                                    enter = scaleIn(animationSpec = spring(dampingRatio = .55f)) + fadeIn(),
                                    exit = scaleOut(tween(180)) + fadeOut(tween(140))
                                ) {
                                    TileCard(
                                        tile = tile,
                                        selected = selected.contains(tile.id),
                                        onClick = {
                                            if (!removed.contains(tile.id) && tray.size < 7) {
                                                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                                selected = selected + tile.id
                                                // brief press effect
                                                android.os.Handler(mainLooper).postDelayed({
                                                    selected = selected - tile.id
                                                }, 90)
                                                removed = removed + tile.id
                                                tray = tray + tile.type
                                                if (tray.count { it == tile.type } >= 3) {
                                                    combo += 1
                                                    val add = 30 + combo * 10
                                                    score += add
                                                    coins += 8 + combo
                                                    banner = "PERFECT! +" + add
                                                    particles = List(26) {
                                                        Particle(
                                                            TileColors[tile.type],
                                                            Random.nextFloat() * 360f,
                                                            35f + Random.nextFloat() * 150f,
                                                            3f + Random.nextFloat() * 6f
                                                        )
                                                    }
                                                    var left = tray
                                                    var count = 0
                                                    left = left.filter {
                                                        if (it == tile.type && count < 3) { count++; false } else true
                                                    }
                                                    tray = left
                                                    android.os.Handler(mainLooper).postDelayed({ banner = null }, 600)
                                                } else combo = 0
                                                if (tray.size >= 7) {
                                                    lives--
                                                    combo = 0
                                                    banner = "TEPSİ DOLDU!"
                                                    tray = emptyList()
                                                    android.os.Handler(mainLooper).postDelayed({ banner = null }, 650)
                                                    if (lives <= 0) gameOver = true
                                                }
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
                ParticleBurst(particles)
                banner?.let {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Surface(color = Color.Black.copy(alpha = .76f), shape = RoundedCornerShape(22.dp)) {
                            Text(it, Modifier.padding(horizontal = 22.dp, vertical = 14.dp), color = Color.White, fontSize = 21.sp, fontWeight = FontWeight.Black)
                        }
                    }
                }
            }

            Spacer(Modifier.height(10.dp))
            Text("TEPSİ " + tray.size + "/7", color = TextMuted, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(6.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                repeat(7) { i -> TraySlot(tray.getOrNull(i)) }
            }
            Spacer(Modifier.height(9.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Action("🔀", "Karıştır") { coins = (coins - 15).coerceAtLeast(0) }
                Action("💡", "İpucu") { coins = (coins - 5).coerceAtLeast(0); banner = "Aynı taştan 2 tane daha bul!" }
                Action("↩", "Geri Al") { if (tray.isNotEmpty()) tray = tray.dropLast(1) }
            }
        }

        if (gameOver) {
            GameOver(score, onRestart = {
                score = 0; coins = 350; lives = 3; combo = 0
                removed = emptySet(); selected = emptySet(); tray = emptyList(); gameOver = false
            }, onBack = onBack)
        }
    }
}

@Composable
private fun TileCard(tile: Tile, selected: Boolean, onClick: () -> Unit) {
    val scale by animateFloatAsState(if (selected) 1.14f else 1f, spring(dampingRatio = .5f), label = "s")
    val rot by animateFloatAsState(if (selected) 7f else 0f, spring(dampingRatio = .5f), label = "r")
    Box(
        Modifier.size(48.dp).scale(scale).rotate(rot).clip(RoundedCornerShape(14.dp))
            .background(Brush.linearGradient(listOf(TileColors[tile.type], TileColors[tile.type].copy(alpha = .58f))))
            .border(1.5.dp, Color.White.copy(alpha = if (selected) .85f else .18f), RoundedCornerShape(14.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(TileIcons[tile.type], color = Color.White, fontSize = 23.sp, fontWeight = FontWeight.Black)
    }
}

@Composable
private fun TraySlot(value: Int?) {
    Box(
        Modifier.weight(1f).height(54.dp).clip(RoundedCornerShape(12.dp))
            .background(Color.White.copy(alpha = .045f))
            .border(1.dp, Color.White.copy(alpha = .08f), RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        if (value != null) {
            Box(Modifier.size(38.dp).clip(RoundedCornerShape(11.dp)).background(TileColors[value]), contentAlignment = Alignment.Center) {
                Text(TileIcons[value], color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Black)
            }
        }
    }
}

@Composable
private fun SmallPill(icon: String, text: String) {
    Surface(color = Color.White.copy(alpha = .06f), shape = RoundedCornerShape(14.dp)) {
        Row(Modifier.padding(horizontal = 8.dp, vertical = 6.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(icon, fontSize = 12.sp)
            Text(text, color = TextMain, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun Action(icon: String, label: String, onClick: () -> Unit) {
    Surface(onClick = onClick, color = Panel, shape = RoundedCornerShape(15.dp), modifier = Modifier.weight(1f)) {
        Column(Modifier.padding(vertical = 8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(icon, fontSize = 18.sp)
            Text(label, color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ParticleBurst(particles: List<Particle>) {
    if (particles.isEmpty()) return
    var p by remember(particles) { mutableFloatStateOf(0f) }
    LaunchedEffect(particles) {
        val start = withFrameNanos { it }
        while (p < 1f) {
            p = ((withFrameNanos { it } - start) / 520_000_000f).coerceIn(0f, 1f)
        }
    }
    Canvas(Modifier.fillMaxSize()) {
        particles.forEach { item ->
            val r = item.distance * p
            val a = Math.toRadians(item.angle.toDouble())
            drawCircle(
                item.color.copy(alpha = 1f - p),
                item.size * (1f - p * .35f),
                Offset(size.width / 2f + cos(a).toFloat() * r, size.height / 2f + sin(a).toFloat() * r)
            )
        }
    }
}

@Composable
private fun GameOver(score: Int, onRestart: () -> Unit, onBack: () -> Unit) {
    Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = .8f)), contentAlignment = Alignment.Center) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF161B2A)),
            shape = RoundedCornerShape(30.dp),
            modifier = Modifier.fillMaxWidth(.88f)
        ) {
            Column(Modifier.padding(26.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("💥", fontSize = 54.sp)
                Text("OYUN BİTTİ", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Black)
                Text("Skor: " + score, color = TextMuted)
                Spacer(Modifier.height(18.dp))
                Button(onClick = onRestart, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                    Text("🔄 TEKRAR OYNA", fontWeight = FontWeight.Black)
                }
                TextButton(onClick = onBack) { Text("Ana Menü") }
            }
        }
    }
}
