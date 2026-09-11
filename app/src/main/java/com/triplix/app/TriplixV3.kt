package com.triplix.app

import android.content.Context
import android.os.Bundle
import android.view.HapticFeedbackConstants
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs
import kotlin.random.Random

private val B3 = Color(0xFF060811)
private val CARD3 = Color(0xFF101527)
private val CARD4 = Color(0xFF171E33)
private val WHITE3 = Color(0xFFF8F9FF)
private val MUTED3 = Color(0xFF8F99B7)
private val PURPLE3 = Color(0xFF7658FF)
private val PURPLE4 = Color(0xFF9C78FF)
private val GOLD3 = Color(0xFFFFD45B)
private val GREEN3 = Color(0xFF51E29A)
private val RED3 = Color(0xFFFF5C78)

private data class TWorld(val id: Int, val name: String, val icon: String, val tiles: List<String>, val accent: Color)
private data class TTile(val id: Int, val type: Int, val x: Int, val y: Int, val layer: Int, val special: TSpecial = TSpecial.NONE)
private data class TSnapshot(val board: List<TTile>, val tray: List<Int>, val score: Int, val combo: Int)
private enum class TSpecial { NONE, ICE, BOMB, JOKER, LOCKED, CHAIN }

private val TWORLDS = listOf(
    TWorld(0, "Meyve Bahçesi", "🍓", listOf("🍎","🍌","🍇","🍊","🍓","🍉"), Color(0xFFFF6677)),
    TWorld(1, "Kristal Vadisi", "💎", listOf("💎","🔷","🟢","🔮","🟡","💠"), Color(0xFF58C9FF)),
    TWorld(2, "Sihirli Objeler", "🔮", listOf("🧙","🧪","📖","🗝️","🪄","🔮"), Color(0xFFB16DFF)),
    TWorld(3, "Kozmik Evren", "🚀", listOf("🚀","🪐","⭐","☄️","🌙","🌌"), Color(0xFF8C7BFF)),
    TWorld(4, "Sevimli Dostlar", "🐱", listOf("🐱","🐶","🐼","🐰","🐸","🐧"), Color(0xFFFFA85B)),
    TWorld(5, "Antik Semboller", "🏺", listOf("☀️","👁️","🔺","🌀","🌿","🜁"), Color(0xFFE9B85C)),
    TWorld(6, "Doğa Elementleri", "🌿", listOf("🌿","💧","🔥","🌪️","🪨","🌸"), Color(0xFF58D98A)),
    TWorld(7, "Mevsimler", "❄️", listOf("❄️","🌸","☀️","🍁","🌧️","🌙"), Color(0xFF72CFFF)),
    TWorld(8, "Kraliyet", "👑", listOf("👑","❤️","⚡","💜","♾️","🌟"), Color(0xFFFFD15C))
)

