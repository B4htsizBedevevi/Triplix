package com.triplix.app

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import kotlin.random.Random

private const val BOARD = 7
private const val GEM_COUNT = 6

private val Bg = Color(0xFF070B13)
private val Panel = Color(0xFF111725)
private val Panel2 = Color(0xFF171F31)
private val TextMain = Color(0xFFF7F8FC)
private val TextDim = Color(0xFF8F99AD)
private val Green = Color(0xFF57E389)
private val Gold = Color(0xFFFFD166)
private val Red = Color(0xFFFF5D73)
private val Blue = Color(0xFF58BFFF)
private val Purple = Color(0xFFA477FF)
private val Gems = listOf("●", "◆", "▲", "■", "★", "✦")
private val GemColors = listOf(
    Color(0xFFFF5D73), Color(0xFFFFC857), Color(0xFFA477FF),
    Color(0xFFFF8A4C), Color(0xFF48D597), Color(0xFF58BFFF)
)

private enum class V5Screen { HOME, LEVELS, GAME }

class TriplixV5ActivityState

@Composable
fun TriplixV5App() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val prefs = remember { context.getSharedPreferences("triplix", Context.MODE_PRIVATE) }
    var screen by remember { mutableStateOf(V5Screen.HOME) }
    var level by remember { mutableIntStateOf(prefs.getInt("level", 1).coerceIn(1, 60)) }
    var coins by remember { mutableIntStateOf(prefs.getInt("coins", 350)) }
    var best by remember { mutableIntStateOf(prefs.getInt("best", 0)) }
    var streak by remember { mutableIntStateOf(prefs.getInt("streak", 1)) }

    fun save() {
        prefs.edit().putInt("level", level).putInt("coins", coins)
            .putInt("best", best).putInt("streak", streak).apply()
    }

    fun finish(score: Int) {
        coins += 20 + score / 100
        if (score > best) best = score
        if (score >= goalFor(level)) level = (level + 1).coerceAtMost(60)
        streak += 1
        save()
        screen = V5Screen.LEVELS
    }

    BackHandler(enabled = screen != V5Screen.HOME) {
        screen = if (screen == V5Screen.GAME) V5Screen.LEVELS else V5Screen.HOME
    }

    MaterialTheme(colorScheme = darkColorScheme(background = Bg, surface = Panel, primary = Green, onPrimary = Color(0xFF04200E), onSurface = TextMain)) {
        Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Bg, Color(0xFF0D1420), Bg)))) {
            when (screen) {
                V5Screen.HOME -> V5Home(level, coins, best, streak) { screen = V5Screen.LEVELS }
                V5Screen.LEVELS -> V5Levels(level, { screen = V5Screen.HOME }) { selected -> level = selected; screen = V5Screen.GAME }
                V5Screen.GAME -> V5Game(level, { screen = V5Screen.LEVELS }, ::finish) { amount -> coins += amount; save() }
            }
        }
    }
}

@Composable
private fun V5Home(level: Int, coins: Int, best: Int, streak: Int, onPlay: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(48.dp).clip(RoundedCornerShape(15.dp)).background(Brush.linearGradient(listOf(Gold, Purple))), contentAlignment = Alignment.Center) {
                Text("3", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Black)
            }
            Spacer(Modifier.width(11.dp))
            Column(Modifier.weight(1f)) {
                Text("TRIPLIX", color = TextMain, fontSize = 24.sp, fontWeight = FontWeight.Black, letterSpacing = 1.5.sp)
                Text("BİRLEŞTİR • KOMBO YAP • YÜKSEL", color = TextDim, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            }
            Chip("$coins", Gold)
        }
        Spacer(Modifier.height(8.dp))
        Surface(color = Panel2, shape = RoundedCornerShape(30.dp), modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("SEVİYE $level", color = Gold, fontSize = 12.sp, fontWeight = FontWeight.Black)
                Text("TRIPLIX", color = TextMain, fontSize = 40.sp, fontWeight = FontWeight.Black, letterSpacing = 2.sp)
                Spacer(Modifier.height(7.dp))
                Text("Aynı taşları birleştir, kombonu büyüt ve hedef puana ulaş.", color = TextDim, fontSize = 12.sp, textAlign = TextAlign.Center)
                Spacer(Modifier.height(18.dp))
                Button(onClick = onPlay, modifier = Modifier.fillMaxWidth().height(58.dp), shape = RoundedCornerShape(18.dp), colors = ButtonDefaults.buttonColors(containerColor = Green)) {
                    Text("▶  OYUNA BAŞLA", fontSize = 17.sp, fontWeight = FontWeight.Black)
                }
            }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(9.dp)) {
            HomeStat("🔥", "$streak", "SERİ", Modifier.weight(1f))
            HomeStat("🏆", "$best", "REKOR", Modifier.weight(1f))
            HomeStat("🎯", "$level/60", "BÖLÜM", Modifier.weight(1f))
        }
        Surface(color = Color.White.copy(.045f), shape = RoundedCornerShape(20.dp), modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("SONRAKİ HEDEF", color = TextDim, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text("${goalFor(level)} PUAN", color = Gold, fontSize = 11.sp, fontWeight = FontWeight.Black)
                }
                Spacer(Modifier.height(9.dp))
                LinearProgressIndicator(progress = { 0f }, modifier = Modifier.fillMaxWidth().height(7.dp), color = Green, trackColor = Color.White.copy(.07f))
            }
        }
        Text("Her bölüm yeni hedef ve daha hızlı oyun getirir.", color = TextDim, fontSize = 11.sp, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
    }
}

