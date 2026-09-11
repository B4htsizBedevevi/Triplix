package com.triplix.app

import android.content.Context
import android.view.HapticFeedbackConstants
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs
import kotlin.random.Random

private val V2_BG = Color(0xFF070914)
private val V2_BG2 = Color(0xFF0C1020)
private val V2_CARD = Color(0xFF11172A)
private val V2_CARD2 = Color(0xFF171E35)
private val V2_WHITE = Color(0xFFF7F8FF)
private val V2_MUTED = Color(0xFF929BB7)
private val V2_PURPLE = Color(0xFF7B5CFF)
private val V2_PURPLE2 = Color(0xFF9B73FF)
private val V2_GOLD = Color(0xFFFFD15C)
private val V2_GREEN = Color(0xFF52E29A)
private val V2_RED = Color(0xFFFF5D79)

private data class V2World(val id: Int, val name: String, val icon: String, val tiles: List<String>, val accent: Color)
private data class V2Tile(val id: Int, val type: Int, val x: Int, val y: Int, val layer: Int, val special: V2Special = V2Special.NONE)
private data class V2Snapshot(val board: List<V2Tile>, val tray: List<Int>, val score: Int, val combo: Int, val coins: Int)
private enum class V2Special { NONE, ICE, BOMB, JOKER, LOCKED, CHAIN }

private val V2_WORLDS = listOf(
    V2World(0, "Meyve Bahçesi", "🍓", listOf("🍓","🍌","🍇","🍎","🍊","🍉"), Color(0xFFFF6A75)),
    V2World(1, "Kristal Vadisi", "💎", listOf("💎","🔷","🟢","🔮","🟡","💠"), Color(0xFF5BC8FF)),
    V2World(2, "Sihirli Objeler", "🔮", listOf("🧙","🧪","📖","🗝️","🪄","🔮"), Color(0xFFB26BFF)),
    V2World(3, "Kozmik Evren", "🚀", listOf("🚀","🪐","⭐","☄️","🌙","🌌"), Color(0xFF8C7BFF)),
    V2World(4, "Sevimli Dostlar", "🐱", listOf("🐱","🐶","🐼","🐰","🐸","🐧"), Color(0xFFFFA85B)),
    V2World(5, "Antik Semboller", "🏺", listOf("☀️","👁️","🔺","🌀","🌿","🜁"), Color(0xFFE9B85C)),
    V2World(6, "Doğa Elementleri", "🌿", listOf("🌿","💧","🔥","🌪️","🪨","🌸"), Color(0xFF58D98A)),
    V2World(7, "Mevsimler", "❄️", listOf("❄️","🌸","☀️","🍁","🌧️","🌙"), Color(0xFF72CFFF)),
    V2World(8, "Kraliyet", "👑", listOf("👑","❤️","⚡","💜","♾️","🌟"), Color(0xFFFFD15C))
)

