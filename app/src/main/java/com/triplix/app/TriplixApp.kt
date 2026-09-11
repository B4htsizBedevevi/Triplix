package com.triplix.app

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

private val Bg = Color(0xFF070A12)
private val Panel = Color(0xFF111827)
private val Panel2 = Color(0xFF172236)
private val White = Color(0xFFF7F9FC)
private val Gray = Color(0xFF93A0B5)
private val Muted = Color(0xFF536176)
private val Green = Color(0xFF6EE7A8)
private val Gold = Color(0xFFFFC857)
private val Gems = listOf(Color(0xFF63DFA0), Color(0xFF6EA8FF), Color(0xFFFF789A), Color(0xFFFFC857), Color(0xFFB58CFF), Color(0xFF49D6D1))
private val Symbols = listOf("◆", "●", "✦", "▲", "■", "⬟")

private enum class Page { HOME, MODES, LEVELS, GAME, SETTINGS }
private enum class GameMode(val title: String, val desc: String, val icon: String) {
    CLASSIC("Klasik", "Üçünü seç, patlat", "◆"),
    FRUIT("Meyve", "Üç meyveyi seç", "🍓"),
    SPACE("Uzay", "Üç yıldızı yakala", "✦"),
    ARCADE("Arcade", "Hızlı üçlüler", "⚡")
}

@Composable
fun TriplixApp() {
    MaterialTheme {
        Surface(Modifier.fillMaxSize(), color = Bg) {
            var page by remember { mutableStateOf(Page.HOME) }
            var mode by remember { mutableStateOf(GameMode.CLASSIC) }
            var level by remember { mutableIntStateOf(1) }
            when (page) {
                Page.HOME -> Home({ page = Page.GAME }, { page = Page.MODES }, { page = Page.LEVELS }, { page = Page.SETTINGS })
                Page.MODES -> Modes(mode, { mode = it }, { page = Page.HOME }, { page = Page.LEVELS })
                Page.LEVELS -> Levels(mode, level, { page = Page.HOME }) { level = it; page = Page.GAME }
                Page.GAME -> Game(mode, level) { page = Page.LEVELS }
                Page.SETTINGS -> Settings { page = Page.HOME }
            }
        }
    }
}

@Composable
private fun Home(onPlay: () -> Unit, onModes: () -> Unit, onLevels: () -> Unit, onSettings: () -> Unit) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(22.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Spacer(Modifier.height(10.dp))
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Column { Text("TRIPLIX", color = White, fontSize = 30.sp, fontWeight = FontWeight.Black, letterSpacing = 2.sp); Text("Seç. Patlat. Tekrar oyna.", color = Gray, fontSize = 13.sp) }
            Logo()
        }
        Surface(Modifier.fillMaxWidth(), shape = RoundedCornerShape(28.dp), color = Panel, border = BorderStroke(1.dp, White.copy(.06f))) {
            Column(Modifier.padding(21.dp)) {
                Text("BUGÜNÜN SERİSİ", color = Green, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
                Spacer(Modifier.height(6.dp)); Text("Hazır mısın?", color = White, fontSize = 27.sp, fontWeight = FontWeight.ExtraBold)
                Text("Aynı türden üç taşı seç. Üçüncü dokunuşta patlat.", color = Gray, fontSize = 13.sp)
                Spacer(Modifier.height(17.dp))
                Button(onClick = onPlay, Modifier.fillMaxWidth().height(54.dp), shape = RoundedCornerShape(18.dp), colors = ButtonDefaults.buttonColors(containerColor = Green, contentColor = Bg)) { Text("OYNA  →", fontWeight = FontWeight.Black, letterSpacing = 1.sp) }
            }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Stat("⭐", "12.480", "EN İYİ", Modifier.weight(1f)); Stat("🔥", "7 gün", "SERİ", Modifier.weight(1f)); Stat("🏆", "12", "SEVİYE", Modifier.weight(1f))
        }
        Text("KEŞFET", color = Muted, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
        CardButton("🎮", "Oyun Modları", "Klasik, Meyve, Uzay ve Arcade", onModes)
        CardButton("🗺", "Bölümler", "Kilidi aç ve üç yıldızı kovala", onLevels)
        CardButton("⚙", "Ayarlar", "Ses ve titreşim tercihleri", onSettings)
        Text("TRIPLIX • v0.2", Modifier.fillMaxWidth(), color = Muted, fontSize = 10.sp, textAlign = TextAlign.Center)
        Spacer(Modifier.height(8.dp))
    }
}

@Composable private fun Logo() {
    Box(Modifier.size(52.dp).clip(RoundedCornerShape(17.dp)).background(Brush.linearGradient(listOf(Green, Color(0xFF36C985)))), contentAlignment = Alignment.Center) { Text("T", color = Bg, fontSize = 29.sp, fontWeight = FontWeight.Black) }
}

