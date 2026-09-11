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
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs
import kotlin.random.Random

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

private data class World(val id: Int, val name: String, val icon: String, val tiles: List<String>, val colors: List<Color>)
private data class Tile(val id: Int, val type: Int, val x: Float, val y: Float, val layer: Int, val special: Special = Special.NONE)
private enum class Special { NONE, ICE, BOMB, JOKER, LOCKED, CHAIN }
private data class Snap(val board: List<Tile>, val tray: List<Int>, val score: Int, val combo: Int, val coins: Int)

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
    var streak by rememberSaveable { mutableIntStateOf(prefs.getInt("streak", 0)) }
    var mission by rememberSaveable { mutableIntStateOf(prefs.getInt("mission", 0)) }
    var claimed by rememberSaveable { mutableStateOf(prefs.getString("dailyDate", "") == today()) }
    var unlocked by rememberSaveable { mutableIntStateOf(prefs.getInt("unlocked", 5)) }
    var achievements by rememberSaveable { mutableIntStateOf(prefs.getInt("achievements", 0)) }
    var collection by rememberSaveable { mutableIntStateOf(prefs.getInt("collection", 0)) }

    fun save() {
        prefs.edit().putInt("coins", coins).putInt("games", games).putInt("streak", streak)
            .putInt("mission", mission).putString("dailyDate", if (claimed) today() else "")
            .putInt("unlocked", unlocked).putInt("achievements", achievements).putInt("collection", collection).apply()
    }

    MaterialTheme(colorScheme = darkColorScheme()) {
        Surface(Modifier.fillMaxSize(), color = BG) {
            when (page) {
                "home" -> Home(coins, streak, games, mission, claimed, collection, { page = "worlds" }, {
                    if (!claimed) { coins += 50 + streak.coerceAtMost(6) * 15; streak++; claimed = true; save() }
                }, { if (mission >= 5) { coins += 100; mission = 0; save() } }, { page = "achievements" })
                "worlds" -> Worlds { page = "home" } { worldId = it; page = "levels" }
                "levels" -> Levels(WORLDS[worldId], unlocked, { page = "worlds" }) { level = it; page = "game" }
                "achievements" -> Achievements(games, achievements, collection) { page = "home" }
                else -> Game(WORLDS[worldId], level, coins, back = { page = "levels" }, changeCoins = { coins += it; save() }, finish = { earned, stars, found ->
                    coins += earned; games++; mission = (mission + 1).coerceAtMost(5); collection += found; achievements = maxOf(achievements, stars); if (level >= unlocked && unlocked < 60) unlocked++; save()
                }, next = { level = (level + 1).coerceAtMost(unlocked.coerceAtLeast(1)) })
            }
        }
    }
}

private fun today(): String = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

