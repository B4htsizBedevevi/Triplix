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

private val TG_Bg = Color(0xFF070A12)
private val TG_Panel = Color(0xFF111827)
private val TG_Panel2 = Color(0xFF18243A)
private val TG_White = Color(0xFFF7F9FC)
private val TG_Gray = Color(0xFF93A0B5)
private val TG_Muted = Color(0xFF59677F)
private val TG_Green = Color(0xFF6EE7A8)
private val TG_Gold = Color(0xFFFFC857)
private val TG_Gems = listOf(
    Color(0xFF48D6C8), Color(0xFF69A6FF), Color(0xFFFF6F9B),
    Color(0xFFFFC84A), Color(0xFFAD82FF), Color(0xFF58D98B)
)
private val TG_Symbols = listOf("◆", "●", "✦", "▲", "■", "⬟")

private enum class TG_Page { HOME, MODES, LEVELS, GAME }
private enum class TG_Mode(val title: String, val icon: String) {
    CLASSIC("Klasik", "◆"), FRUIT("Meyve", "🍓"), SPACE("Uzay", "✦"), ARCADE("Arcade", "⚡")
}

@Composable
fun TriplixGameApp() {
    MaterialTheme {
        Surface(Modifier.fillMaxSize(), color = TG_Bg) {
            var page by remember { mutableStateOf(TG_Page.HOME) }
            var mode by remember { mutableStateOf(TG_Mode.CLASSIC) }
            var level by remember { mutableIntStateOf(1) }
            when (page) {
                TG_Page.HOME -> TGHome(
                    play = { page = TG_Page.GAME },
                    modes = { page = TG_Page.MODES },
                    levels = { page = TG_Page.LEVELS }
                )
                TG_Page.MODES -> TGModes(mode, { mode = it }, { page = TG_Page.HOME }) { page = TG_Page.LEVELS }
                TG_Page.LEVELS -> TGLevels(mode, level, { page = TG_Page.HOME }) { level = it; page = TG_Page.GAME }
                TG_Page.GAME -> TGGame(mode, level) { page = TG_Page.LEVELS }
            }
        }
    }
}

@Composable
private fun TGHome(play: () -> Unit, modes: () -> Unit, levels: () -> Unit) {
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(22.dp),
        verticalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        Spacer(Modifier.height(10.dp))
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text("TRIPLIX", color = TG_White, fontSize = 31.sp, fontWeight = FontWeight.Black, letterSpacing = 2.sp)
                Text("Seç. Üçle. Patlat. 🎯", color = TG_Gray, fontSize = 13.sp)
            }
            Box(Modifier.size(54.dp).clip(RoundedCornerShape(18.dp)).background(Brush.linearGradient(listOf(TG_Green, Color(0xFF35C983)))), contentAlignment = Alignment.Center) {
                Text("T", color = TG_Bg, fontSize = 30.sp, fontWeight = FontWeight.Black)
            }
        }
        Surface(Modifier.fillMaxWidth(), RoundedCornerShape(28.dp), color = TG_Panel, border = BorderStroke(1.dp, TG_White.copy(.06f))) {
            Column(Modifier.padding(21.dp)) {
                Text("YENİ OYUN", color = TG_Green, fontSize = 10.sp, fontWeight = FontWeight.Black, letterSpacing = 1.5.sp)
                Spacer(Modifier.height(5.dp))
                Text("3 taşı seç, üçüncüde patlat.", color = TG_White, fontSize = 25.sp, fontWeight = FontWeight.ExtraBold)
                Spacer(Modifier.height(5.dp))
                Text("Aynı renkten üç taşı işaretle. Üçüncü seçimde TRIPLIX patlaması gelir.", color = TG_Gray, fontSize = 13.sp, lineHeight = 19.sp)
                Spacer(Modifier.height(17.dp))
                Button(play, Modifier.fillMaxWidth().height(55.dp), shape = RoundedCornerShape(18.dp), colors = ButtonDefaults.buttonColors(containerColor = TG_Green, contentColor = TG_Bg)) {
                    Text("OYUNA BAŞLA  →", fontWeight = FontWeight.Black)
                }
            }
        }
        TGMenuCard("🎮", "Oyun Modları", "Klasik • Meyve • Uzay • Arcade", modes)
        TGMenuCard("🗺", "Bölümler", "60 bölüm ve üç yıldız hedefi", levels)
    }
}

