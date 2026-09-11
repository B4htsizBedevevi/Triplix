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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { TriplixApp() }
    }
}

private val Bg = Color(0xFF070914)
private val Panel = Color(0xFF111629)
private val Panel2 = Color(0xFF171D32)
private val TextMain = Color(0xFFF7F9FF)
private val TextMuted = Color(0xFF9CA7C2)
private val Accent = Color(0xFF7C5CFF)

private data class World(
    val id: Int,
    val name: String,
    val subtitle: String,
    val icon: String,
    val tiles: List<String>,
    val colors: List<Color>,
    val locked: Boolean = false
)

private val Worlds = listOf(
    World(0, "Meyve Bahçesi", "Tatlı eşleşmeler", "🍓", listOf("🍓", "🍌", "🍇", "🍎", "🍊", "🍉"), listOf(Color(0xFFFF5D76), Color(0xFFFFC94D), Color(0xFFAA67E8), Color(0xFFE94B5F), Color(0xFFFF8A3D), Color(0xFF48C96B))),
    World(1, "Kristal Dünyası", "Parla • Eşleştir • Patlat", "💎", listOf("💎", "🔷", "🟢", "🔮", "🟡", "💠"), listOf(Color(0xFF54C7FF), Color(0xFF4B8CFF), Color(0xFF42D99A), Color(0xFFB75CFF), Color(0xFFFFC94D), Color(0xFF6A7CFF))),
    World(2, "Sihirli Objeler", "Büyülü maceralar", "🔮", listOf("🧙", "🧪", "📖", "🗝️", "🪄", "🔮"), listOf(Color(0xFF9A62FF), Color(0xFF5BE0C2), Color(0xFFE9B35C), Color(0xFFFFC95A), Color(0xFF6D8CFF), Color(0xFFEA63D1))),
    World(3, "Kozmik Evren", "Uzayın derinlikleri", "🚀", listOf("🚀", "🪐", "⭐", "☄️", "🌙", "🌌"), listOf(Color(0xFFFF5E74), Color(0xFF4AA8FF), Color(0xFFFFD45B), Color(0xFFFF8A4D), Color(0xFF9B76FF), Color(0xFF55D8FF))),
    World(4, "Sevimli Dostlar", "Tatlı arkadaşlar", "🐱", listOf("🐱", "🐶", "🐼", "🐰", "🐸", "🐧"), listOf(Color(0xFFFFA45B), Color(0xFFB8A38E), Color(0xFF6FA8DC), Color(0xFFFF8FB5), Color(0xFF62D59A), Color(0xFF73B9E8))),
    World(5, "Antik Semboller", "Zamanı aşan taşlar", "🏺", listOf("☀️", "👁️", "🔺", "🌀", "🌿", "🜁"), listOf(Color(0xFFE8B45A), Color(0xFF6FC4FF), Color(0xFFFF7E56), Color(0xFFB179FF), Color(0xFF62D29A), Color(0xFFD2A85D))),
    World(6, "Doğa Elementleri", "Doğanın gücü", "🌿", listOf("🌿", "💧", "🔥", "🌪️", "🪨", "🌸"), listOf(Color(0xFF55D77B), Color(0xFF4DA9FF), Color(0xFFFF654F), Color(0xFF71C9FF), Color(0xFF9B8068), Color(0xFFFF7FB2))),
    World(7, "Mevsimler", "Her mevsim başka", "❄️", listOf("❄️", "🌸", "☀️", "🍁", "🌧️", "🌙"), listOf(Color(0xFF73CFFF), Color(0xFFFF8CB8), Color(0xFFFFC94D), Color(0xFFFF8A4D), Color(0xFF6CA7D9), Color(0xFF9B82FF))),
    World(8, "Özel Koleksiyon", "TRIPLIX'in nadirleri", "👑", listOf("👑", "❤️", "⚡", "💜", "♾️", "🌟"), listOf(Color(0xFFFFC84D), Color(0xFFFF5870), Color(0xFF62CFFF), Color(0xFFB35CFF), Color(0xFF5CD9B0), Color(0xFFFFD75C)))
)

private data class Tile(val id: Int, val type: Int)
private data class Particle(val color: Color, val angle: Float, val distance: Float, val size: Float)