@Composable private fun Home(coins: Int, streak: Int, games: Int, mission: Int, claimed: Boolean, collection: Int, play: () -> Unit, daily: () -> Unit, reward: () -> Unit, achievements: () -> Unit) {
    val pulse by rememberInfiniteTransition(label = "logo").animateFloat(.97f, 1.04f, infiniteRepeatable(tween(1000), RepeatMode.Reverse), label = "pulse")
    LazyColumn(Modifier.fillMaxSize().padding(16.dp), contentPadding = PaddingValues(bottom = 24.dp)) {
        item {
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) { Column { Text("HOŞ GELDİN 👋", color = MUTED, fontSize = 11.sp, fontWeight = FontWeight.Bold); Text("TRIPLIX", color = TXT, fontSize = 28.sp, fontWeight = FontWeight.Black) }; Pill("🪙 $coins", GOLD) }
            Spacer(Modifier.height(14.dp))
            Box(Modifier.fillMaxWidth().height(220.dp).clip(RoundedCornerShape(30.dp)).background(Brush.linearGradient(listOf(Color(0xFF202B61), Color(0xFF38205F), Color(0xFF0D3049)))).border(1.dp, ACC.copy(.5f), RoundedCornerShape(30.dp)), Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(Modifier.size(78.dp).scale(pulse).clip(RoundedCornerShape(24.dp)).background(Brush.linearGradient(listOf(Color(0xFFFFB93E), Color(0xFFFF6D55)))), Alignment.Center) { Text("3", color = Color.White, fontSize = 42.sp, fontWeight = FontWeight.Black) }
                    Text("TRIPLIX", color = TXT, fontSize = 36.sp, fontWeight = FontWeight.Black)
                    Text("AYNI 3'Ü BİRLEŞTİR • KATMANLARI AÇ", color = Color.White.copy(.78f), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(9.dp)); Text("💥 COMBO • ⭐ YILDIZ • 🧊 ÖZEL TAŞLAR", color = GOLD, fontSize = 9.sp, fontWeight = FontWeight.Black)
                }
            }
            Spacer(Modifier.height(12.dp))
            Button(play, Modifier.fillMaxWidth().height(57.dp), shape = RoundedCornerShape(18.dp), colors = ButtonDefaults.buttonColors(containerColor = ACC)) { Text("🌎 DÜNYANI SEÇ VE OYNA", fontSize = 16.sp, fontWeight = FontWeight.Black) }
            Spacer(Modifier.height(12.dp))
        }
        item { Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(8.dp)) { Stat("🔥", "$streak", "Seri", Modifier.weight(1f)); Stat("⭐", "$games", "Oyun", Modifier.weight(1f)); Stat("🧩", "$collection", "Koleksiyon", Modifier.weight(1f)) }; Spacer(Modifier.height(12.dp)) }
        item {
            Surface(color = PANEL, shape = RoundedCornerShape(22.dp), modifier = Modifier.fillMaxWidth()) { Column(Modifier.padding(16.dp)) {
                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) { Column { Text("🎁 GÜNLÜK ÖDÜL", color = TXT, fontWeight = FontWeight.Black); Text("Serini koru, her gün daha büyük ödül!", color = MUTED, fontSize = 11.sp) }; Button(daily, enabled = !claimed, shape = RoundedCornerShape(13.dp), contentPadding = PaddingValues(horizontal = 12.dp, vertical = 7.dp)) { Text(if (claimed) "ALINDI ✓" else "+ ÖDÜL", fontSize = 11.sp, fontWeight = FontWeight.Black) } }
                Spacer(Modifier.height(11.dp)); Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(6.dp)) { (1..7).forEach { d -> Box(Modifier.size(34.dp).clip(RoundedCornerShape(10.dp)).background(if (d <= streak) ACC.copy(.28f) else Color.White.copy(.04f)).border(1.dp, if (d <= streak) ACC else Color.White.copy(.07f), RoundedCornerShape(10.dp)), Alignment.Center) { Text(if (d == 7) "🎁" else "$d", color = if (d <= streak) GOLD else MUTED, fontSize = 10.sp, fontWeight = FontWeight.Black) } } }
            } }; Spacer(Modifier.height(12.dp))
        }
        item {
            Surface(color = PANEL, shape = RoundedCornerShape(22.dp), modifier = Modifier.fillMaxWidth()) { Column(Modifier.padding(16.dp)) {
                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) { Text("🎯 GÜNLÜK GÖREV", color = TXT, fontWeight = FontWeight.Black); Text("$mission/5", color = GOLD, fontWeight = FontWeight.Black) }
                Spacer(Modifier.height(7.dp)); Text("5 bölüm tamamla ve 100 🪙 kazan", color = MUTED, fontSize = 11.sp); Spacer(Modifier.height(9.dp)); LinearProgressIndicator(progress = { mission / 5f }, modifier = Modifier.fillMaxWidth().height(7.dp)); Spacer(Modifier.height(10.dp)); Button(reward, enabled = mission >= 5, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(13.dp)) { Text(if (mission >= 5) "🎁 ÖDÜLÜ AL" else "GÖREVE DEVAM ET", fontWeight = FontWeight.Black) }
            } }; Spacer(Modifier.height(10.dp))
        }
        item { OutlinedButton(achievements, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) { Text("🏆 BAŞARIMLAR & KOLEKSİYON", fontWeight = FontWeight.Black) } }
    }
}

@Composable private fun Pill(text: String, color: Color) { Surface(color = Color.White.copy(.06f), shape = RoundedCornerShape(15.dp)) { Text(text, Modifier.padding(horizontal = 11.dp, vertical = 8.dp), color = color, fontWeight = FontWeight.Black) } }
@Composable private fun Stat(icon: String, value: String, label: String, modifier: Modifier) { Surface(color = PANEL, shape = RoundedCornerShape(17.dp), modifier = modifier.height(75.dp)) { Column(Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) { Text(icon, fontSize = 17.sp); Text(value, color = TXT, fontWeight = FontWeight.Black); Text(label, color = MUTED, fontSize = 9.sp) } } }

