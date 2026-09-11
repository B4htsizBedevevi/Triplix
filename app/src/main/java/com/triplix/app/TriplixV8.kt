package com.triplix.app

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.abs
import kotlin.random.Random

private const val V8N = 7
private const val V8TYPES = 6
private val V8Bg = Color(0xFF050812)
private val V8Panel = Color(0xFF111827)
private val V8Panel2 = Color(0xFF182338)
private val V8Text = Color(0xFFF7F9FF)
private val V8Dim = Color(0xFF929DB2)
private val V8Green = Color(0xFF5BE59A)
private val V8Gold = Color(0xFFFFD166)

private enum class V8Page { HOME, MODES, LEVELS, GAME }
private enum class V8Mode(val title: String, val subtitle: String, val icons: List<String>, val colors: List<Color>) {
    CLASSIC("KLASİK", "Klasik mücevherler", listOf("●", "◆", "▲", "■", "★", "✦"), listOf(Color(0xFFFF5272), Color(0xFFFFC857), Color(0xFF9A78FF), Color(0xFFFF8A4C), Color(0xFF43D99B), Color(0xFF51B9FF))),
    FRUIT("MEYVE", "Tatlı meyve yağmuru", listOf("🍎", "🍋", "🍇", "🍊", "🍉", "🥝"), listOf(Color(0xFFFF5267), Color(0xFFFFD447), Color(0xFFB478FF), Color(0xFFFF9648), Color(0xFFFF5E86), Color(0xFF63D56F))),
    SPACE("UZAY", "Galaktik kristaller", listOf("☄", "✦", "☾", "✺", "✹", "✧"), listOf(Color(0xFF5E8BFF), Color(0xFFFFC857), Color(0xFF9C6BFF), Color(0xFFFF5DA2), Color(0xFF4CE1D4), Color(0xFFFF8C5A))),
    ARCADE("ARCADE", "Neon güç taşları", listOf("⚡", "◆", "♥", "☢", "★", "☯"), listOf(Color(0xFF4FD6FF), Color(0xFFFFC14D), Color(0xFFFF4F8B), Color(0xFF9B6CFF), Color(0xFFFFE05A), Color(0xFF55F0A2)))
}

@Composable
fun TriplixV8App() {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("triplix", Context.MODE_PRIVATE) }
    var page by remember { mutableStateOf(V8Page.HOME) }
    var mode by remember { mutableStateOf(V8Mode.CLASSIC) }
    var level by remember { mutableIntStateOf(prefs.getInt("level", 1).coerceIn(1, 60)) }
    var coins by remember { mutableIntStateOf(prefs.getInt("coins", 350)) }
    var best by remember { mutableIntStateOf(prefs.getInt("best", 0)) }
    var streak by remember { mutableIntStateOf(prefs.getInt("streak", 1)) }
    fun save() = prefs.edit().putInt("level", level).putInt("coins", coins).putInt("best", best).putInt("streak", streak).apply()
    fun finish(score: Int) { if (score > best) best = score; coins += 15 + score / 100; if (score >= v8Goal(level)) level = (level + 1).coerceAtMost(60); streak++; save(); page = V8Page.LEVELS }
    BackHandler(enabled = page != V8Page.HOME) { page = when (page) { V8Page.GAME -> V8Page.LEVELS; V8Page.LEVELS -> V8Page.MODES; V8Page.MODES -> V8Page.HOME; else -> V8Page.HOME } }
    MaterialTheme(colorScheme = darkColorScheme(background = V8Bg, surface = V8Panel, primary = V8Green, onSurface = V8Text)) {
        Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(V8Bg, Color(0xFF0B1425), V8Bg)))) {
            when (page) {
                V8Page.HOME -> V8Home(level, coins, best, streak, mode) { page = V8Page.MODES }
                V8Page.MODES -> V8Modes(mode, { page = V8Page.HOME }) { mode = it; page = V8Page.LEVELS }
                V8Page.LEVELS -> V8Levels(level, mode, { page = V8Page.MODES }) { level = it; page = V8Page.GAME }
                V8Page.GAME -> V8Game(level, mode, { page = V8Page.LEVELS }, ::finish) { coins += it; save() }
            }
        }
    }
}