@Composable
fun TriplixV3App() {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("triplix_v3", Context.MODE_PRIVATE) }
    var page by rememberSaveable { mutableStateOf("home") }
    var world by rememberSaveable { mutableIntStateOf(0) }
    var level by rememberSaveable { mutableIntStateOf(1) }
    var coins by rememberSaveable { mutableIntStateOf(prefs.getInt("coins", 350)) }
    var lives by rememberSaveable { mutableIntStateOf(prefs.getInt("lives", 3)) }
    var games by rememberSaveable { mutableIntStateOf(prefs.getInt("games", 0)) }
    var unlocked by rememberSaveable { mutableIntStateOf(prefs.getInt("unlocked", 5)) }
    var streak by rememberSaveable { mutableIntStateOf(prefs.getInt("streak", 0)) }
    var mission by rememberSaveable { mutableIntStateOf(prefs.getInt("mission", 0)) }
    var chest by rememberSaveable { mutableIntStateOf(prefs.getInt("chest", 0)) }
    var bombs by rememberSaveable { mutableIntStateOf(prefs.getInt("bombs", 2)) }
    var hints by rememberSaveable { mutableIntStateOf(prefs.getInt("hints", 2)) }
    var shuffles by rememberSaveable { mutableIntStateOf(prefs.getInt("shuffles", 2)) }
    var undos by rememberSaveable { mutableIntStateOf(prefs.getInt("undos", 2)) }
    var daily by rememberSaveable { mutableStateOf(prefs.getString("daily", "") == tToday()) }
    var stars by remember { mutableStateOf(prefs.getStringSet("stars", emptySet()) ?: emptySet()) }

    fun save() = prefs.edit().putInt("coins", coins).putInt("lives", lives).putInt("games", games)
        .putInt("unlocked", unlocked).putInt("streak", streak).putInt("mission", mission)
        .putInt("chest", chest).putInt("bombs", bombs).putInt("hints", hints).putInt("shuffles", shuffles)
        .putInt("undos", undos).putString("daily", if (daily) tToday() else "").putStringSet("stars", stars).apply()

    BackHandler(enabled = page != "home") {
        page = when (page) { "worlds" -> "home"; "levels" -> "worlds"; "achievements" -> "home"; else -> "levels" }
    }

    MaterialTheme(colorScheme = darkColorScheme(background = B3, surface = CARD3, primary = PURPLE3)) {
        Surface(Modifier.fillMaxSize(), color = B3) {
            when (page) {
                "home" -> THome(coins, lives, streak, games, mission, chest, daily,
                    { page = "worlds" },
                    { if (!daily) { coins += 75 + streak.coerceAtMost(6) * 15; streak++; daily = true; save() } },
                    { if (chest >= 3) { chest -= 3; coins += 150; bombs++; hints++; save() } },
                    { page = "achievements" })
                "worlds" -> TWorlds({ page = "home" }) { world = it; page = "levels" }
                "levels" -> TLevels(TWORLDS[world], unlocked, stars, { page = "worlds" }) { level = it; page = "game" }
                "achievements" -> TAchievements(games, streak, bombs, hints, shuffles, undos) { page = "home" }
                else -> TGame(TWORLDS[world], level, coins, lives, bombs, hints, shuffles, undos,
                    { page = "levels" },
                    { b, h, s, u -> bombs = b; hints = h; shuffles = s; undos = u; save() },
                    { earned, gotStars ->
                        coins += earned; games++; mission = (mission + 1).coerceAtMost(5); chest++
                        if (gotStars >= 3) coins += 30
                        stars = stars.filterNot { it.startsWith("$world:$level:") }.toMutableSet().apply { add("$world:$level:$gotStars") }
                        if (level >= unlocked && unlocked < 60) unlocked++
                        save()
                    },
                    { level = (level + 1).coerceAtMost(unlocked.coerceAtLeast(1)); page = "game" })
            }
        }
    }
}