@Composable private fun Worlds(back: () -> Unit, choose: (Int) -> Unit) {
    Column(Modifier.fillMaxSize().padding(16.dp)) { Top("DÜNYALAR", "Her dünya yeni taşlar", back); Spacer(Modifier.height(12.dp)); LazyVerticalGrid(GridCells.Fixed(2), Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 20.dp), verticalArrangement = Arrangement.spacedBy(12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) { items(WORLDS) { w -> Surface(onClick = { choose(w.id) }, color = PANEL, shape = RoundedCornerShape(22.dp), modifier = Modifier.height(150.dp)) { Column(Modifier.padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) { Text(w.icon, fontSize = 35.sp); Text(w.name, color = TXT, fontWeight = FontWeight.Black, fontSize = 13.sp); Text("24+ bölüm", color = MUTED, fontSize = 10.sp); Spacer(Modifier.height(7.dp)); Text(w.tiles.take(5).joinToString(" "), fontSize = 17.sp) } } } } }
}

@Composable private fun Levels(world: World, unlocked: Int, back: () -> Unit, choose: (Int) -> Unit) {
    Column(Modifier.fillMaxSize().padding(16.dp)) { Top("${world.icon} ${world.name.uppercase()}", "Katmanları aç ve ilerle", back); Spacer(Modifier.height(10.dp)); LazyVerticalGrid(GridCells.Fixed(4), Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 20.dp), verticalArrangement = Arrangement.spacedBy(10.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) { items((1..60).toList()) { n -> val open = n <= unlocked; Surface(onClick = { if (open) choose(n) }, color = if (open) PANEL else Color.White.copy(.025f), shape = RoundedCornerShape(15.dp), modifier = Modifier.aspectRatio(1f)) { Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) { Text(if (open) "$n" else "🔒", color = if (open) TXT else MUTED, fontWeight = FontWeight.Black, fontSize = 17.sp); if (open) Text("${"⭐".repeat(if (n % 10 == 0) 3 else if (n % 3 == 0) 2 else 1)}", fontSize = 9.sp) } } } } }
}

@Composable private fun Achievements(games: Int, achievements: Int, collection: Int, back: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(16.dp)) { Top("🏆 BAŞARIMLAR", "Her hamle koleksiyonuna değer katar", back); Spacer(Modifier.height(12.dp)); Achievement("🎮 İlk Adım", "1 oyun tamamla", games >= 1, "1/1"); Achievement("🔥 Usta Oyuncu", "10 oyun tamamla", games >= 10, "$games/10"); Achievement("💥 Combo Canavarı", "5 yıldızlı bölüm kazan", achievements >= 3, "$achievements/3"); Achievement("🧩 Koleksiyoncu", "20 özel taş topla", collection >= 20, "$collection/20"); Spacer(Modifier.height(12.dp)); Surface(color = PANEL, shape = RoundedCornerShape(20.dp)) { Column(Modifier.padding(16.dp)) { Text("🎁 KOLEKSİYON", color = TXT, fontWeight = FontWeight.Black); Spacer(Modifier.height(8.dp)); Text("Toplanan özel taşlar: $collection", color = MUTED); Spacer(Modifier.height(9.dp)); LinearProgressIndicator(progress = { (collection / 20f).coerceIn(0f, 1f) }, modifier = Modifier.fillMaxWidth()) } } }
}

@Composable private fun Achievement(title: String, desc: String, done: Boolean, progress: String) { Surface(color = if (done) ACC.copy(.16f) else PANEL, shape = RoundedCornerShape(18.dp), modifier = Modifier.fillMaxWidth().padding(bottom = 9.dp)) { Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) { Text(if (done) "✅" else "🔒", fontSize = 23.sp); Spacer(Modifier.width(12.dp)); Column(Modifier.weight(1f)) { Text(title, color = TXT, fontWeight = FontWeight.Black); Text(desc, color = MUTED, fontSize = 11.sp) }; Text(progress, color = if (done) GREEN else GOLD, fontWeight = FontWeight.Black, fontSize = 11.sp) } } }

