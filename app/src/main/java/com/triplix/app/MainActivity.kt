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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
private val Gold = Color(0xFFFFD45B)

private data class World(
    val id: Int,
    val name: String,
    val subtitle: String,
    val icon: String,
    val tiles: List<String>,
    val colors: List<Color>
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

@Composable
fun TriplixApp() {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("triplix", Context.MODE_PRIVATE) }
    var screen by rememberSaveable { mutableStateOf("home") }
    var worldId by rememberSaveable { mutableIntStateOf(0) }
    var level by rememberSaveable { mutableIntStateOf(1) }
    var coins by rememberSaveable { mutableIntStateOf(prefs.getInt("coins", 395)) }
    var streak by rememberSaveable { mutableIntStateOf(prefs.getInt("streak", 1)) }
    var games by rememberSaveable { mutableIntStateOf(prefs.getInt("games", 0)) }
    var dailyClaimed by rememberSaveable { mutableStateOf(prefs.getBoolean("daily_claimed", false)) }
    var missionProgress by rememberSaveable { mutableIntStateOf(prefs.getInt("mission", 0)) }
    val world = Worlds[worldId]

    fun save() {
        prefs.edit().putInt("coins", coins).putInt("streak", streak).putInt("games", games).putBoolean("daily_claimed", dailyClaimed).putInt("mission", missionProgress).apply()
    }

    MaterialTheme(colorScheme = darkColorScheme()) {
        Surface(Modifier.fillMaxSize(), color = Bg) {
            when (screen) {
                "home" -> HomeScreen(coins, streak, games, missionProgress, dailyClaimed, onPlay = { screen = "worlds" }, onDaily = {
                    if (!dailyClaimed) { coins += 50 + streak * 10; streak += 1; dailyClaimed = true; save() }
                }, onMission = {
                    if (missionProgress >= 5) { coins += 100; missionProgress = 0; save() }
                })
                "worlds" -> WorldScreen(Worlds, worldId, { screen = "home" }) { id -> worldId = id; screen = "levels" }
                "levels" -> LevelScreen(world, { screen = "worlds" }) { n -> level = n; screen = "game" }
                else -> GameScreen(world, level, coins, { screen = "levels" }, { earned -> coins += earned; games += 1; missionProgress = (missionProgress + 1).coerceAtMost(5); save() }, { level = (level + 1).coerceAtMost(50) })
            }
        }
    }
}

