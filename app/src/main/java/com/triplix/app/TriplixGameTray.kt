package com.triplix.app

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import kotlinx.coroutines.delay
import kotlin.random.Random

private val TV_Bg = Color(0xFF070A12)
private val TV_Panel = Color(0xFF111827)
private val TV_Panel2 = Color(0xFF18243A)
private val TV_White = Color(0xFFF7F9FC)
private val TV_Gray = Color(0xFF93A0B5)
private val TV_Muted = Color(0xFF59677F)
private val TV_Green = Color(0xFF6EE7A8)
private val TV_Gold = Color(0xFFFFC857)
private val TV_Gems = listOf(
    Color(0xFF4A8FEA), Color(0xFFDF5D91), Color(0xFF3CC0B5),
    Color(0xFFFFB93E), Color(0xFF9B78E8), Color(0xFF55C982)
)
private val TV_Symbols = listOf("●", "✦", "◆", "▲", "■", "⬟")

private enum class TVPage { HOME, GAME }
private enum class TVMode(val title: String, val icon: String) { CLASSIC("Klasik", "◆"), FRUIT("Meyve", "🍓"), SPACE("Uzay", "✦"), ARCADE("Arcade", "⚡") }

private fun tvBoard(colors: Int): List<Int?> = List(49) { Random.nextInt(colors) }

private fun tvCollapse(board: List<Int?>, colors: Int): List<Int?> {
    val next = board.toMutableList()
    for (c in 0 until 7) {
        val values = (0 until 7).mapNotNull { r -> next[r * 7 + c] }
        val missing = 7 - values.size
        for (r in 0 until 7) next[r * 7 + c] = if (r < missing) Random.nextInt(colors) else values[r - missing]
    }
    return next
}

@Composable
fun TriplixTrayApp() {
    MaterialTheme {
        Surface(Modifier.fillMaxSize(), color = TV_Bg) {
            var page by remember { mutableStateOf(TVPage.HOME) }
            var mode by remember { mutableStateOf(TVMode.FRUIT) }
            var level by remember { mutableIntStateOf(1) }
            when (page) {
                TVPage.HOME -> TVHome(
                    play = { page = TVPage.GAME },
                    chooseMode = { mode = it; page = TVPage.GAME }
                )
                TVPage.GAME -> TVGame(mode, level) { page = TVPage.HOME }
            }
        }
    }
}

@Composable
private fun TVHome(play: () -> Unit, chooseMode: (TVMode) -> Unit) {
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(22.dp),
        verticalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        Spacer(Modifier.height(10.dp))
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text("TRIPLIX", color = TV_White, fontSize = 31.sp, fontWeight = FontWeight.Black, letterSpacing = 2.sp)
                Text("Seç. Üçle. Patlat. 🎯", color = TV_Gray, fontSize = 13.sp)
            }
            Box(Modifier.size(54.dp).clip(RoundedCornerShape(18.dp)).background(Brush.linearGradient(listOf(TV_Green, Color(0xFF35C983)))), contentAlignment = Alignment.Center) {
                Text("T", color = TV_Bg, fontSize = 30.sp, fontWeight = FontWeight.Black)
            }
        }
        Surface(Modifier.fillMaxWidth(), shape = RoundedCornerShape(28.dp), color = TV_Panel, border = BorderStroke(1.dp, TV_White.copy(alpha = .06f))) {
            Column(Modifier.padding(21.dp)) {
                Text("YENİ OYUN", color = TV_Green, fontSize = 10.sp, fontWeight = FontWeight.Black, letterSpacing = 1.5.sp)
                Spacer(Modifier.height(5.dp))
                Text("Taşları aşağıda biriktir.", color = TV_White, fontSize = 25.sp, fontWeight = FontWeight.ExtraBold)
                Spacer(Modifier.height(5.dp))
                Text("Seçtiğin taşlar artık tahtadan kopup aşağıdaki tepsiye gelir. Üç aynı taşı biriktirince patlat!", color = TV_Gray, fontSize = 13.sp, lineHeight = 19.sp)
                Spacer(Modifier.height(17.dp))
                Button(onClick = play, Modifier.fillMaxWidth().height(55.dp), shape = RoundedCornerShape(18.dp), colors = ButtonDefaults.buttonColors(containerColor = TV_Green, contentColor = TV_Bg)) {
                    Text("OYUNA BAŞLA  →", fontWeight = FontWeight.Black)
                }
            }
        }
        TVModeCard(TVMode.CLASSIC, chooseMode)
        TVModeCard(TVMode.FRUIT, chooseMode)
        TVModeCard(TVMode.SPACE, chooseMode)
    }
}

