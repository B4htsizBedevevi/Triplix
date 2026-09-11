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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.abs

private val Bg = Color(0xFF070A12)
private val Bg2 = Color(0xFF0C1220)
private val Card = Color(0xFF111827)
private val Card2 = Color(0xFF151F31)
private val Accent = Color(0xFF6EE7A8)
private val AccentDark = Color(0xFF2DBA78)
private val Primary = Color(0xFFF8FAFC)
private val Secondary = Color(0xFF8F9BB0)
private val Muted = Color(0xFF536176)
private val Gold = Color(0xFFFFC857)
private val Red = Color(0xFFFF6B78)
private val TileColors = listOf(
    Color(0xFF6EE7A8), Color(0xFF6EA8FF), Color(0xFFFF7A9B),
    Color(0xFFFFC857), Color(0xFFB58CFF), Color(0xFF49D6D1)
)
private val TileSymbols = listOf("◆", "●", "✦", "▲", "■", "⬟")

private enum class Screen { HOME, MODES, LEVELS, GAME, SETTINGS }
private enum class Mode(val title: String, val subtitle: String, val icon: String) {
    CLASSIC("Klasik", "Saf eşleştirme", "◆"),
    FRUIT("Meyve", "Tatlı seri", "🍓"),
    SPACE("Uzay", "Yıldız avı", "✦"),
    ARCADE("Arcade", "Hızlı ve çılgın", "⚡")
}

@Composable
fun TriplixApp() {
    MaterialTheme {
        Surface(Modifier.fillMaxSize(), color = Bg) {
            var screen by remember { mutableStateOf(Screen.HOME) }
            var mode by remember { mutableStateOf(Mode.CLASSIC) }
            var level by remember { mutableIntStateOf(1) }

            when (screen) {
                Screen.HOME -> HomeScreen(
                    onPlay = { screen = Screen.GAME },
                    onModes = { screen = Screen.MODES },
                    onLevels = { screen = Screen.LEVELS },
                    onSettings = { screen = Screen.SETTINGS }
                )
                Screen.MODES -> ModesScreen(
                    selected = mode,
                    onSelect = { mode = it },
                    onBack = { screen = Screen.HOME },
                    onContinue = { screen = Screen.LEVELS }
                )
                Screen.LEVELS -> LevelsScreen(
                    mode = mode,
                    currentLevel = level,
                    onBack = { screen = Screen.HOME },
                    onPlay = { level = it; screen = Screen.GAME }
                )
                Screen.GAME -> GameScreen(
                    mode = mode,
                    level = level,
                    onBack = { screen = Screen.LEVELS }
                )
                Screen.SETTINGS -> SettingsScreen(onBack = { screen = Screen.HOME })
            }
        }
    }
}

@Composable
private fun HomeScreen(onPlay: () -> Unit, onModes: () -> Unit, onLevels: () -> Unit, onSettings: () -> Unit) {
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 22.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Spacer(Modifier.height(12.dp))
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text("TRIPLIX", color = Primary, fontSize = 29.sp, fontWeight = FontWeight.Black, letterSpacing = 2.sp)
                Text("Eşleştir. Patlat. Tekrar oyna.", color = Secondary, fontSize = 13.sp)
            }
            LogoMark()
        }

        Surface(
            Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(30.dp),
            color = Card,
            border = BorderStroke(1.dp, Color.White.copy(alpha = .06f))
        ) {
            Column(Modifier.padding(22.dp)) {
                Text("BUGÜNÜN SERİSİ", color = Accent, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.4.sp)
                Spacer(Modifier.height(7.dp))
                Text("Hazır mısın?", color = Primary, fontSize = 27.sp, fontWeight = FontWeight.ExtraBold)
                Spacer(Modifier.height(5.dp))
                Text("Kısa bir oyun, yüksek skor ve biraz daha iyi bir hamle.", color = Secondary, fontSize = 13.sp, lineHeight = 19.sp)
                Spacer(Modifier.height(18.dp))
                Button(
                    onClick = onPlay,
                    Modifier.fillMaxWidth().height(54.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Accent, contentColor = Bg)
                ) { Text("OYNA  →", fontWeight = FontWeight.Black, letterSpacing = 1.sp) }
            }
        }

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            HomeStat("⭐", "EN İYİ", "12.480", Modifier.weight(1f))
            HomeStat("🔥", "SERİ", "7 gün", Modifier.weight(1f))
            HomeStat("🏆", "SEVİYE", "12", Modifier.weight(1f))
        }

        Text("KEŞFET", color = Muted, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
        MenuCard("🎮", "Oyun Modları", "Klasik, Meyve, Uzay ve Arcade", onModes)
        MenuCard("🗺", "Bölümler", "Kilidi aç, ilerle, üç yıldızı kovala", onLevels)
        MenuCard("⚙", "Ayarlar", "Ses, titreşim ve oyun tercihleri", onSettings)

        Spacer(Modifier.height(10.dp))
        Text("TRIPLIX  •  v0.1", Modifier.fillMaxWidth(), color = Muted, fontSize = 11.sp, textAlign = TextAlign.Center)
    }
}