@Composable
fun TriplixV2App() {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("triplix_v2", Context.MODE_PRIVATE) }
    var splash by rememberSaveable { mutableStateOf(true) }
    var page by rememberSaveable { mutableStateOf("home") }
    var worldId by rememberSaveable { mutableIntStateOf(0) }
    var level by rememberSaveable { mutableIntStateOf(1) }
    var coins by rememberSaveable { mutableIntStateOf(prefs.getInt("coins", 350)) }
    var lives by rememberSaveable { mutableIntStateOf(prefs.getInt("lives", 3)) }
    var games by rememberSaveable { mutableIntStateOf(prefs.getInt("games", 0)) }
    var streak by rememberSaveable { mutableIntStateOf(prefs.getInt("streak", 0)) }
    var mission by rememberSaveable { mutableIntStateOf(prefs.getInt("mission", 0)) }
    var unlocked by rememberSaveable { mutableIntStateOf(prefs.getInt("unlocked", 5)) }
    var dailyClaimed by rememberSaveable { mutableStateOf(prefs.getString("daily", "") == v2Today()) }
    var chest by rememberSaveable { mutableIntStateOf(prefs.getInt("chest", 0)) }
    var bombs by rememberSaveable { mutableIntStateOf(prefs.getInt("bombs", 2)) }
    var hints by rememberSaveable { mutableIntStateOf(prefs.getInt("hints", 2)) }
    var shuffles by rememberSaveable { mutableIntStateOf(prefs.getInt("shuffles", 2)) }
    var undoCount by rememberSaveable { mutableIntStateOf(prefs.getInt("undo", 2)) }
    var starMap by remember { mutableStateOf(prefs.getStringSet("stars", emptySet()) ?: emptySet()) }

    fun save() {
        prefs.edit().putInt("coins", coins).putInt("lives", lives).putInt("games", games)
            .putInt("streak", streak).putInt("mission", mission).putInt("unlocked", unlocked)
            .putInt("chest", chest).putInt("bombs", bombs).putInt("hints", hints)
            .putInt("shuffles", shuffles).putInt("undo", undoCount)
            .putString("daily", if (dailyClaimed) v2Today() else "")
            .putStringSet("stars", starMap).apply()
    }

    LaunchedEffect(Unit) { delay(850); splash = false }
    if (splash) { V2Splash(); return }

    MaterialTheme(colorScheme = darkColorScheme(background = V2_BG, surface = V2_CARD, primary = V2_PURPLE)) {
        Surface(Modifier.fillMaxSize(), color = V2_BG) {
            when (page) {
                "home" -> V2Home(coins, lives, streak, games, mission, chest, dailyClaimed,
                    onPlay = { page = "worlds" },
                    onDaily = { if (!dailyClaimed) { coins += 75 + streak.coerceAtMost(6) * 15; streak++; dailyClaimed = true; save() } },
                    onChest = { if (chest >= 3) { chest -= 3; coins += 150; bombs++; hints++; save() } },
                    onAchievements = { page = "achievements" })
                "worlds" -> V2Worlds({ page = "home" }) { worldId = it; page = "levels" }
                "levels" -> V2Levels(V2_WORLDS[worldId], unlocked, starMap, { page = "worlds" }) { level = it; page = "game" }
                "achievements" -> V2Achievements(games, streak, bombs, hints, shuffles) { page = "home" }
                else -> V2Game(V2_WORLDS[worldId], level, coins, lives, bombs, hints, shuffles, undoCount,
                    onBack = { page = "levels" },
                    onBoosters = { b, h, s, u -> bombs = b; hints = h; shuffles = s; undoCount = u; save() },
                    onFinish = { earned, stars ->
                        coins += earned; games++; mission = (mission + 1).coerceAtMost(5); chest++
                        if (stars >= 3) coins += 30
                        starMap = starMap.filterNot { it.startsWith("$worldId:$level:") }.toMutableSet().apply { add("$worldId:$level:$stars") }
                        if (level >= unlocked && unlocked < 60) unlocked++
                        save()
                    },
                    onNext = { level = (level + 1).coerceAtMost(unlocked.coerceAtLeast(1)); page = "game" })
            }
        }
    }
}

@Composable private fun V2Splash() {
    val pulse by rememberInfiniteTransition(label = "splash").animateFloat(.96f, 1.05f, infiniteRepeatable(tween(850), RepeatMode.Reverse), label = "pulse")
    Box(Modifier.fillMaxSize().background(V2_BG), Alignment.Center) { Column(horizontalAlignment = Alignment.CenterHorizontally) { Box(Modifier.size(112.dp).scale(pulse).clip(RoundedCornerShape(34.dp)).background(Brush.linearGradient(listOf(V2_PURPLE2, Color(0xFF6B3CFF)))).border(2.dp, Color.White.copy(.22f), RoundedCornerShape(34.dp)), Alignment.Center) { Text("3", color = V2_WHITE, fontSize = 64.sp, fontWeight = FontWeight.Black) }; Spacer(Modifier.height(22.dp)); Text("TRIPLIX", color = V2_WHITE, fontSize = 46.sp, fontWeight = FontWeight.Black, letterSpacing = 1.sp); Text("TRIPLE MATCH PUZZLE", color = V2_MUTED, fontSize = 13.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp) } }
}