@Composable private fun V8Home(level: Int, coins: Int, best: Int, streak: Int, mode: V8Mode, play: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) { Box(Modifier.size(50.dp).clip(RoundedCornerShape(16.dp)).background(Brush.linearGradient(listOf(V8Gold, Color(0xFF9C6BFF)))), contentAlignment = Alignment.Center) { Text("3", color = Color.White, fontSize = 29.sp, fontWeight = FontWeight.Black) }; Spacer(Modifier.width(11.dp)); Column(Modifier.weight(1f)) { Text("TRIPLIX", color = V8Text, fontSize = 24.sp, fontWeight = FontWeight.Black, letterSpacing = 1.5.sp); Text("3D PUZZLE • KOMBO • MODLAR", color = V8Dim, fontSize = 9.sp, fontWeight = FontWeight.Bold) }; V8Coin(coins) }
        Surface(color = V8Panel2, shape = RoundedCornerShape(30.dp), modifier = Modifier.fillMaxWidth()) { Column(Modifier.padding(22.dp), horizontalAlignment = Alignment.CenterHorizontally) { Text("${mode.title} MODU", color = V8Gold, fontSize = 11.sp, fontWeight = FontWeight.Black); Text(mode.icons.take(3).joinToString("  "), fontSize = 30.sp); Text("TRIPLIX", color = V8Text, fontSize = 38.sp, fontWeight = FontWeight.Black, letterSpacing = 2.sp); Text(mode.subtitle, color = V8Dim, fontSize = 12.sp); Spacer(Modifier.height(16.dp)); Button(onClick = play, modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(18.dp), colors = ButtonDefaults.buttonColors(containerColor = V8Green)) { Text("▶  OYUNA BAŞLA", color = Color(0xFF03150A), fontSize = 16.sp, fontWeight = FontWeight.Black) } } }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) { V8Stat("🔥", "$streak", "SERİ", Modifier.weight(1f)); V8Stat("🏆", "$best", "REKOR", Modifier.weight(1f)); V8Stat("🎯", "$level/60", "BÖLÜM", Modifier.weight(1f)) }
        Surface(color = Color.White.copy(.045f), shape = RoundedCornerShape(20.dp), modifier = Modifier.fillMaxWidth().clickable { play() }) { Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) { Text("💎", fontSize = 26.sp); Spacer(Modifier.width(12.dp)); Column(Modifier.weight(1f)) { Text("MOD SEÇ", color = V8Text, fontWeight = FontWeight.Black); Text("Taşların teması ve ikonları değişsin", color = V8Dim, fontSize = 11.sp) }; Text("›", color = V8Gold, fontSize = 30.sp) } }
    }
}

@Composable private fun V8Modes(current: V8Mode, back: () -> Unit, select: (V8Mode) -> Unit) {
    Column(Modifier.fillMaxSize()) { V8Top("MODLAR", "Her mod farklı taş seti", back); LazyVerticalGrid(columns = GridCells.Fixed(2), contentPadding = PaddingValues(15.dp), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) { items(V8Mode.entries.toList()) { m -> val active = m == current; Surface(color = if (active) Color.White.copy(.08f) else V8Panel, shape = RoundedCornerShape(22.dp), modifier = Modifier.aspectRatio(.9f).clickable { select(m) }.border(if (active) 2.dp else 1.dp, if (active) V8Green else Color.White.copy(.06f), RoundedCornerShape(22.dp))) { Column(Modifier.padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) { Text(m.icons.take(3).joinToString("  "), fontSize = 28.sp); Spacer(Modifier.height(10.dp)); Text(m.title, color = V8Text, fontWeight = FontWeight.Black); Text(m.subtitle, color = V8Dim, fontSize = 9.sp, textAlign = TextAlign.Center); Spacer(Modifier.height(8.dp)); Text(if (active) "SEÇİLİ" else "OYNA", color = if (active) V8Green else V8Gold, fontSize = 9.sp, fontWeight = FontWeight.Black) } } } } }
}