@Composable private fun THome(coins: Int, lives: Int, streak: Int, games: Int, mission: Int, chest: Int, daily: Boolean, play: () -> Unit, dailyAction: () -> Unit, chestAction: () -> Unit, achievements: () -> Unit) {
    val pulse by rememberInfiniteTransition(label = "home").animateFloat(.97f, 1.03f, infiniteRepeatable(tween(1200), RepeatMode.Reverse), label = "p")
    LazyColumn(Modifier.fillMaxSize().padding(horizontal = 18.dp), contentPadding = PaddingValues(top = 14.dp, bottom = 28.dp)) {
        item {
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) { Column { Text("TRIPLIX", color = WHITE3, fontSize = 27.sp, fontWeight = FontWeight.Black); Text("Üçlü eşleştir. Katmanları aç.", color = MUTED3, fontSize = 11.sp) }; Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) { TPill("🪙 $coins", GOLD3); TPill("❤️ $lives", RED3) } }
            Spacer(Modifier.height(16.dp))
            Box(Modifier.fillMaxWidth().height(260.dp).clip(RoundedCornerShape(32.dp)).background(Brush.linearGradient(listOf(Color(0xFF27215C), Color(0xFF342064), Color(0xFF102A43)))).border(1.dp, PURPLE3.copy(.45f), RoundedCornerShape(32.dp)), Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) { Box(Modifier.size(82.dp).scale(pulse).clip(RoundedCornerShape(26.dp)).background(Brush.linearGradient(listOf(Color(0xFFFFBA3D), Color(0xFFFF655F)))), Alignment.Center) { Text("3", color = WHITE3, fontSize = 46.sp, fontWeight = FontWeight.Black) }; Spacer(Modifier.height(8.dp)); Text("TRIPLIX", color = WHITE3, fontSize = 40.sp, fontWeight = FontWeight.Black); Text("TRIPLE MATCH PUZZLE", color = Color.White.copy(.66f), fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp); Spacer(Modifier.height(13.dp)); Text("💥 COMBO   ⭐ YILDIZ   🧊 ÖZEL TAŞ", color = GOLD3, fontSize = 10.sp, fontWeight = FontWeight.Black) }
            }
            Spacer(Modifier.height(12.dp)); Button(onClick = play, modifier = Modifier.fillMaxWidth().height(58.dp), shape = RoundedCornerShape(19.dp), colors = ButtonDefaults.buttonColors(containerColor = PURPLE3)) { Text("▶  OYUNA BAŞLA", fontSize = 17.sp, fontWeight = FontWeight.Black) }; Spacer(Modifier.height(12.dp))
        }
        item { Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(9.dp)) { TStat("🔥", "$streak", "Seri", Modifier.weight(1f)); TStat("🎮", "$games", "Oyun", Modifier.weight(1f)); TStat("🎁", "$chest", "Sandık", Modifier.weight(1f)) }; Spacer(Modifier.height(12.dp)) }
        item { TCard { Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) { Column(Modifier.weight(1f)) { Text("🎁 GÜNLÜK ÖDÜL", color = WHITE3, fontWeight = FontWeight.Black); Text("Her gün daha büyük ödül.", color = MUTED3, fontSize = 11.sp) }; TButton(if (daily) "ALINDI ✓" else "+75 🪙", !daily, dailyAction) }; Spacer(Modifier.height(11.dp)); Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(6.dp)) { (1..7).forEach { TDay(it, it <= streak) } } }; Spacer(Modifier.height(12.dp)) }
        item { TCard { Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) { Column { Text("🎯 GÜNLÜK GÖREV", color = WHITE3, fontWeight = FontWeight.Black); Text("5 bölüm tamamla", color = MUTED3, fontSize = 11.sp) }; Text("$mission/5", color = GOLD3, fontWeight = FontWeight.Black) }; Spacer(Modifier.height(9.dp)); LinearProgressIndicator(progress = { mission / 5f }, modifier = Modifier.fillMaxWidth().height(7.dp), color = PURPLE3, trackColor = Color.White.copy(.06f)) }; Spacer(Modifier.height(12.dp)) }
        item { TCard { Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) { Column { Text("🧰 SANDIK ODASI", color = WHITE3, fontWeight = FontWeight.Black); Text("3 parça = sürpriz ödül", color = MUTED3, fontSize = 11.sp) }; TButton("AÇ 🎁", chest >= 3, chestAction) }; Spacer(Modifier.height(9.dp)); Text("▰  $chest / 3 parça", color = GOLD3, fontSize = 12.sp, fontWeight = FontWeight.Black) }; Spacer(Modifier.height(12.dp)); OutlinedButton(onClick = achievements, modifier = Modifier.fillMaxWidth().height(50.dp), shape = RoundedCornerShape(16.dp)) { Text("🏆 BAŞARIMLAR & KOLEKSİYON", fontWeight = FontWeight.Black) } }
    }
}