@Composable
private fun HomeScreen(coins: Int, streak: Int, games: Int, mission: Int, dailyClaimed: Boolean, onPlay: () -> Unit, onDaily: () -> Unit, onMission: () -> Unit) {
    val infinite = rememberInfiniteTransition(label = "home")
    val pulse by infinite.animateFloat(.97f, 1.04f, infiniteRepeatable(tween(1100), RepeatMode.Reverse), label = "pulse")
    LazyColumn(Modifier.fillMaxSize().padding(16.dp), contentPadding = PaddingValues(bottom = 24.dp)) {
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column { Text("HOŞ GELDİN 👋", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold); Text("TRIPLIX", color = TextMain, fontSize = 28.sp, fontWeight = FontWeight.Black) }
                Surface(color = Color.White.copy(.06f), shape = RoundedCornerShape(15.dp)) { Text("🪙 $coins", Modifier.padding(horizontal = 11.dp, vertical = 8.dp), color = Gold, fontWeight = FontWeight.Black) }
            }
            Spacer(Modifier.height(14.dp))
            Box(Modifier.fillMaxWidth().height(205.dp).clip(RoundedCornerShape(28.dp)).background(Brush.linearGradient(listOf(Color(0xFF202A5B), Color(0xFF351D61), Color(0xFF102D4C)))).border(1.dp, Accent.copy(.35f), RoundedCornerShape(28.dp)), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(Modifier.size(74.dp).scale(pulse).clip(RoundedCornerShape(24.dp)).background(Brush.linearGradient(listOf(Accent, Color(0xFF4BC4FF)))), contentAlignment = Alignment.Center) { Text("3", color = Color.White, fontSize = 40.sp, fontWeight = FontWeight.Black) }
                    Spacer(Modifier.height(8.dp)); Text("TRIPLIX", color = TextMain, fontSize = 34.sp, fontWeight = FontWeight.Black, letterSpacing = 1.sp); Text("AYNI 3'Ü BİRLEŞTİR • DAHA FAZLASINI KEŞFET", color = Color.White.copy(.75f), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.height(12.dp)); Button(onClick = onPlay, Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(18.dp), colors = ButtonDefaults.buttonColors(containerColor = Accent)) { Text("🌎 DÜNYANI SEÇ VE OYNA", fontWeight = FontWeight.Black, fontSize = 16.sp) }; Spacer(Modifier.height(12.dp))
        }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(9.dp)) { StatCard("🔥", "$streak", "Gün serisi", Modifier.weight(1f)); StatCard("⭐", "$games", "Oyun", Modifier.weight(1f)); StatCard("🏆", "9", "Dünya", Modifier.weight(1f)) }; Spacer(Modifier.height(12.dp))
        }
        item {
            Surface(color = Panel, shape = RoundedCornerShape(22.dp), modifier = Modifier.fillMaxWidth()) { Column(Modifier.padding(16.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) { Column { Text("🎁 GÜNLÜK ÖDÜL", color = TextMain, fontWeight = FontWeight.Black); Text("Serini koru, ödülü kap!", color = TextMuted, fontSize = 11.sp) }; Button(onClick = onDaily, enabled = !dailyClaimed, shape = RoundedCornerShape(13.dp), contentPadding = PaddingValues(horizontal = 13.dp, vertical = 8.dp)) { Text(if (dailyClaimed) "ALINDI ✓" else "+ ÖDÜL", fontSize = 11.sp, fontWeight = FontWeight.Black) } }
                Spacer(Modifier.height(12.dp)); Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) { (1..7).forEach { RewardDot(it, streak, dailyClaimed) } }
            } }; Spacer(Modifier.height(12.dp))
        }
        item {
            Surface(color = Panel, shape = RoundedCornerShape(22.dp), modifier = Modifier.fillMaxWidth()) { Column(Modifier.padding(16.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text("🎯 GÜNLÜK GÖREV", color = TextMain, fontWeight = FontWeight.Black); Text("$mission/5", color = Gold, fontWeight = FontWeight.Black) }; Spacer(Modifier.height(8.dp)); Text("5 bölüm tamamla ve 100 🪙 kazan", color = TextMuted, fontSize = 11.sp); Spacer(Modifier.height(9.dp)); LinearProgressIndicator(progress = { mission / 5f }, modifier = Modifier.fillMaxWidth().height(7.dp).clip(RoundedCornerShape(8.dp))); Spacer(Modifier.height(10.dp)); Button(onClick = onMission, enabled = mission >= 5, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(13.dp)) { Text(if (mission >= 5) "🎁 ÖDÜLÜ AL" else "GÖREVİ TAMAMLA", fontWeight = FontWeight.Black) }
            } }
        }
    }
}