@Composable
private fun V5Levels(unlocked: Int, onBack: () -> Unit, onLevel: (Int) -> Unit) {
    Column(Modifier.fillMaxSize()) {
        TopBar("BÖLÜMLER", "İlerlemeni seç", onBack)
        Surface(color = Color.White.copy(.045f), shape = RoundedCornerShape(18.dp), modifier = Modifier.fillMaxWidth().padding(horizontal = 15.dp)) {
            Column(Modifier.padding(14.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("İLERLEME", color = TextDim, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text("$unlocked / 60", color = Gold, fontSize = 11.sp, fontWeight = FontWeight.Black)
                }
                Spacer(Modifier.height(8.dp))
                LinearProgressIndicator(progress = { unlocked / 60f }, modifier = Modifier.fillMaxWidth().height(7.dp), color = Green, trackColor = Color.White.copy(.07f))
            }
        }
        Column(Modifier.fillMaxSize().padding(15.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
            for (row in 0 until 15) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                    for (col in 0 until 4) {
                        val n = row * 4 + col + 1
                        LevelTile(n, n <= unlocked, Modifier.weight(1f)) { onLevel(n) }
                    }
                }
            }
        }
    }
}

@Composable
private fun LevelTile(n: Int, open: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Box(modifier.aspectRatio(1f).clip(RoundedCornerShape(16.dp)).background(if (open) Panel else Color.White.copy(.025f)).border(1.dp, if (open) Color.White.copy(.07f) else Color.White.copy(.025f), RoundedCornerShape(16.dp)).clickable(enabled = open, onClick = onClick), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(if (open) "$n" else "🔒", color = if (open) TextMain else TextDim, fontSize = 16.sp, fontWeight = FontWeight.Black)
            if (open) Text("★".repeat((n % 3) + 1), color = Gold, fontSize = 8.sp)
        }
    }
}