@Composable private fun TPill(text: String, color: Color) { Surface(color = Color.White.copy(.06f), shape = RoundedCornerShape(15.dp)) { Text(text, Modifier.padding(horizontal = 10.dp, vertical = 8.dp), color = color, fontWeight = FontWeight.Black, fontSize = 11.sp) } }
@Composable private fun TStat(icon: String, value: String, label: String, modifier: Modifier) { Surface(color = CARD3, shape = RoundedCornerShape(18.dp), modifier = modifier.height(76.dp)) { Column(Modifier.fillMaxSize(), Alignment.CenterHorizontally, Arrangement.Center) { Text(icon, fontSize = 17.sp); Text(value, color = WHITE3, fontWeight = FontWeight.Black); Text(label, color = MUTED3, fontSize = 9.sp) } } }
@Composable private fun TCard(content: @Composable ColumnScope.() -> Unit) { Surface(color = CARD3, shape = RoundedCornerShape(22.dp), modifier = Modifier.fillMaxWidth(), tonalElevation = 2.dp) { Column(Modifier.padding(16.dp), content = content) } }
@Composable private fun TButton(text: String, enabled: Boolean, action: () -> Unit) { Button(onClick = action, enabled = enabled, contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp), shape = RoundedCornerShape(13.dp), colors = ButtonDefaults.buttonColors(containerColor = PURPLE3)) { Text(text, fontSize = 10.sp, fontWeight = FontWeight.Black) } }
@Composable private fun TDay(day: Int, active: Boolean) { Box(Modifier.size(35.dp).clip(RoundedCornerShape(10.dp)).background(if (active) PURPLE3.copy(.24f) else Color.White.copy(.035f)).border(1.dp, if (active) PURPLE3 else Color.White.copy(.06f), RoundedCornerShape(10.dp)), Alignment.Center) { Text(if (day == 7) "🎁" else "$day", color = if (active) GOLD3 else MUTED3, fontSize = 10.sp, fontWeight = FontWeight.Black) } }

@Composable private fun TWorlds(back: () -> Unit, choose: (Int) -> Unit) { Column(Modifier.fillMaxSize().padding(18.dp)) { THeader("DÜNYALAR", "Her dünya yeni taşlar", back); Spacer(Modifier.height(12.dp)); LazyVerticalGrid(GridCells.Fixed(2), Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 24.dp), verticalArrangement = Arrangement.spacedBy(12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) { items(TWORLDS) { w -> Surface(color = CARD3, shape = RoundedCornerShape(24.dp), modifier = Modifier.height(160.dp).clickable { choose(w.id) }) { Column(Modifier.fillMaxSize().padding(14.dp), Alignment.CenterHorizontally, Arrangement.Center) { Text(w.icon, fontSize = 38.sp); Spacer(Modifier.height(5.dp)); Text(w.name, color = WHITE3, fontWeight = FontWeight.Black, fontSize = 13.sp, textAlign = TextAlign.Center); Text("60 bölüm", color = MUTED3, fontSize = 10.sp); Spacer(Modifier.height(7.dp)); Text(w.tiles.take(5).joinToString(" "), fontSize = 16.sp) } } } } } }

@Composable private fun TLevels(world: TWorld, unlocked: Int, stars: Set<String>, back: () -> Unit, choose: (Int) -> Unit) { Column(Modifier.fillMaxSize().padding(horizontal = 18.dp)) { THeader("${world.icon} ${world.name}", "Bölümünü seç", back); Spacer(Modifier.height(12.dp)); TCard { Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) { Text("İLERLEME", color = MUTED3, fontSize = 10.sp, fontWeight = FontWeight.Black); Text("$unlocked / 60", color = GOLD3, fontWeight = FontWeight.Black, fontSize = 12.sp) }; Spacer(Modifier.height(8.dp)); LinearProgressIndicator(progress = { unlocked / 60f }, modifier = Modifier.fillMaxWidth().height(7.dp), color = world.accent, trackColor = Color.White.copy(.06f)) }; Spacer(Modifier.height(12.dp)); LazyVerticalGrid(GridCells.Fixed(4), Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 30.dp), verticalArrangement = Arrangement.spacedBy(10.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) { items((1..60).toList()) { n -> val open = n <= unlocked; val saved = stars.firstOrNull { it.startsWith("${world.id}:$n:") }?.substringAfterLast(":")?.toIntOrNull() ?: 0; Surface(color = if (open) CARD3 else Color.White.copy(.025f), shape = RoundedCornerShape(16.dp), modifier = Modifier.aspectRatio(1f).clickable(enabled = open) { choose(n) }) { Column(Modifier.fillMaxSize(), Alignment.CenterHorizontally, Arrangement.Center) { Text(if (open) "$n" else "🔒", color = if (open) WHITE3 else MUTED3, fontSize = 18.sp, fontWeight = FontWeight.Black); if (open) Text(if (saved > 0) "${"⭐".repeat(saved)}" else "☆ ☆ ☆", color = if (saved > 0) GOLD3 else MUTED3, fontSize = 8.sp) } } } } } }