@Composable private fun V2Home(coins: Int, lives: Int, streak: Int, games: Int, mission: Int, chest: Int, dailyClaimed: Boolean, onPlay: () -> Unit, onDaily: () -> Unit, onChest: () -> Unit, onAchievements: () -> Unit) {
    val glow by rememberInfiniteTransition(label = "homeGlow").animateFloat(.95f, 1.03f, infiniteRepeatable(tween(1200), RepeatMode.Reverse), label = "glow")
    LazyColumn(Modifier.fillMaxSize().padding(horizontal = 18.dp), contentPadding = PaddingValues(top = 16.dp, bottom = 30.dp)) {
        item { Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) { Column { Text("TRIPLIX", color = V2_WHITE, fontSize = 27.sp, fontWeight = FontWeight.Black); Text("Bugün biraz taş kıralım. 😎", color = V2_MUTED, fontSize = 11.sp) }; Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) { V2Pill("🪙 $coins", V2_GOLD); V2Pill("❤️ $lives", V2_RED) } }; Spacer(Modifier.height(16.dp)); Box(Modifier.fillMaxWidth().height(255.dp).clip(RoundedCornerShape(32.dp)).background(Brush.linearGradient(listOf(Color(0xFF25215A), Color(0xFF342061), Color(0xFF102B45)))).border(1.dp, V2_PURPLE.copy(.45f), RoundedCornerShape(32.dp)), Alignment.Center) { Column(horizontalAlignment = Alignment.CenterHorizontally) { Box(Modifier.size(78.dp).scale(glow).clip(RoundedCornerShape(25.dp)).background(Brush.linearGradient(listOf(Color(0xFFFFB93D), Color(0xFFFF675E)))), Alignment.Center) { Text("3", color = V2_WHITE, fontSize = 44.sp, fontWeight = FontWeight.Black) }; Spacer(Modifier.height(8.dp)); Text("TRIPLIX", color = V2_WHITE, fontSize = 38.sp, fontWeight = FontWeight.Black); Text("AYNI 3 TAŞI BİRLEŞTİR", color = Color.White.copy(.72f), fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp); Spacer(Modifier.height(14.dp)); Text("💥 COMBO   ⭐ YILDIZ   🧊 ÖZEL TAŞ", color = V2_GOLD, fontSize = 10.sp, fontWeight = FontWeight.Black) } }; Spacer(Modifier.height(12.dp)); Button(onClick = onPlay, modifier = Modifier.fillMaxWidth().height(58.dp), shape = RoundedCornerShape(19.dp), colors = ButtonDefaults.buttonColors(containerColor = V2_PURPLE)) { Text("▶  OYUNA BAŞLA", fontSize = 17.sp, fontWeight = FontWeight.Black) }; Spacer(Modifier.height(12.dp)) }
        item { Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(9.dp)) { V2Stat("🔥", "$streak", "Seri", Modifier.weight(1f)); V2Stat("🎮", "$games", "Oyun", Modifier.weight(1f)); V2Stat("🎁", "$chest", "Sandık", Modifier.weight(1f)) }; Spacer(Modifier.height(12.dp)) }
        item { V2Card { Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) { Column(Modifier.weight(1f)) { Text("🎁 GÜNLÜK ÖDÜL", color = V2_WHITE, fontWeight = FontWeight.Black); Text("Serini koru, ödülün büyüsün.", color = V2_MUTED, fontSize = 11.sp) }; V2SmallButton(if (dailyClaimed) "ALINDI ✓" else "+75 🪙", !dailyClaimed, onDaily) }; Spacer(Modifier.height(12.dp)); Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(6.dp)) { (1..7).forEach { V2Day(it, it <= streak) } } }; Spacer(Modifier.height(12.dp)) }
        item { V2Card { Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) { Column { Text("🎯 GÜNLÜK GÖREV", color = V2_WHITE, fontWeight = FontWeight.Black); Text("5 bölüm tamamla", color = V2_MUTED, fontSize = 11.sp) }; Text("$mission/5", color = V2_GOLD, fontWeight = FontWeight.Black) }; Spacer(Modifier.height(9.dp)); LinearProgressIndicator(progress = { mission / 5f }, modifier = Modifier.fillMaxWidth().height(7.dp), color = V2_PURPLE, trackColor = Color.White.copy(.07f)) }; Spacer(Modifier.height(12.dp)) }
        item { V2Card { Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) { Column { Text("🧰 SANDIK ODASI", color = V2_WHITE, fontWeight = FontWeight.Black); Text("3 parça = sürpriz ödül", color = V2_MUTED, fontSize = 11.sp) }; V2SmallButton("AÇ 🎁", chest >= 3, onChest) }; Spacer(Modifier.height(10.dp)); Text("▰  $chest / 3 parça", color = V2_GOLD, fontSize = 12.sp, fontWeight = FontWeight.Black) }; Spacer(Modifier.height(12.dp)); OutlinedButton(onClick = onAchievements, modifier = Modifier.fillMaxWidth().height(50.dp), shape = RoundedCornerShape(16.dp)) { Text("🏆 BAŞARIMLAR & KOLEKSİYON", fontWeight = FontWeight.Black) } }
    }
}