@Composable
private fun TGMenuCard(icon: String, title: String, desc: String, click: () -> Unit) {
    Surface(Modifier.fillMaxWidth().clip(RoundedCornerShape(21.dp)).clickable(onClick = click), RoundedCornerShape(21.dp), color = TG_Panel, border = BorderStroke(1.dp, TG_White.copy(.05f))) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(48.dp).clip(RoundedCornerShape(15.dp)).background(TG_Bg), contentAlignment = Alignment.Center) { Text(icon, fontSize = 22.sp) }
            Spacer(Modifier.width(13.dp))
            Column(Modifier.weight(1f)) {
                Text(title, color = TG_White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text(desc, color = TG_Gray, fontSize = 11.sp)
            }
            Text("›", color = TG_Muted, fontSize = 27.sp)
        }
    }
}

@Composable
private fun TGHeader(title: String, back: () -> Unit) {
    Row(Modifier.fillMaxWidth().padding(bottom = 13.dp), verticalAlignment = Alignment.CenterVertically) {
        Surface(Modifier.size(43.dp).clip(CircleShape).clickable(onClick = back), CircleShape, color = TG_Panel) {
            Box(contentAlignment = Alignment.Center) { Text("‹", color = TG_White, fontSize = 28.sp) }
        }
        Spacer(Modifier.width(13.dp))
        Text(title, color = TG_White, fontSize = 23.sp, fontWeight = FontWeight.ExtraBold)
    }
}

