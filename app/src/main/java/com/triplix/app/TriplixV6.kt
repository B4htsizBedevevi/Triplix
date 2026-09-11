package com.triplix.app

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.abs
import kotlin.random.Random

private const val N = 7
private const val TYPES = 6
private val Bg = Color(0xFF070B13)
private val Panel = Color(0xFF111827)
private val Panel2 = Color(0xFF171F31)
private val Main = Color(0xFFF7F8FC)
private val Dim = Color(0xFF929CAF)
private val Green = Color(0xFF57E389)
private val Gold = Color(0xFFFFD166)
private val Gems = listOf("●", "◆", "▲", "■", "★", "✦")
private val GemColors = listOf(Color(0xFFFF5D73), Color(0xFFFFC857), Color(0xFFA477FF), Color(0xFFFF8A4C), Color(0xFF48D597), Color(0xFF58BFFF))
private enum class Page { HOME, LEVELS, GAME }

@Composable
fun TriplixV6App() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val prefs = remember { context.getSharedPreferences("triplix", Context.MODE_PRIVATE) }
    var page by remember { mutableStateOf(Page.HOME) }
    var level by remember { mutableIntStateOf(prefs.getInt("level", 1).coerceIn(1, 60)) }
    var coins by remember { mutableIntStateOf(prefs.getInt("coins", 350)) }
    var best by remember { mutableIntStateOf(prefs.getInt("best", 0)) }
    var streak by remember { mutableIntStateOf(prefs.getInt("streak", 1)) }

    fun save() = prefs.edit().putInt("level", level).putInt("coins", coins).putInt("best", best).putInt("streak", streak).apply()
    fun finish(score: Int) {
        if (score > best) best = score
        coins += 20 + score / 100
        if (score >= goal(level)) level = (level + 1).coerceAtMost(60)
        streak++
        save(); page = Page.LEVELS
    }
    BackHandler(enabled = page != Page.HOME) { page = if (page == Page.GAME) Page.LEVELS else Page.HOME }

    MaterialTheme(colorScheme = darkColorScheme(background = Bg, surface = Panel, primary = Green, onPrimary = Color(0xFF04200E), onSurface = Main)) {
        Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Bg, Color(0xFF0C1320), Bg)))) {
            when (page) {
                Page.HOME -> HomeV6(level, coins, best, streak) { page = Page.GAME }
                Page.LEVELS -> LevelsV6(level, { page = Page.HOME }) { level = it; page = Page.GAME }
                Page.GAME -> GameV6(level, { page = Page.LEVELS }, ::finish) { coins += it; save() }
            }
        }
    }
}

@Composable
private fun HomeV6(level: Int, coins: Int, best: Int, streak: Int, play: () -> Unit) {
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(18.dp, 20.dp, 18.dp, 28.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(50.dp).clip(RoundedCornerShape(16.dp)).background(Brush.linearGradient(listOf(Gold, Color(0xFFB47CFF)))), contentAlignment = Alignment.Center) { Text("3", color = Color.White, fontSize = 29.sp, fontWeight = FontWeight.Black) }
                Spacer(Modifier.width(11.dp))
                Column(Modifier.weight(1f)) { Text("TRIPLIX", color = Main, fontSize = 24.sp, fontWeight = FontWeight.Black, letterSpacing = 1.5.sp); Text("BİRLEŞTİR • KOMBO YAP • YÜKSEL", color = Dim, fontSize = 9.sp, fontWeight = FontWeight.Bold) }
                CoinChip(coins)
            }
        }
        item {
            Surface(color = Panel2, shape = RoundedCornerShape(30.dp), modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("SEVİYE $level", color = Gold, fontSize = 12.sp, fontWeight = FontWeight.Black)
                    Text("TRIPLIX", color = Main, fontSize = 41.sp, fontWeight = FontWeight.Black, letterSpacing = 2.sp)
                    Spacer(Modifier.height(6.dp))
                    Text("Aynı taşları birleştir, kombonu büyüt ve hedefe ulaş.", color = Dim, fontSize = 12.sp, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(18.dp))
                    Button(onClick = play, modifier = Modifier.fillMaxWidth().height(58.dp), shape = RoundedCornerShape(18.dp), colors = ButtonDefaults.buttonColors(containerColor = Green)) { Text("▶  OYUNA BAŞLA", color = Color(0xFF04150A), fontSize = 17.sp, fontWeight = FontWeight.Black) }
                }
            }
        }
        item { Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(9.dp)) { HomeStatV6("🔥", "$streak", "SERİ", Modifier.weight(1f)); HomeStatV6("🏆", "$best", "REKOR", Modifier.weight(1f)); HomeStatV6("🎯", "$level/60", "BÖLÜM", Modifier.weight(1f)) } }
        item {
            Surface(color = Color.White.copy(.045f), shape = RoundedCornerShape(20.dp), modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text("SONRAKİ HEDEF", color = Dim, fontSize = 10.sp, fontWeight = FontWeight.Bold); Text("${goal(level)} PUAN", color = Gold, fontSize = 11.sp, fontWeight = FontWeight.Black) }
                    Spacer(Modifier.height(10.dp)); LinearProgressIndicator(progress = { 0f }, modifier = Modifier.fillMaxWidth().height(7.dp), color = Green, trackColor = Color.White.copy(.07f))
                }
            }
        }
        item { Text("60 bölüm • giderek zorlaşan hedefler • yüksek skor için kombo", color = Dim, fontSize = 10.sp, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center) }
    }
}