@Composable private fun Stat(icon: String, value: String, label: String, modifier: Modifier) {
    Surface(modifier, RoundedCornerShape(18.dp), color = Panel2) { Column(Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) { Text(icon); Text(value, color = White, fontSize = 14.sp, fontWeight = FontWeight.Bold); Text(label, color = Muted, fontSize = 8.sp, fontWeight = FontWeight.Bold) } }
}

@Composable private fun CardButton(icon: String, title: String, desc: String, click: () -> Unit) {
    Surface(Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).clickable(onClick = click), RoundedCornerShape(20.dp), color = Panel, border = BorderStroke(1.dp, White.copy(.05f))) {
        Row(Modifier.padding(15.dp), verticalAlignment = Alignment.CenterVertically) { Box(Modifier.size(46.dp).clip(RoundedCornerShape(15.dp)).background(Bg), contentAlignment = Alignment.Center) { Text(icon, fontSize = 21.sp) }; Spacer(Modifier.width(13.dp)); Column(Modifier.weight(1f)) { Text(title, color = White, fontSize = 15.sp, fontWeight = FontWeight.Bold); Text(desc, color = Gray, fontSize = 11.sp) }; Text("›", color = Muted, fontSize = 27.sp) }
    }
}

@Composable private fun Header(title: String, back: () -> Unit) {
    Row(Modifier.fillMaxWidth().padding(bottom = 12.dp), verticalAlignment = Alignment.CenterVertically) { Surface(Modifier.size(42.dp).clip(CircleShape).clickable(onClick = back), CircleShape, color = Panel) { Box(contentAlignment = Alignment.Center) { Text("‹", color = White, fontSize = 27.sp) } }; Spacer(Modifier.width(13.dp)); Text(title, color = White, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold) }
}

@Composable private fun Modes(selected: GameMode, select: (GameMode) -> Unit, back: () -> Unit, next: () -> Unit) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(22.dp)) {
        Header("Oyun Modları", back); Text("Üçlüleri nasıl patlatmak istediğini seç.", color = Gray, fontSize = 13.sp); Spacer(Modifier.height(18.dp))
        GameMode.values().forEach { m -> val active = m == selected; Surface(Modifier.fillMaxWidth().padding(bottom = 11.dp).clip(RoundedCornerShape(21.dp)).clickable { select(m) }, RoundedCornerShape(21.dp), color = if (active) Panel2 else Panel, border = BorderStroke(1.dp, if (active) Green.copy(.55f) else White.copy(.05f))) { Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) { Box(Modifier.size(54.dp).clip(RoundedCornerShape(17.dp)).background(Bg), contentAlignment = Alignment.Center) { Text(m.icon, fontSize = 24.sp) }; Spacer(Modifier.width(14.dp)); Column(Modifier.weight(1f)) { Text(m.title, color = White, fontSize = 16.sp, fontWeight = FontWeight.Bold); Text(m.desc, color = Gray, fontSize = 12.sp) }; Text(if (active) "✓" else "○", color = if (active) Green else Muted, fontSize = 21.sp, fontWeight = FontWeight.Bold) } } }
        Spacer(Modifier.height(5.dp)); Button(next, Modifier.fillMaxWidth().height(53.dp), shape = RoundedCornerShape(18.dp), colors = ButtonDefaults.buttonColors(containerColor = Green, contentColor = Bg)) { Text("BÖLÜMLERE GEÇ  →", fontWeight = FontWeight.Black) }
    }
}

@Composable private fun Levels(mode: GameMode, current: Int, back: () -> Unit, play: (Int) -> Unit) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(22.dp)) {
        Header("Bölümler", back); Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) { Text(mode.icon, fontSize = 22.sp); Spacer(Modifier.width(8.dp)); Text(mode.title, color = Green, fontWeight = FontWeight.Bold); Spacer(Modifier.weight(1f)); Text("60 bölüm", color = Gray, fontSize = 12.sp) }; Spacer(Modifier.height(15.dp)); Progress(current); Spacer(Modifier.height(16.dp))
        for (r in 0 until 20) { Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) { for (c in 0 until 3) { val n = r * 3 + c + 1; val locked = n > current + 5; LevelCard(n, locked, Modifier.weight(1f), play) } }; Spacer(Modifier.height(10.dp)) }
    }
}