@Composable private fun StatCard(icon: String, value: String, label: String, modifier: Modifier) { Surface(color = Panel, shape = RoundedCornerShape(17.dp), modifier = modifier.height(75.dp)) { Column(Modifier.padding(9.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) { Text(icon, fontSize = 17.sp); Text(value, color = TextMain, fontWeight = FontWeight.Black); Text(label, color = TextMuted, fontSize = 9.sp) } } }

@Composable private fun RewardDot(day: Int, streak: Int, claimed: Boolean) { val active = day <= streak; Box(Modifier.size(34.dp).clip(RoundedCornerShape(10.dp)).background(if (active) Accent.copy(.25f) else Color.White.copy(.04f)).border(1.dp, if (active) Accent else Color.White.copy(.06f), RoundedCornerShape(10.dp)), contentAlignment = Alignment.Center) { Text(if (day == 7) "🎁" else if (claimed && day == streak) "✓" else "$day", color = if (active) Gold else TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Black) } }

@Composable private fun WorldScreen(worlds: List<World>, selected: Int, onBack: () -> Unit, onWorld: (Int) -> Unit) { Column(Modifier.fillMaxSize().padding(16.dp)) { TopBar("DÜNYALAR", "İstediğini seç, istediğini oyna", onBack); Spacer(Modifier.height(12.dp)); LazyVerticalGrid(columns = GridCells.Fixed(2), modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 20.dp), verticalArrangement = Arrangement.spacedBy(12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) { items(worlds) { world -> WorldCard(world, selected == world.id) { onWorld(world.id) } } } } }

@Composable private fun WorldCard(world: World, selected: Boolean, onClick: () -> Unit) { val infinite = rememberInfiniteTransition(label = "world${world.id}"); val glow by infinite.animateFloat(.35f, .85f, infiniteRepeatable(tween(1500), RepeatMode.Reverse), label = "glow"); Surface(onClick = onClick, color = Panel, shape = RoundedCornerShape(22.dp), modifier = Modifier.fillMaxWidth().height(156.dp).border(1.dp, if (selected) Accent.copy(glow) else Color.White.copy(.07f), RoundedCornerShape(22.dp))) { Column(Modifier.padding(13.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) { Box(Modifier.size(58.dp).clip(RoundedCornerShape(18.dp)).background(Brush.linearGradient(world.colors.take(3))), contentAlignment = Alignment.Center) { Text(world.icon, fontSize = 32.sp) }; Spacer(Modifier.height(7.dp)); Text(world.name, color = TextMain, fontSize = 13.sp, fontWeight = FontWeight.Black); Text(world.subtitle, color = TextMuted, fontSize = 9.sp); Spacer(Modifier.height(4.dp)); Text("50 BÖLÜM ›", color = Accent, fontSize = 9.sp, fontWeight = FontWeight.Black) } } }

@Composable private fun LevelScreen(world: World, onBack: () -> Unit, onLevel: (Int) -> Unit) { Column(Modifier.fillMaxSize().padding(16.dp)) { TopBar(world.name.uppercase(), "Bölümünü seç", onBack, world.icon); Spacer(Modifier.height(10.dp)); Surface(color = Panel, shape = RoundedCornerShape(20.dp), modifier = Modifier.fillMaxWidth()) { Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) { Text(world.tiles.take(3).joinToString(" "), fontSize = 25.sp); Spacer(Modifier.width(12.dp)); Column(Modifier.weight(1f)) { Text("MACERA", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold); Text("50 bölüm seni bekliyor", color = TextMain, fontSize = 14.sp, fontWeight = FontWeight.Black) }; Text("⭐ 200", color = Gold, fontWeight = FontWeight.Black) } }; Spacer(Modifier.height(14.dp)); LazyVerticalGrid(columns = GridCells.Fixed(4), modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 20.dp), verticalArrangement = Arrangement.spacedBy(12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) { items((1..24).toList()) { n -> LevelButton(n, n <= 5, onLevel) } } } }

@Composable private fun LevelButton(number: Int, unlocked: Boolean, onLevel: (Int) -> Unit) { Surface(onClick = { if (unlocked) onLevel(number) }, enabled = unlocked, color = if (unlocked) Panel2 else Color(0xFF0D1020), shape = RoundedCornerShape(18.dp), modifier = Modifier.height(82.dp).border(1.dp, if (unlocked) Color.White.copy(.08f) else Color.White.copy(.035f), RoundedCornerShape(18.dp))) { Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) { Text(if (unlocked) if (number == 1) "▶" else "⭐" else "🔒", color = if (unlocked) Gold else TextMuted, fontSize = 18.sp); Text("$number", color = if (unlocked) TextMain else TextMuted, fontWeight = FontWeight.Black) } } }

