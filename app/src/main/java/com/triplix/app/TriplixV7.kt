package com.triplix.app

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.random.Random

private const val SIZE = 7
private const val TYPES = 6
private val Bg = Color(0xFF060A12)
private val Board = Color(0xFF0D1422)
private val Main = Color(0xFFF8FAFF)
private val Dim = Color(0xFF8995AA)
private val Green = Color(0xFF57E389)
private val Gold = Color(0xFFFFD166)
private val Gems = listOf("●", "◆", "▲", "■", "★", "✦")
private val GemColors = listOf(Color(0xFFFF5270), Color(0xFFFFC857), Color(0xFFA477FF), Color(0xFFFF8452), Color(0xFF42D79A), Color(0xFF4FB9FF))
private enum class Page { HOME, LEVELS, GAME }
private data class Snapshot(val board: List<Int>, val moves: Int, val score: Int, val combo: Int)

@Composable
fun TriplixV7App() {
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
        if (score >= levelGoal(level)) { coins += 20 + score / 100; level = (level + 1).coerceAtMost(60); streak++ }
        save(); page = Page.LEVELS
    }
    BackHandler(enabled = page != Page.HOME) { page = if (page == Page.GAME) Page.LEVELS else Page.HOME }
    MaterialTheme(colorScheme = darkColorScheme(background = Bg, surface = Board, primary = Green, onSurface = Main)) {
        Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Bg, Color(0xFF0A1220), Bg)))) {
            when (page) {
                Page.HOME -> HomeV7(level, coins, best, streak) { page = Page.GAME }
                Page.LEVELS -> LevelsV7(level, { page = Page.HOME }) { level = it; page = Page.GAME }
                Page.GAME -> GameV7(level, { page = Page.LEVELS }, ::finish) { coins += it; save() }
            }
        }
    }
}

@Composable
private fun HomeV7(level: Int, coins: Int, best: Int, streak: Int, play: () -> Unit) {
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(18.dp, 22.dp, 18.dp, 30.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item { Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(50.dp).clip(RoundedCornerShape(16.dp)).background(Brush.linearGradient(listOf(Gold, Color(0xFF8D6BFF)))), contentAlignment = Alignment.Center) { Text("3", color = Color.White, fontSize = 29.sp, fontWeight = FontWeight.Black) }
            Spacer(Modifier.width(11.dp)); Column(Modifier.weight(1f)) { Text("TRIPLIX", color = Main, fontSize = 24.sp, fontWeight = FontWeight.Black, letterSpacing = 1.5.sp); Text("BİRLEŞTİR • PATLAT • KOMBO YAP", color = Dim, fontSize = 9.sp, fontWeight = FontWeight.Bold) }; CoinChipV7(coins)
        } }
        item { Surface(color = Board, shape = RoundedCornerShape(30.dp), modifier = Modifier.fillMaxWidth().border(1.dp, Color.White.copy(.055f), RoundedCornerShape(30.dp))) {
            Column(Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) { Text("SEVİYE $level", color = Gold, fontSize = 12.sp, fontWeight = FontWeight.Black); Text("TRIPLIX", color = Main, fontSize = 41.sp, fontWeight = FontWeight.Black, letterSpacing = 2.sp); Text("Taşları kaydır, eşleştir ve zinciri büyüt.", color = Dim, fontSize = 12.sp, textAlign = TextAlign.Center); Spacer(Modifier.height(18.dp)); Button(onClick = play, modifier = Modifier.fillMaxWidth().height(58.dp), shape = RoundedCornerShape(18.dp), colors = ButtonDefaults.buttonColors(containerColor = Green)) { Text("▶  OYUNA BAŞLA", color = Color(0xFF04150A), fontSize = 17.sp, fontWeight = FontWeight.Black) } }
        } }
        item { Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(9.dp)) { HomeStatV7("🔥", "$streak", "SERİ", Modifier.weight(1f)); HomeStatV7("🏆", "$best", "REKOR", Modifier.weight(1f)); HomeStatV7("🎯", "$level/60", "BÖLÜM", Modifier.weight(1f)) } }
        item { Surface(color = Color.White.copy(.045f), shape = RoundedCornerShape(20.dp), modifier = Modifier.fillMaxWidth()) { Column(Modifier.padding(16.dp)) { Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text("SONRAKİ HEDEF", color = Dim, fontSize = 10.sp, fontWeight = FontWeight.Bold); Text("${levelGoal(level)} PUAN", color = Gold, fontSize = 11.sp, fontWeight = FontWeight.Black) }; Spacer(Modifier.height(10.dp)); LinearProgressIndicator(progress = { 0f }, modifier = Modifier.fillMaxWidth().height(7.dp), color = Green, trackColor = Color.White.copy(.07f)) } } }
        item { Text("60 bölüm • gerçek tahta animasyonları • zincir kombolar", color = Dim, fontSize = 10.sp, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center) }
    }
}