private fun boardForLevel(level: Int): List<Tile> {
    val coords = listOf(0 to 0, 1 to 0, 2 to 0, 3 to 0, 4 to 0, 5 to 0, 0 to 1, 1 to 1, 2 to 1, 3 to 1, 4 to 1, 5 to 1, 0 to 2, 1 to 2, 2 to 2, 3 to 2, 4 to 2, 5 to 2, 0 to 3, 1 to 3, 2 to 3, 3 to 3, 4 to 3, 5 to 3, 0 to 4, 1 to 4, 2 to 4, 3 to 4, 4 to 4, 5 to 4, 0 to 5, 1 to 5, 2 to 5, 3 to 5, 4 to 5, 5 to 5)
    val layers = when { level < 8 -> 1; level < 20 -> 2; level < 35 -> 3; level < 50 -> 4; else -> 5 }
    val count = when (layers) { 1 -> 24; 2 -> 30; 3 -> 34; 4 -> 36; else -> 36 }
    val result = mutableListOf<Tile>()
    for (i in 0 until count) {
        val p = coords[i]
        val layer = when { i < 18 -> 0; i < 28 -> 1; i < 33 -> 2; i < 35 -> 3; else -> 4 }.coerceAtMost(layers - 1)
        val special = when {
            level >= 8 && i % 17 == 0 -> Special.ICE
            level >= 15 && i % 19 == 0 -> Special.BOMB
            level >= 22 && i % 23 == 0 -> Special.JOKER
            level >= 30 && i % 13 == 0 -> Special.LOCKED
            level >= 40 && i % 11 == 0 -> Special.CHAIN
            else -> Special.NONE
        }
        result += Tile(i, (i + level) % 6, p.first.toFloat(), p.second.toFloat(), layer, special)
    }
    return result.shuffled(Random(level * 91L))
}

private fun isBlocked(tile: Tile, board: List<Tile>): Boolean = board.any { other -> other.id != tile.id && other.layer > tile.layer && abs(other.x - tile.x) < .9f && abs(other.y - tile.y) < .9f }