@Composable private fun GameScreen(world: World, level: Int, coins: Int, onBack: () -> Unit, onWin: (Int) -> Unit, onNext: () -> Unit) {
    val view = LocalView.current
    var localCoins by rememberSaveable(world.id, level) { mutableIntStateOf(coins) }
    var score by rememberSaveable(world.id, level) { mutableIntStateOf(200 + level * 5) }
    var lives by rememberSaveable(world.id, level) { mutableIntStateOf(3) }
    var combo by rememberSaveable(world.id, level) { mutableIntStateOf(0) }
    var round by rememberSaveable(world.id, level) { mutableIntStateOf(0) }
    var removed by remember { mutableStateOf(setOf<Int>()) }
    var tray by remember { mutableStateOf(listOf<Int>()) }
    var banner by remember { mutableStateOf<String?>(null) }
    var won by remember { mutableStateOf(false) }
    var gameOver by remember { mutableStateOf(false) }
    val tiles = remember(world.id, level, round) { (0 until 36).map { Tile(it, (it / 2) % world.tiles.size) }.shuffled(Random(level * 97 + world.id * 31 + round)) }

    Box(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize().padding(horizontal = 14.dp, vertical = 7.dp)) {
            TopBar("SEVİYE $level", world.name, onBack, world.icon, "🪙 $localCoins")
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text("⭐ $score", color = TextMain, fontWeight = FontWeight.Black); Text("🔥 x$combo", color = Color(0xFFFFB55B), fontWeight = FontWeight.Black); Text("❤️ $lives", color = TextMain, fontWeight = FontWeight.Bold) }
            Spacer(Modifier.height(8.dp))
            Box(Modifier.fillMaxWidth().weight(1f).clip(RoundedCornerShape(28.dp)).background(Brush.verticalGradient(listOf(Color(0xFF151B30), Color(0xFF0B0F1D)))).border(1.dp, Color.White.copy(.08f), RoundedCornerShape(28.dp)).padding(9.dp)) {
                Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    Text("${world.icon}  ${world.name}", color = TextMain, fontSize = 12.sp, fontWeight = FontWeight.Black); Text("AYNI 3'Ü BİRLEŞTİR", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold); Spacer(Modifier.height(9.dp))
                    tiles.chunked(6).forEachIndexed { rowIndex, row ->
                        Row(horizontalArrangement = Arrangement.spacedBy(5.dp), modifier = Modifier.padding(vertical = 2.dp).offset(x = if (rowIndex % 2 == 0) 0.dp else 5.dp)) {
                            row.forEach { tile -> AnimatedVisibility(!removed.contains(tile.id), enter = scaleIn(spring(dampingRatio = .55f)) + fadeIn(), exit = scaleOut(tween(180)) + fadeOut(tween(130))) { GameTile(tile, world) { if (!removed.contains(tile.id) && tray.size < 7 && !won) { view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY); removed = removed + tile.id; val nextTray = tray + tile.type; if (nextTray.count { it == tile.type } >= 3) { combo += 1; val add = 35 + combo * 15 + level * 2; score += add; localCoins += 8 + combo; banner = if (combo >= 3) "🔥 COMBO x$combo  +$add" else "PERFECT!  +$add"; var count = 0; tray = nextTray.filter { if (it == tile.type && count < 3) { count++; false } else true }; view.postDelayed({ banner = null }, 700) } else { tray = nextTray; combo = 0 }; if (removed.size + 1 >= tiles.size) { won = true; onWin(75 + level * 5) }; if (tray.size >= 7 && !won) { lives--; combo = 0; tray = emptyList(); banner = "TEPSİ DOLDU!"; view.postDelayed({ banner = null }, 700); if (lives <= 0) gameOver = true } } } } }
                        }
                    }
                }
                banner?.let { msg -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Surface(color = Color.Black.copy(.8f), shape = RoundedCornerShape(22.dp), modifier = Modifier.border(1.dp, Accent.copy(.55f), RoundedCornerShape(22.dp))) { Text(msg, Modifier.padding(horizontal = 22.dp, vertical = 14.dp), color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Black) } } }
            }
            Spacer(Modifier.height(7.dp)); Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text("TEPSİ ${tray.size}/7", color = TextMuted, fontWeight = FontWeight.Black); Text("3 aynı = PATLAR ✨", color = TextMuted, fontSize = 9.sp) }
            Spacer(Modifier.height(5.dp)); Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) { repeat(7) { TraySlot(tray.getOrNull(it), world) } }
            Spacer(Modifier.height(7.dp)); Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(7.dp)) { Action("🔀", "Karıştır") { if (localCoins >= 15) { localCoins -= 15; round++ } }; Action("💡", "İpucu") { if (localCoins >= 5) { localCoins -= 5; banner = "Aynı sembolden 2 tane daha ara!"; view.postDelayed({ banner = null }, 900) } }; Action("↩", "Geri Al") { if (tray.isNotEmpty()) tray = tray.dropLast(1) } }
        }
        if (won) WinOverlay(score, { won = false; round++; onNext() }, onBack)
        if (gameOver) GameOver(score, { score = 200 + level * 5; lives = 3; combo = 0; removed = emptySet(); tray = emptyList(); round++; gameOver = false }, onBack)
    }
}