@Composable private fun TAchievements(games: Int, streak: Int, bombs: Int, hints: Int, shuffles: Int, undos: Int, back: () -> Unit) { Column(Modifier.fillMaxSize().padding(18.dp)) { THeader("🏆 BAŞARIMLAR", "Oynadıkça hesabın güçlenir", back); Spacer(Modifier.height(12.dp)); TAchievement("🎮", "İlk Hamle", "1 oyun tamamla", games >= 1, "$games/1"); TAchievement("🔥", "Ateş Kesildi", "7 günlük seri", streak >= 7, "$streak/7"); TAchievement("💣", "Yıkıcı Güç", "3 bomba biriktir", bombs >= 3, "$bombs/3"); TAchievement("💡", "Zeki Oyuncu", "3 ipucu biriktir", hints >= 3, "$hints/3"); TAchievement("🔀", "Karıştır Ustası", "3 karıştırıcı biriktir", shuffles >= 3, "$shuffles/3"); TAchievement("↶", "Geri Dönüş", "3 geri alma biriktir", undos >= 3, "$undos/3"); Spacer(Modifier.height(12.dp)); TCard { Text("🧩 KOLEKSİYON", color = WHITE3, fontWeight = FontWeight.Black); Spacer(Modifier.height(8.dp)); Text("🧊   💣   🃏   🔒   ⛓️   ✨", fontSize = 28.sp); Spacer(Modifier.height(6.dp)); Text("Özel taşlar ilerleyen bölümlerde açılır.", color = MUTED3, fontSize = 11.sp) } } }
@Composable private fun TAchievement(icon: String, title: String, desc: String, done: Boolean, progress: String) { Surface(color = if (done) PURPLE3.copy(.13f) else CARD3, shape = RoundedCornerShape(18.dp), modifier = Modifier.fillMaxWidth().padding(bottom = 9.dp)) { Row(Modifier.padding(14.dp), Alignment.CenterVertically) { Text(if (done) "✅" else icon, fontSize = 23.sp); Spacer(Modifier.width(12.dp)); Column(Modifier.weight(1f)) { Text(title, color = WHITE3, fontWeight = FontWeight.Black); Text(desc, color = MUTED3, fontSize = 11.sp) }; Text(progress, color = if (done) GREEN3 else GOLD3, fontWeight = FontWeight.Black, fontSize = 11.sp) } } }

private fun tBoard(level: Int): List<TTile> {
    val base = (0 until 6).flatMap { y -> (0 until 6).map { x -> x to y } }
    val counts = when { level < 6 -> listOf(18, 6); level < 16 -> listOf(18, 9, 3); level < 30 -> listOf(18, 10, 6, 2); level < 45 -> listOf(18, 10, 6, 4, 2); else -> listOf(18, 10, 6, 4, 3, 1) }
    val pool = (0 until 6).flatMap { type -> List(6) { type } }.shuffled(Random(level * 8191L))
    var pIndex = 0; var id = 0; val out = mutableListOf<TTile>()
    counts.forEachIndexed { layer, count ->
        val positions = if (layer == 0) base.shuffled(Random(level * 97L + layer)) else base.shuffled(Random(level * 97L + layer)).take(count + 5)
        repeat(count) { j ->
            val p = positions[j]
            val special = when { level >= 8 && id % 19 == 0 -> TSpecial.ICE; level >= 15 && id % 23 == 0 -> TSpecial.BOMB; level >= 24 && id % 29 == 0 -> TSpecial.JOKER; level >= 32 && id % 17 == 0 -> TSpecial.LOCKED; level >= 42 && id % 13 == 0 -> TSpecial.CHAIN; else -> TSpecial.NONE }
            out += TTile(id, pool[pIndex++ % pool.size], p.first, p.second, layer, special); id++
        }
    }
    return out.shuffled(Random(level * 104729L))
}

private fun tBlocked(t: TTile, board: List<TTile>): Boolean = board.any { o -> o.id != t.id && o.layer > t.layer && abs(o.x - t.x) <= 1 && abs(o.y - t.y) <= 1 }