@Composable private fun Game(world: World, level: Int, coins: Int, back: () -> Unit, changeCoins: (Int) -> Unit, finish: (Int, Int, Int) -> Unit, next: () -> Unit) {
    val view = LocalView.current
    var board by remember(level, world.id) { mutableStateOf(boardForLevel(level)) }
    var tray by remember(level, world.id) { mutableStateOf(listOf<Int>()) }
    var score by remember(level, world.id) { mutableIntStateOf(0) }
    var combo by remember(level, world.id) { mutableIntStateOf(0) }
    var maxCombo by remember(level, world.id) { mutableIntStateOf(0) }
    var stars by remember(level, world.id) { mutableIntStateOf(0) }
    var over by remember(level, world.id) { mutableStateOf(false) }
    var won by remember(level, world.id) { mutableStateOf(false) }
    var message by remember(level, world.id) { mutableStateOf("") }
    var hint by remember { mutableStateOf(-1) }
    var history by remember { mutableStateOf(listOf<Snap>()) }
    var burst by remember { mutableStateOf(false) }
    var boosterInfo by remember { mutableStateOf("") }
    var localCoins by remember(level, world.id) { mutableIntStateOf(coins) }

    fun pushSnap() { history = (history + Snap(board, tray, score, combo, localCoins)).takeLast(8) }
    fun evaluate() {
        if (board.isEmpty()) { stars = when { score >= 900 -> 3; score >= 600 -> 2; else -> 1 }; won = true; finish(75 + level * 6 + stars * 20, stars, if (maxCombo >= 4) 1 else 0); return }
        if (tray.size >= 7) { over = true; return }
    }
    fun tap(tile: Tile) {
        if (over || won || isBlocked(tile, board) || tile.special == Special.LOCKED || tile.special == Special.CHAIN) return
        pushSnap()
        board = board.filterNot { it.id == tile.id }
        var nextTray = tray + tile.type
        var nextCombo = combo
        val count = nextTray.count { it == tile.type }
        if (tile.special == Special.BOMB) { board = board.filterNot { abs(it.x - tile.x) <= 1f && abs(it.y - tile.y) <= 1f }; message = "💣 BOMBA!" }
        if (tile.special == Special.ICE) { nextTray = nextTray.filter { it != tile.type }; message = "🧊 BUZ ÇÖZÜLDÜ!" }
        if (tile.special == Special.JOKER) { val target = nextTray.groupingBy { it }.eachCount().maxByOrNull { it.value }?.key ?: tile.type; nextTray = nextTray + target + target; message = "🃏 JOKER COMBO!" }
        if (count >= 3) {
            nextTray = removeThree(nextTray, tile.type); nextCombo = combo + 1; combo = nextCombo; maxCombo = maxOf(maxCombo, nextCombo); val points = 45 + nextCombo * 25 + level * 3; score += points; localCoins += 6 + nextCombo; message = if (nextCombo >= 5) "🔥 MEGA COMBO x$nextCombo! +$points" else "✨ COMBO x$nextCombo! +$points"; burst = true; view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
        } else if (nextTray.size > 0) { combo = 0 }
        tray = nextTray
        if (burst) { /* animation state is consumed by overlay below */ }
        evaluate()
    }

    Column(Modifier.fillMaxSize().padding(horizontal = 12.dp, vertical = 8.dp)) {
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) { Column { Text("${world.icon} ${world.name}", color = TXT, fontWeight = FontWeight.Black); Text("Bölüm $level  •  ${board.size} taş", color = MUTED, fontSize = 10.sp) }; Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) { Pill("⭐ $score", GOLD); Pill("🪙 $localCoins", GOLD) } }
        Spacer(Modifier.height(7.dp))
        Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(7.dp)) { Info("🔥", if (maxCombo > 0) "x$maxCombo" else "—", "EN İYİ"); Info("⭐", if (stars > 0) "$stars/3" else "—", "YILDIZ"); Info("📦", "${board.size}", "KALAN") }
        Spacer(Modifier.height(7.dp))
        Box(Modifier.fillMaxWidth().weight(1f).clip(RoundedCornerShape(24.dp)).background(Brush.radialGradient(listOf(PANEL2, BG))), Alignment.Center) {
            board.forEach { tile -> LayeredTile(tile, world, isBlocked(tile, board), tile.type == hint, ::tap) }
            AnimatedVisibility(visible = burst, enter = scaleIn() + fadeIn(), exit = scaleOut() + fadeOut()) { Text("💥", fontSize = 58.sp) }
            if (message.isNotEmpty()) Text(message, Modifier.align(Alignment.BottomCenter).padding(bottom = 8.dp), color = GOLD, fontWeight = FontWeight.Black, fontSize = 13.sp)
        }
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(7.dp)) { TraySlot(tray, 0); TraySlot(tray, 1); TraySlot(tray, 2); TraySlot(tray, 3); TraySlot(tray, 4); TraySlot(tray, 5); TraySlot(tray, 6) }
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(7.dp)) {
            Action("↩️", "Geri", enabled = history.isNotEmpty() && !over && !won) { val s = history.last(); history = history.dropLast(1); board = s.board; tray = s.tray; score = s.score; combo = s.combo; localCoins = s.coins }
            Action("🔀", "Karıştır", enabled = board.isNotEmpty() && localCoins >= 15 && !over && !won) { localCoins -= 15; val types = board.map { it.type }.shuffled(); board = board.mapIndexed { i, t -> t.copy(type = types[i]) }; message = "🔀 Taşlar karıştı" }
            Action("💡", "İpucu", enabled = board.isNotEmpty() && localCoins >= 5 && !over && !won) { localCoins -= 5; val counts = board.filterNot { isBlocked(it, board) }.groupingBy { it.type }.eachCount(); hint = counts.maxByOrNull { it.value }?.key ?: -1; message = "💡 Parlayan taşı aç" }
            Action("💣", "Bomba", enabled = localCoins >= 25 && !over && !won) { localCoins -= 25; val t = board.filterNot { isBlocked(it, board) }.firstOrNull(); if (t != null) { board = board.filterNot { abs(it.x - t.x) <= 1f && abs(it.y - t.y) <= 1f }; message = "💣 Alan temizlendi"; evaluate() } }
        }
        Spacer(Modifier.height(7.dp)); Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text("7 yuvalık tepsi • 3 aynı taş = PARÇALA", color = MUTED, fontSize = 9.sp); Text("🪙 $localCoins", color = GOLD, fontSize = 10.sp, fontWeight = FontWeight.Black) }
    }
    LaunchedEffect(burst) { if (burst) { delay(420); burst = false; hint = -1 } }
    LaunchedEffect(message) { if (message.isNotEmpty()) { delay(1700); message = "" } }
    if (over || won) {
        AlertDialog(onDismissRequest = {}, title = { Text(if (won) "🎉 BÖLÜM TAMAMLANDI" else "💥 TEPSİ DOLDU") }, text = { Column { if (won) { Text("Skor: $score"); Text("Yıldız: ${"⭐".repeat(stars)}"); Text("En iyi combo: x$maxCombo"); Text("Ödül hesabına eklendi.") } else { Text("Bu kez taşlar seni yendi. Bir daha dene!"); Text("İpucu ve bomba için jeton kullanabilirsin.") }; if (boosterInfo.isNotEmpty()) Text(boosterInfo) } }, confirmButton = { Button({ if (won) { next(); } else { board = boardForLevel(level); tray = emptyList(); score = 0; combo = 0; maxCombo = 0; over = false; won = false } }) { Text(if (won) "SONRAKİ BÖLÜM 🚀" else "TEKRAR OYNA") } }, dismissButton = { TextButton(back) { Text("BÖLÜMLER") } })
    }
}