@Composable
fun TriplixApp() {
    var screen by rememberSaveable { mutableStateOf("home") }
    var worldId by rememberSaveable { mutableIntStateOf(0) }
    var level by rememberSaveable { mutableIntStateOf(1) }
    val world = Worlds[worldId]

    MaterialTheme(colorScheme = darkColorScheme()) {
        Surface(Modifier.fillMaxSize(), color = Bg) {
            when (screen) {
                "home" -> HomeScreen(
                    onPlay = { screen = "worlds" },
                    onQuickPlay = { screen = "game" }
                )
                "worlds" -> WorldScreen(
                    worlds = Worlds,
                    selected = worldId,
                    onBack = { screen = "home" },
                    onWorld = { id -> worldId = id; screen = "levels" }
                )
                "levels" -> LevelScreen(
                    world = world,
                    onBack = { screen = "worlds" },
                    onLevel = { n -> level = n; screen = "game" }
                )
                else -> GameScreen(
                    world = world,
                    level = level,
                    onBack = { screen = "levels" },
                    onNext = { level += 1 }
                )
            }
        }
    }
}

@Composable
private fun HomeScreen(onPlay: () -> Unit, onQuickPlay: () -> Unit) {
    val infinite = rememberInfiniteTransition(label = "home")
    val pulse by infinite.animateFloat(0.96f, 1.04f, infiniteRepeatable(tween(1000), RepeatMode.Reverse), label = "pulse")

    Column(
        Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            Modifier.size(112.dp).scale(pulse).clip(RoundedCornerShape(32.dp))
                .background(Brush.linearGradient(listOf(Accent, Color(0xFFB85CFF), Color(0xFF4BBEFF))))
                .border(2.dp, Color.White.copy(.22f), RoundedCornerShape(32.dp)),
            contentAlignment = Alignment.Center
        ) { Text("3", color = Color.White, fontSize = 56.sp, fontWeight = FontWeight.Black) }

        Spacer(Modifier.height(18.dp))
        Text("TRIPLIX", color = TextMain, fontSize = 46.sp, fontWeight = FontWeight.Black, letterSpacing = 1.sp)
        Text("AYNI 3'Ü BİRLEŞTİR • DAHA FAZLASINI KEŞFET", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(28.dp))

        Surface(color = Panel, shape = RoundedCornerShape(24.dp), modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(18.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🌎", fontSize = 34.sp)
                Text("DÜNYANI SEÇ", color = TextMain, fontSize = 18.sp, fontWeight = FontWeight.Black)
                Text("9 farklı tema • yüzlerce bölüm", color = TextMuted, fontSize = 12.sp)
            }
        }
        Spacer(Modifier.height(16.dp))
        Button(onClick = onPlay, modifier = Modifier.fillMaxWidth().height(58.dp), shape = RoundedCornerShape(18.dp), colors = ButtonDefaults.buttonColors(containerColor = Accent)) {
            Text("DÜNYALARI KEŞFET  ›", fontSize = 17.sp, fontWeight = FontWeight.Black)
        }
        Spacer(Modifier.height(10.dp))
        OutlinedButton(onClick = onQuickPlay, modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(17.dp)) {
            Text("⚡ HIZLI OYNA", fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(18.dp))
        Text("TRIPLIX • V0.3", color = TextMuted, fontSize = 11.sp)
    }
}

@Composable
private fun WorldScreen(worlds: List<World>, selected: Int, onBack: () -> Unit, onWorld: (Int) -> Unit) {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        TopBar(title = "DÜNYALAR", subtitle = "Kendi maceranı seç", onBack = onBack)
        Spacer(Modifier.height(12.dp))
        LazyVerticalGrid(columns = GridCells.Fixed(2), modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 20.dp), verticalArrangement = Arrangement.spacedBy(12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(worlds) { world -> WorldCard(world, selected == world.id) { onWorld(world.id) } }
        }
    }
}

@Composable
private fun WorldCard(world: World, selected: Boolean, onClick: () -> Unit) {
    val infinite = rememberInfiniteTransition(label = "world${world.id}")
    val glow by infinite.animateFloat(0.5f, 0.9f, infiniteRepeatable(tween(1400), RepeatMode.Reverse), label = "glow")
    Surface(onClick = onClick, color = Panel, shape = RoundedCornerShape(22.dp), modifier = Modifier.fillMaxWidth().height(154.dp).border(1.dp, if (selected) Accent.copy(glow) else Color.White.copy(.07f), RoundedCornerShape(22.dp))) {
        Column(Modifier.padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Box(Modifier.size(58.dp).clip(RoundedCornerShape(18.dp)).background(Brush.linearGradient(world.colors.take(3))), contentAlignment = Alignment.Center) {
                Text(world.icon, fontSize = 32.sp)
            }
            Spacer(Modifier.height(8.dp))
            Text(world.name, color = TextMain, fontSize = 14.sp, fontWeight = FontWeight.Black)
            Text(world.subtitle, color = TextMuted, fontSize = 10.sp)
            Spacer(Modifier.height(5.dp))
            Text("50 BÖLÜM  ›", color = Accent, fontSize = 9.sp, fontWeight = FontWeight.Black)
        }
    }
}