@Composable private fun Progress(level: Int) {
    Surface(Modifier.fillMaxWidth(), RoundedCornerShape(19.dp), color = Panel) { Column(Modifier.padding(15.dp)) { Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text("İLERLEME", color = Muted, fontSize = 9.sp, fontWeight = FontWeight.Bold); Text("%${(level * 100 / 60).coerceAtLeast(2)}", color = Green, fontSize = 10.sp, fontWeight = FontWeight.Bold) }; Spacer(Modifier.height(9.dp)); Box(Modifier.fillMaxWidth().height(8.dp).clip(CircleShape).background(Bg)) { Box(Modifier.fillMaxWidth((level / 60f).coerceIn(.03f, 1f)).height(8.dp).clip(CircleShape).background(Green)) } } }
}

@Composable private fun LevelCard(number: Int, locked: Boolean, modifier: Modifier, play: (Int) -> Unit) {
    Surface(modifier.height(82.dp).clip(RoundedCornerShape(18.dp)).clickable(enabled = !locked) { play(number) }, RoundedCornerShape(18.dp), color = if (locked) Panel.copy(.5f) else Panel2, border = BorderStroke(1.dp, if (number == 1) Green.copy(.5f) else White.copy(.04f))) { Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) { Text(if (locked) "🔒" else number.toString(), color = if (locked) Muted else White, fontSize = if (locked) 18.sp else 20.sp, fontWeight = FontWeight.ExtraBold); if (!locked) Text("★★★", color = if (number <= 3) Gold else Muted, fontSize = 9.sp) } }
}

@Composable private fun Game(mode: GameMode, level: Int, back: () -> Unit) {
    val size = 7
    val scope = rememberCoroutineScope()
    var board by remember(level) { mutableStateOf((0 until 49).map { (it * 13 + level * 5) % 6 }) }
    var selected by remember(level) { mutableStateOf(emptyList<Int>()) }
    var popping by remember(level) { mutableStateOf(emptySet<Int>()) }
    var score by remember(level) { mutableIntStateOf(0) }
    var moves by remember(level) { mutableIntStateOf(30) }
    var combo by remember(level) { mutableIntStateOf(0) }
    var resolving by remember(level) { mutableStateOf(false) }
    var banner by remember(level) { mutableStateOf("") }

    fun refill(indices: Set<Int>) {
        val next = board.toMutableList()
        indices.forEach { next[it] = Random.nextInt(Gems.size) }
        board = next
    }

    fun tap(i: Int) {
        if (resolving || moves <= 0) return
        if (selected.contains(i)) { selected = selected.filterNot { it == i }; return }
        val chosenType = selected.firstOrNull()?.let { board[it] }
        if (chosenType != null && board[i] != chosenType) { selected = listOf(i); return }
        val next = selected + i
        if (next.size < 3) { selected = next; return }
        val burst = next.toSet()
        resolving = true
        selected = next
        popping = burst
        moves--
        combo++
        val gained = 150 * combo
        score += gained
        banner = "+$gained   x$combo COMBO"
        scope.launch {
            delay(240)
            refill(burst)
            popping = emptySet()
            selected = emptyList()
            resolving = false
            delay(650)
            banner = ""
        }
    }

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(18.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Surface(Modifier.size(42.dp).clip(CircleShape).clickable(onClick = back), CircleShape, color = Panel) { Box(contentAlignment = Alignment.Center) { Text("‹", color = White, fontSize = 27.sp) } }
            Spacer(Modifier.width(12.dp)); Column(Modifier.weight(1f)) { Text(mode.title.uppercase(), color = Green, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp); Text("Bölüm $level", color = White, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold) }
            Text("⭐ $score", color = Gold, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(13.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(9.dp)) { GameStat("SEÇİM", "${selected.size}/3", Modifier.weight(1f)); GameStat("COMBO", "x$combo", Modifier.weight(1f)); GameStat("HAMLE", "$moves", Modifier.weight(1f)) }
        Spacer(Modifier.height(14.dp))
        Box(Modifier.fillMaxWidth()) {
            Surface(Modifier.fillMaxWidth(), RoundedCornerShape(24.dp), color = Panel, border = BorderStroke(1.dp, if (selected.isNotEmpty()) Green.copy(.35f) else White.copy(.06f))) {
                Column(Modifier.padding(9.dp)) {
                    for (r in 0 until size) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            for (c in 0 until size) { val i = r * size + c; Tile(board[i], selected.contains(i), popping.contains(i), Modifier.weight(1f)) { tap(i) } }
                        }
                        if (r < size - 1) Spacer(Modifier.height(4.dp))
                    }
                }
            }
            AnimatedVisibility(visible = banner.isNotEmpty(), enter = fadeIn(tween(120)), exit = fadeOut(tween(220)), modifier = Modifier.align(Alignment.Center)) {
                Surface(RoundedCornerShape(18.dp), color = Green, shadowElevation = 10.dp) { Text(banner, Modifier.padding(horizontal = 18.dp, vertical = 11.dp), color = Bg, fontSize = 16.sp, fontWeight = FontWeight.Black) }
            }
        }
        Spacer(Modifier.height(12.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Tool("↻", "KARIŞTIR", Modifier.weight(1f)) { if (!resolving) { board = board.shuffled(); selected = emptyList(); combo = 0 } }
            Tool("💡", "İPUCU", Modifier.weight(1f)) { if (!resolving) { val t = board.first(); selected = board.indices.filter { board[it] == t }.take(2) } }
            Tool("↶", "SIFIRLA", Modifier.weight(1f)) { if (!resolving) { board = (0 until 49).map { (it * 13 + level * 5) % 6 }; selected = emptyList(); score = 0; combo = 0; moves = 30 } }
        }
        Spacer(Modifier.height(12.dp)); Surface(Modifier.fillMaxWidth(), RoundedCornerShape(17.dp), color = Panel2) { Text(if (selected.isEmpty()) "💡 Aynı türden üç taşı seç. Üçüncü dokunuşta patlar!" else "💎 ${selected.size}/3 seçildi — aynı türden devam et.", Modifier.padding(13.dp), color = Gray, fontSize = 11.sp) }
    }
}

