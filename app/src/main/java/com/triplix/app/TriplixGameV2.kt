package com.triplix.app

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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

private val V2_BG = Color(0xFF060914)
private val V2_PANEL = Color(0xFF10182A)
private val V2_PANEL_2 = Color(0xFF17233A)
private val V2_TEXT = Color(0xFFF6F8FF)
private val V2_MUTED = Color(0xFF91A0B9)
private val V2_GREEN = Color(0xFF6EF2B1)
private val V2_GOLD = Color(0xFFFFCF58)
private val V2_RED = Color(0xFFFF657E)
private val V2_BLUE = Color(0xFF62A8FF)

private val V2_GEMS = listOf(
    Pair(Color(0xFFFF5C83), "🍓"),
    Pair(Color(0xFFFFC94D), "🍋"),
    Pair(Color(0xFF9C6BFF), "🍇"),
    Pair(Color(0xFFFF6B58), "🍎"),
    Pair(Color(0xFF5CA8FF), "🫐"),
    Pair(Color(0xFF5CDB8B), "🍃")
)

private enum class V2_Page { HOME, LEVELS, GAME }

@Composable
fun TriplixGameV2() {
    MaterialTheme {
        Surface(Modifier.fillMaxSize(), color = V2_BG) {
            var page by remember { mutableStateOf(V2_Page.HOME) }
            var level by remember { mutableIntStateOf(1) }
            when (page) {
                V2_Page.HOME -> V2Home(
                    play = { page = V2_Page.GAME },
                    levels = { page = V2_Page.LEVELS }
                )
                V2_Page.LEVELS -> V2Levels(level) { level = it; page = V2_Page.GAME }
                V2_Page.GAME -> V2Game(level) { page = V2_Page.LEVELS }
            }
        }
    }
}

@Composable
private fun V2Home(play: () -> Unit, levels: () -> Unit) {
    Column(
        Modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Spacer(Modifier.height(12.dp))
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("TRIPLIX", color = V2_TEXT, fontSize = 35.sp, fontWeight = FontWeight.Black, letterSpacing = 2.sp)
                Text("AYNI 3'Ü BİRLEŞTİR", color = V2_GREEN, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
            }
            Surface(Modifier.size(54.dp), CircleShape, color = V2_PANEL_2, border = BorderStroke(1.dp, V2_GREEN.copy(.35f))) {
                Box(contentAlignment = Alignment.Center) { Text("👑", fontSize = 26.sp) }
            }
        }
        Spacer(Modifier.height(4.dp))
        Surface(
            Modifier.fillMaxWidth(), RoundedCornerShape(30.dp),
            color = V2_PANEL,
            border = BorderStroke(1.dp, V2_BLUE.copy(.22f))
        ) {
            Column(Modifier.padding(22.dp)) {
                Text("MEYVE BAHÇESİ", color = V2_GOLD, fontSize = 11.sp, fontWeight = FontWeight.Black, letterSpacing = 1.3.sp)
                Spacer(Modifier.height(7.dp))
                Text("Üçünü bul. Patlat.\nCombo'yu büyüt.", color = V2_TEXT, fontSize = 28.sp, fontWeight = FontWeight.Black, lineHeight = 31.sp)
                Spacer(Modifier.height(8.dp))
                Text("Aynı türden 3 taşı seç. Üçüncü taşta eşleşme patlar ve boşluklar yeniden dolar.", color = V2_MUTED, fontSize = 13.sp, lineHeight = 19.sp)
                Spacer(Modifier.height(18.dp))
                Button(
                    onClick = play,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = V2_GREEN, contentColor = V2_BG)
                ) { Text("OYNA  →", fontWeight = FontWeight.Black, fontSize = 15.sp) }
            }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            V2SmallCard("🗺️", "BÖLÜMLER", "60 bölüm", levels, Modifier.weight(1f))
            V2SmallCard("🏆", "HEDEF", "3 yıldız", {}, Modifier.weight(1f))
        }
        Spacer(Modifier.weight(1f))
        Text("Rahatla • Eşleştir • Keşfet", Modifier.fillMaxWidth(), color = V2_MUTED, fontSize = 12.sp, textAlign = TextAlign.Center)
    }
}