@Composable
private fun V5Game(level: Int, onBack: () -> Unit, onFinish: (Int) -> Unit, onCoin: (Int) -> Unit) {
    val state = remember(level) { Match3State(level) }
    var selected by remember(level) { mutableIntStateOf(-1) }
    var message by remember(level) { mutableStateOf("Komşu bir taş seç") }
    var shuffleUsed by remember(level) { mutableStateOf(false) }
    var hintUsed by remember(level) { mutableStateOf(false) }
    var showResult by remember(level) { mutableStateOf(false) }

    fun tap(index: Int) {
        if (showResult || state.moves <= 0) return
        if (selected == -1) { selected = index; message = "Şimdi komşu taşı seç"; return }
        if (selected == index) { selected = -1; message = "Seçim iptal edildi"; return }
        if (!adjacent(selected, index)) { selected = index; message = "Sadece yanındaki taşı seç"; return }

        val a = selected
        val b = index
        val swapped = state.board.toMutableList().also { it[a] = state.board[b]; it[b] = state.board[a] }
        val matches = findMatches(swapped)
        selected = -1

        if (matches.isEmpty()) {
            message = "Eşleşme yok — hamle geri alındı"
            return
        }

        state.board = resolveBoard(swapped, matches)
        state.moves -= 1
        state.combo += 1
        val gained = matches.size * 20 * state.combo
        state.score += gained
        message = "+$gained  •  ${matches.size} taş  •  x${state.combo} KOMBO"
        onCoin(matches.size)
        if (state.score >= state.goal || state.moves <= 0) showResult = true
    }

    Column(Modifier.fillMaxSize()) {
        TopBar("SEVİYE $level", "${state.moves} HAMLE  •  ${state.goal} HEDEF", onBack)
        Row(Modifier.fillMaxWidth().padding(horizontal = 15.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            GameStat("⭐", state.score.toString(), "PUAN", Modifier.weight(1f))
            GameStat("🔥", "x${state.combo}", "KOMBO", Modifier.weight(1f))
            GameStat("🎯", state.goal.toString(), "HEDEF", Modifier.weight(1f))
        }
        Spacer(Modifier.height(10.dp))
        Text(message, color = if (message.startsWith("+")) Green else TextDim, fontSize = 11.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(horizontal = 15.dp))
        Spacer(Modifier.height(9.dp))
        Surface(color = Color.White.copy(.035f), shape = RoundedCornerShape(25.dp), modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp).aspectRatio(1f)) {
            Column(Modifier.fillMaxSize().padding(9.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                for (r in 0 until BOARD) {
                    Row(Modifier.weight(1f).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                        for (c in 0 until BOARD) {
                            val index = r * BOARD + c
                            GemTile(state.board[index], index == selected, Modifier.weight(1f).fillMaxSize()) { tap(index) }
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(10.dp))
        Row(Modifier.fillMaxWidth().padding(horizontal = 15.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            GameButton("↻", "KARIŞTIR", shuffleUsed, Modifier.weight(1f)) {
                if (!shuffleUsed && state.moves > 0) { state.board = newBoard(); state.moves -= 1; shuffleUsed = true; selected = -1; message = "Tahta karıştırıldı" }
            }
            GameButton("💡", "İPUCU", hintUsed, Modifier.weight(1f)) {
                if (!hintUsed && state.moves > 0) { hintUsed = true; message = "İpucu: aynı renkteki komşuları ara" }
            }
            GameButton("↩", "GERİ AL", false, Modifier.weight(1f)) { selected = -1; message = "Bir taş seçerek devam et" }
        }
        Spacer(Modifier.height(6.dp))
    }

    if (showResult) ResultOverlay(state.score >= state.goal, state.score, state.goal) { onFinish(state.score) }
}

@Composable
private fun GemTile(type: Int, selected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    val color = GemColors[type]
    Box(modifier.clip(RoundedCornerShape(13.dp)).background(color.copy(if (selected) .34f else .18f)).border(if (selected) 2.dp else 1.dp, if (selected) Color.White else color.copy(.42f), RoundedCornerShape(13.dp)).clickable(onClick = onClick), contentAlignment = Alignment.Center) {
        Text(Gems[type], color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Black)
        if (selected) Text("✦", color = Color.White, fontSize = 9.sp, modifier = Modifier.align(Alignment.TopEnd).padding(4.dp))
    }
}

@Composable
private fun GameButton(icon: String, label: String, used: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Surface(color = if (used) Color.White.copy(.025f) else Panel, shape = RoundedCornerShape(17.dp), modifier = modifier.clickable(enabled = !used, onClick = onClick)) {
        Column(Modifier.padding(vertical = 10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(icon, fontSize = 20.sp)
            Text(if (used) "KULLANILDI" else label, color = if (used) TextDim.copy(.55f) else TextDim, fontSize = 9.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ResultOverlay(win: Boolean, score: Int, goal: Int, onContinue: () -> Unit) {
    Box(Modifier.fillMaxSize().background(Color.Black.copy(.72f)), contentAlignment = Alignment.Center) {
        Surface(color = Panel2, shape = RoundedCornerShape(28.dp), modifier = Modifier.fillMaxWidth().padding(28.dp)) {
            Column(Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(if (win) "🎉" else "💪", fontSize = 50.sp)
                Text(if (win) "BÖLÜM TAMAMLANDI" else "TEKRAR DENE", color = TextMain, fontSize = 19.sp, fontWeight = FontWeight.Black)
                Spacer(Modifier.height(8.dp))
                Text("$score / $goal PUAN", color = if (win) Green else Gold, fontSize = 15.sp, fontWeight = FontWeight.Black)
                Spacer(Modifier.height(17.dp))
                Button(onClick = onContinue, modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.buttonColors(containerColor = if (win) Green else Gold)) {
                    Text(if (win) "DEVAM ET" else "BÖLÜMLERE DÖN", color = Color.Black, fontWeight = FontWeight.Black)
                }
            }
        }
    }
}

@Composable
private fun TopBar(title: String, subtitle: String, onBack: () -> Unit) {
    Row(Modifier.fillMaxWidth().padding(horizontal = 15.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(42.dp).clip(CircleShape).background(Color.White.copy(.055f)).clickable(onClick = onBack), contentAlignment = Alignment.Center) { Text("‹", color = TextMain, fontSize = 30.sp) }
        Column(Modifier.weight(1f).padding(horizontal = 12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(title, color = TextMain, fontSize = 18.sp, fontWeight = FontWeight.Black)
            Text(subtitle, color = TextDim, fontSize = 9.sp, fontWeight = FontWeight.Bold)
        }
        Box(Modifier.size(42.dp).clip(CircleShape).background(Color.White.copy(.055f)), contentAlignment = Alignment.Center) { Text("3", color = Gold, fontWeight = FontWeight.Black) }
    }
}

@Composable
private fun Chip(value: String, color: Color) {
    Surface(color = Color.White.copy(.055f), shape = RoundedCornerShape(15.dp)) { Text("🪙 $value", color = color, fontSize = 12.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 11.dp, vertical = 8.dp)) }
}

@Composable
private fun HomeStat(icon: String, value: String, label: String, modifier: Modifier) {
    Surface(color = Color.White.copy(.045f), shape = RoundedCornerShape(18.dp), modifier = modifier) { Column(Modifier.padding(vertical = 13.dp), horizontalAlignment = Alignment.CenterHorizontally) { Text(icon, fontSize = 17.sp); Text(value, color = TextMain, fontSize = 15.sp, fontWeight = FontWeight.Black); Text(label, color = TextDim, fontSize = 8.sp, fontWeight = FontWeight.Bold) } }
}

@Composable
private fun GameStat(icon: String, value: String, label: String, modifier: Modifier) {
    Surface(color = Color.White.copy(.045f), shape = RoundedCornerShape(15.dp), modifier = modifier) { Column(Modifier.padding(vertical = 8.dp), horizontalAlignment = Alignment.CenterHorizontally) { Text(icon, fontSize = 13.sp); Text(value, color = TextMain, fontSize = 13.sp, fontWeight = FontWeight.Black); Text(label, color = TextDim, fontSize = 7.sp, fontWeight = FontWeight.Bold) } }
}

private class Match3State(level: Int) {
    var board: List<Int> = newBoard()
    var score: Int = 0
    var moves: Int = movesFor(level)
    var combo: Int = 0
    val goal: Int = goalFor(level)
}

private fun goalFor(level: Int): Int = 600 + (level - 1) * 80
private fun movesFor(level: Int): Int = (24 - (level / 12)).coerceAtLeast(18)
private fun adjacent(a: Int, b: Int): Boolean {
    val ar = a / BOARD; val ac = a % BOARD
    val br = b / BOARD; val bc = b % BOARD
    return kotlin.math.abs(ar - br) + kotlin.math.abs(ac - bc) == 1
}

private fun newBoard(): List<Int> {
    repeat(200) {
        val board = MutableList(BOARD * BOARD) { 0 }
        for (i in board.indices) {
            var value: Int
            do {
                value = Random.nextInt(GEM_COUNT)
            } while ((i % BOARD >= 2 && board[i - 1] == value && board[i - 2] == value) ||
                (i >= BOARD * 2 && board[i - BOARD] == value && board[i - BOARD * 2] == value))
            board[i] = value
        }
        if (hasValidMove(board)) return board
    }
    return MutableList(BOARD * BOARD) { Random.nextInt(GEM_COUNT) }
}

private fun findMatches(board: List<Int>): Set<Int> {
    val result = mutableSetOf<Int>()
    for (r in 0 until BOARD) {
        var start = 0
        while (start < BOARD) {
            val value = board[r * BOARD + start]
            var end = start + 1
            while (end < BOARD && board[r * BOARD + end] == value) end++
            if (end - start >= 3) for (c in start until end) result += r * BOARD + c
            start = end
        }
    }
    for (c in 0 until BOARD) {
        var start = 0
        while (start < BOARD) {
            val value = board[start * BOARD + c]
            var end = start + 1
            while (end < BOARD && board[end * BOARD + c] == value) end++
            if (end - start >= 3) for (r in start until end) result += r * BOARD + c
            start = end
        }
    }
    return result
}

private fun resolveBoard(input: List<Int>, firstMatches: Set<Int>): List<Int> {
    var board = input
    var matches = firstMatches
    repeat(20) {
        if (matches.isEmpty()) return board
        val next = board.toMutableList()
        for (i in matches) next[i] = -1
        for (c in 0 until BOARD) {
            val survivors = (BOARD - 1 downTo 0).mapNotNull { r -> next[r * BOARD + c].takeIf { it >= 0 } }
            var write = BOARD - 1
            for (v in survivors) { next[write * BOARD + c] = v; write-- }
            while (write >= 0) { next[write * BOARD + c] = Random.nextInt(GEM_COUNT); write-- }
        }
        board = next
        matches = findMatches(board)
    }
    return board
}

private fun hasValidMove(board: List<Int>): Boolean {
    for (i in board.indices) {
        val candidates = listOf(i + 1, i + BOARD)
        for (j in candidates) {
            if (j !in board.indices || !adjacent(i, j)) continue
            val next = board.toMutableList(); next[i] = board[j]; next[j] = board[i]
            if (findMatches(next).isNotEmpty()) return true
        }
    }
    return false
}
