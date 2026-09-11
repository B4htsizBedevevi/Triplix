package com.triplix.app

import android.content.Context
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
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { App() }
    }
}

private val BG = Color(0xFF050711)
private val PANEL = Color(0xFF111629)
private val PANEL2 = Color(0xFF1A2140)
private val TXT = Color(0xFFF8FAFF)
private val MUTED = Color(0xFF9DA8C5)
private val ACC = Color(0xFF795BFF)
private val GOLD = Color(0xFFFFD45B)
private val RED = Color(0xFFFF5E76)
private val GREEN = Color(0xFF5BE09A)

private data class World(
    val id: Int,
    val name: String,
    val icon: String,
    val tiles: List<String>,
    val colors: List<Color>
)

private data class Tile(
    val id: Int,
    val type: Int,
    val x: Float,
    val y: Float,
    val layer: Int,
    val removed: Boolean = false
)

private data class Snap(
    val board: List<Tile>,
    val tray: List<Int>,
    val score: Int,
    val combo: Int,
    val coins: Int
)

private val WORLDS = listOf(
    World(0, "Meyve Bahçesi", "🍓", listOf("🍓", "🍌", "🍇", "🍎", "🍊", "🍉"), listOf(Color(0xFFFF5570), Color(0xFFFFC94D), Color(0xFFAA67E8), Color(0xFFE84E62), Color(0xFFFF8A3D), Color(0xFF45C96B))),
    World(1, "Kristal Dünyası", "💎", listOf("💎", "🔷", "🟢", "🔮", "🟡", "💠"), listOf(Color(0xFF54C7FF), Color(0xFF4B8CFF), Color(0xFF42D99A), Color(0xFFB75CFF), Color(0xFFFFC94D), Color(0xFF6A7CFF))),
    World(2, "Sihirli Objeler", "🔮", listOf("🧙", "🧪", "📖", "🗝️", "🪄", "🔮"), listOf(Color(0xFF9A62FF), Color(0xFF5BE0C2), Color(0xFFE9B35C), Color(0xFFFFC95A), Color(0xFF6D8CFF), Color(0xFFEA63D1))),
    World(3, "Kozmik Evren", "🚀", listOf("🚀", "🪐", "⭐", "☄️", "🌙", "🌌"), listOf(Color(0xFFFF5E74), Color(0xFF4AA8FF), Color(0xFFFFD45B), Color(0xFFFF8A4D), Color(0xFF9B76FF), Color(0xFF55D8FF))),
    World(4, "Sevimli Dostlar", "🐱", listOf("🐱", "🐶", "🐼", "🐰", "🐸", "🐧"), listOf(Color(0xFFFFA45B), Color(0xFFB8A38E), Color(0xFF6FA8DC), Color(0xFFFF8FB5), Color(0xFF62D59A), Color(0xFF73B9E8))),
    World(5, "Antik Semboller", "🏺", listOf("☀️", "👁️", "🔺", "🌀", "🌿", "🜁"), listOf(Color(0xFFE8B45A), Color(0xFF6FC4FF), Color(0xFFFF7E56), Color(0xFFB179FF), Color(0xFF62D29A), Color(0xFFD2A85D))),
    World(6, "Doğa Elementleri", "🌿", listOf("🌿", "💧", "🔥", "🌪️", "🪨", "🌸"), listOf(Color(0xFF55D77B), Color(0xFF4DA9FF), Color(0xFFFF654F), Color(0xFF71C9FF), Color(0xFF9B8068), Color(0xFFFF7FB2))),
    World(7, "Mevsimler", "❄️", listOf("❄️", "🌸", "☀️", "🍁", "🌧️", "🌙"), listOf(Color(0xFF73CFFF), Color(0xFFFF8CB8), Color(0xFFFFC94D), Color(0xFFFF8A4D), Color(0xFF6CA7D9), Color(0xFF9B82FF))),
    World(8, "Özel Koleksiyon", "👑", listOf("👑", "❤️", "⚡", "💜", "♾️", "🌟"), listOf(Color(0xFFFFC84D), Color(0xFFFF5870), Color(0xFF62CFFF), Color(0xFFB35CFF), Color(0xFF5CD9B0), Color(0xFFFFD75C)))
)