@Composable private fun V2Card(content: @Composable ColumnScope.() -> Unit) { Surface(color = V2_CARD, shape = RoundedCornerShape(22.dp), modifier = Modifier.fillMaxWidth(), tonalElevation = 2.dp) { Column(Modifier.padding(16.dp), content = content) } }
@Composable private fun V2Pill(text: String, color: Color) { Surface(color = Color.White.copy(.06f), shape = RoundedCornerShape(15.dp)) { Text(text, Modifier.padding(horizontal = 10.dp, vertical = 8.dp), color = color, fontWeight = FontWeight.Black, fontSize = 11.sp) } }
@Composable private fun V2Stat(icon: String, value: String, label: String, modifier: Modifier) { Surface(color = V2_CARD, shape = RoundedCornerShape(18.dp), modifier = modifier.height(76.dp)) { Column(Modifier.fillMaxSize(), Alignment.CenterHorizontally, Arrangement.Center) { Text(icon, fontSize = 17.sp); Text(value, color = V2_WHITE, fontWeight = FontWeight.Black); Text(label, color = V2_MUTED, fontSize = 9.sp) } } }
@Composable private fun V2Day(day: Int, active: Boolean) { Box(Modifier.size(35.dp).clip(RoundedCornerShape(10.dp)).background(if (active) V2_PURPLE.copy(.25f) else Color.White.copy(.035f)).border(1.dp, if (active) V2_PURPLE else Color.White.copy(.06f), RoundedCornerShape(10.dp)), Alignment.Center) { Text(if (day == 7) "🎁" else "$day", color = if (active) V2_GOLD else V2_MUTED, fontSize = 10.sp, fontWeight = FontWeight.Black) } }
@Composable private fun V2SmallButton(text: String, enabled: Boolean, onClick: () -> Unit) { Button(onClick = onClick, enabled = enabled, contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp), shape = RoundedCornerShape(13.dp), colors = ButtonDefaults.buttonColors(containerColor = V2_PURPLE)) { Text(text, fontSize = 10.sp, fontWeight = FontWeight.Black) } }

@Composable private fun V2Worlds(onBack: () -> Unit, onChoose: (Int) -> Unit) { Column(Modifier.fillMaxSize().padding(18.dp)) { V2Header("DÜNYALAR", "Her dünya yeni taşlar getirir", onBack); Spacer(Modifier.height(12.dp)); LazyVerticalGrid(GridCells.Fixed(2), Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 24.dp), verticalArrangement = Arrangement.spacedBy(12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) { items(V2_WORLDS) { w -> Surface(color = V2_CARD, shape = RoundedCornerShape(24.dp), modifier = Modifier.height(155.dp).clickable { onChoose(w.id) }) { Column(Modifier.fillMaxSize().padding(14.dp), Alignment.CenterHorizontally, Arrangement.Center) { Text(w.icon, fontSize = 36.sp); Spacer(Modifier.height(4.dp)); Text(w.name, color = V2_WHITE, fontWeight = FontWeight.Black, fontSize = 13.sp, textAlign = TextAlign.Center); Text("60 bölüm", color = V2_MUTED, fontSize = 10.sp); Spacer(Modifier.height(7.dp)); Text(w.tiles.take(5).joinToString(" "), fontSize = 16.sp) } } } } } }