@Composable
private fun LogoMark() {
    Box(Modifier.size(50.dp).clip(RoundedCornerShape(16.dp)).background(Brush.linearGradient(listOf(Accent, Color(0xFF3ED18A)))), contentAlignment = Alignment.Center) {
        Text("T", color = Bg, fontSize = 28.sp, fontWeight = FontWeight.Black)
    }
}

@Composable
private fun HomeStat(icon: String, label: String, value: String, modifier: Modifier) {
    Surface(modifier, shape = RoundedCornerShape(18.dp), color = Card2) {
        Column(Modifier.padding(vertical = 13.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(icon, fontSize = 17.sp)
            Text(value, color = Primary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text(label, color = Muted, fontSize = 8.sp, fontWeight = FontWeight.Bold, letterSpacing = .8.sp)
        }
    }
}

@Composable
private fun MenuCard(icon: String, title: String, subtitle: String, onClick: () -> Unit) {
    Surface(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp), color = Card,
        border = BorderStroke(1.dp, Color.White.copy(alpha = .05f))
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(Modifier.size(46.dp), shape = RoundedCornerShape(15.dp), color = Bg2) { Box(contentAlignment = Alignment.Center) { Text(icon, fontSize = 21.sp) } }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(title, color = Primary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Text(subtitle, color = Secondary, fontSize = 11.sp)
            }
            Text("›", color = Muted, fontSize = 27.sp)
        }
    }
}

@Composable
private fun TopBar(title: String, onBack: () -> Unit) {
    Row(Modifier.fillMaxWidth().padding(bottom = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Surface(Modifier.size(42.dp).clip(CircleShape).clickable(onClick = onBack), color = Card) {
            Box(contentAlignment = Alignment.Center) { Text("‹", color = Primary, fontSize = 27.sp) }
        }
        Spacer(Modifier.width(14.dp))
        Text(title, color = Primary, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
    }
}

@Composable
private fun ModesScreen(selected: Mode, onSelect: (Mode) -> Unit, onBack: () -> Unit, onContinue: () -> Unit) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(22.dp)) {
        TopBar("Oyun Modları", onBack)
        Text("Nasıl oynamak istiyorsun?", color = Secondary, fontSize = 13.sp)
        Spacer(Modifier.height(18.dp))
        Mode.values().forEach { mode ->
            val active = mode == selected
            Surface(
                Modifier.fillMaxWidth().padding(bottom = 12.dp).clip(RoundedCornerShape(22.dp)).clickable { onSelect(mode) },
                shape = RoundedCornerShape(22.dp),
                color = if (active) Card2 else Card,
                border = BorderStroke(1.dp, if (active) Accent.copy(alpha = .55f) else Color.White.copy(alpha = .05f))
            ) {
                Row(Modifier.padding(17.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(54.dp).clip(RoundedCornerShape(17.dp)).background(if (active) Accent.copy(alpha = .14f) else Bg2), contentAlignment = Alignment.Center) { Text(mode.icon, fontSize = 25.sp) }
                    Spacer(Modifier.width(15.dp))
                    Column(Modifier.weight(1f)) {
                        Text(mode.title, color = Primary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text(mode.subtitle, color = Secondary, fontSize = 12.sp)
                    }
                    Text(if (active) "✓" else "○", color = if (active) Accent else Muted, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
        Spacer(Modifier.height(5.dp))
        Button(onClick = onContinue, Modifier.fillMaxWidth().height(54.dp), shape = RoundedCornerShape(18.dp), colors = ButtonDefaults.buttonColors(containerColor = Accent, contentColor = Bg)) {
            Text("BÖLÜMLERE GEÇ  →", fontWeight = FontWeight.Black)
        }
    }
}

@Composable
private fun LevelsScreen(mode: Mode, currentLevel: Int, onBack: () -> Unit, onPlay: (Int) -> Unit) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(22.dp)) {
        TopBar("Bölümler", onBack)
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(mode.icon, fontSize = 22.sp)
            Spacer(Modifier.width(8.dp))
            Text(mode.title, color = Accent, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.weight(1f))
            Text("12 / 60", color = Secondary, fontSize = 12.sp)
        }
        Spacer(Modifier.height(18.dp))
        LevelProgress(currentLevel)
        Spacer(Modifier.height(18.dp))
        for (row in 0 until 10) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                for (col in 0 until 3) {
                    val number = row * 3 + col + 1
                    val locked = number > currentLevel + 5
                    LevelCard(number, locked) { if (!locked) onPlay(number) }
                }
            }
            Spacer(Modifier.height(10.dp))
        }
    }
}