@Composable private fun V8Levels(unlocked: Int, mode: V8Mode, back: () -> Unit, select: (Int) -> Unit) {
    Column(Modifier.fillMaxSize()) { V8Top("BÖLÜMLER", "${mode.title} • İlerlemeni seç", back); LazyVerticalGrid(columns = GridCells.Fixed(4), contentPadding = PaddingValues(15.dp), horizontalArrangement = Arrangement.spacedBy(9.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) { items((1..60).toList()) { n -> val open = n <= unlocked; Box(Modifier.aspectRatio(1f).clip(RoundedCornerShape(17.dp)).background(if (open) V8Panel else Color.White.copy(.025f)).border(1.dp, if (open) mode.colors[n % 6].copy(.55f) else Color.White.copy(.025f), RoundedCornerShape(17.dp)).clickable(enabled = open) { select(n) }, contentAlignment = Alignment.Center) { Column(horizontalAlignment = Alignment.CenterHorizontally) { Text(if (open) "$n" else "🔒", color = if (open) V8Text else V8Dim, fontSize = 17.sp, fontWeight = FontWeight.Black); if (open) Text(mode.icons[n % 6], fontSize = 13.sp) } } } } }
}

@Composable private fun V8Game(level: Int, mode: V8Mode, back: () -> Unit, finish: (Int) -> Unit, coin: (Int) -> Unit) {
    val state = remember(level, mode) { V8GameState(level, mode) }
    var selected by remember(level, mode) { mutableIntStateOf(-1) }
    var status by remember(level, mode) { mutableStateOf("Bir taş seç") }
    var burst by remember(level, mode) { mutableStateOf(-1) }
    var result by remember(level, mode) { mutableStateOf(false) }
    fun tap(i: Int) {
        if (result || state.moves <= 0) return
        if (selected < 0) { selected = i; status = "Komşu taşı seç"; return }
        if (selected == i) { selected = -1; status = "Seçim temizlendi"; return }
        if (!v8Adj(selected, i)) { selected = i; status = "Sadece yanındaki taşı seç"; return }
        val next = state.board.toMutableList(); next[selected] = state.board[i]; next[i] = state.board[selected]; selected = -1
        val hit = v8Matches(next)
        if (hit.isEmpty()) { status = "Eşleşme yok — hamle geri alındı"; return }
        state.board = v8Resolve(next, hit); state.moves--; state.combo++; val gain = hit.size * 25 * state.combo; state.score += gain; burst = hit.size; status = "+$gain  •  KOMBO x${state.combo}"; coin(hit.size)
        if (state.score >= state.goal || state.moves <= 0) result = true
    }
    Column(Modifier.fillMaxSize()) {
        V8Top("${mode.title} • LV.$level", "${state.moves} HAMLE  •  ${state.goal} HEDEF", back)
        Row(Modifier.fillMaxWidth().padding(horizontal = 15.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) { V8Stat("⭐", state.score.toString(), "PUAN", Modifier.weight(1f)); V8Stat("🔥", "x${state.combo}", "KOMBO", Modifier.weight(1f)); V8Stat("🎯", state.goal.toString(), "HEDEF", Modifier.weight(1f)) }
        Spacer(Modifier.height(7.dp)); Text(status, color = if (status.startsWith("+")) V8Green else V8Dim, fontSize = 11.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(7.dp))
        Box(Modifier.fillMaxWidth().padding(horizontal = 10.dp).aspectRatio(1f).clip(RoundedCornerShape(28.dp)).background(Brush.verticalGradient(listOf(Color(0xFF1B2940), Color(0xFF0B1220)))).border(1.dp, mode.colors[state.board.first()].copy(.22f), RoundedCornerShape(28.dp)), contentAlignment = Alignment.Center) {
            Column(Modifier.fillMaxSize().padding(8.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) { for (r in 0 until V8N) Row(Modifier.weight(1f).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(5.dp)) { for (c in 0 until V8N) { val idx = r * V8N + c; V8Gem(state.board[idx], idx == selected, mode, Modifier.weight(1f).fillMaxHeight()) { tap(idx) } } } }
            if (burst > 0) Text("💥  +${burst * 25}", color = V8Gold, fontSize = 25.sp, fontWeight = FontWeight.Black, modifier = Modifier.align(Alignment.Center))
        }
        Spacer(Modifier.height(10.dp)); Row(Modifier.fillMaxWidth().padding(horizontal = 15.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) { V8Tool("↻", "KARIŞTIR", Modifier.weight(1f)) { state.board = v8NewBoard(); selected = -1; status = "Yeni kombinasyon hazır" }; V8Tool("💡", "İPUCU", Modifier.weight(1f)) { val p = v8Hint(state.board); status = if (p >= 0) "İpucu: ${p / 7 + 1}. sıra • ${p % 7 + 1}. sütun" else "Yeni hamle bulamadım" }; V8Tool("↩", "TEMİZLE", Modifier.weight(1f)) { selected = -1; status = "Bir taş seç" } }
    }
    if (result) V8Result(state.score >= state.goal, state.score, state.goal) { finish(state.score) }
}

@Composable private fun V8Gem(type: Int, selected: Boolean, mode: V8Mode, modifier: Modifier, click: () -> Unit) {
    val color = mode.colors[type]
    val s by animateFloatAsState(if (selected) 1.10f else 1f, tween(180), label = "gemScale")
    Box(modifier.scale(s).padding(1.dp).clip(RoundedCornerShape(14.dp)).background(Brush.verticalGradient(listOf(color.copy(.92f), color.copy(.48f), Color(0xFF0B1020)))).border(if (selected) 2.dp else 1.dp, if (selected) Color.White else color.copy(.55f), RoundedCornerShape(14.dp)).clickable(onClick = click), contentAlignment = Alignment.Center) {
        Box(Modifier.fillMaxSize().padding(3.dp).clip(RoundedCornerShape(11.dp)).background(Brush.verticalGradient(listOf(Color.White.copy(.20f), Color.Transparent))))
        Text(mode.icons[type], color = Color.White, fontSize = if (mode == V8Mode.FRUIT) 22.sp else 25.sp, fontWeight = FontWeight.Black)
        Box(Modifier.fillMaxWidth(.58f).height(3.dp).align(Alignment.TopCenter).clip(CircleShape).background(Color.White.copy(.35f)))
        if (selected) Text("✦", color = Color.White, fontSize = 10.sp, modifier = Modifier.align(Alignment.TopEnd).padding(4.dp))
    }
}

@Composable private fun V8Top(title: String, sub: String, back: () -> Unit) { Row(Modifier.fillMaxWidth().padding(horizontal = 15.dp, vertical = 13.dp), verticalAlignment = Alignment.CenterVertically) { Text("‹", color = V8Text, fontSize = 38.sp, modifier = Modifier.clickable { back() }); Spacer(Modifier.width(8.dp)); Column { Text(title, color = V8Text, fontSize = 19.sp, fontWeight = FontWeight.Black); Text(sub, color = V8Dim, fontSize = 10.sp, fontWeight = FontWeight.Bold) } } }
@Composable private fun V8Coin(n: Int) { Surface(color = Color(0xFF1A74FF), shape = RoundedCornerShape(22.dp)) { Text("💎  $n", color = Color.White, fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 13.dp, vertical = 8.dp)) } }
@Composable private fun V8Stat(icon: String, value: String, label: String, modifier: Modifier) { Surface(color = Color.White.copy(.045f), shape = RoundedCornerShape(18.dp), modifier = modifier) { Column(Modifier.padding(vertical = 12.dp), horizontalAlignment = Alignment.CenterHorizontally) { Text(icon, fontSize = 18.sp); Text(value, color = V8Text, fontSize = 15.sp, fontWeight = FontWeight.Black); Text(label, color = V8Dim, fontSize = 8.sp, fontWeight = FontWeight.Bold) } } }
@Composable private fun V8Tool(icon: String, label: String, modifier: Modifier, click: () -> Unit) { Surface(color = V8Panel, shape = RoundedCornerShape(17.dp), modifier = modifier.clickable { click() }) { Column(Modifier.padding(vertical = 10.dp), horizontalAlignment = Alignment.CenterHorizontally) { Text(icon, fontSize = 21.sp); Text(label, color = V8Dim, fontSize = 8.sp, fontWeight = FontWeight.Bold) } } }
@Composable private fun V8Result(win: Boolean, score: Int, goal: Int, cont: () -> Unit) { Box(Modifier.fillMaxSize().background(Color.Black.copy(.76f)), contentAlignment = Alignment.Center) { Surface(color = V8Panel2, shape = RoundedCornerShape(28.dp), modifier = Modifier.fillMaxWidth().padding(28.dp)) { Column(Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) { Text(if (win) "💎✨" else "↻", fontSize = 42.sp); Text(if (win) "BÖLÜM TAMAMLANDI" else "TEKRAR DENE", color = V8Text, fontSize = 18.sp, fontWeight = FontWeight.Black); Text("$score / $goal PUAN", color = if (win) V8Green else V8Gold, fontWeight = FontWeight.Black); Spacer(Modifier.height(16.dp)); Button(onClick = cont, modifier = Modifier.fillMaxWidth().height(52.dp)) { Text("DEVAM ET", fontWeight = FontWeight.Black) } } } } }

private class V8GameState(level: Int, mode: V8Mode) { var board = v8NewBoard(); var moves = (24 - level / 8).coerceAtLeast(12); var combo = 0; var score = 0; val goal = v8Goal(level) }
private fun v8Goal(level: Int) = 500 + level * 90
private fun v8Adj(a: Int, b: Int) = abs(a / V8N - b / V8N) + abs(a % V8N - b % V8N) == 1
private fun v8Matches(b: List<Int>): Set<Int> { val hit = mutableSetOf<Int>(); for (r in 0 until V8N) { var c = 0; while (c < V8N) { var e = c + 1; while (e < V8N && b[r * V8N + e] == b[r * V8N + c]) e++; if (e - c >= 3) for (x in c until e) hit.add(r * V8N + x); c = e } }; for (c in 0 until V8N) { var r = 0; while (r < V8N) { var e = r + 1; while (e < V8N && b[e * V8N + c] == b[r * V8N + c]) e++; if (e - r >= 3) for (x in r until e) hit.add(x * V8N + c); r = e } }; return hit }
private fun v8Resolve(src: List<Int>, hit: Set<Int>): MutableList<Int> { val out = src.toMutableList(); for (i in hit) out[i] = -1; for (c in 0 until V8N) { var write = V8N - 1; for (r in V8N - 1 downTo 0) { val v = out[r * V8N + c]; if (v >= 0) { out[write * V8N + c] = v; write-- } }; while (write >= 0) { out[write * V8N + c] = Random.nextInt(V8TYPES); write-- } }; return out }
private fun v8NewBoard(): MutableList<Int> { repeat(100) { val b = MutableList(V8N * V8N) { Random.nextInt(V8TYPES) }; if (v8Matches(b).isEmpty() && v8Hint(b) >= 0) return b }; return MutableList(V8N * V8N) { Random.nextInt(V8TYPES) }
}
private fun v8Hint(b: List<Int>): Int { for (i in b.indices) for (d in listOf(1, V8N)) { val j = i + d; if (j >= b.size || (d == 1 && i % V8N == V8N - 1)) continue; val n = b.toMutableList(); n[i] = b[j]; n[j] = b[i]; if (v8Matches(n).isNotEmpty()) return i }; return -1 }