@Composable private fun V2Levels(world: V2World, unlocked: Int, stars: Set<String>, onBack: () -> Unit, onChoose: (Int) -> Unit) { Column(Modifier.fillMaxSize().padding(horizontal = 18.dp)) { V2Header("${world.icon} ${world.name}", "Bölümünü seç", onBack); Spacer(Modifier.height(12.dp)); V2Card { Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) { Text("İLERLEME", color = V2_MUTED, fontSize = 10.sp, fontWeight = FontWeight.Black); Text("$unlocked / 60", color = V2_GOLD, fontWeight = FontWeight.Black, fontSize = 12.sp) }; Spacer(Modifier.height(8.dp)); LinearProgressIndicator(progress = { unlocked / 60f }, modifier = Modifier.fillMaxWidth().height(7.dp), color = world.accent, trackColor = Color.White.copy(.06f)) }; Spacer(Modifier.height(12.dp)); LazyVerticalGrid(GridCells.Fixed(4), Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 30.dp), verticalArrangement = Arrangement.spacedBy(10.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) { items((1..60).toList()) { n -> val open = n <= unlocked; val saved = stars.firstOrNull { it.startsWith("${world.id}:$n:") }?.substringAfterLast(":")?.toIntOrNull() ?: 0; Surface(color = if (open) V2_CARD else Color.White.copy(.025f), shape = RoundedCornerShape(16.dp), modifier = Modifier.aspectRatio(1f).clickable(enabled = open) { onChoose(n) }) { Column(Modifier.fillMaxSize(), Alignment.CenterHorizontally, Arrangement.Center) { Text(if (open) "$n" else "🔒", color = if (open) V2_WHITE else V2_MUTED, fontSize = 18.sp, fontWeight = FontWeight.Black); if (open) Text(if (saved > 0) "${"⭐".repeat(saved)}" else "☆ ☆ ☆", color = if (saved > 0) V2_GOLD else V2_MUTED, fontSize = 8.sp) } } } } } }

@Composable private fun V2Achievements(games: Int, streak: Int, bombs: Int, hints: Int, shuffles: Int, onBack: () -> Unit) { Column(Modifier.fillMaxSize().padding(18.dp)) { V2Header("🏆 BAŞARIMLAR", "Oynadıkça koleksiyon büyür", onBack); Spacer(Modifier.height(12.dp)); V2Achievement("🎮", "İlk Hamle", "1 oyun tamamla", games >= 1, "$games/1"); V2Achievement("🔥", "Ateş Kesildi", "7 günlük seri", streak >= 7, "$streak/7"); V2Achievement("💥", "Yıkıcı Güç", "3 bomba biriktir", bombs >= 3, "$bombs/3"); V2Achievement("💡", "Zeki Oyuncu", "3 ipucu biriktir", hints >= 3, "$hints/3"); V2Achievement("🔀", "Karıştır Ustası", "3 karıştırıcı biriktir", shuffles >= 3, "$shuffles/3"); Spacer(Modifier.height(12.dp)); V2Card { Text("🧩 KOLEKSİYON", color = V2_WHITE, fontWeight = FontWeight.Black); Spacer(Modifier.height(8.dp)); Text("Özel taşlar bölüm ilerledikçe açılır. Her dünya kendi temalı koleksiyonuna sahip.", color = V2_MUTED, fontSize = 12.sp); Spacer(Modifier.height(12.dp)); Text("🧊  💣  🃏  🔒  ⛓️  ✨", fontSize = 27.sp, letterSpacing = 4.sp) } } }
@Composable private fun V2Achievement(icon: String, title: String, desc: String, done: Boolean, progress: String) { Surface(color = if (done) V2_PURPLE.copy(.13f) else V2_CARD, shape = RoundedCornerShape(18.dp), modifier = Modifier.fillMaxWidth().padding(bottom = 9.dp)) { Row(Modifier.padding(14.dp), Alignment.CenterVertically) { Text(if (done) "✅" else icon, fontSize = 23.sp); Spacer(Modifier.width(12.dp)); Column(Modifier.weight(1f)) { Text(title, color = V2_WHITE, fontWeight = FontWeight.Black); Text(desc, color = V2_MUTED, fontSize = 11.sp) }; Text(progress, color = if (done) V2_GREEN else V2_GOLD, fontWeight = FontWeight.Black, fontSize = 11.sp) } } }