@Composable
private fun LevelProgress(currentLevel: Int) {
    Surface(Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), color = Card) {
        Column(Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("İLERLEME", color = Muted, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Text("%${(currentLevel * 100 / 60).coerceAtLeast(2)}", color = Accent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(10.dp))
            Box(Modifier.fillMaxWidth().height(8.dp).clip(CircleShape).background(Bg2)) {
                Box(Modifier.fillMaxWidth((currentLevel / 60f).coerceIn(.03f, 1f)).height(8.dp).clip(CircleShape).background(Accent))
            }
        }
    }
}

@Composable
private fun LevelCard(number: Int, locked: Boolean, onClick: () -> Unit) {
    Surface(
        Modifier.weight(1f).height(88.dp).clip(RoundedCornerShape(19.dp)).clickable(enabled = !locked, onClick = onClick),
        shape = RoundedCornerShape(19.dp), color = if (locked) Card.copy(alpha = .55f) else Card2,
        border = BorderStroke(1.dp, if (number == 1) Accent.copy(alpha = .55f) else Color.White.copy(alpha = .04f))
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text(if (locked) "🔒" else number.toString(), color = if (locked) Muted else Primary, fontSize = if (locked) 18.sp else 20.sp, fontWeight = FontWeight.ExtraBold)
            if (!locked) {
                Text("★★★", color = if (number <= 3) Gold else Muted, fontSize = 9.sp, letterSpacing = 1.sp)
            }
        }
    }
}

@Composable
private fun GameScreen(mode: Mode, level: Int, onBack: () -> Unit) {
    val boardSize = 7
    var board by remember { mutableStateOf((0 until boardSize * boardSize).map { (it * 17 + level * 3) % TileColors.size }) }
    var selected by remember { mutableStateOf<Int?>(null) }
    var score by remember { mutableIntStateOf(0) }
    var moves by remember { mutableIntStateOf(30) }
    var combo by remember { mutableIntStateOf(0) }

    fun swap(a: Int, b: Int) {
        if (moves <= 0) return
        val next = board.toMutableList()
        val temp = next[a]; next[a] = next[b]; next[b] = temp
        board = next
        moves--
        combo = if (combo >= 4) 1 else combo + 1
        score += 120 * combo
    }

    fun handleTap(index: Int) {
        val first = selected
        if (first == null) { selected = index; return }
        if (first == index) { selected = null; return }
        val ar = first / boardSize; val ac = first % boardSize
        val br = index / boardSize; val bc = index % boardSize
        if (abs(ar - br) + abs(ac - bc) == 1) swap(first, index)
        selected = null
    }

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 18.dp, vertical = 18.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Surface(Modifier.size(42.dp).clip(CircleShape).clickable(onClick = onBack), color = Card) { Box(contentAlignment = Alignment.Center) { Text("‹", color = Primary, fontSize = 27.sp) } }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(mode.title.uppercase(), color = Accent, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.3.sp)
                Text("Bölüm $level", color = Primary, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
            }
            Surface(shape = RoundedCornerShape(14.dp), color = Card) { Text("⭐ $score", Modifier.padding(horizontal = 11.dp, vertical = 8.dp), color = Gold, fontSize = 12.sp, fontWeight = FontWeight.Bold) }
        }
        Spacer(Modifier.height(15.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            GameStat("HAMLE", "$moves", Modifier.weight(1f))
            GameStat("COMBO", "x$combo", Modifier.weight(1f))
            GameStat("HEDEF", "${level * 500}", Modifier.weight(1f))
        }
        Spacer(Modifier.height(16.dp))
        Surface(Modifier.fillMaxWidth(), shape = RoundedCornerShape(25.dp), color = Card, border = BorderStroke(1.dp, Color.White.copy(alpha = .06f))) {
            Column(Modifier.padding(10.dp)) {
                for (r in 0 until boardSize) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                        for (c in 0 until boardSize) {
                            val index = r * boardSize + c
                            Tile(board[index], selected == index, Modifier.weight(1f)) { handleTap(index) }
                        }
                    }
                    if (r != boardSize - 1) Spacer(Modifier.height(5.dp))
                }
            }
        }
        Spacer(Modifier.height(14.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(9.dp)) {
            ToolButton("↻", "KARIŞTIR", Modifier.weight(1f)) { board = board.shuffled(); selected = null }
            ToolButton("💡", "İPUCU", Modifier.weight(1f)) { selected = 0 }
            ToolButton("↶", "GERİ AL", Modifier.weight(1f)) { combo = 0 }
        }
        Spacer(Modifier.height(14.dp))
        Surface(Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp), color = Card2) {
            Text("💡  İki komşu taşı seç. Üçlü ve daha uzun seriler daha yüksek skor getirir.", Modifier.padding(14.dp), color = Secondary, fontSize = 11.sp, lineHeight = 16.sp)
        }
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun GameStat(label: String, value: String, modifier: Modifier) {
    Surface(modifier, shape = RoundedCornerShape(16.dp), color = Card) {
        Column(Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, color = Primary, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
            Text(label, color = Muted, fontSize = 8.sp, fontWeight = FontWeight.Bold, letterSpacing = .8.sp)
        }
    }
}