@Composable
private fun LevelsV7(unlocked: Int, back: () -> Unit, select: (Int) -> Unit) {
    Column(Modifier.fillMaxSize()) { TopV7("BÖLÜMLER", "İlerlemeni seç", back); Surface(color = Color.White.copy(.045f), shape = RoundedCornerShape(18.dp), modifier = Modifier.fillMaxWidth().padding(horizontal = 15.dp)) { Column(Modifier.padding(14.dp)) { Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text("İLERLEME", color = Dim, fontSize = 10.sp, fontWeight = FontWeight.Bold); Text("$unlocked / 60", color = Gold, fontSize = 11.sp, fontWeight = FontWeight.Black) }; Spacer(Modifier.height(8.dp)); LinearProgressIndicator(progress = { unlocked / 60f }, modifier = Modifier.fillMaxWidth().height(7.dp), color = Green, trackColor = Color.White.copy(.07f)) } }; LazyVerticalGrid(columns = GridCells.Fixed(4), contentPadding = PaddingValues(15.dp), horizontalArrangement = Arrangement.spacedBy(9.dp), verticalArrangement = Arrangement.spacedBy(9.dp), modifier = Modifier.fillMaxSize()) { items((1..60).toList()) { n -> val open = n <= unlocked; Box(Modifier.aspectRatio(1f).clip(RoundedCornerShape(17.dp)).background(if (open) Board else Color.White.copy(.025f)).border(1.dp, if (open) Color.White.copy(.07f) else Color.White.copy(.025f), RoundedCornerShape(17.dp)).clickable(enabled = open) { select(n) }, contentAlignment = Alignment.Center) { Column(horizontalAlignment = Alignment.CenterHorizontally) { Text(if (open) "$n" else "🔒", color = if (open) Main else Dim, fontSize = 17.sp, fontWeight = FontWeight.Black); if (open) Text("★".repeat((n % 3) + 1), color = Gold, fontSize = 8.sp) } } } } }
}