private fun v2Board(level: Int): List<V2Tile> {
    val base = (0 until 6).flatMap { y -> (0 until 6).map { x -> x to y } }
    val layers = when { level < 8 -> 1; level < 20 -> 2; level < 35 -> 3; level < 50 -> 4; else -> 5 }
    val counts = when (layers) { 1 -> listOf(24); 2 -> listOf(24, 8); 3 -> listOf(20, 10, 6); 4 -> listOf(18, 9, 6, 3); else -> listOf(16, 9, 6, 3, 2) }
    val out = mutableListOf<V2Tile>(); var id = 0
    counts.forEachIndexed { layer, count -> val pool = if (layer == 0) base.shuffled(Random(level * 7919L + layer)) else base.drop(5 + layer).take(18).shuffled(Random(level * 7919L + layer)); repeat(count) { j -> val p = pool[j % pool.size]; val special = when { level >= 8 && id % 17 == 0 -> V2Special.ICE; level >= 15 && id % 19 == 0 -> V2Special.BOMB; level >= 22 && id % 23 == 0 -> V2Special.JOKER; level >= 30 && id % 13 == 0 -> V2Special.LOCKED; level >= 40 && id % 11 == 0 -> V2Special.CHAIN; else -> V2Special.NONE }; out += V2Tile(id, (id * 7 + level) % 6, p.first, p.second, layer, special); id++ } }
    return out.shuffled(Random(level * 104729L))
}
private fun v2Blocked(t: V2Tile, board: List<V2Tile>): Boolean = board.any { o -> o.id != t.id && o.layer > t.layer && o.x == t.x && o.y == t.y }