@Composable private fun TGame(world: TWorld, level: Int, coins: Int, lives: Int, bombs: Int, hints: Int, shuffles: Int, undos: Int, back: () -> Unit, boosters: (Int, Int, Int, Int) -> Unit, finish: (Int, Int) -> Unit, next: () -> Unit) {
    val view = LocalView.current
    var board by remember(level, world.id) { mutableStateOf(tBoard(level)) }
    var tray by remember(level, world.id) { mutableStateOf(emptyList<Int>()) }
    var score by remember(level, world.id) { mutableIntStateOf(0) }
    var combo by remember(level, world.id) { mutableIntStateOf(0) }
    var bestCombo by remember(level, world.id) { mutableIntStateOf(0) }
    var message by remember(level, world.id) { mutableStateOf("AYNI 3 TAŞI BİRLEŞTİR") }
    var hintId by remember { mutableIntStateOf(-1) }
    var history by remember { mutableStateOf(emptyList<TSnapshot>()) }
    var burst by remember { mutableStateOf(false) }
    var won by remember { mutableStateOf(false) }
    var over by remember { mutableStateOf(false) }
    var localBombs by remember(level, world.id) { mutableIntStateOf(bombs) }
    var localHints by remember(level, world.id) { mutableIntStateOf(hints) }
    var localShuffles by remember(level, world.id) { mutableIntStateOf(shuffles) }
    var localUndos by remember(level, world.id) { mutableIntStateOf(undos) }
    var localCoins by remember(level, world.id) { mutableIntStateOf(coins) }

    fun snap() { history = (history + TSnapshot(board, tray, score, combo)).takeLast(6) }
    fun check() {
        val counts = tray.groupingBy { it }.eachCount()
        val match = counts.entries.firstOrNull { it.value >= 3 }
        if (match != null) {
            val type = match.key
            tray = tray.toMutableList().apply { repeat(3) { remove(type) } }
            combo++
            bestCombo = maxOf(bestCombo, combo)
            val bonus = 45 + combo * 20
            score += bonus
            burst = true
            message = "💥 COMBO x$combo   +$bonus"
            view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
        }
        if (board.isEmpty()) { won = true; val stars = when { score >= 900 -> 3; score >= 550 -> 2; else -> 1 }; finish(75 + level * 5, stars) }
        if (tray.size >= 7 && !won) { over = true; combo = 0; view.performHapticFeedback(HapticFeedbackConstants.REJECT) }
    }
    fun tap(t: TTile) {
        if (won || over || tBlocked(t, board)) return
        snap(); hintId = -1
        board = board.filterNot { it.id == t.id }
        tray = tray + t.type
        score += 10 + combo * 3
        check()
    }

    Column(Modifier.fillMaxSize().padding(horizontal = 14.dp, vertical = 8.dp)) {
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
            IconButton(onClick = back, modifier = Modifier.size(50.dp)) { Text("‹", color = WHITE3, fontSize = 42.sp) }
            Column(horizontalAlignment = Alignment.CenterHorizontally) { Text("LEVEL $level", color = WHITE3, fontSize = 21.sp, fontWeight = FontWeight.Black); Text(world.name, color = world.accent, fontSize = 10.sp, fontWeight = FontWeight.Bold) }
            Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) { TPill("🪙 $localCoins", GOLD3); TPill("❤️ $lives", RED3) }
        }
        Row(Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 2.dp), Arrangement.SpaceBetween, Alignment.CenterVertically) { Text("⭐ ${if (won) when { score >= 900 -> 3; score >= 550 -> 2; else -> 1 } else 0}", color = GOLD3, fontSize = 19.sp, fontWeight = FontWeight.Black); Text("🔥 x$bestCombo", color = GOLD3, fontSize = 18.sp, fontWeight = FontWeight.Black) }
        Spacer(Modifier.height(7.dp))
        Surface(color = Color(0xFF0D1120), shape = RoundedCornerShape(28.dp), modifier = Modifier.fillMaxWidth().weight(1f), border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(.07f))) {
            Column(Modifier.fillMaxSize().padding(10.dp)) {
                AnimatedVisibility(message.isNotBlank(), enter = fadeIn(tween(120)), exit = fadeOut(tween(100))) { Text(message, color = if (message.contains("💥")) GOLD3 else MUTED3, modifier = Modifier.fillMaxWidth().padding(top = 2.dp), textAlign = TextAlign.Center, fontSize = 10.sp, fontWeight = FontWeight.Black) }
                Spacer(Modifier.height(7.dp))
                LayerBoard(board, world, hintId, ::tBlocked, ::tap)
            }
        }
        Spacer(Modifier.height(8.dp))
        Text("TEPSİ  ${tray.size}/7", color = MUTED3, fontSize = 12.sp, fontWeight = FontWeight.Black)
        Spacer(Modifier.height(5.dp))
        Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(6.dp)) { repeat(7) { i -> TTray(tray, i, world) } }
        Spacer(Modifier.height(9.dp))
        Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(6.dp)) {
            TBoost("🔀", "Karıştır", localShuffles > 0) { if (localShuffles > 0 && !won && !over) { snap(); localShuffles--; board = board.shuffled(); localCoins = (localCoins - 15).coerceAtLeast(0); boosters(localBombs, localHints, localShuffles, localUndos) } }
            TBoost("💡", "İpucu", localHints > 0) { if (localHints > 0 && !won && !over) { val target = board.filterNot { tBlocked(it, board) }.groupingBy { it.type }.eachCount().maxByOrNull { it.value }?.key; hintId = board.firstOrNull { it.type == target && !tBlocked(it, board) }?.id ?: -1; localHints--; boosters(localBombs, localHints, localShuffles, localUndos) } }
            TBoost("💣", "Bomba", localBombs > 0) { if (localBombs > 0 && !won && !over) { snap(); localBombs--; val target = board.filterNot { tBlocked(it, board) }.maxByOrNull { it.layer }; if (target != null) { board = board.filterNot { it.id == target.id }; score += 60; message = "💣 PATLADI! +60"; burst = true; check() }; boosters(localBombs, localHints, localShuffles, localUndos) } }
            TBoost("↶", "Geri Al", localUndos > 0 && history.isNotEmpty()) { if (localUndos > 0 && history.isNotEmpty() && !won && !over) { val s = history.last(); history = history.dropLast(1); board = s.board; tray = s.tray; score = s.score; combo = s.combo; localUndos--; message = "↶ HAMLE GERİ ALINDI"; boosters(localBombs, localHints, localShuffles, localUndos) } }
        }
    }
    LaunchedEffect(burst) { if (burst) { delay(260); burst = false } }
    AnimatedVisibility(burst, enter = scaleIn(tween(140)), exit = fadeOut(tween(100))) { Box(Modifier.fillMaxSize(), Alignment.Center) { Text("💥✨", fontSize = 66.sp) } }
    if (won || over) {
        AlertDialog(onDismissRequest = {}, containerColor = CARD3,
            title = { Text(if (won) "BÖLÜM TAMAMLANDI! 🎉" else "TEPSİ DOLDU 😅", color = WHITE3, fontWeight = FontWeight.Black) },
            text = { Column { Text(if (won) "Skor: $score\nEn iyi combo: x$bestCombo" else "Bu hamlede tepsi doldu. Biraz daha planlı git!", color = MUTED3); if (won) { Spacer(Modifier.height(8.dp)); Text("⭐ ${when { score >= 900 -> 3; score >= 550 -> 2; else -> 1 }}     🪙 +${75 + level * 5}", color = GOLD3, fontWeight = FontWeight.Black) } } },
            confirmButton = { Button(onClick = { if (won) next() else { board = tBoard(level); tray = emptyList(); score = 0; combo = 0; bestCombo = 0; over = false; history = emptyList() } }) { Text(if (won) "SONRAKİ BÖLÜM ▶" else "TEKRAR OYNA", fontWeight = FontWeight.Black) } },
            dismissButton = { TextButton(onClick = back) { Text("HARİTA", color = MUTED3) } })
    }
}