@Composable
private fun GameV7(level: Int, back: () -> Unit, finish: (Int) -> Unit, coin: (Int) -> Unit) {
    val state = remember(level) { GameV7State(level) }; val scope = rememberCoroutineScope()
    var selected by remember(level) { mutableIntStateOf(-1) }; var status by remember(level) { mutableStateOf("Bir taş seç") }; var locked by remember(level) { mutableStateOf(false) }; var result by remember(level) { mutableStateOf(false) }; var shuffleUsed by remember(level) { mutableStateOf(false) }; var hintCells by remember(level) { mutableStateOf(setOf<Int>()) }; var flashCells by remember(level) { mutableStateOf(setOf<Int>()) }; var boardRevision by remember(level) { mutableIntStateOf(0) }; var burstToken by remember(level) { mutableIntStateOf(0) }; var burstText by remember(level) { mutableStateOf("") }; var undo by remember(level) { mutableStateOf<Snapshot?>(null) }

    fun animateMove(index: Int) {
        if (locked || result || state.moves <= 0) return
        if (selected < 0) { selected = index; status = "Komşu taşı seç"; return }
        if (selected == index) { selected = -1; status = "Seçim temizlendi"; return }
        if (!adj(selected, index)) { selected = index; status = "Sadece yanındaki taşı seç"; return }
        val a = selected; val b = index; val before = Snapshot(state.board.toList(), state.moves, state.score, state.combo); val next = state.board.toMutableList(); next[a] = state.board[b]; next[b] = state.board[a]; selected = -1
        val hit = findMatches(next)
        if (hit.isEmpty()) { status = "Eşleşme yok — hamle geri alındı"; return }
        undo = before; locked = true; state.board = next; boardRevision++; flashCells = hit; burstToken++; burstText = if (state.combo >= 2) "COMBO x${state.combo + 1}" else "PATLAMA!"
        scope.launch { delay(260); val gain = hit.size * 20 * (state.combo + 1); state.score += gain; state.combo++; state.moves--; state.board = resolveBoard(state.board, hit); boardRevision++; flashCells = emptySet(); status = "+$gain  •  x${state.combo} KOMBO"; coin(hit.size); locked = false; if (state.score >= state.goal || state.moves <= 0) result = true }
    }
    fun doHint() { if (locked || result) return; val move = findValidMove(state.board); if (move == null) { status = "Hamle kalmadı — karıştırmayı dene"; return }; hintCells = setOf(move.first, move.second); status = "Parlayan iki taşı eşleştir"; scope.launch { delay(1200); hintCells = emptySet(); if (!result) status = "Bir taş seç" } }
    fun doShuffle() { if (locked || result || shuffleUsed || state.moves <= 0) return; undo = Snapshot(state.board.toList(), state.moves, state.score, state.combo); state.board = newBoard(); state.moves--; shuffleUsed = true; selected = -1; hintCells = emptySet(); boardRevision++; status = "Tahta yenilendi • yeni hamleni bul" }

    Column(Modifier.fillMaxSize()) {
        TopV7("SEVİYE $level", "${state.moves} HAMLE  •  ${state.goal} HEDEF", back)
        Row(Modifier.fillMaxWidth().padding(horizontal = 15.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) { GameStatV7("⭐", state.score.toString(), "PUAN", Modifier.weight(1f)); GameStatV7("🔥", "x${state.combo}", "KOMBO", Modifier.weight(1f)); GameStatV7("🎯", state.goal.toString(), "HEDEF", Modifier.weight(1f)) }
        Spacer(Modifier.height(9.dp)); Text(status, color = if (status.startsWith("+")) Green else Dim, fontSize = 11.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()); Spacer(Modifier.height(8.dp))
        Box(Modifier.fillMaxWidth().padding(horizontal = 12.dp).aspectRatio(1f).clip(RoundedCornerShape(28.dp)).background(Brush.radialGradient(listOf(Color(0xFF17243A), Board)))) {
            Column(Modifier.fillMaxSize().padding(9.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) { for (r in 0 until SIZE) Row(Modifier.weight(1f).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(5.dp)) { for (c in 0 until SIZE) { val i = r * SIZE + c; GemV7(state.board[i], i == selected, i in hintCells, i in flashCells, boardRevision, Modifier.weight(1f).fillMaxSize()) { animateMove(i) } } } }
            AnimatedVisibility(visible = burstToken > 0, enter = fadeIn(), exit = fadeOut(), modifier = Modifier.align(Alignment.Center)) { Text(burstText, color = Gold, fontSize = 27.sp, fontWeight = FontWeight.Black, modifier = Modifier.background(Color.Black.copy(.38f), RoundedCornerShape(20.dp)).padding(horizontal = 18.dp, vertical = 10.dp)) }
        }
        Spacer(Modifier.height(10.dp)); Row(Modifier.fillMaxWidth().padding(horizontal = 15.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ToolV7("↻", "KARIŞTIR", shuffleUsed, Modifier.weight(1f)) { doShuffle() }; ToolV7("💡", "İPUCU", false, Modifier.weight(1f)) { doHint() }; ToolV7("↩", "GERİ AL", undo == null, Modifier.weight(1f)) { val s = undo; if (s != null && !locked && !result) { state.board = s.board.toList(); state.moves = s.moves; state.score = s.score; state.combo = s.combo; undo = null; selected = -1; hintCells = emptySet(); boardRevision++; status = "Son hamle geri alındı" } }
        }
    }
    LaunchedEffect(burstToken) { if (burstToken > 0) { delay(480); burstToken = 0 } }
    if (result) ResultV7(state.score >= state.goal, state.score, state.goal) { finish(state.score) }
}

@Composable
private fun GemV7(type: Int, selected: Boolean, hint: Boolean, flash: Boolean, revision: Int, modifier: Modifier, click: () -> Unit) {
    key(revision * 100 + type * 10 + if (flash) 1 else 0) {
        val color = GemColors[type]; val target = if (selected || hint) 1.10f else 1f; val scale by animateFloatAsState(target, spring(dampingRatio = .48f, stiffness = 520f), label = "gemScale"); val pop by animateFloatAsState(if (flash) 1f else 0f, spring(dampingRatio = .45f, stiffness = 700f), label = "gemPop")
        Box(modifier.scale(scale * if (flash) .82f + .18f * pop else 1f).clip(RoundedCornerShape(14.dp)).background(color.copy(if (selected || hint) .32f else .16f)).border(if (selected || hint) 2.dp else 1.dp, if (selected) Color.White else if (hint) Gold else color.copy(.48f), RoundedCornerShape(14.dp)).clickable(onClick = click), contentAlignment = Alignment.Center) {
            Text(Gems[type], color = Color.White, fontSize = if (selected || hint) 27.sp else 24.sp, fontWeight = FontWeight.Black)
            if (selected || hint) { Text("✦", color = if (hint) Gold else Color.White, fontSize = 10.sp, modifier = Modifier.align(Alignment.TopEnd).padding(4.dp)); Text("✦", color = Color.White.copy(.75f), fontSize = 9.sp, modifier = Modifier.align(Alignment.BottomStart).padding(4.dp)) }
            if (flash) { Text("✦", color = Color.White, fontSize = 17.sp, modifier = Modifier.align(Alignment.TopStart).offset((-3).dp, (-3).dp)); Text("✦", color = Gold, fontSize = 13.sp, modifier = Modifier.align(Alignment.BottomEnd).offset(3.dp, 3.dp)) }
        }
    }
}

@Composable private fun ToolV7(icon: String, label: String, disabled: Boolean, modifier: Modifier, click: () -> Unit) { Surface(color = if (disabled) Color.White.copy(.025f) else Board, shape = RoundedCornerShape(18.dp), modifier = modifier.clickable(enabled = !disabled, onClick = click).border(1.dp, Color.White.copy(if (disabled) .025f else .055f), RoundedCornerShape(18.dp))) { Column(Modifier.padding(vertical = 10.dp), horizontalAlignment = Alignment.CenterHorizontally) { Text(icon, fontSize = 20.sp); Text(label, color = if (disabled) Dim.copy(.45f) else Dim, fontSize = 8.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center) } } }
@Composable private fun ResultV7(win: Boolean, score: Int, goal: Int, cont: () -> Unit) { Box(Modifier.fillMaxSize().background(Color.Black.copy(.76f)), contentAlignment = Alignment.Center) { Surface(color = Color(0xFF151F31), shape = RoundedCornerShape(30.dp), modifier = Modifier.fillMaxWidth().padding(28.dp).border(1.dp, Color.White.copy(.08f), RoundedCornerShape(30.dp))) { Column(Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) { Text(if (win) "✦" else "↻", color = if (win) Green else Gold, fontSize = 54.sp, fontWeight = FontWeight.Black); Text(if (win) "BÖLÜM TAMAMLANDI" else "TEKRAR DENE", color = Main, fontSize = 19.sp, fontWeight = FontWeight.Black); Spacer(Modifier.height(8.dp)); Text("$score / $goal PUAN", color = if (win) Green else Gold, fontSize = 15.sp, fontWeight = FontWeight.Black); Spacer(Modifier.height(17.dp)); Button(onClick = cont, modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.buttonColors(containerColor = Green)) { Text(if (win) "DEVAM ET" else "BÖLÜMLERE DÖN", color = Color(0xFF04150A), fontWeight = FontWeight.Black) } } } } }

private class GameV7State(level: Int) { var board = newBoard(); var moves = levelMoves(level); var score = 0; var combo = 0; val goal = levelGoal(level) }
private fun levelGoal(level: Int) = 520 + level * 80
private fun levelMoves(level: Int) = (24 - level / 12).coerceAtLeast(18)
private fun idx(r: Int, c: Int) = r * SIZE + c
private fun adj(a: Int, b: Int): Boolean { val ar = a / SIZE; val ac = a % SIZE; val br = b / SIZE; val bc = b % SIZE; return abs(ar - br) + abs(ac - bc) == 1 }
private fun findMatches(board: List<Int>): Set<Int> { val hit = mutableSetOf<Int>(); for (r in 0 until SIZE) { var start = 0; while (start < SIZE) { var end = start + 1; while (end < SIZE && board[idx(r, end)] == board[idx(r, start)]) end++; if (end - start >= 3) for (c in start until end) hit += idx(r, c); start = end } }; for (c in 0 until SIZE) { var start = 0; while (start < SIZE) { var end = start + 1; while (end < SIZE && board[idx(end, c)] == board[idx(start, c)]) end++; if (end - start >= 3) for (r in start until end) hit += idx(r, c); start = end } }; return hit }
private fun findValidMove(board: List<Int>): Pair<Int, Int>? { for (i in board.indices) { val r = i / SIZE; val c = i % SIZE; val ns = intArrayOf(if (c < SIZE - 1) i + 1 else -1, if (r < SIZE - 1) i + SIZE else -1); for (n in ns) if (n >= 0) { val b = board.toMutableList(); val t = b[i]; b[i] = b[n]; b[n] = t; if (findMatches(b).isNotEmpty()) return i to n } }; return null }
private fun newBoard(): List<Int> { repeat(500) { val b = List(SIZE * SIZE) { Random.nextInt(TYPES) }; if (findMatches(b).isEmpty() && findValidMove(b) != null) return b }; return List(SIZE * SIZE) { it % TYPES } }
private fun resolveBoard(input: List<Int>, initial: Set<Int>): List<Int> { val b = input.toMutableList(); var hit = initial; repeat(20) { if (hit.isEmpty()) return b; for (i in hit) b[i] = -1; for (c in 0 until SIZE) { val vals = (SIZE - 1 downTo 0).map { r -> b[idx(r, c)] }.filter { it >= 0 }.toMutableList(); var r = SIZE - 1; for (v in vals) { b[idx(r, c)] = v; r-- }; while (r >= 0) { b[idx(r, c)] = Random.nextInt(TYPES); r-- } }; hit = findMatches(b) }; return b }

@Composable private fun TopV7(title: String, sub: String, back: () -> Unit) { Row(Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 14.dp), verticalAlignment = Alignment.CenterVertically) { Text("‹", color = Main, fontSize = 38.sp, modifier = Modifier.clickable(onClick = back)); Spacer(Modifier.width(12.dp)); Column(Modifier.weight(1f)) { Text(title, color = Main, fontSize = 24.sp, fontWeight = FontWeight.Black); Text(sub, color = Dim, fontSize = 11.sp, fontWeight = FontWeight.Bold) } } }
@Composable private fun CoinChipV7(coins: Int) { Surface(color = Color.White.copy(.055f), shape = RoundedCornerShape(18.dp)) { Text("🪙  $coins", color = Gold, fontSize = 12.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp)) } }
@Composable private fun HomeStatV7(icon: String, value: String, label: String, modifier: Modifier) { Surface(color = Color.White.copy(.045f), shape = RoundedCornerShape(18.dp), modifier = modifier) { Column(Modifier.padding(vertical = 13.dp), horizontalAlignment = Alignment.CenterHorizontally) { Text(icon, fontSize = 18.sp); Text(value, color = Main, fontSize = 17.sp, fontWeight = FontWeight.Black); Text(label, color = Dim, fontSize = 8.sp, fontWeight = FontWeight.Bold) } } }
@Composable private fun GameStatV7(icon: String, value: String, label: String, modifier: Modifier) { Surface(color = Board, shape = RoundedCornerShape(17.dp), modifier = modifier) { Column(Modifier.padding(vertical = 11.dp), horizontalAlignment = Alignment.CenterHorizontally) { Text(icon, fontSize = 16.sp); Text(value, color = Main, fontSize = 16.sp, fontWeight = FontWeight.Black); Text(label, color = Dim, fontSize = 8.sp, fontWeight = FontWeight.Bold) } } }