@Composable private fun GameStat(label: String, value: String, modifier: Modifier) {
    Surface(modifier, RoundedCornerShape(15.dp), color = Panel) { Column(Modifier.padding(9.dp), horizontalAlignment = Alignment.CenterHorizontally) { Text(value, color = White, fontWeight = FontWeight.ExtraBold); Text(label, color = Muted, fontSize = 8.sp, fontWeight = FontWeight.Bold) } }
}

@Composable private fun Tile(type: Int, selected: Boolean, popping: Boolean, modifier: Modifier, click: () -> Unit) {
    val scale by animateFloatAsState(if (popping) 1.22f else 1f, tween(if (popping) 180 else 220), label = "tileScale")
    val alpha by animateFloatAsState(if (popping) 0.18f else 1f, tween(if (popping) 220 else 180), label = "tileAlpha")
    val glow = if (selected) Green else Gems[type]
    Box(modifier.height(42.dp).graphicsLayer { scaleX = scale; scaleY = scale; this.alpha = alpha }.clip(RoundedCornerShape(11.dp)).background(Brush.linearGradient(listOf(Gems[type], Gems[type].copy(.45f)))).clickable(onClick = click), contentAlignment = Alignment.Center) {
        Text(Symbols[type], color = Color.White, fontSize = if (selected) 20.sp else 17.sp, fontWeight = FontWeight.Black)
        if (selected) Box(Modifier.fillMaxSize().background(glow.copy(.24f)))
        if (popping) Text("✦", color = White, fontSize = 28.sp, fontWeight = FontWeight.Black)
    }
}

@Composable private fun Tool(icon: String, label: String, modifier: Modifier, click: () -> Unit) {
    Surface(modifier.clip(RoundedCornerShape(16.dp)).clickable(onClick = click), RoundedCornerShape(16.dp), color = Panel) { Column(Modifier.padding(vertical = 10.dp), horizontalAlignment = Alignment.CenterHorizontally) { Text(icon); Text(label, color = Gray, fontSize = 8.sp, fontWeight = FontWeight.Bold) } }
}

@Composable private fun Settings(back: () -> Unit) {
    var sound by remember { mutableStateOf(true) }
    var vibration by remember { mutableStateOf(true) }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(22.dp)) { Header("Ayarlar", back); Setting("🔊", "Ses efektleri", sound) { sound = !sound }; Setting("📳", "Titreşim", vibration) { vibration = !vibration }; Spacer(Modifier.height(15.dp)); OutlinedButton({}, Modifier.fillMaxWidth().height(50.dp), shape = RoundedCornerShape(17.dp), border = BorderStroke(1.dp, White.copy(.08f))) { Text("TRIPLIX HAKKINDA", color = Gray, fontWeight = FontWeight.Bold) } }
}

@Composable private fun Setting(icon: String, title: String, on: Boolean, click: () -> Unit) {
    Surface(Modifier.fillMaxWidth().padding(bottom=10.dp).clip(RoundedCornerShape(19.dp)).clickable(onClick=click), RoundedCornerShape(19.dp), color=Panel) { Row(Modifier.padding(16.dp), verticalAlignment=Alignment.CenterVertically) { Text(icon, fontSize=21.sp); Spacer(Modifier.width(14.dp)); Text(title, Modifier.weight(1f), color=White, fontWeight=FontWeight.Bold); Text(if(on) "AÇIK" else "KAPALI", color=if(on) Green else Muted, fontSize=9.sp, fontWeight=FontWeight.Black) } }
}