@Composable
private fun V2SmallCard(icon: String, title: String, desc: String, click: () -> Unit, modifier: Modifier) {
    Surface(
        modifier.clip(RoundedCornerShape(20.dp)).clickable(onClick = click),
        RoundedCornerShape(20.dp), color = V2_PANEL,
        border = BorderStroke(1.dp, Color.White.copy(.06f))
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(icon, fontSize = 24.sp)
            Spacer(Modifier.height(7.dp))
            Text(title, color = V2_TEXT, fontSize = 12.sp, fontWeight = FontWeight.Black)
            Text(desc, color = V2_MUTED, fontSize = 10.sp)
        }
    }
}

@Composable
private fun V2Levels(current: Int, play: (Int) -> Unit) {
    Column(Modifier.fillMaxSize().padding(20.dp)) {
        V2TopBar("BÖLÜMLER", { })
        Text("Meyve Bahçesi", color = V2_GREEN, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(14.dp))
        Surface(Modifier.fillMaxWidth(), RoundedCornerShape(20.dp), color = V2_PANEL) {
            Column(Modifier.padding(15.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("İLERLEME", color = V2_MUTED, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    Text("$current / 60", color = V2_TEXT, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(9.dp))
                Box(Modifier.fillMaxWidth().height(8.dp).clip(CircleShape).background(V2_BG)) {
                    Box(Modifier.fillMaxWidth((current / 60f).coerceIn(.02f, 1f)).height(8.dp).clip(CircleShape).background(V2_GREEN))
                }
            }
        }
        Spacer(Modifier.height(15.dp))
        Column(Modifier.fillMaxWidth().weight(1f)) {
            for (row in 0 until 20) {
                Row(Modifier.fillMaxWidth().padding(bottom = 9.dp), horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                    for (col in 0 until 3) {
                        val n = row * 3 + col + 1
                        val locked = n > current + 5
                        Surface(
                            Modifier.weight(1f).height(72.dp).clip(RoundedCornerShape(18.dp)).clickable(enabled = !locked) { play(n) },
                            RoundedCornerShape(18.dp),
                            color = if (locked) V2_PANEL else V2_PANEL_2,
                            border = BorderStroke(1.dp, if (n == current) V2_GREEN.copy(.7f) else Color.White.copy(.05f))
                        ) {
                            Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                                Text(if (locked) "🔒" else n.toString(), color = if (locked) V2_MUTED else V2_TEXT, fontSize = 19.sp, fontWeight = FontWeight.Black)
                                if (!locked) Text(if (n < current) "★★★" else if (n == current) "★☆☆" else "☆☆☆", color = if (n <= current) V2_GOLD else V2_MUTED, fontSize = 9.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun V2TopBar(title: String, back: () -> Unit) {
    Row(Modifier.fillMaxWidth().padding(bottom = 15.dp), verticalAlignment = Alignment.CenterVertically) {
        Surface(Modifier.size(42.dp).clip(CircleShape).clickable(onClick = back), CircleShape, color = V2_PANEL) {
            Box(contentAlignment = Alignment.Center) { Text("‹", color = V2_TEXT, fontSize = 28.sp) }
        }
        Spacer(Modifier.width(12.dp))
        Text(title, color = V2_TEXT, fontSize = 22.sp, fontWeight = FontWeight.Black)
    }
}

@Composable
private fun V2Game(level: Int, back: () -> Unit) {
    val size = 7
    val total = size * size
    fun freshBoard(): List<Int> = List(total) { Random.nextInt(V2_GEMS.size) }

    var board by remember(level) { mutableStateOf(freshBoard()) }
    var selected by remember { mutableStateOf(emptyList<Int>()) }
    var popping by remember { mutableStateOf(emptyList<Int>()) }
    var moves by remember(level) { mutableIntStateOf((30 - level / 5).coerceAtLeast(18)) }
    var score by remember(level) { mutableIntStateOf(0) }
    var combo by remember { mutableIntStateOf(0) }
    var collected by remember(level) { mutableIntStateOf(0) }
    var showResult by remember(level) { mutableStateOf(false) }
    var won by remember(level) { mutableStateOf(false) }
    val target = 10 + level * 2

    LaunchedEffect(popping) {
        if (popping.isNotEmpty()) {
            delay(170)
            val next = board.toMutableList()
            for (column in 0 until size) {
                val remaining = mutableListOf<Int>()
                for (row in size - 1 downTo 0) {
                    val index = row * size + column
                    if (!popping.contains(index)) remaining.add(board[index])
                }
                var writeRow = size - 1
                for (value in remaining) {
                    next[writeRow * size + column] = value
                    writeRow--
                }
                while (writeRow >= 0) {
                    next[writeRow * size + column] = Random.nextInt(V2_GEMS.size)
                    writeRow--
                }
            }
            board = next
            selected = emptyList()
            popping = emptyList()
            if (collected >= target) {
                won = true
                showResult = true
            } else if (moves <= 0) {
                won = false
                showResult = true
            }
        }
    }

    fun reset() {
        board = freshBoard()
        selected = emptyList()
        popping = emptyList()
        moves = (30 - level / 5).coerceAtLeast(18)
        score = 0
        combo = 0
        collected = 0
        showResult = false
        won = false
    }

    fun tap(index: Int) {
        if (showResult || popping.isNotEmpty() || moves <= 0) return
        if (selected.contains(index)) {
            selected = selected - index
            return
        }
        if (selected.isEmpty() || board[index] != board[selected.first()]) {
            selected = listOf(index)
            return
        }
        val next = selected + index
        if (next.size < 3) {
            selected = next
        } else {
            popping = next
            moves--
            combo++
            collected += 3
            score += 100 * combo
        }
    }

    Column(Modifier.fillMaxSize().padding(horizontal = 14.dp, vertical = 12.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Surface(Modifier.size(42.dp).clip(CircleShape).clickable(onClick = back), CircleShape, color = V2_PANEL) {
                Box(contentAlignment = Alignment.Center) { Text("‹", color = V2_TEXT, fontSize = 28.sp) }
            }
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text("MEYVE BAHÇESİ", color = V2_GREEN, fontSize = 9.sp, fontWeight = FontWeight.Black, letterSpacing = 1.2.sp)
                Text("Bölüm $level", color = V2_TEXT, fontSize = 19.sp, fontWeight = FontWeight.Black)
            }
            Text("💰 $score", color = V2_GOLD, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
        Spacer(Modifier.height(11.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            V2Stat("HAMLE", moves.toString(), Modifier.weight(1f))
            V2Stat("HEDEF", "$collected/$target", Modifier.weight(1f))
            V2Stat("COMBO", "x$combo", Modifier.weight(1f))
        }
        Spacer(Modifier.height(10.dp))
        Surface(Modifier.fillMaxWidth(), RoundedCornerShape(25.dp), color = V2_PANEL, border = BorderStroke(1.dp, Color.White.copy(.07f))) {
            Column(Modifier.padding(8.dp)) {
                for (row in 0 until size) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        for (column in 0 until size) {
                            val index = row * size + column
                            V2Tile(
                                type = board[index],
                                selected = selected.contains(index),
                                popping = popping.contains(index),
                                modifier = Modifier.weight(1f),
                                click = { tap(index) }
                            )
                        }
                    }
                    if (row < size - 1) Spacer(Modifier.height(4.dp))
                }
            }
        }
        Spacer(Modifier.height(10.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            V2Tool("🔀", "KARIŞTIR", Modifier.weight(1f)) { if (!showResult) { board = freshBoard(); selected = emptyList() } }
            V2Tool("💡", "İPUCU", Modifier.weight(1f)) {
                if (!showResult) {
                    val candidate = V2_GEMS.indices.map { t -> board.indices.filter { board[it] == t } }.firstOrNull { it.size >= 3 }?.take(3)
                    if (candidate != null) selected = candidate
                }
            }
            V2Tool("↻", "YENİLE", Modifier.weight(1f)) { reset() }
        }
        Spacer(Modifier.height(8.dp))
        Text(
            if (popping.isNotEmpty()) "💥 TRIPLIX!" else if (selected.isEmpty()) "Aynı türden 3 taş seç." else "${selected.size}/3 seçildi — devam et!",
            Modifier.fillMaxWidth(), color = if (popping.isNotEmpty()) V2_GOLD else V2_MUTED,
            fontSize = 11.sp, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold
        )
    }

    if (showResult) {
        V2Result(won, level, score, combo, reset, back)
    }
}

@Composable
private fun V2Stat(label: String, value: String, modifier: Modifier) {
    Surface(modifier, RoundedCornerShape(15.dp), color = V2_PANEL) {
        Column(Modifier.padding(vertical = 8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, color = V2_TEXT, fontSize = 15.sp, fontWeight = FontWeight.Black)
            Text(label, color = V2_MUTED, fontSize = 8.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun V2Tool(icon: String, label: String, modifier: Modifier, click: () -> Unit) {
    Surface(modifier.clip(RoundedCornerShape(16.dp)).clickable(onClick = click), RoundedCornerShape(16.dp), color = V2_PANEL) {
        Column(Modifier.padding(vertical = 9.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(icon, fontSize = 18.sp)
            Text(label, color = V2_MUTED, fontSize = 8.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun V2Tile(type: Int, selected: Boolean, popping: Boolean, modifier: Modifier, click: () -> Unit) {
    val (gemColor, emoji) = V2_GEMS[type]
    val scale by animateFloatAsState(
        targetValue = if (selected) 1.08f else if (popping) 1.18f else 1f,
        animationSpec = tween(130, easing = FastOutSlowInEasing),
        label = "tile-scale"
    )
    val alpha by animateFloatAsState(
        targetValue = if (popping) 0f else 1f,
        animationSpec = tween(150),
        label = "tile-alpha"
    )
    Box(
        modifier
            .height(47.dp)
            .clip(RoundedCornerShape(13.dp))
            .background(
                Brush.linearGradient(
                    listOf(gemColor.copy(alpha = .98f), gemColor.copy(alpha = .42f))
                )
            )
            .clickable(onClick = click),
        contentAlignment = Alignment.Center
    ) {
        Text(emoji, fontSize = 22.sp, modifier = Modifier.scaleSafe(scale).then(Modifier), color = Color.Unspecified)
        if (selected) {
            Box(Modifier.fillMaxSize().padding(3.dp).clip(RoundedCornerShape(10.dp)).background(Color.White.copy(.22f)))
            Text("✓", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Black)
        }
        if (popping) Text("✦", color = Color.White.copy(alpha), fontSize = 26.sp, fontWeight = FontWeight.Black)
    }
}

private fun Modifier.scaleSafe(scale: Float): Modifier = this

@Composable
private fun V2Result(won: Boolean, level: Int, score: Int, combo: Int, retry: () -> Unit, back: () -> Unit) {
    Box(Modifier.fillMaxSize().background(Color.Black.copy(.68f)), contentAlignment = Alignment.Center) {
        Surface(Modifier.fillMaxWidth().padding(28.dp), RoundedCornerShape(30.dp), color = V2_PANEL_2, border = BorderStroke(1.dp, if (won) V2_GREEN.copy(.5f) else V2_RED.copy(.5f))) {
            Column(Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(if (won) "🏆" else "💔", fontSize = 48.sp)
                Spacer(Modifier.height(8.dp))
                Text(if (won) "BÖLÜM TAMAMLANDI!" else "HAMLELER BİTTİ", color = V2_TEXT, fontSize = 21.sp, fontWeight = FontWeight.Black, textAlign = TextAlign.Center)
                Spacer(Modifier.height(7.dp))
                Text(if (won) "Bölüm $level temizlendi." else "Bir kez daha dene, bu kez combo'yu büyüt.", color = V2_MUTED, fontSize = 12.sp, textAlign = TextAlign.Center)
                Spacer(Modifier.height(15.dp))
                Text(if (won) "★★★" else "★☆☆", color = V2_GOLD, fontSize = 25.sp, fontWeight = FontWeight.Black)
                Text("Skor $score  •  En iyi combo x$combo", color = V2_MUTED, fontSize = 11.sp)
                Spacer(Modifier.height(18.dp))
                Button(
                    onClick = if (won) back else retry,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(17.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = V2_GREEN, contentColor = V2_BG)
                ) { Text(if (won) "SONRAKİ BÖLÜM  →" else "TEKRAR OYNA", fontWeight = FontWeight.Black) }
                Spacer(Modifier.height(8.dp))
                Text("Bölümlere dön", Modifier.clickable(onClick = back).padding(8.dp), color = V2_MUTED, fontSize = 11.sp)
            }
        }
    }
}