@Composable private fun LayerBoard(board: List<TTile>, world: TWorld, hintId: Int, blocked: (TTile, List<TTile>) -> Boolean, tap: (TTile) -> Unit) {
    BoxWithConstraints(Modifier.fillMaxWidth().weight(1f).clip(RoundedCornerShape(22.dp)).background(Brush.radialGradient(listOf(Color(0xFF141A30), Color(0xFF0B0F1D))))) {
        val cell = (maxWidth - 28.dp) / 6f
        Box(Modifier.fillMaxSize().padding(14.dp)) {
            board.sortedWith(compareBy<TTile> { it.layer }.thenBy { it.id }).forEach { t ->
                val left = cell * t.x + cell * t.layer * .11f
                val top = cell * t.y + cell * t.layer * .11f
                TTileView(t, world, blocked(t, board), t.id == hintId, cell, Modifier.offset(left, top).zIndex(t.layer.toFloat() + .1f)) { tap(t) }
            }
        }
    }
}

@Composable private fun TTileView(t: TTile, world: TWorld, blocked: Boolean, hint: Boolean, size: Dp, modifier: Modifier, tap: () -> Unit) {
    val icon = when (t.special) { TSpecial.ICE -> "🧊"; TSpecial.BOMB -> "💣"; TSpecial.JOKER -> "🃏"; TSpecial.LOCKED -> "🔒"; TSpecial.CHAIN -> "⛓️"; TSpecial.NONE -> world.tiles[t.type] }
    val bg = when (t.special) { TSpecial.ICE -> Color(0xFF286080); TSpecial.BOMB -> Color(0xFF8B354A); TSpecial.JOKER -> Color(0xFF6843A7); TSpecial.LOCKED -> Color(0xFF514E5A); TSpecial.CHAIN -> Color(0xFF3F626B); TSpecial.NONE -> world.accent.copy(.86f) }
    val scale by remember { androidx.compose.animation.core.Animatable(.88f) }
    LaunchedEffect(t.id) { scale.animateTo(1f, tween(220, easing = FastOutSlowInEasing)) }
    Box(modifier.size(size * .88f).scale(scale.value).clip(RoundedCornerShape(18.dp)).background(if (blocked) bg.copy(.34f) else bg).border(if (hint) 3.dp else 1.dp, if (hint) GOLD3 else Color.White.copy(.13f), RoundedCornerShape(18.dp)).clickable(enabled = !blocked) { tap() }, Alignment.Center) {
        Text(icon, fontSize = if (t.special == TSpecial.NONE) 26.sp else 24.sp)
        if (t.layer > 0) Box(Modifier.fillMaxSize().padding(5.dp), Alignment.TopEnd) { Text("•".repeat(t.layer.coerceAtMost(3)), color = Color.White.copy(.5f), fontSize = 7.sp) }
        if (blocked) Box(Modifier.fillMaxSize().background(Color.Black.copy(.08f)))
    }
}