@Composable
fun App() {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("triplix", Context.MODE_PRIVATE) }
    var page by rememberSaveable { mutableStateOf("home") }
    var worldId by rememberSaveable { mutableIntStateOf(0) }
    var level by rememberSaveable { mutableIntStateOf(1) }
    var coins by rememberSaveable { mutableIntStateOf(prefs.getInt("coins", 395)) }
    var games by rememberSaveable { mutableIntStateOf(prefs.getInt("games", 0)) }
    var streak by rememberSaveable { mutableIntStateOf(prefs.getInt("streak", 1)) }
    var mission by rememberSaveable { mutableIntStateOf(prefs.getInt("mission", 0)) }
    var claimed by rememberSaveable { mutableStateOf(prefs.getBoolean("daily", false)) }

    fun save() {
        prefs.edit()
            .putInt("coins", coins)
            .putInt("games", games)
            .putInt("streak", streak)
            .putInt("mission", mission)
            .putBoolean("daily", claimed)
            .apply()
    }

    MaterialTheme(colorScheme = darkColorScheme()) {
        Surface(Modifier.fillMaxSize(), color = BG) {
            when (page) {
                "home" -> Home(
                    coins, streak, games, mission, claimed,
                    play = { page = "worlds" },
                    daily = {
                        if (!claimed) {
                            coins += 50 + streak * 10
                            streak++
                            claimed = true
                            save()
                        }
                    },
                    reward = {
                        if (mission >= 5) {
                            coins += 100
                            mission = 0
                            save()
                        }
                    }
                )
                "worlds" -> Worlds(worldId, { page = "home" }) {
                    worldId = it
                    page = "levels"
                }
                "levels" -> Levels(WORLDS[worldId], { page = "worlds" }) {
                    level = it
                    page = "game"
                }
                else -> Game(
                    world = WORLDS[worldId],
                    level = level,
                    coins = coins,
                    back = { page = "levels" },
                    changeCoins = { coins += it; save() },
                    finish = { earned ->
                        coins += earned
                        games++
                        mission = (mission + 1).coerceAtMost(5)
                        save()
                    },
                    next = { level = (level + 1).coerceAtMost(50) }
                )
            }
        }
    }
}