@Composable
private fun Tile(type: Int, selected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    val base = TileColors[type]
    val brush = Brush.linearGradient(listOf(base.copy(alpha = .98f), base.copy(alpha = .48f)))
    Box(
        modifier.size(42.dp).clip(RoundedCornerShape(12.dp)).background(brush).clickable(onClick = onClick).alpha(if (selected) 1f else .93f),
        contentAlignment = Alignment.Center
    ) {
        Surface(Modifier.fillMaxSize().padding(3.dp), shape = RoundedCornerShape(10.dp), color = Color.White.copy(alpha = .08f)) { }
        Text(TileSymbols[type], color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Black)
        if (selected) Box(Modifier.fillMaxSize().background(Color.White.copy(alpha = .16f)))
    }
}

@Composable
private fun ToolButton(icon: String, label: String, modifier: Modifier, onClick: () -> Unit) {
    Surface(modifier.clip(RoundedCornerShape(16.dp)).clickable(onClick = onClick), shape = RoundedCornerShape(16.dp), color = Card) {
        Column(Modifier.padding(vertical = 11.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(icon, fontSize = 18.sp)
            Text(label, color = Secondary, fontSize = 8.sp, fontWeight = FontWeight.Bold, letterSpacing = .5.sp)
        }
    }
}

@Composable
private fun SettingsScreen(onBack: () -> Unit) {
    var sound by remember { mutableStateOf(true) }
    var vibration by remember { mutableStateOf(true) }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(22.dp)) {
        TopBar("Ayarlar", onBack)
        SettingRow("🔊", "Ses efektleri", "Taş ve combo sesleri", sound) { sound = !sound }
        SettingRow("📳", "Titreşim", "Hamle geri bildirimi", vibration) { vibration = !vibration }
        SettingRow("🎨", "Tema", "Koyu tema", true, enabled = false) { }
        Spacer(Modifier.height(20.dp))
        OutlinedButton(onClick = { }, Modifier.fillMaxWidth().height(50.dp), shape = RoundedCornerShape(17.dp), border = BorderStroke(1.dp, Color.White.copy(alpha = .08f))) {
            Text("OYNANIŞ HAKKINDA", color = Secondary, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun SettingRow(icon: String, title: String, subtitle: String, checked: Boolean, enabled: Boolean = true, onClick: () -> Unit) {
    Surface(Modifier.fillMaxWidth().padding(bottom = 10.dp).clip(RoundedCornerShape(20.dp)).clickable(enabled = enabled, onClick = onClick), shape = RoundedCornerShape(20.dp), color = Card) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(icon, fontSize = 21.sp)
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) { Text(title, color = if (enabled) Primary else Muted, fontSize = 15.sp, fontWeight = FontWeight.Bold); Text(subtitle, color = Muted, fontSize = 11.sp) }
            Text(if (checked) "AÇIK" else "KAPALI", color = if (checked && enabled) Accent else Muted, fontSize = 9.sp, fontWeight = FontWeight.Black)
        }
    }
}