@Composable private fun V2Game(world: V2World, level: Int, coins: Int, lives: Int, bombs: Int, hints: Int, shuffles: Int, undoCount: Int, onBack: () -> Unit, onBoosters: (Int, Int, Int, Int) -> Unit, onFinish: (Int, Int) -> Unit, onNext: () -> Unit) {
    val view = LocalView.current
    var board by remember(level, world.id) { mutableStateOf(v2Board(level)) }
    var tray by remember(level, world.id) { mutableStateOf(emptyList<Int>()) }
    var score by remember(level, world.id) { mutableIntStateOf(0) }
    var combo by remember(level, world.id) { mutableIntStateOf(0) }
    var bestCombo by remember(level, world.id) { mutableIntStateOf(0) }
    var won by remember(level, world.id) { mutableStateOf(false) }
    var over by remember(level, world.id) { mutableStateOf(false) }
    var message by remember(level, world.id) { mutableStateOf("Açık taşları seç!") }
    var hintId by remember { mutableIntStateOf(-1) }
    var burst by remember { mutableStateOf(false) }
    var history by remember { mutableStateOf(emptyList<V2Snapshot>()) }
    var localBombs by remember(level) { mutableIntStateOf(bombs) }
    var localHints by remember(level) { mutableIntStateOf(hints) }
    var localShuffles by remember(level) { mutableIntStateOf(shuffles) }
    var localUndo by remember(level) { mutableIntStateOf(undoCount) }
    var localCoins by remember(level) { mutableIntStateOf(coins) }

    fun snap() { history = (history + V2Snapshot(board, tray, score, combo, localCoins)).takeLast(8) }
    fun evaluate() { if (board.isEmpty()) { won = true; val stars = when { score >= 900 -> 3; score >= 600 -> 2; else -> 1 }; onFinish(75 + level * 5, stars) } else if (tray.size >= 7) over = true }
    fun tripleMatch() { val matchType = tray.groupingBy { it }.eachCount().entries.firstOrNull { it.value >= 3 }?.key; if (matchType != null) { tray = tray.toMutableList().apply { repeat(3) { remove(matchType) } }; combo++; bestCombo = maxOf(bestCombo, combo); score += 45 + combo * 20 + level * 2; localCoins += 2 + combo; message = "💥 COMBO x$combo   +${45 + combo * 20}"; burst = true } }
    fun tap(tile: V2Tile) {
        if (won || over || v2Blocked(tile, board)) return
        snap(); hintId = -1
        if (tile.special == V2Special.LOCKED) { board = board.map { if (it.id == tile.id) it.copy(special = V2Special.NONE) else it }; message = "🔒 Kilit kırıldı! Şimdi alabilirsin."; return }
        if (tile.special == V2Special.BOMB) { board = board.filterNot { it.id == tile.id || (abs(it.x - tile.x) <= 1 && abs(it.y - tile.y) <= 1 && !v2Blocked(it, board)) }; score += 70; message = "💣 BOOM! Alan temizlendi"; burst = true; evaluate(); return }
        board = board.filterNot { it.id == tile.id }
        tray = tray + if (tile.special == V2Special.JOKER) (tray.firstOrNull() ?: tile.type) else tile.type
        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
        tripleMatch(); evaluate()
    }

    Column(Modifier.fillMaxSize().padding(horizontal = 15.dp, vertical = 9.dp)) {
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) { Text("‹", color = V2_WHITE, fontSize = 40.sp, modifier = Modifier.clickable { onBack() }); Column(horizontalAlignment = Alignment.CenterHorizontally) { Text("LEVEL $level", color = V2_WHITE, fontSize = 21.sp, fontWeight = FontWeight.Black); Text(world.name, color = world.accent, fontSize = 10.sp, fontWeight = FontWeight.Bold) }; Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) { V2Pill("🪙 $localCoins", V2_GOLD); V2Pill("❤️ $lives", V2_RED) } }
        Row(Modifier.fillMaxWidth().padding(horizontal = 2.dp), Arrangement.SpaceBetween, Alignment.CenterVertically) { Text("⭐ ${if (won) 3 else 0}", color = V2_GOLD, fontSize = 20.sp, fontWeight = FontWeight.Black); Text("🔥 x$bestCombo", color = V2_GOLD, fontSize = 18.sp, fontWeight = FontWeight.Black) }
        Spacer(Modifier.height(7.dp))
        Surface(color = V2_BG2, shape = RoundedCornerShape(28.dp), modifier = Modifier.fillMaxWidth().weight(1f), border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(.06f))) { Column(Modifier.fillMaxSize().padding(11.dp), Arrangement.Center) { Text(message, color = if (message.contains("💥") || message.contains("BOOM")) V2_GOLD else V2_MUTED, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, fontSize = 10.sp, fontWeight = FontWeight.Black); Spacer(Modifier.height(9.dp)); LazyVerticalGrid(GridCells.Fixed(6), Modifier.fillMaxWidth().weight(1f), contentPadding = PaddingValues(2.dp), verticalArrangement = Arrangement.spacedBy(7.dp), horizontalArrangement = Arrangement.spacedBy(7.dp)) { items(board.sortedBy { it.layer }) { t -> V2TileView(t, world, v2Blocked(t, board), t.id == hintId) { tap(t) } } } } }
        Spacer(Modifier.height(9.dp)); Text("TEPSİ  ${tray.size}/7", color = V2_MUTED, fontSize = 12.sp, fontWeight = FontWeight.Black); Spacer(Modifier.height(5.dp)); Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(5.dp)) { repeat(7) { i -> V2Tray(tray, i, world) } }; Spacer(Modifier.height(9.dp))
        Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(6.dp)) {
            V2Booster("🔀", "Karıştır", localShuffles > 0) { if (localShuffles > 0 && !won && !over) { localShuffles--; board = board.shuffled(); localCoins = (localCoins - 15).coerceAtLeast(0); onBoosters(localBombs, localHints, localShuffles, localUndo) } }
            V2Booster("💡", "İpucu", localHints > 0) { if (localHints > 0 && !won && !over) { val t = board.filterNot { v2Blocked(it, board) }.groupingBy { it.type }.eachCount().maxByOrNull { it.value }?.key; hintId = board.firstOrNull { it.type == t && !v2Blocked(it, board) }?.id ?: -1; localHints--; onBoosters(localBombs, localHints, localShuffles, localUndo) } }
            V2Booster("💣", "Bomba", localBombs > 0) { if (localBombs > 0 && !won && !over) { localBombs--; val t = board.filterNot { v2Blocked(it, board) }.maxByOrNull { it.layer }; if (t != null) { board = board.filterNot { it.id == t.id }; score += 60; burst = true; message = "💣 BOOSTER!"; evaluate() }; onBoosters(localBombs, localHints, localShuffles, localUndo) } }
            V2Booster("↶", "Geri Al", localUndo > 0 && history.isNotEmpty()) { if (localUndo > 0 && history.isNotEmpty()) { val s = history.last(); history = history.dropLast(1); board = s.board; tray = s.tray; score = s.score; combo = s.combo; localCoins = s.coins; localUndo--; onBoosters(localBombs, localHints, localShuffles, localUndo) } }
        }
    }
    LaunchedEffect(burst) { if (burst) { delay(280); burst = false } }
    AnimatedVisibility(burst) { Text("💥✨", Modifier.fillMaxSize(), textAlign = TextAlign.Center, fontSize = 58.sp) }
    if (won || over) { AlertDialog(onDismissRequest = {}, containerColor = V2_CARD, title = { Text(if (won) "BÖLÜM TAMAMLANDI! 🎉" else "TEPSİ DOLDU 😅", color = V2_WHITE, fontWeight = FontWeight.Black) }, text = { Column { Text(if (won) "Skor: $score\nEn iyi combo: x$bestCombo" else "Bu kez olmadı. Hamlelerini biraz daha planla.", color = V2_MUTED); if (won) { Spacer(Modifier.height(8.dp)); Text("⭐ ${when { score >= 900 -> 3; score >= 600 -> 2; else -> 1 }}   🪙 +${75 + level * 5}", color = V2_GOLD, fontWeight = FontWeight.Black) } } }, confirmButton = { Button(onClick = { if (won) onNext() else { board = v2Board(level); tray = emptyList(); score = 0; combo = 0; over = false } }) { Text(if (won) "SONRAKİ BÖLÜM ▶" else "TEKRAR OYNA", fontWeight = FontWeight.Black) } }, dismissButton = { TextButton(onClick = onBack) { Text("HARİTA", color = V2_MUTED) } }) }
}