@Composable
private fun Home(
    coins: Int,
    streak: Int,
    games: Int,
    mission: Int,
    claimed: Boolean,
    play: () -> Unit,
    daily: () -> Unit,
    reward: () -> Unit
) {
    val transition = rememberInfiniteTransition(label = "logo")
    val pulse by transition.animateFloat(.97f, 1.04f, infiniteRepeatable(tween(1000), RepeatMode.Reverse), label = "pulse")

    LazyColumn(Modifier.fillMaxSize().padding(16.dp), contentPadding = PaddingValues(bottom = 24.dp)) {
        item {
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                Column {
                    Text("HOŞ GELDİN 👋", color = MUTED, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text("TRIPLIX", color = TXT, fontSize = 28.sp, fontWeight = FontWeight.Black)
                }
                Pill("🪙 $coins", GOLD)
            }
            Spacer(Modifier.height(14.dp))
            Box(
                Modifier.fillMaxWidth().height(220.dp)
                    .clip(RoundedCornerShape(30.dp))
                    .background(Brush.linearGradient(listOf(Color(0xFF202B61), Color(0xFF38205F), Color(0xFF0D3049))))
                    .border(1.dp, ACC.copy(.5f), RoundedCornerShape(30.dp)),
                Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(Modifier.size(78.dp).scale(pulse).clip(RoundedCornerShape(24.dp)).background(Brush.linearGradient(listOf(Color(0xFFFFB93E), Color(0xFFFF6D55)))), Alignment.Center) {
                        Text("3", color = Color.White, fontSize = 42.sp, fontWeight = FontWeight.Black)
                    }
                    Text("TRIPLIX", color = TXT, fontSize = 36.sp, fontWeight = FontWeight.Black)
                    Text("AYNI 3'Ü BİRLEŞTİR • DAHA FAZLASINI KEŞFET", color = Color.White.copy(.78f), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(10.dp))
                    Text("✨ KATMANLI 3D TAŞLAR • PARÇALAMA EFEKTLERİ", color = GOLD, fontSize = 9.sp, fontWeight = FontWeight.Black)
                }
            }
            Spacer(Modifier.height(12.dp))
            Button(play, Modifier.fillMaxWidth().height(57.dp), shape = RoundedCornerShape(18.dp), colors = ButtonDefaults.buttonColors(containerColor = ACC)) {
                Text("🌎 DÜNYANI SEÇ VE OYNA", fontSize = 16.sp, fontWeight = FontWeight.Black)
            }
            Spacer(Modifier.height(12.dp))
        }
        item {
            Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(9.dp)) {
                Stat("🔥", "$streak", "Seri", Modifier.weight(1f))
                Stat("⭐", "$games", "Oyun", Modifier.weight(1f))
                Stat("🌎", "9", "Dünya", Modifier.weight(1f))
            }
            Spacer(Modifier.height(12.dp))
        }
        item {
            Surface(color = PANEL, shape = RoundedCornerShape(22.dp), modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                        Column {
                            Text("🎁 GÜNLÜK ÖDÜL", color = TXT, fontWeight = FontWeight.Black)
                            Text("Serini koru, ödülü kap!", color = MUTED, fontSize = 11.sp)
                        }
                        Button(daily, enabled = !claimed, shape = RoundedCornerShape(13.dp), contentPadding = PaddingValues(horizontal = 12.dp, vertical = 7.dp)) {
                            Text(if (claimed) "ALINDI ✓" else "+ ÖDÜL", fontSize = 11.sp, fontWeight = FontWeight.Black)
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(6.dp)) {
                        (1..7).forEach { day ->
                            Box(Modifier.size(34.dp).clip(RoundedCornerShape(10.dp)).background(if (day <= streak) ACC.copy(.28f) else Color.White.copy(.04f)).border(1.dp, if (day <= streak) ACC else Color.White.copy(.07f), RoundedCornerShape(10.dp)), Alignment.Center) {
                                Text(if (day == 7) "🎁" else "$day", color = if (day <= streak) GOLD else MUTED, fontSize = 10.sp, fontWeight = FontWeight.Black)
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
        }
        item {
            Surface(color = PANEL, shape = RoundedCornerShape(22.dp), modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                        Text("🎯 GÜNLÜK GÖREV", color = TXT, fontWeight = FontWeight.Black)
                        Text("$mission/5", color = GOLD, fontWeight = FontWeight.Black)
                    }
                    Spacer(Modifier.height(7.dp))
                    Text("5 bölüm tamamla ve 100 🪙 kazan", color = MUTED, fontSize = 11.sp)
                    Spacer(Modifier.height(9.dp))
                    LinearProgressIndicator(progress = { mission / 5f }, Modifier.fillMaxWidth().height(7.dp))
                    Spacer(Modifier.height(10.dp))
                    Button(reward, enabled = mission >= 5, Modifier.fillMaxWidth(), shape = RoundedCornerShape(13.dp)) {
                        Text(if (mission >= 5) "🎁 ÖDÜLÜ AL" else "GÖREVE DEVAM ET", fontWeight = FontWeight.Black)
                    }
                }
            }
        }
    }
}