@Composable
private fun LevelScreen(world: World, onBack: () -> Unit, onLevel: (Int) -> Unit) {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        TopBar(title = world.name.uppercase(), subtitle = "Bölümünü seç", onBack = onBack, icon = world.icon)
        Spacer(Modifier.height(10.dp))
        Surface(color = Panel, shape = RoundedCornerShape(20.dp), modifier = Modifier.fillMaxWidth()) {
            Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(world.tiles.take(3).joinToString(" "), fontSize = 25.sp)
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text("İLERLEME", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text("1 / 50", color = TextMain, fontSize = 16.sp, fontWeight = FontWeight.Black)
                }
                Text("⭐ 200", color = Color(0xFFFFD45B), fontWeight = FontWeight.Black)
            }
        }
        Spacer(Modifier.height(14.dp))
        LazyVerticalGrid(columns = GridCells.Fixed(4), modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 20.dp), verticalArrangement = Arrangement.spacedBy(12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items((1..24).toList()) { n ->
                LevelButton(n, unlocked = n <= 3, onClick = { if (n <= 3) onLevel(n) })
            }
        }
    }
}

@Composable
private fun LevelButton(number: Int, unlocked: Boolean, onClick: () -> Unit) {
    Surface(onClick = onClick, enabled = unlocked, color = if (unlocked) Panel2 else Color(0xFF0D1020), shape = RoundedCornerShape(18.dp), modifier = Modifier.height(82.dp).border(1.dp, if (unlocked) Color.White.copy(.08f) else Color.White.copy(.035f), RoundedCornerShape(18.dp))) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            if (unlocked) {
                Text(if (number == 1) "▶" else "🔒".takeIf { false } ?: "⭐", color = if (number == 1) Color(0xFF63E6BE) else Color(0xFFFFD45B), fontSize = 19.sp)
                Text("$number", color = TextMain, fontWeight = FontWeight.Black)
            } else {
                Text("🔒", fontSize = 17.sp)
                Text("$number", color = TextMuted, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun GameScreen(world: World, level: Int, onBack: () -> Unit, onNext: () -> Unit) {
    val view = LocalView.current
    var score by rememberSaveable(world.id, level) { mutableIntStateOf(200) }
    var coins by rememberSaveable(world.id, level) { mutableIntStateOf(395) }
    var lives by rememberSaveable(world.id, level) { mutableIntStateOf(3) }
    var combo by rememberSaveable(world.id, level) { mutableIntStateOf(0) }
    var round by rememberSaveable(world.id, level) { mutableIntStateOf(0) }
    var removed by remember { mutableStateOf(setOf<Int>()) }
    var selected by remember { mutableStateOf(setOf<Int>()) }
    var tray by remember { mutableStateOf(listOf<Int>()) }
    var particles by remember { mutableStateOf(listOf<Particle>()) }
    var banner by remember { mutableStateOf<String?>(null) }
    var gameOver by remember { mutableStateOf(false) }
    var won by remember { mutableStateOf(false) }
    val tiles = remember(world.id, level, round) { (0 until 36).map { Tile(it, (it / 2) % world.tiles.size) }.shuffled(Random(level * 97 + world.id * 31 + round)) }

    Box(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize().padding(horizontal = 14.dp, vertical = 8.dp)) {
            TopBar(title = "SEVİYE $level", subtitle = world.name, onBack = onBack, icon = world.icon, trailing = "🪙 $coins")
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("⭐ $score", color = TextMain, fontWeight = FontWeight.Black, fontSize = 16.sp)
                Text("🔥 x$combo", color = Color(0xFFFFB55B), fontWeight = FontWeight.Black, fontSize = 16.sp)
                Text("❤️ $lives", color = TextMain, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(9.dp))

            Box(Modifier.fillMaxWidth().weight(1f).clip(RoundedCornerShape(28.dp)).background(Brush.verticalGradient(listOf(Color(0xFF151B30), Color(0xFF0B0F1D)))).border(1.dp, Color.White.copy(.08f), RoundedCornerShape(28.dp)).padding(10.dp)) {
                Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    Text("AYNI 3'Ü BİRLEŞTİR", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Black)
                    Text("${world.icon}  ${world.name}", color = TextMain, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(10.dp))
                    tiles.chunked(6).forEachIndexed { rowIndex, row ->
                        Row(horizontalArrangement = Arrangement.spacedBy(5.dp), modifier = Modifier.padding(vertical = 2.dp).offset(x = if (rowIndex % 2 == 0) 0.dp else 5.dp)) {
                            row.forEach { tile ->
                                AnimatedVisibility(visible = !removed.contains(tile.id), enter = scaleIn(spring(dampingRatio = .55f)) + fadeIn(), exit = scaleOut(tween(180)) + fadeOut(tween(130))) {
                                    GameTile(tile, world, selected.contains(tile.id)) {
                                        if (!removed.contains(tile.id) && tray.size < 7) {
                                            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                            selected = selected + tile.id
                                            view.postDelayed({ selected = selected - tile.id }, 100)
                                            removed = removed + tile.id
                                            tray = tray + tile.type
                                            if (tray.count { it == tile.type } >= 3) {
                                                combo += 1
                                                val add = 30 + combo * 10
                                                score += add
                                                coins += 8 + combo
                                                banner = "PERFECT!  +$add"
                                                particles = List(30) { Particle(world.colors[tile.type], Random.nextFloat() * 360f, 25f + Random.nextFloat() * 150f, 3f + Random.nextFloat() * 7f) }
                                                var count = 0
                                                tray = tray.filter { if (it == tile.type && count < 3) { count++; false } else true }
                                                view.postDelayed({ banner = null }, 650)
                                            } else combo = 0
                                            if (removed.size == tiles.size) won = true
                                            if (tray.size >= 7) {
                                                lives--
                                                combo = 0
                                                banner = "TEPSİ DOLDU!"
                                                tray = emptyList()
                                                view.postDelayed({ banner = null }, 650)
                                                if (lives <= 0) gameOver = true
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                ParticleBurst(particles)
                banner?.let { msg ->
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Surface(color = Color.Black.copy(.78f), shape = RoundedCornerShape(22.dp), modifier = Modifier.border(1.dp, Accent.copy(.5f), RoundedCornerShape(22.dp))) {
                            Text(msg, Modifier.padding(horizontal = 24.dp, vertical = 15.dp), color = Color.White, fontSize = 21.sp, fontWeight = FontWeight.Black)
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("TEPSİ ${tray.size}/7", color = TextMuted, fontWeight = FontWeight.Black)
                Text("3 eşleşince patlar ✨", color = TextMuted, fontSize = 10.sp)
            }
            Spacer(Modifier.height(5.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) { repeat(7) { TraySlot(tray.getOrNull(it), world) } }
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                Action("🔀", "Karıştır") { if (coins >= 15) { coins -= 15; round++ } }
                Action("💡", "İpucu") { if (coins >= 5) { coins -= 5; banner = "Aynı sembolden 2 tane daha ara!"; view.postDelayed({ banner = null }, 900) } }
                Action("↩", "Geri Al") { if (tray.isNotEmpty()) tray = tray.dropLast(1) }
            }
        }
        if (won) WinOverlay(score, coins, onNext = { won = false; round++; onNext() }, onLevels = onBack)
        if (gameOver) GameOver(score, onRestart = { score = 200; coins = 395; lives = 3; combo = 0; removed = emptySet(); selected = emptySet(); tray = emptyList(); round++; gameOver = false }, onBack = onBack)
    }
}

@Composable
private fun GameTile(tile: Tile, world: World, selected: Boolean, onClick: () -> Unit) {
    val scale by animateFloatAsState(if (selected) 1.13f else 1f, spring(dampingRatio = .5f), label = "tileScale")
    val rot by animateFloatAsState(if (selected) 6f else 0f, spring(dampingRatio = .5f), label = "tileRot")
    Box(Modifier.size(48.dp).scale(scale).rotate(rot).clip(RoundedCornerShape(14.dp)).background(Brush.linearGradient(listOf(world.colors[tile.type], world.colors[tile.type].copy(alpha = .55f)))).border(1.5.dp, Color.White.copy(if (selected) .9f else .16f), RoundedCornerShape(14.dp)).clickable { onClick() }, contentAlignment = Alignment.Center) {
        Text(world.tiles[tile.type], fontSize = 24.sp)
    }
}

@Composable
private fun RowScope.TraySlot(value: Int?, world: World) {
    Box(Modifier.weight(1f).height(52.dp).clip(RoundedCornerShape(12.dp)).background(Color.White.copy(.045f)).border(1.dp, Color.White.copy(.08f), RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
        if (value != null) Box(Modifier.size(37.dp).clip(RoundedCornerShape(10.dp)).background(world.colors[value]), contentAlignment = Alignment.Center) { Text(world.tiles[value], fontSize = 18.sp) }
    }
}

@Composable
private fun RowScope.Action(icon: String, label: String, onClick: () -> Unit) {
    Surface(onClick = onClick, color = Panel, shape = RoundedCornerShape(15.dp), modifier = Modifier.weight(1f)) {
        Column(Modifier.padding(vertical = 7.dp), horizontalAlignment = Alignment.CenterHorizontally) { Text(icon, fontSize = 18.sp); Text(label, color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold) }
    }
}

@Composable
private fun TopBar(title: String, subtitle: String, onBack: () -> Unit, icon: String? = null, trailing: String? = null) {
    Row(Modifier.fillMaxWidth().height(58.dp), verticalAlignment = Alignment.CenterVertically) {
        TextButton(onClick = onBack) { Text("‹", color = TextMain, fontSize = 32.sp) }
        if (icon != null) Text(icon, fontSize = 22.sp)
        Spacer(Modifier.width(7.dp))
        Column(Modifier.weight(1f)) { Text(title, color = TextMain, fontSize = 17.sp, fontWeight = FontWeight.Black); Text(subtitle, color = Accent, fontSize = 10.sp, fontWeight = FontWeight.Bold) }
        if (trailing != null) Surface(color = Color.White.copy(.06f), shape = RoundedCornerShape(14.dp)) { Text(trailing, Modifier.padding(horizontal = 10.dp, vertical = 7.dp), color = TextMain, fontSize = 11.sp, fontWeight = FontWeight.Bold) }
    }
}

@Composable
private fun ParticleBurst(particles: List<Particle>) {
    if (particles.isEmpty()) return
    var progress by remember(particles) { mutableFloatStateOf(0f) }
    LaunchedEffect(particles) {
        val start = withFrameNanos { it }
        while (progress < 1f) progress = ((withFrameNanos { it } - start) / 520_000_000f).coerceIn(0f, 1f)
    }
    Canvas(Modifier.fillMaxSize()) {
        particles.forEach { item ->
            val radius = item.distance * progress
            val angle = Math.toRadians(item.angle.toDouble())
            drawCircle(item.color.copy(alpha = 1f - progress), item.size * (1f - progress * .35f), Offset(size.width / 2f + cos(angle).toFloat() * radius, size.height / 2f + sin(angle).toFloat() * radius))
        }
    }
}

@Composable
private fun WinOverlay(score: Int, coins: Int, onNext: () -> Unit, onLevels: () -> Unit) {
    Box(Modifier.fillMaxSize().background(Color.Black.copy(.82f)), contentAlignment = Alignment.Center) {
        Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF151B2D)), shape = RoundedCornerShape(30.dp), modifier = Modifier.fillMaxWidth(.88f)) {
            Column(Modifier.padding(26.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("✨🏆✨", fontSize = 38.sp)
                Text("SEVİYE TAMAMLANDI!", color = TextMain, fontSize = 23.sp, fontWeight = FontWeight.Black)
                Spacer(Modifier.height(8.dp))
                Text("⭐ $score     🪙 $coins", color = Color(0xFFFFD45B), fontWeight = FontWeight.Black)
                Spacer(Modifier.height(20.dp))
                Button(onClick = onNext, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.buttonColors(containerColor = Accent)) { Text("SONRAKİ SEVİYE  ›", fontWeight = FontWeight.Black) }
                TextButton(onClick = onLevels) { Text("Bölümlere Dön") }
            }
        }
    }
}

@Composable
private fun GameOver(score: Int, onRestart: () -> Unit, onBack: () -> Unit) {
    Box(Modifier.fillMaxSize().background(Color.Black.copy(.82f)), contentAlignment = Alignment.Center) {
        Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF151B2D)), shape = RoundedCornerShape(30.dp), modifier = Modifier.fillMaxWidth(.88f)) {
            Column(Modifier.padding(26.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("💥", fontSize = 50.sp)
                Text("OYUN BİTTİ", color = TextMain, fontSize = 28.sp, fontWeight = FontWeight.Black)
                Text("Skor: $score", color = TextMuted)
                Spacer(Modifier.height(18.dp))
                Button(onClick = onRestart, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) { Text("🔄 TEKRAR OYNA", fontWeight = FontWeight.Black) }
                TextButton(onClick = onBack) { Text("Bölümlere Dön") }
            }
        }
    }
}