@Composable
private fun LevelsV6(unlocked: Int, back: () -> Unit, select: (Int) -> Unit) {
    Column(Modifier.fillMaxSize()) {
        TopV6("BÖLÜMLER", "İlerlemeni seç", back)
        Surface(color = Color.White.copy(.045f), shape = RoundedCornerShape(18.dp), modifier = Modifier.fillMaxWidth().padding(horizontal = 15.dp)) {
            Column(Modifier.padding(14.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text("İLERLEME", color = Dim, fontSize = 10.sp, fontWeight = FontWeight.Bold); Text("$unlocked / 60", color = Gold, fontSize = 11.sp, fontWeight = FontWeight.Black) }
                Spacer(Modifier.height(8.dp)); LinearProgressIndicator(progress = { unlocked / 60f }, modifier = Modifier.fillMaxWidth().height(7.dp), color = Green, trackColor = Color.White.copy(.07f))
            }
        }
        LazyVerticalGrid(columns = GridCells.Fixed(4), contentPadding = PaddingValues(15.dp), horizontalArrangement = Arrangement.spacedBy(9.dp), verticalArrangement = Arrangement.spacedBy(9.dp), modifier = Modifier.fillMaxSize()) {
            items((1..60).toList()) { n ->
                val open = n <= unlocked
                Box(Modifier.aspectRatio(1f).clip(RoundedCornerShape(17.dp)).background(if (open) Panel else Color.White.copy(.025f)).border(1.dp, if (open) Color.White.copy(.07f) else Color.White.copy(.025f), RoundedCornerShape(17.dp)).clickable(enabled = open) { select(n) }, contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) { Text(if (open) "$n" else "🔒", color = if (open) Main else Dim, fontSize = 17.sp, fontWeight = FontWeight.Black); if (open) Text("★".repeat((n % 3) + 1), color = Gold, fontSize = 8.sp) }
                }
            }
        }
    }
}