@Composable private fun RowScope.TTray(tray: List<Int>, index: Int, world: TWorld) { Box(Modifier.weight(1f).height(55.dp).clip(RoundedCornerShape(14.dp)).background(if (index < tray.size) CARD4 else Color.White.copy(.025f)).border(1.dp, if (index < tray.size) world.accent.copy(.45f) else Color.White.copy(.06f), RoundedCornerShape(14.dp)), Alignment.Center) { if (index < tray.size) Text(world.tiles[tray[index]], fontSize = 21.sp) else Text("·", color = Color.White.copy(.12f)) } }
@Composable private fun RowScope.TBoost(icon: String, label: String, enabled: Boolean, action: () -> Unit) { Surface(color = if (enabled) CARD3 else Color.White.copy(.025f), shape = RoundedCornerShape(18.dp), modifier = Modifier.weight(1f).height(70.dp).clickable(enabled = enabled) { action() }) { Column(Modifier.fillMaxSize(), Alignment.CenterHorizontally, Arrangement.Center) { Text(icon, fontSize = 20.sp); Text(label, color = if (enabled) MUTED3 else Color.White.copy(.18f), fontSize = 8.sp, fontWeight = FontWeight.Bold) } } }
@Composable private fun THeader(title: String, subtitle: String, back: () -> Unit) { Row(Modifier.fillMaxWidth().height(52.dp), Alignment.CenterVertically) { IconButton(onClick = back, modifier = Modifier.size(50.dp)) { Text("‹", color = WHITE3, fontSize = 42.sp) }; Spacer(Modifier.width(7.dp)); Column { Text(title, color = WHITE3, fontSize = 21.sp, fontWeight = FontWeight.Black); Text(subtitle, color = MUTED3, fontSize = 10.sp) } } }
private fun tToday(): String = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