@Composable private fun Pill(text: String, color: Color) {
    Surface(color = Color.White.copy(.06f), shape = RoundedCornerShape(15.dp)) {
        Text(text, Modifier.padding(horizontal = 11.dp, vertical = 8.dp), color = color, fontWeight = FontWeight.Black)
    }
}

@Composable private fun Stat(icon: String, value: String, label: String, modifier: Modifier) {
    Surface(color = PANEL, shape = RoundedCornerShape(17.dp), modifier = modifier.height(75.dp)) {
        Column(Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text(icon, fontSize = 17.sp)
            Text(value, color = TXT, fontWeight = FontWeight.Black)
            Text(label, color = MUTED, fontSize = 9.sp)
        }
    }
}

@Composable
private fun Worlds(selected: Int, back: () -> Unit, choose: (Int) -> Unit) {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Top("DÜNYALAR", "Her dünya yeni taşlar", back)
        Spacer(Modifier.height(12.dp))
        LazyVerticalGrid(GridCells.Fixed(2), Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 20.dp), verticalArrangement = Arrangement.spacedBy(12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(WORLDS) { world ->
                Surface(onClick = { choose(world.id) }, color = PANEL, shape = RoundedCornerShape(22.dp), modifier = Modifier.height(156.dp).border(1.dp, if (selected == world.id) ACC.copy(.7f) else Color.White.copy(.07f), RoundedCornerShape(22.dp))) {
                    Column(Modifier.fillMaxSize().padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                        Box(Modifier.size(60.dp).clip(RoundedCornerShape(19.dp)).background(Brush.linearGradient(world.colors.take(3))), Alignment.Center) { Text(world.icon, fontSize = 31.sp) }
                        Spacer(Modifier.height(8.dp))
                        Text(world.name, color = TXT, fontWeight = FontWeight.Black, fontSize = 13.sp)
                        Text("6 özel taş", color = MUTED, fontSize = 9.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun Levels(world: World, back: () -> Unit, go: (Int) -> Unit) {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Top(world.name.uppercase(), "Katmanları aç, yıldızları topla", back)
        Spacer(Modifier.height(12.dp))
        Surface(color = PANEL, shape = RoundedCornerShape(22.dp), modifier = Modifier.fillMaxWidth()) {
            Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(64.dp).clip(RoundedCornerShape(18.dp)).background(Brush.linearGradient(world.colors.take(3))), Alignment.Center) { Text(world.icon, fontSize = 34.sp) }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text("MACERA HARİTASI", color = TXT, fontWeight = FontWeight.Black)
                    Text("İlk 5 bölüm açık", color = MUTED, fontSize = 10.sp)
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        LazyVerticalGrid(GridCells.Fixed(3), Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 20.dp), verticalArrangement = Arrangement.spacedBy(12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items((1..24).toList()) { number ->
                val open = number <= 5
                Surface(onClick = { if (open) go(number) }, color = if (open) PANEL2 else PANEL, shape = RoundedCornerShape(18.dp), modifier = Modifier.height(92.dp)) {
                    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                        Text(if (open) "★" else "🔒", color = if (open) GOLD else MUTED, fontSize = 22.sp)
                        Text("Bölüm $number", color = if (open) TXT else MUTED, fontSize = 11.sp, fontWeight = FontWeight.Black)
                    }
                }
            }
        }
    }
}

@Composable private fun Top(title: String, subtitle: String, back: () -> Unit) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Button(back, shape = RoundedCornerShape(13.dp), contentPadding = PaddingValues(0.dp), modifier = Modifier.size(42.dp)) { Text("‹", fontSize = 27.sp) }
        Spacer(Modifier.width(9.dp))
        Column {
            Text(title, color = TXT, fontWeight = FontWeight.Black, fontSize = 18.sp)
            Text(subtitle, color = MUTED, fontSize = 10.sp)
        }
    }
}

private fun boardForLevel(level: Int): List<Tile> {
    val positions = mutableListOf<Triple<Float, Float, Int>>()
    // Layer 0: wide base — 20 stones.
    listOf(1, 2, 3, 4).forEach { x -> positions += Triple(x.toFloat(), 1f, 0) }
    listOf(0, 1, 2, 3, 4, 5).forEach { x -> positions += Triple(x.toFloat(), 2f, 0) }
    listOf(0, 1, 2, 3, 4, 5).forEach { x -> positions += Triple(x.toFloat(), 3f, 0) }
    listOf(1, 2, 3, 4).forEach { x -> positions += Triple(x.toFloat(), 4f, 0) }
    // Layer 1: 10 stones sitting over the base.
    listOf(1.2f, 2.2f, 3.2f, 4.2f).forEach { x -> positions += Triple(x, 1.65f, 1) }
    listOf(1.2f, 2.2f, 3.2f, 4.2f).forEach { x -> positions += Triple(x, 2.65f, 1) }
    listOf(1.2f, 3.2f).forEach { x -> positions += Triple(x, 3.65f, 1) }
    // Layer 2: six crown stones.
    listOf(1.7f, 2.7f, 3.7f).forEach { x -> positions += Triple(x, 2.1f, 2) }
    listOf(1.7f, 2.7f, 3.7f).forEach { x -> positions += Triple(x, 3.1f, 2) }

    // Rotate the type sequence by level so repeated play does not feel identical.
    val types = MutableList(36) { it / 6 }
    val shift = (level * 7) % types.size
    val rotated = types.drop(shift) + types.take(shift)
    return positions.mapIndexed { index, (x, y, layer) ->
        Tile(index, rotated[index], x, y, layer)
    }
}

private fun isBlocked(tile: Tile, board: List<Tile>): Boolean {
    if (tile.removed) return true
    return board.any { other ->
        !other.removed && other.layer > tile.layer && abs(other.x - tile.x) < .78f && abs(other.y - tile.y) < .78f
    }
}

@Composable
private fun Game(
    world: World,
    level: Int,
    coins: Int,
    back: () -> Unit,
    changeCoins: (Int) -> Unit,
    finish: (Int) -> Unit,
    next: () -> Unit
) {
    val view = LocalView.current
    val density = LocalDensity.current
    var board by remember(level, world.id) { mutableStateOf(boardForLevel(level)) }
    var tray by remember(level, world.id) { mutableStateOf(emptyList<Int>()) }
    var score by remember(level, world.id) { mutableIntStateOf(0) }
    var combo by remember(level, world.id) { mutableIntStateOf(0) }
    var gameOver by remember(level, world.id) { mutableStateOf(false) }
    var won by remember(level, world.id) { mutableStateOf(false) }
    var hintType by remember(level, world.id) { mutableIntStateOf(-1) }
    var message by remember(level, world.id) { mutableStateOf("") }
    var history by remember(level, world.id) { mutableStateOf(emptyList<Snap>()) }
    var burst by remember(level, world.id) { mutableStateOf(emptyList<Int>()) }

    fun snapshot() = Snap(board, tray, score, combo, coins)

    fun tapTile(tile: Tile) {
        if (won || gameOver || tile.removed || isBlocked(tile, board)) return
        history = (history + snapshot()).takeLast(12)
        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
        hintType = -1
        board = board.map { if (it.id == tile.id) it.copy(removed = true) else it }
        val newTray = tray + tile.type
        tray = newTray
        combo++

        val count = newTray.count { it == tile.type }
        if (count >= 3) {
            val positions = newTray.indices.filter { newTray[it] == tile.type }
            burst = positions
            tray = newTray.filterIndexed { index, type -> !(type == tile.type && positions.take(3).contains(index)) }
            val gained = 35 + combo * 15 + level * 2
            score += gained
            changeCoins(8 + combo)
            message = "💥 3'lü KIRILDI! +$gained"
            view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
        } else if (newTray.size >= 7) {
            gameOver = true
            combo = 0
        }

        if (board.all { it.removed }) {
            won = true
            finish(75 + level * 5)
        }
    }

    fun undo() {
        val last = history.lastOrNull() ?: return
        if (won || gameOver) return
        board = last.board
        tray = last.tray
        score = last.score
        combo = last.combo
        history = history.dropLast(1)
        message = "↩️ Son hamle geri alındı"
        hintType = -1
    }

    fun shuffle() {
        if (coins < 15 || won || gameOver) {
            message = if (coins < 15) "🪙 En az 15 jeton lazım" else "Şu an kullanılamaz"
            return
        }
        val remaining = board.filter { !it.removed }.map { it.type }.shuffled()
        var i = 0
        board = board.map { tile -> if (!tile.removed) tile.copy(type = remaining[i++]) else tile }
        changeCoins(-15)
        hintType = -1
        message = "🔀 Taşlar yeniden dağıtıldı"
    }

    fun hint() {
        if (coins < 5 || won || gameOver) {
            message = if (coins < 5) "🪙 En az 5 jeton lazım" else "Şu an ipucu yok"
            return
        }
        val accessible = board.filter { !it.removed && !isBlocked(it, board) }
        val best = accessible.groupBy { it.type }.maxByOrNull { it.value.size }?.key ?: -1
        hintType = best
        changeCoins(-5)
        message = if (best >= 0) "💡 Parlayan taşları birleştirmeyi dene" else "💡 Önce üst katmanı aç"
    }

    Column(Modifier.fillMaxSize().padding(horizontal = 14.dp, vertical = 10.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Button(back, shape = RoundedCornerShape(13.dp), contentPadding = PaddingValues(0.dp), modifier = Modifier.size(42.dp)) { Text("‹", fontSize = 26.sp) }
            Spacer(Modifier.width(8.dp))
            Column(Modifier.weight(1f)) {
                Text(world.name.uppercase(), color = TXT, fontSize = 15.sp, fontWeight = FontWeight.Black)
                Text("BÖLÜM $level • KATMAN ${board.maxOf { it.layer } + 1}", color = MUTED, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            }
            Pill("🪙 $coins", GOLD)
        }
        Spacer(Modifier.height(9.dp))

        Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(7.dp)) {
            GameStat("⭐", "$score", "SKOR", Modifier.weight(1f))
            GameStat("🔥", "x$combo", "COMBO", Modifier.weight(1f))
            GameStat("🧱", "${board.count { !it.removed }}", "KALAN", Modifier.weight(1f))
        }
        Spacer(Modifier.height(8.dp))

        BoxWithConstraints(Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
            val tileSize = (maxWidth / 6.45f).coerceAtMost(58.dp)
            val boardWidth = tileSize * 6.45f
            val boardHeight = tileSize * 5.15f
            val tilePx = with(density) { tileSize.toPx() }
            Box(
                Modifier.width(boardWidth).height(boardHeight).clip(RoundedCornerShape(28.dp))
                    .background(Brush.linearGradient(listOf(Color(0xFF0C1228), Color(0xFF151B38), Color(0xFF0A2030))))
                    .border(1.dp, ACC.copy(.22f), RoundedCornerShape(28.dp))
            ) {
                board.sortedBy { it.layer }.forEach { tile ->
                    if (!tile.removed) {
                        val blocked = isBlocked(tile, board)
                        val highlighted = hintType >= 0 && tile.type == hintType && !blocked
                        LayeredTile(
                            tile = tile,
                            world = world,
                            tileSize = tileSize,
                            tilePx = tilePx,
                            blocked = blocked,
                            highlighted = highlighted,
                            onClick = { tapTile(tile) }
                        )
                    }
                }
            }
        }

        AnimatedVisibility(message.isNotEmpty(), enter = fadeIn(), exit = fadeOut()) {
            Text(message, Modifier.fillMaxWidth().padding(bottom = 5.dp), color = if (message.contains("KIRILDI")) GOLD else TXT, fontSize = 11.sp, fontWeight = FontWeight.Black, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        }

        Tray(tray, world)
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(7.dp)) {
            Action("↩", "GERİ AL", history.isNotEmpty() && !gameOver && !won, Modifier.weight(1f), ::undo)
            Action("🔀", "KARIŞTIR", coins >= 15 && !gameOver && !won, Modifier.weight(1f), ::shuffle)
            Action("💡", "İPUCU", coins >= 5 && !gameOver && !won, Modifier.weight(1f), ::hint)
        }
    }

    if (burst.isNotEmpty()) {
        LaunchedEffect(burst) {
            delay(360)
            burst = emptyList()
        }
    }

    AnimatedVisibility(won, enter = fadeIn() + scaleIn(), exit = fadeOut() + scaleOut()) {
        ResultOverlay(
            title = "BÖLÜM TAMAMLANDI! 🎉",
            subtitle = "Katmanları temizledin",
            score = score,
            reward = 75 + level * 5,
            primary = "SONRAKİ BÖLÜM →",
            primaryAction = { won = false; next() },
            secondary = "BÖLÜMLERE DÖN",
            secondaryAction = back
        )
    }

    AnimatedVisibility(gameOver, enter = fadeIn() + scaleIn(), exit = fadeOut() + scaleOut()) {
        ResultOverlay(
            title = "TEPSİ DOLDU! 😵",
            subtitle = "7 yuva dolmadan üçlüleri yakala.",
            score = score,
            reward = 0,
            primary = "TEKRAR DENE",
            primaryAction = {
                board = boardForLevel(level)
                tray = emptyList()
                score = 0
                combo = 0
                gameOver = false
                hintType = -1
                history = emptyList()
                message = ""
            },
            secondary = "BÖLÜMLERE DÖN",
            secondaryAction = back
        )
    }
}

@Composable
private fun LayeredTile(
    tile: Tile,
    world: World,
    tileSize: androidx.compose.ui.unit.Dp,
    tilePx: Float,
    blocked: Boolean,
    highlighted: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(if (highlighted) 1.06f else 1f, spring(stiffness = Spring.StiffnessMedium), label = "tile-scale")
    val alpha = if (blocked) .56f else 1f
    val shadow = tile.layer * 5 + 3
    val x = (tile.x * tilePx).roundToInt()
    val y = (tile.y * tilePx).roundToInt()
    val bg = Brush.linearGradient(listOf(world.colors[tile.type].copy(alpha = .95f), world.colors[tile.type].copy(alpha = .52f)))

    Box(
        Modifier.offset { IntOffset(x, y) }
            .size(tileSize)
            .zIndex(tile.layer * 10f + tile.id / 1000f)
            .scale(scale)
            .graphicsLayerCompat(alpha = alpha)
            .clip(RoundedCornerShape(16.dp))
            .background(bg)
            .border(if (highlighted) 3.dp else 1.dp, if (highlighted) GOLD else Color.White.copy(.24f), RoundedCornerShape(16.dp))
            .clickable(enabled = !blocked, onClick = onClick)
    ) {
        Box(Modifier.fillMaxSize().padding(5.dp).clip(RoundedCornerShape(12.dp)).background(Color.Black.copy(.10f)), Alignment.Center) {
            Text(world.tiles[tile.type], fontSize = (tileSize.value * .42f).sp)
        }
        if (tile.layer > 0) {
            Text("L${tile.layer + 1}", Modifier.align(Alignment.TopEnd).padding(4.dp), color = Color.White.copy(.72f), fontSize = 7.sp, fontWeight = FontWeight.Black)
        }
        if (blocked) {
            Box(Modifier.align(Alignment.BottomCenter).fillMaxWidth().height(5.dp).background(Color.Black.copy(.18f)))
        }
    }
}

private fun Modifier.graphicsLayerCompat(alpha: Float): Modifier = this.graphicsLayer(alpha = alpha)

@Composable
private fun Tray(tray: List<Int>, world: World) {
    Surface(color = PANEL, shape = RoundedCornerShape(19.dp), modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(8.dp)) {
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                Text("🧺 TEPSİ", color = TXT, fontSize = 10.sp, fontWeight = FontWeight.Black)
                Text("${tray.size}/7", color = if (tray.size >= 6) RED else MUTED, fontSize = 10.sp, fontWeight = FontWeight.Black)
            }
            Spacer(Modifier.height(6.dp))
            Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(5.dp)) {
                repeat(7) { index ->
                    val type = tray.getOrNull(index)
                    Box(Modifier.weight(1f).aspectRatio(1f).clip(RoundedCornerShape(10.dp)).background(if (type == null) Color.White.copy(.035f) else world.colors[type].copy(.28f)).border(1.dp, if (type == null) Color.White.copy(.06f) else world.colors[type].copy(.7f), RoundedCornerShape(10.dp)), Alignment.Center) {
                        if (type != null) Text(world.tiles[type], fontSize = 17.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun GameStat(icon: String, value: String, label: String, modifier: Modifier) {
    Surface(color = PANEL, shape = RoundedCornerShape(13.dp), modifier = modifier.height(48.dp)) {
        Row(Modifier.fillMaxSize().padding(horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
            Text(icon, fontSize = 14.sp)
            Spacer(Modifier.width(5.dp))
            Column {
                Text(value, color = TXT, fontSize = 12.sp, fontWeight = FontWeight.Black)
                Text(label, color = MUTED, fontSize = 7.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun Action(icon: String, text: String, enabled: Boolean, modifier: Modifier, click: () -> Unit) {
    Surface(onClick = click, enabled = enabled, color = if (enabled) PANEL2 else PANEL, shape = RoundedCornerShape(14.dp), modifier = modifier.height(48.dp)) {
        Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text(icon, fontSize = 15.sp)
            Text(text, color = if (enabled) TXT else MUTED, fontSize = 8.sp, fontWeight = FontWeight.Black)
        }
    }
}

@Composable
private fun ResultOverlay(
    title: String,
    subtitle: String,
    score: Int,
    reward: Int,
    primary: String,
    primaryAction: () -> Unit,
    secondary: String,
    secondaryAction: () -> Unit
) {
    Box(Modifier.fillMaxSize().background(Color.Black.copy(.72f)), Alignment.Center) {
        Surface(color = PANEL, shape = RoundedCornerShape(28.dp), modifier = Modifier.fillMaxWidth().padding(25.dp)) {
            Column(Modifier.padding(22.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(title, color = TXT, fontSize = 21.sp, fontWeight = FontWeight.Black)
                Spacer(Modifier.height(7.dp))
                Text(subtitle, color = MUTED, fontSize = 11.sp)
                Spacer(Modifier.height(17.dp))
                Text("⭐ $score", color = GOLD, fontSize = 30.sp, fontWeight = FontWeight.Black)
                if (reward > 0) {
                    Spacer(Modifier.height(4.dp))
                    Text("🪙 +$reward", color = GREEN, fontSize = 15.sp, fontWeight = FontWeight.Black)
                }
                Spacer(Modifier.height(18.dp))
                Button(primaryAction, Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(15.dp), colors = ButtonDefaults.buttonColors(containerColor = ACC)) {
                    Text(primary, fontWeight = FontWeight.Black)
                }
                Spacer(Modifier.height(8.dp))
                OutlinedButton(secondaryAction, Modifier.fillMaxWidth().height(48.dp), shape = RoundedCornerShape(15.dp)) {
                    Text(secondary, fontWeight = FontWeight.Black)
                }
            }
        }
    }
}