@Composable private fun GameTile(tile: Tile, world: World, onClick: () -> Unit) { var selected by remember { mutableStateOf(false) }; val scale by animateFloatAsState(if (selected) 1.12f else 1f, spring(dampingRatio = .5f), label = "scale"); val rot by animateFloatAsState(if (selected) 6f else 0f, spring(dampingRatio = .5f), label = "rot"); Box(Modifier.size(48.dp).scale(scale).rotate(rot).clip(RoundedCornerShape(14.dp)).background(Brush.linearGradient(listOf(world.colors[tile.type], world.colors[tile.type].copy(.5f)))).border(1.5.dp, Color.White.copy(.18f), RoundedCornerShape(14.dp)).clickable { selected = true; onClick() }, contentAlignment = Alignment.Center) { Text(world.tiles[tile.type], fontSize = 24.sp) } }

@Composable private fun RowScope.TraySlot(value: Int?, world: World) { Box(Modifier.weight(1f).height(51.dp).clip(RoundedCornerShape(12.dp)).background(Color.White.copy(.045f)).border(1.dp, Color.White.copy(.08f), RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) { if (value != null) Box(Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(world.colors[value]), contentAlignment = Alignment.Center) { Text(world.tiles[value], fontSize = 17.sp) } } }

@Composable private fun RowScope.Action(icon: String, label: String, onClick: () -> Unit) { Surface(onClick = onClick, color = Panel, shape = RoundedCornerShape(15.dp), modifier = Modifier.weight(1f)) { Column(Modifier.padding(vertical = 7.dp), horizontalAlignment = Alignment.CenterHorizontally) { Text(icon, fontSize = 18.sp); Text(label, color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold) } } }

@Composable private fun TopBar(title: String, subtitle: String, onBack: () -> Unit, icon: String? = null, trailing: String? = null) { Row(Modifier.fillMaxWidth().height(58.dp), verticalAlignment = Alignment.CenterVertically) { TextButton(onClick = onBack) { Text("‹", color = TextMain, fontSize = 32.sp) }; if (icon != null) Text(icon, fontSize = 21.sp); Spacer(Modifier.width(6.dp)); Column(Modifier.weight(1f)) { Text(title, color = TextMain, fontSize = 16.sp, fontWeight = FontWeight.Black); Text(subtitle, color = Accent, fontSize = 9.sp, fontWeight = FontWeight.Bold) }; if (trailing != null) Surface(color = Color.White.copy(.06f), shape = RoundedCornerShape(14.dp)) { Text(trailing, Modifier.padding(horizontal = 9.dp, vertical = 7.dp), color = TextMain, fontSize = 10.sp, fontWeight = FontWeight.Bold) } } }

@Composable private fun WinOverlay(score: Int, onNext: () -> Unit, onLevels: () -> Unit) { Box(Modifier.fillMaxSize().background(Color.Black.copy(.83f)), contentAlignment = Alignment.Center) { Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF151B2D)), shape = RoundedCornerShape(30.dp), modifier = Modifier.fillMaxWidth(.88f)) { Column(Modifier.padding(26.dp), horizontalAlignment = Alignment.CenterHorizontally) { Text("✨🏆✨", fontSize = 38.sp); Text("SEVİYE TAMAMLANDI!", color = TextMain, fontSize = 22.sp, fontWeight = FontWeight.Black); Spacer(Modifier.height(8.dp)); Text("⭐ $score", color = Gold, fontWeight = FontWeight.Black); Spacer(Modifier.height(18.dp)); Button(onClick = onNext, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.buttonColors(containerColor = Accent)) { Text("SONRAKİ SEVİYE  ›", fontWeight = FontWeight.Black) }; TextButton(onClick = onLevels) { Text("Bölümlere Dön") } } } } }

@Composable private fun GameOver(score: Int, onRestart: () -> Unit, onBack: () -> Unit) { Box(Modifier.fillMaxSize().background(Color.Black.copy(.83f)), contentAlignment = Alignment.Center) { Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF151B2D)), shape = RoundedCornerShape(30.dp), modifier = Modifier.fillMaxWidth(.88f)) { Column(Modifier.padding(26.dp), horizontalAlignment = Alignment.CenterHorizontally) { Text("💥", fontSize = 50.sp); Text("OYUN BİTTİ", color = TextMain, fontSize = 27.sp, fontWeight = FontWeight.Black); Text("Skor: $score", color = TextMuted); Spacer(Modifier.height(18.dp)); Button(onClick = onRestart, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) { Text("🔄 TEKRAR OYNA", fontWeight = FontWeight.Black) }; TextButton(onClick = onBack) { Text("Bölümlere Dön") } } } } }