@Composable
private fun TGModes(selected: TG_Mode, select: (TG_Mode) -> Unit, back: () -> Unit, next: () -> Unit) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(22.dp)) {
        TGHeader("Oyun Modları", back)
        Text("Oynama şeklini seç.", color = TG_Gray, fontSize = 13.sp)
        Spacer(Modifier.height(18.dp))
        TG_Mode.values().forEach { m ->
            val active = m == selected
            Surface(
                Modifier.fillMaxWidth().padding(bottom = 11.dp).clip(RoundedCornerShape(21.dp)).clickable { select(m) },
                RoundedCornerShape(21.dp), color = if (active) TG_Panel2 else TG_Panel,
                border = BorderStroke(1.dp, if (active) TG_Green.copy(.65f) else TG_White.copy(.05f))
            ) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(55.dp).clip(RoundedCornerShape(17.dp)).background(TG_Bg), contentAlignment = Alignment.Center) { Text(m.icon, fontSize = 25.sp) }
                    Spacer(Modifier.width(14.dp))
                    Column(Modifier.weight(1f)) {
                        Text(m.title, color = TG_White, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                        Text(if (m == TG_Mode.CLASSIC) "Üç aynı taşı seç" else "Aynı kuralla farklı atmosfer", color = TG_Gray, fontSize = 12.sp)
                    }
                    Text(if (active) "✓" else "○", color = if (active) TG_Green else TG_Muted, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
        Button(next, Modifier.fillMaxWidth().height(54.dp), shape = RoundedCornerShape(18.dp), colors = ButtonDefaults.buttonColors(containerColor = TG_Green, contentColor = TG_Bg)) {
            Text("BÖLÜMLERE GEÇ  →", fontWeight = FontWeight.Black)
        }
    }
}

@Composable
private fun TGLevels(mode: TG_Mode, current: Int, back: () -> Unit, play: (Int) -> Unit) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(22.dp)) {
        TGHeader("Bölümler", back)
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(mode.icon, fontSize = 22.sp); Spacer(Modifier.width(8.dp)); Text(mode.title, color = TG_Green, fontWeight = FontWeight.Bold)
            Spacer(Modifier.weight(1f)); Text("60 bölüm", color = TG_Gray, fontSize = 12.sp)
        }
        Spacer(Modifier.height(15.dp))
        Surface(Modifier.fillMaxWidth(), RoundedCornerShape(19.dp), color = TG_Panel) {
            Column(Modifier.padding(15.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("İLERLEME", color = TG_Muted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    Text("Bölüm $current / 60", color = TG_Green, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(9.dp))
                Box(Modifier.fillMaxWidth().height(8.dp).clip(CircleShape).background(TG_Bg)) {
                    Box(Modifier.fillMaxWidth((current / 60f).coerceIn(.03f, 1f)).height(8.dp).clip(CircleShape).background(TG_Green))
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        for (r in 0 until 20) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                for (c in 0 until 3) {
                    val n = r * 3 + c + 1
                    val locked = n > current + 5
                    Surface(
                        Modifier.weight(1f).height(78.dp).clip(RoundedCornerShape(18.dp)).clickable(enabled = !locked) { play(n) },
                        RoundedCornerShape(18.dp), color = if (locked) TG_Panel else TG_Panel2,
                        border = BorderStroke(1.dp, if (n == current) TG_Green.copy(.6f) else TG_White.copy(.04f))
                    ) {
                        Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                            Text(if (locked) "🔒" else n.toString(), color = if (locked) TG_Muted else TG_White, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                            if (!locked) Text(if (n <= current) "★★★" else "☆ ☆ ☆", color = if (n <= current) TG_Gold else TG_Muted, fontSize = 9.sp)
                        }
                    }
                }
            }
            Spacer(Modifier.height(10.dp))
        }
    }
}

@Composable
private fun TGGame(mode: TG_Mode, level: Int, back: () -> Unit) {
    val size = 7
    val total = size * size
    fun newBoard() = List(total) { Random.nextInt(TG_Gems.size) }

    var board by remember(level) { mutableStateOf(newBoard()) }
    var selected by remember { mutableStateOf(emptyList<Int>()) }
    var popping by remember { mutableStateOf(emptyList<Int>()) }
    var score by remember { mutableIntStateOf(0) }
    var moves by remember { mutableIntStateOf(30) }
    var combo by remember { mutableIntStateOf(0) }
    var message by remember { mutableStateOf("Aynı renkten 3 taş seç.") }

    LaunchedEffect(popping) {
        if (popping.isNotEmpty()) {
            delay(190)
            val next = board.toMutableList()
            popping.forEach { next[it] = Random.nextInt(TG_Gems.size) }
            board = next
            selected = emptyList()
            popping = emptyList()
            message = "Yeni üçlü için hazırsın!"
        }
    }

    fun tap(index: Int) {
        if (popping.isNotEmpty() || moves <= 0) return
        if (selected.contains(index)) {
            selected = selected - index
            message = "${selected.size}/3 taş seçili."
            return
        }
        if (selected.isEmpty()) {
            selected = listOf(index)
            message = "1/3 — aynı renkten iki tane daha seç."
            return
        }
        if (board[index] != board[selected.first()]) {
            selected = listOf(index)
            message = "Farklı renk. Yeni üçlüye buradan başla."
            return
        }
        val next = selected + index
        if (next.size < 3) {
            selected = next
            message = "${next.size}/3 — biraz daha!"
        } else {
            selected = next
            popping = next
            moves -= 1
            combo += 1
            score += 100 * combo
            message = "💥 TRIPLIX! +${100 * combo}"
        }
    }

    fun hint() {
        val candidate = (0 until TG_Gems.size).asSequence()
            .map { type -> board.indices.filter { board[it] == type } }
            .firstOrNull { it.size >= 3 }
            ?.take(3)
        if (candidate != null) {
            selected = candidate
            message = "İpucu: bu üç taşı seç!"
        }
    }

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(18.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Surface(Modifier.size(43.dp).clip(CircleShape).clickable(onClick = back), CircleShape, color = TG_Panel) {
                Box(contentAlignment = Alignment.Center) { Text("‹", color = TG_White, fontSize = 28.sp) }
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(mode.title.uppercase(), color = TG_Green, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp)
                Text("Bölüm $level", color = TG_White, fontSize = 19.sp, fontWeight = FontWeight.ExtraBold)
            }
            Text("⭐ $score", color = TG_Gold, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(13.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(9.dp)) {
            TGStat("HAMLE", "$moves", Modifier.weight(1f)); TGStat("SEÇİM", "${selected.size}/3", Modifier.weight(1f)); TGStat("COMBO", "x$combo", Modifier.weight(1f))
        }
        Spacer(Modifier.height(13.dp))
        Surface(Modifier.fillMaxWidth(), RoundedCornerShape(24.dp), color = TG_Panel, border = BorderStroke(1.dp, TG_White.copy(.06f))) {
            Column(Modifier.padding(9.dp)) {
                for (r in 0 until size) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        for (c in 0 until size) {
                            val i = r * size + c
                            TGTile(board[i], selected.contains(i), popping.contains(i), Modifier.weight(1f)) { tap(i) }
                        }
                    }
                    if (r < size - 1) Spacer(Modifier.height(4.dp))
                }
            }
        }
        Spacer(Modifier.height(11.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TGTool("↻", "KARIŞTIR", Modifier.weight(1f)) { board = newBoard(); selected = emptyList(); message = "Tahta karıştırıldı." }
            TGTool("💡", "İPUCU", Modifier.weight(1f)) { hint() }
            TGTool("↶", "SIFIRLA", Modifier.weight(1f)) { board = newBoard(); selected = emptyList(); popping = emptyList(); score = 0; moves = 30; combo = 0; message = "Aynı renkten 3 taş seç." }
        }
        Spacer(Modifier.height(11.dp))
        Surface(Modifier.fillMaxWidth(), RoundedCornerShape(17.dp), color = TG_Panel2) {
            Row(Modifier.padding(13.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(if (popping.isNotEmpty()) "💥" else "💡", fontSize = 18.sp)
                Spacer(Modifier.width(9.dp))
                Text(message, color = TG_Gray, fontSize = 11.sp, lineHeight = 16.sp)
            }
        }
    }
}

@Composable
private fun TGStat(label: String, value: String, modifier: Modifier) {
    Surface(modifier, RoundedCornerShape(15.dp), color = TG_Panel) {
        Column(Modifier.padding(9.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, color = TG_White, fontWeight = FontWeight.ExtraBold)
            Text(label, color = TG_Muted, fontSize = 8.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun TGTile(type: Int, selected: Boolean, popping: Boolean, modifier: Modifier, click: () -> Unit) {
    val gem = TG_Gems[type]
    Box(
        modifier.height(43.dp).clip(RoundedCornerShape(13.dp)).background(
            if (popping) Brush.linearGradient(listOf(Color.White, Color.White.copy(.55f)))
            else Brush.linearGradient(listOf(gem, gem.copy(alpha = .45f)))
        ).clickable(onClick = click),
        contentAlignment = Alignment.Center
    ) {
        if (popping) {
            Text("✦", color = TG_Bg, fontSize = 25.sp, fontWeight = FontWeight.Black)
            Text("💥", fontSize = 12.sp, modifier = Modifier.padding(top = 30.dp))
        } else {
            Text(TG_Symbols[type], color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Black)
            if (selected) {
                Box(Modifier.fillMaxSize().padding(3.dp).clip(RoundedCornerShape(11.dp)).background(Color.White.copy(.18f)))
                Text("✓", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Black)
            }
        }
    }
}

@Composable
private fun TGTool(icon: String, label: String, modifier: Modifier, click: () -> Unit) {
    Surface(modifier.clip(RoundedCornerShape(16.dp)).clickable(onClick = click), RoundedCornerShape(16.dp), color = TG_Panel) {
        Column(Modifier.padding(vertical = 10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(icon, fontSize = 17.sp)
            Text(label, color = TG_Gray, fontSize = 8.sp, fontWeight = FontWeight.Bold)
        }
    }
}