@Composable
private fun TVModeCard(mode: TVMode, choose: (TVMode) -> Unit) {
    Surface(Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).clickable { choose(mode) }, shape = RoundedCornerShape(20.dp), color = TV_Panel, border = BorderStroke(1.dp, TV_White.copy(alpha = .05f))) {
        Row(Modifier.padding(15.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(48.dp).clip(RoundedCornerShape(15.dp)).background(TV_Bg), contentAlignment = Alignment.Center) { Text(mode.icon, fontSize = 22.sp) }
            Spacer(Modifier.width(13.dp))
            Column(Modifier.weight(1f)) {
                Text(mode.title, color = TV_White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text("Üçlü tepsi mekaniği", color = TV_Gray, fontSize = 11.sp)
            }
            Text("›", color = TV_Muted, fontSize = 27.sp)
        }
    }
}

@Composable
private fun TVGame(mode: TVMode, level: Int, back: () -> Unit) {
    val colors = 4
    val target = 5 + level / 3
    var board by remember(level, mode) { mutableStateOf(tvBoard(colors)) }
    var selected by remember(level, mode) { mutableStateOf(emptySet<Int>()) }
    var tray by remember(level, mode) { mutableStateOf(emptyList<Int>()) }
    var moves by remember(level, mode) { mutableIntStateOf(30) }
    var triples by remember(level, mode) { mutableIntStateOf(0) }
    var score by remember(level, mode) { mutableIntStateOf(0) }
    var combo by remember(level, mode) { mutableIntStateOf(0) }
    var clearType by remember(level, mode) { mutableStateOf<Int?>(null) }
    var message by remember(level, mode) { mutableStateOf("Bir taş seç; seçtiğin taş aşağıya gelsin.") }
    var won by remember(level, mode) { mutableStateOf(false) }
    var lost by remember(level, mode) { mutableStateOf(false) }

    LaunchedEffect(clearType) {
        val type = clearType ?: return@LaunchedEffect
        delay(180)
        var remaining = tray.toMutableList()
        var removed = 0
        remaining = remaining.filter { value -> if (value == type && removed < 3) { removed++; false } else true }.toMutableList()
        tray = remaining
        board = tvCollapse(board, colors)
        triples += 1
        combo += 1
        score += 100 * combo
        clearType = null
        message = "💥 ÜÇLÜ PATLADI! +${100 * combo}"
        if (triples >= target) won = true
    }

    fun tap(index: Int) {
        if (won || lost || clearType != null || moves <= 0) return
        val value = board[index] ?: return
        if (selected.contains(index)) return
        if (tray.size >= 7) { lost = true; return }
        val newTray = tray + value
        tray = newTray
        selected = selected + index
        val nextBoard = board.toMutableList()
        nextBoard[index] = null
        board = nextBoard
        moves -= 1
        message = "Tepside ${newTray.size}/7 taş var."
        if (newTray.count { it == value } >= 3) {
            clearType = value
            message = "💥 Üçlü tamamlandı!"
        } else if (newTray.size >= 7) {
            lost = true
            message = "Tepsi doldu."
        }
    }

    fun reset() {
        board = tvBoard(colors); selected = emptySet(); tray = emptyList(); moves = 30; triples = 0; score = 0; combo = 0; clearType = null; won = false; lost = false
        message = "Bir taş seç; seçtiğin taş aşağıya gelsin."
    }

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(18.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Surface(Modifier.size(43.dp).clip(CircleShape).clickable(onClick = back), shape = CircleShape, color = TV_Panel) {
                Box(contentAlignment = Alignment.Center) { Text("‹", color = TV_White, fontSize = 28.sp) }
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(mode.title.uppercase(), color = TV_Green, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp)
                Text("Bölüm $level", color = TV_White, fontSize = 19.sp, fontWeight = FontWeight.ExtraBold)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("🪙 ${400 + level * 5}", color = TV_Gold, fontWeight = FontWeight.Bold)
                Text("⭐ $score", color = TV_Gray, fontSize = 10.sp)
            }
        }
        Spacer(Modifier.height(11.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
            TVStat("HAMLE", "$moves", Modifier.weight(1f))
            TVStat("HEDEF", "$triples/$target", Modifier.weight(1f))
            TVStat("COMBO", "x$combo", Modifier.weight(1f))
        }
        Spacer(Modifier.height(10.dp))
        Surface(Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp), color = TV_Panel, border = BorderStroke(1.dp, TV_White.copy(alpha = .06f))) {
            Column(Modifier.padding(9.dp)) {
                for (r in 0 until 7) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        for (c in 0 until 7) {
                            val index = r * 7 + c
                            TVTile(board[index] ?: 0, board[index] != null, Modifier.weight(1f)) { tap(index) }
                        }
                    }
                    if (r < 6) Spacer(Modifier.height(4.dp))
                }
            }
        }
        Spacer(Modifier.height(9.dp))
        Surface(Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), color = TV_Panel2, border = BorderStroke(1.dp, TV_Green.copy(alpha = .16f))) {
            Column(Modifier.padding(10.dp)) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text("🧺", fontSize = 19.sp)
                    Spacer(Modifier.width(8.dp))
                    Column(Modifier.weight(1f)) {
                        Text("SEÇİLEN TAŞLAR", color = TV_Green, fontSize = 9.sp, fontWeight = FontWeight.Black, letterSpacing = 1.sp)
                        Text("Üç aynı taş burada birleşir", color = TV_Gray, fontSize = 10.sp)
                    }
                    Text("${tray.size}/7", color = TV_White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
                Spacer(Modifier.height(9.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    for (i in 0 until 7) {
                        val value = tray.getOrNull(i)
                        Box(Modifier.weight(1f).height(44.dp).clip(RoundedCornerShape(11.dp)).background(if (value == null) TV_Bg.copy(alpha = .45f) else TV_Gems[value]), contentAlignment = Alignment.Center) {
                            if (value != null) Text(TV_Symbols[value], color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Black)
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(9.dp))
        Surface(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), color = TV_Panel2) {
            Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(if (clearType != null) "💥" else "🎯", fontSize = 18.sp)
                Spacer(Modifier.width(9.dp))
                Text(message, color = TV_Gray, fontSize = 11.sp, modifier = Modifier.weight(1f))
            }
        }
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TVTool("↻", "YENİLE", Modifier.weight(1f)) { reset() }
            TVTool("💡", "İPUCU", Modifier.weight(1f)) {
                val candidate = board.indexOfFirst { it != null }
                if (candidate >= 0) message = "İpucu: önce bu taşı seç 😉"
            }
            TVTool("↶", "SIFIRLA", Modifier.weight(1f)) { reset() }
        }
        Spacer(Modifier.height(7.dp))
        Text("Seçtiğin her taş aşağıdaki tepsiye gelir • 3 aynı taş = patlama", color = TV_Muted, fontSize = 9.sp, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
    }

    if (won) {
        AlertDialog(onDismissRequest = { }, title = { Text("Bölüm tamamlandı! 🎉", fontWeight = FontWeight.ExtraBold) }, text = { Text("$triples üçlü tamamlandı. Skor: $score") }, confirmButton = { TextButton(onClick = back) { Text("BÖLÜMLERE DÖN", color = TV_Green, fontWeight = FontWeight.Bold) } })
    }
    if (lost) {
        AlertDialog(onDismissRequest = { }, title = { Text("Tepsi doldu 😅", fontWeight = FontWeight.ExtraBold) }, text = { Text("Taşları daha dikkatli seç. Skor: $score") }, confirmButton = { TextButton(onClick = { reset() }) { Text("TEKRAR OYNA", color = TV_Green, fontWeight = FontWeight.Bold) } }, dismissButton = { TextButton(onClick = back) { Text("ÇIK", color = TV_Gray) } })
    }
}

@Composable
private fun TVStat(label: String, value: String, modifier: Modifier) {
    Surface(modifier, shape = RoundedCornerShape(15.dp), color = TV_Panel2) {
        Column(Modifier.padding(9.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, color = TV_White, fontWeight = FontWeight.ExtraBold)
            Text(label, color = TV_Muted, fontSize = 8.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun TVTile(type: Int, visible: Boolean, modifier: Modifier, click: () -> Unit) {
    val safe = type.coerceIn(0, TV_Gems.lastIndex)
    Box(modifier.height(43.dp).clip(RoundedCornerShape(13.dp)).background(if (visible) Brush.linearGradient(listOf(TV_Gems[safe], TV_Gems[safe].copy(alpha = .45f))) else TV_Bg.copy(alpha = .42f)).clickable(enabled = visible, onClick = click), contentAlignment = Alignment.Center) {
        if (visible) Text(TV_Symbols[safe], color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Black)
    }
}

@Composable
private fun TVTool(icon: String, label: String, modifier: Modifier, click: () -> Unit) {
    Surface(modifier.clip(RoundedCornerShape(16.dp)).clickable(onClick = click), shape = RoundedCornerShape(16.dp), color = TV_Panel) {
        Column(Modifier.padding(vertical = 10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(icon, fontSize = 17.sp)
            Text(label, color = TV_Gray, fontSize = 8.sp, fontWeight = FontWeight.Bold)
        }
    }
}