@Composable
private fun GameV6(level: Int, back: () -> Unit, finish: (Int) -> Unit, coin: (Int) -> Unit) {
    val state = remember(level) { GameState(level) }
    var selected by remember(level) { mutableIntStateOf(-1) }
    var status by remember(level) { mutableStateOf("Bir taş seç") }
    var shuffleUsed by remember(level) { mutableStateOf(false) }
    var hintUsed by remember(level) { mutableStateOf(false) }
    var result by remember(level) { mutableStateOf(false) }

    fun tap(index: Int) {
        if (result || state.moves <= 0) return
        if (selected < 0) { selected = index; status = "Şimdi komşu taşı seç"; return }
        if (selected == index) { selected = -1; status = "Seçim iptal edildi"; return }
        if (!adj(selected, index)) { selected = index; status = "Sadece yanındaki taşı seç"; return }
        val next = state.board.toMutableList(); next[selected] = state.board[index]; next[index] = state.board[selected]
        val matches = matches(next)
        selected = -1
        if (matches.isEmpty()) { status = "Eşleşme yok — hamle geri alındı"; return }
        state.board = resolve(next, matches)
        state.moves--; state.combo++; val gain = matches.size * 20 * state.combo; state.score += gain
        status = "+$gain  •  x${state.combo} KOMBO"; coin(matches.size)
        if (state.score >= state.goal || state.moves <= 0) result = true
    }

    Column(Modifier.fillMaxSize()) {
        TopV6("SEVİYE $level", "${state.moves} HAMLE  •  ${state.goal} HEDEF", back)
        Row(Modifier.fillMaxWidth().padding(horizontal = 15.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) { GameStatV6("⭐", state.score.toString(), "PUAN", Modifier.weight(1f)); GameStatV6("🔥", "x${state.combo}", "KOMBO", Modifier.weight(1f)); GameStatV6("🎯", state.goal.toString(), "HEDEF", Modifier.weight(1f)) }
        Spacer(Modifier.height(9.dp))
        Text(status, color = if (status.startsWith("+")) Green else Dim, fontSize = 11.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        Surface(color = Color.White.copy(.035f), shape = RoundedCornerShape(25.dp), modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp).aspectRatio(1f)) {
            Column(Modifier.fillMaxSize().padding(9.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                for (r in 0 until N) Row(Modifier.weight(1f).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    for (c in 0 until N) { val i = r * N + c; GemV6(state.board[i], i == selected, Modifier.weight(1f).fillMaxSize()) { tap(i) } }
                }
            }
        }
        Spacer(Modifier.height(10.dp))
        Row(Modifier.fillMaxWidth().padding(horizontal = 15.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ToolV6("↻", "KARIŞTIR", shuffleUsed, Modifier.weight(1f)) { if (!shuffleUsed && state.moves > 0) { state.board = newBoard(); state.moves--; shuffleUsed = true; selected = -1; status = "Tahta karıştırıldı" } }
            ToolV6("💡", "İPUCU", hintUsed, Modifier.weight(1f)) { if (!hintUsed && state.moves > 0) { hintUsed = true; status = "İpucu: eşleşme oluşturabilecek komşuları ara" } }
            ToolV6("↩", "SEÇİMİ TEMİZLE", false, Modifier.weight(1f)) { selected = -1; status = "Bir taş seç" }
        }
    }
    if (result) ResultV6(state.score >= state.goal, state.score, state.goal) { finish(state.score) }
}

@Composable private fun GemV6(type: Int, selected: Boolean, modifier: Modifier, click: () -> Unit) { val color = GemColors[type]; Box(modifier.clip(RoundedCornerShape(13.dp)).background(color.copy(if (selected) .35f else .18f)).border(if (selected) 2.dp else 1.dp, if (selected) Color.White else color.copy(.43f), RoundedCornerShape(13.dp)).clickable(onClick = click), contentAlignment = Alignment.Center) { Text(Gems[type], color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Black); if (selected) Text("✦", color = Color.White, fontSize = 9.sp, modifier = Modifier.align(Alignment.TopEnd).padding(4.dp)) } }
@Composable private fun ToolV6(icon: String, label: String, used: Boolean, modifier: Modifier, click: () -> Unit) { Surface(color = if (used) Color.White.copy(.025f) else Panel, shape = RoundedCornerShape(17.dp), modifier = modifier.clickable(enabled = !used, onClick = click)) { Column(Modifier.padding(vertical = 10.dp), horizontalAlignment = Alignment.CenterHorizontally) { Text(icon, fontSize = 20.sp); Text(if (used) "KULLANILDI" else label, color = if (used) Dim.copy(.55f) else Dim, fontSize = 8.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center) } } }
@Composable private fun ResultV6(win: Boolean, score: Int, goal: Int, cont: () -> Unit) { Box(Modifier.fillMaxSize().background(Color.Black.copy(.74f)), contentAlignment = Alignment.Center) { Surface(color = Panel2, shape = RoundedCornerShape(28.dp), modifier = Modifier.fillMaxWidth().padding(28.dp)) { Column(Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) { Text(if (win) "✦" else "↻", color = if (win) Green else Gold, fontSize = 50.sp, fontWeight = FontWeight.Black); Text(if (win) "BÖLÜM TAMAMLANDI" else "TEKRAR DENE", color = Main, fontSize = 19.sp, fontWeight = FontWeight.Black); Spacer(Modifier.height(8.dp)); Text("$score / $goal PUAN", color = if (win) Green else Gold, fontSize = 15.sp, fontWeight = FontWeight.Black); Spacer(Modifier.height(17.dp)); Button(onClick = cont, modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.buttonColors(containerColor = if (win) Green else Gold)) { Text("BÖLÜMLERE DÖN", color = Color.Black, fontWeight = FontWeight.Black) } } } } }
@Composable private fun TopV6(title: String, sub: String, back: () -> Unit) { Row(Modifier.fillMaxWidth().padding(horizontal = 15.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) { Box(Modifier.size(42.dp).clip(CircleShape).background(Color.White.copy(.055f)).clickable(onClick = back), contentAlignment = Alignment.Center) { Text("‹", color = Main, fontSize = 30.sp) }; Column(Modifier.weight(1f).padding(horizontal = 12.dp), horizontalAlignment = Alignment.CenterHorizontally) { Text(title, color = Main, fontSize = 18.sp, fontWeight = FontWeight.Black); Text(sub, color = Dim, fontSize = 9.sp, fontWeight = FontWeight.Bold) }; Box(Modifier.size(42.dp).clip(CircleShape).background(Color.White.copy(.055f)), contentAlignment = Alignment.Center) { Text("3", color = Gold, fontWeight = FontWeight.Black) } } }
@Composable private fun CoinChip(coins: Int) { Surface(color = Color.White.copy(.055f), shape = RoundedCornerShape(15.dp)) { Text("🪙 $coins", color = Gold, fontSize = 12.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 11.dp, vertical = 8.dp)) } }
@Composable private fun HomeStatV6(icon: String, value: String, label: String, modifier: Modifier) { Surface(color = Color.White.copy(.045f), shape = RoundedCornerShape(18.dp), modifier = modifier) { Column(Modifier.padding(vertical = 13.dp), horizontalAlignment = Alignment.CenterHorizontally) { Text(icon, fontSize = 17.sp); Text(value, color = Main, fontSize = 15.sp, fontWeight = FontWeight.Black); Text(label, color = Dim, fontSize = 8.sp, fontWeight = FontWeight.Bold) } } }
@Composable private fun GameStatV6(icon: String, value: String, label: String, modifier: Modifier) { Surface(color = Color.White.copy(.045f), shape = RoundedCornerShape(15.dp), modifier = modifier) { Column(Modifier.padding(vertical = 8.dp), horizontalAlignment = Alignment.CenterHorizontally) { Text(icon, fontSize = 13.sp); Text(value, color = Main, fontSize = 13.sp, fontWeight = FontWeight.Black); Text(label, color = Dim, fontSize = 7.sp, fontWeight = FontWeight.Bold) } } }

private class GameState(level: Int) { var board = newBoard(); var score = 0; var combo = 0; var moves = (24 - level / 12).coerceAtLeast(18); val goal = goal(level) }
private fun goal(level: Int) = 600 + (level - 1) * 80
private fun adj(a: Int, b: Int): Boolean { val ar = a / N; val ac = a % N; val br = b / N; val bc = b % N; return abs(ar - br) + abs(ac - bc) == 1 }
private fun newBoard(): List<Int> { repeat(250) { val b = MutableList(N * N) { 0 }; for (i in b.indices) { var v: Int; do { v = Random.nextInt(TYPES) } while ((i % N >= 2 && b[i - 1] == v && b[i - 2] == v) || (i >= N * 2 && b[i - N] == v && b[i - N * 2] == v)); b[i] = v }; if (hasMove(b)) return b }; return MutableList(N * N) { Random.nextInt(TYPES) } }
private fun matches(b: List<Int>): Set<Int> { val out = mutableSetOf<Int>(); for (r in 0 until N) { var s = 0; while (s < N) { val v = b[r * N + s]; var e = s + 1; while (e < N && b[r * N + e] == v) e++; if (e - s >= 3) for (c in s until e) out += r * N + c; s = e } }; for (c in 0 until N) { var s = 0; while (s < N) { val v = b[s * N + c]; var e = s + 1; while (e < N && b[e * N + c] == v) e++; if (e - s >= 3) for (r in s until e) out += r * N + c; s = e } }; return out }
private fun resolve(input: List<Int>, first: Set<Int>): List<Int> { var b = input; var m = first; repeat(20) { if (m.isEmpty()) return b; val x = b.toMutableList(); m.forEach { x[it] = -1 }; for (c in 0 until N) { val keep = (N - 1 downTo 0).mapNotNull { r -> x[r * N + c].takeIf { it >= 0 } }; var w = N - 1; keep.forEach { x[w * N + c] = it; w-- }; while (w >= 0) { x[w * N + c] = Random.nextInt(TYPES); w-- } }; b = x; m = matches(b) }; return b }
private fun hasMove(b: List<Int>): Boolean { for (i in b.indices) for (j in listOf(i + 1, i + N)) if (j in b.indices && adj(i, j)) { val x = b.toMutableList(); x[i] = b[j]; x[j] = b[i]; if (matches(x).isNotEmpty()) return true }; return false }