private fun removeThree(list: List<Int>, type: Int): List<Int> { var removed = 0; return list.filter { if (it == type && removed < 3) { removed++; false } else true } }

@Composable private fun LayeredTile(tile: Tile, world: World, blocked: Boolean, highlighted: Boolean, tap: (Tile) -> Unit) {
    val scale by animateFloatAsState(if (highlighted) 1.08f else 1f, label = "tile")
    val emoji = when (tile.special) { Special.ICE -> "🧊${world.tiles[tile.type]}"; Special.BOMB -> "💣"; Special.JOKER -> "🃏"; Special.LOCKED -> "🔒"; Special.CHAIN -> "⛓️"; Special.NONE -> world.tiles[tile.type] }
    val size = 52.dp
    Box(Modifier.offset(x = (tile.x * 50).dp, y = (tile.y * 50).dp).zIndex(tile.layer.toFloat()).size(size).scale(scale).clip(RoundedCornerShape(15.dp)).background(if (blocked) Color(0xFF252A3D) else world.colors[tile.type].copy(.25f)).border(2.dp, if (highlighted) GOLD else world.colors[tile.type].copy(if (blocked) .18f else .7f), RoundedCornerShape(15.dp)).clickable(enabled = !blocked) { tap(tile) }, Alignment.Center) { Column(horizontalAlignment = Alignment.CenterHorizontally) { Text(emoji, fontSize = if (tile.special == Special.NONE) 24.sp else 20.sp); if (tile.layer > 0) Text("L${tile.layer + 1}", color = Color.White.copy(.45f), fontSize = 7.sp) } }
}

@Composable private fun RowScope.TraySlot(tray: List<Int>, index: Int) { Box(Modifier.weight(1f).height(46.dp).clip(RoundedCornerShape(12.dp)).background(if (index < tray.size) ACC.copy(.2f) else Color.White.copy(.035f)).border(1.dp, Color.White.copy(.07f), RoundedCornerShape(12.dp)), Alignment.Center) { if (index < tray.size) Text("${WORLDS[0].tiles.getOrElse(tray[index]) { "❔" }}", fontSize = 22.sp) else Text("·", color = MUTED) } }
@Composable private fun Info(icon: String, value: String, label: String) { Surface(color = PANEL, shape = RoundedCornerShape(12.dp), modifier = Modifier.weight(1f)) { Column(Modifier.padding(7.dp), horizontalAlignment = Alignment.CenterHorizontally) { Text(icon, fontSize = 12.sp); Text(value, color = TXT, fontWeight = FontWeight.Black, fontSize = 11.sp); Text(label, color = MUTED, fontSize = 7.sp) } } }
@Composable private fun RowScope.Action(icon: String, label: String, enabled: Boolean, onClick: () -> Unit) { Surface(onClick = onClick, enabled = enabled, color = if (enabled) PANEL else Color.White.copy(.025f), shape = RoundedCornerShape(13.dp), modifier = Modifier.weight(1f).height(55.dp)) { Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) { Text(icon, fontSize = 17.sp); Text(label, color = if (enabled) TXT else MUTED, fontSize = 8.sp, fontWeight = FontWeight.Bold) } } }

@Composable private fun Top(title: String, sub: String, back: () -> Unit) { Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(10.dp), Alignment.CenterVertically) { TextButton(back) { Text("‹", fontSize = 30.sp, color = TXT) }; Column { Text(title, color = TXT, fontWeight = FontWeight.Black, fontSize = 18.sp); Text(sub, color = MUTED, fontSize = 10.sp) } } }