@Composable private fun V2TileView(tile: V2Tile, world: V2World, blocked: Boolean, hint: Boolean, onTap: () -> Unit) { val emoji = when(tile.special) { V2Special.ICE -> "🧊"; V2Special.BOMB -> "💣"; V2Special.JOKER -> "🃏"; V2Special.LOCKED -> "🔒"; V2Special.CHAIN -> "⛓️"; V2Special.NONE -> world.tiles[tile.type] }; val bg = when(tile.special) { V2Special.ICE -> Color(0xFF2B5E7C); V2Special.BOMB -> Color(0xFF8E3348); V2Special.JOKER -> Color(0xFF6541A4); V2Special.LOCKED -> Color(0xFF514D58); V2Special.CHAIN -> Color(0xFF3F5E68); V2Special.NONE -> world.accent.copy(.72f) }; Box(Modifier.aspectRatio(1f).clip(RoundedCornerShape(17.dp)).background(if (blocked) bg.copy(.38f) else bg).border(if (hint) 3.dp else 1.dp, if (hint) V2_GOLD else Color.White.copy(.12f), RoundedCornerShape(17.dp)).clickable(enabled = !blocked) { onTap() }, Alignment.Center) { Text(emoji, fontSize = if (tile.special == V2Special.NONE) 27.sp else 25.sp); if (tile.layer > 0) Box(Modifier.fillMaxSize().padding(5.dp), Alignment.TopEnd) { Text("•".repeat(tile.layer.coerceAtMost(3)), color = Color.White.copy(.42f), fontSize = 7.sp) } } }
@Composable private fun RowScope.V2Tray(tray: List<Int>, index: Int, world: V2World) { Box(Modifier.weight(1f).height(55.dp).clip(RoundedCornerShape(13.dp)).background(if (index < tray.size) V2_CARD2 else Color.White.copy(.025f)).border(1.dp, if (index < tray.size) world.accent.copy(.4f) else Color.White.copy(.06f), RoundedCornerShape(13.dp)), Alignment.Center) { if (index < tray.size) Text(world.tiles[tray[index]], fontSize = 21.sp) else Text("·", color = Color.White.copy(.12f)) } }
@Composable private fun RowScope.V2Booster(icon: String, label: String, enabled: Boolean, onClick: () -> Unit) { Surface(color = if (enabled) V2_CARD else Color.White.copy(.025f), shape = RoundedCornerShape(17.dp), modifier = Modifier.weight(1f).height(67.dp).clickable(enabled = enabled) { onClick() }) { Column(Modifier.fillMaxSize(), Alignment.CenterHorizontally, Arrangement.Center) { Text(icon, fontSize = 20.sp); Text(label, color = if (enabled) V2_MUTED else Color.White.copy(.18f), fontSize = 8.sp, fontWeight = FontWeight.Bold) } } }
@Composable private fun V2Header(title: String, subtitle: String, onBack: () -> Unit) { Row(Modifier.fillMaxWidth(), Alignment.CenterVertically) { Text("‹", color = V2_WHITE, fontSize = 40.sp, modifier = Modifier.clickable { onBack() }); Spacer(Modifier.width(8.dp)); Column { Text(title, color = V2_WHITE, fontSize = 21.sp, fontWeight = FontWeight.Black); Text(subtitle, color = V2_MUTED, fontSize = 10.sp) } } }
private fun v2Today(): String = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
