package com.triplix.app

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.foundation.lazy.grid.itemsIndexed
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
import androidx.compose.runtime.rememberSaveable
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

private val Bg = Color(0xFF07110D)
private val Panel = Color(0xFF10231A)
private val Panel2 = Color(0xFF173326)
private val Cream = Color(0xFFFFF5E6)
private val Green = Color(0xFF49E27D)
private val Gold = Color(0xFFFFD166)
private val Blue = Color(0xFF65C9FF)
private val Purple = Color(0xFF9A76FF)

private val Gems = listOf("🍓", "🍋", "🍇", "🍊", "🥝", "🫐")
private val GemColors = listOf(
    Color(0xFFFF647C), Color(0xFFFFD166), Color(0xFFB07CFF),
    Color(0xFFFF9A5C), Color(0xFF63D88A), Color(0xFF6EC8FF)
)
private enum class Screen { HOME, LEVELS, GAME, REWARDS }

class TriplixV4Activity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { TriplixV4App() }
    }
}

@Composable
fun TriplixV4App() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val prefs = remember { context.getSharedPreferences("triplix", Context.MODE_PRIVATE) }
    var screen by rememberSaveable { mutableStateOf(Screen.HOME) }
    var level by rememberSaveable { mutableIntStateOf(prefs.getInt("level", 1)) }
    var coins by rememberSaveable { mutableIntStateOf(prefs.getInt("coins", 250)) }
    var best by rememberSaveable { mutableIntStateOf(prefs.getInt("best", 0)) }
    var streak by rememberSaveable { mutableIntStateOf(prefs.getInt("streak", 1)) }
    var dailyTaken by rememberSaveable { mutableStateOf(prefs.getBoolean("daily", false)) }

    fun save() {
        prefs.edit().putInt("level", level).putInt("coins", coins)
            .putInt("best", best).putInt("streak", streak)
            .putBoolean("daily", dailyTaken).apply()
    }
    fun finishLevel(score: Int) {
        coins += 20 + score / 100
        if (score > best) best = score
        if (level < 60) level += 1
        streak += 1
        save()
        screen = Screen.LEVELS
    }

    BackHandler(enabled = screen != Screen.HOME) {
        screen = when (screen) {
            Screen.GAME -> Screen.LEVELS
            Screen.LEVELS, Screen.REWARDS -> Screen.HOME
            Screen.HOME -> Screen.HOME
        }
    }

    MaterialTheme(colorScheme = darkColorScheme(background = Bg, surface = Panel, primary = Green, onPrimary = Color(0xFF002D15), onSurface = Cream)) {
        Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Bg, Color(0xFF0B1C13), Bg)))) {
            when (screen) {
                Screen.HOME -> HomeScreen(level, coins, best, streak, dailyTaken, { screen = Screen.LEVELS }, { screen = Screen.REWARDS }) {
                    if (!dailyTaken) { coins += 100; dailyTaken = true; save() }
                }
                Screen.LEVELS -> LevelScreen(level, { screen = Screen.HOME }) { selected -> level = selected; screen = Screen.GAME }
                Screen.GAME -> GameScreen(level, { screen = Screen.LEVELS }, ::finishLevel) { coins += it; save() }
                Screen.REWARDS -> RewardsScreen(coins, streak) { screen = Screen.HOME }
            }
        }
    }
}

@Composable
private fun HomeScreen(level: Int, coins: Int, best: Int, streak: Int, dailyTaken: Boolean, onPlay: () -> Unit, onRewards: () -> Unit, onDaily: () -> Unit) {
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp, 18.dp, 16.dp, 28.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(48.dp).clip(CircleShape).background(Brush.linearGradient(listOf(Gold, Purple))), contentAlignment = Alignment.Center) { Text("3", fontSize = 28.sp, fontWeight = FontWeight.Black, color = Color.White) }
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) { Text("TRIPLIX", color = Cream, fontSize = 23.sp, fontWeight = FontWeight.Black); Text("BİRLEŞTİR • PATLAT • YÜKSEL", color = Color.White.copy(.48f), fontSize = 9.sp, fontWeight = FontWeight.Bold) }
                Pill("🪙 $coins", Gold)
            }
        }
        item {
            Surface(color = Panel2, shape = RoundedCornerShape(30.dp), modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(22.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("✨", fontSize = 28.sp); Spacer(Modifier.height(5.dp)); Text("SEVİYE $level", color = Gold, fontSize = 13.sp, fontWeight = FontWeight.Black)
                    Text("TRIPLIX", color = Cream, fontSize = 42.sp, fontWeight = FontWeight.Black, letterSpacing = 2.sp)
                    Text("Aynı 3 taşı birleştir. Kombonu büyüt. Rekorunu kır.", color = Color.White.copy(.62f), fontSize = 11.sp, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = onPlay, modifier = Modifier.fillMaxWidth().height(58.dp), shape = RoundedCornerShape(18.dp), colors = ButtonDefaults.buttonColors(containerColor = Green)) { Text("▶  OYNA", fontSize = 19.sp, fontWeight = FontWeight.Black) }
                }
            }
        }
        item { Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) { StatCard("🔥", "$streak", "Seri", Modifier.weight(1f)); StatCard("🏆", "$best", "Rekor", Modifier.weight(1f)); StatCard("🎯", "$level/60", "İlerleme", Modifier.weight(1f)) } }
        item {
            Surface(color = Color.White.copy(.045f), shape = RoundedCornerShape(20.dp), modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(15.dp)) {
                    Text("🎁 GÜNLÜK ÖDÜL", color = Cream, fontWeight = FontWeight.Black); Text("Her gün gir, bonusunu büyüt.", color = Color.White.copy(.48f), fontSize = 10.sp); Spacer(Modifier.height(9.dp))
                    Button(onClick = onDaily, enabled = !dailyTaken, modifier = Modifier.fillMaxWidth().height(46.dp), shape = RoundedCornerShape(14.dp)) { Text(if (dailyTaken) "✓ BUGÜN ALINDI" else "🪙  +100 COIN AL") }
                }
            }
        }
        item { Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) { ActionCard("🎁", "ÖDÜLLER", onRewards, Modifier.weight(1f)); ActionCard("💥", "BOOSTER", onPlay, Modifier.weight(1f)) } }
        item {
            Surface(color = Color.White.copy(.04f), shape = RoundedCornerShape(20.dp), modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(15.dp)) { Text("⚡ OYUNUN KURALI", color = Gold, fontWeight = FontWeight.Black, fontSize = 12.sp); Text("Komşu iki taşı değiştir. 3 veya daha fazlasını yatay/dikey yakala. Seri yaptıkça puan çarpanı yükselir.", color = Color.White.copy(.58f), fontSize = 10.sp, lineHeight = 15.sp) }
            }
        }
    }
}

@Composable
private fun LevelScreen(unlocked: Int, onBack: () -> Unit, onLevel: (Int) -> Unit) {
    Column(Modifier.fillMaxSize()) {
        Header("BÖLÜMLER", "60 seviyelik macera", onBack)
        Surface(color = Color.White.copy(.045f), shape = RoundedCornerShape(18.dp), modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp)) {
            Column(Modifier.padding(13.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text("İLERLEME", color = Color.White.copy(.48f), fontSize = 10.sp, fontWeight = FontWeight.Black); Text("$unlocked / 60", color = Gold, fontWeight = FontWeight.Black, fontSize = 11.sp) }
                Spacer(Modifier.height(7.dp)); LinearProgressIndicator(progress = { unlocked.coerceIn(0, 60) / 60f }, modifier = Modifier.fillMaxWidth().height(7.dp), color = Green, trackColor = Color.White.copy(.07f))
            }
        }
        Spacer(Modifier.height(8.dp))
        LazyVerticalGrid(columns = GridCells.Fixed(4), contentPadding = PaddingValues(14.dp), horizontalArrangement = Arrangement.spacedBy(9.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
            itemsIndexed((1..60).toList()) { _, n ->
                val open = n <= unlocked
                Surface(color = if (open) Color.White.copy(.065f) else Color.White.copy(.025f), shape = RoundedCornerShape(16.dp), modifier = Modifier.aspectRatio(1f).clickable(enabled = open) { onLevel(n) }) {
                    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) { Text(if (open) "$n" else "🔒", color = if (open) Cream else Color.White.copy(.25f), fontSize = 17.sp, fontWeight = FontWeight.Black); Text(if (open) "${"★".repeat((n % 3) + 1)}" else "", color = Gold, fontSize = 9.sp) }
                }
            }
        }
    }
}

@Composable
private fun GameScreen(level: Int, onBack: () -> Unit, onWin: (Int) -> Unit, onCoin: (Int) -> Unit) {
    val size = 7
    var board by remember(level) { mutableStateOf(newBoard(size)) }
    var selected by remember(level) { mutableIntStateOf(-1) }
    var score by remember(level) { mutableIntStateOf(0) }
    var moves by remember(level) { mutableIntStateOf(24) }
    var combo by remember(level) { mutableIntStateOf(0) }
    var message by remember(level) { mutableStateOf("İki komşu taşı değiştir") }
    var finished by remember(level) { mutableStateOf(false) }

    fun tap(index: Int) {
        if (finished || moves <= 0) return
        if (selected < 0) { selected = index; message = "Şimdi komşu bir taş seç"; return }
        if (selected == index) { selected = -1; message = "İptal edildi"; return }
        if (!adjacent(selected, index, size)) { selected = index; message = "Sadece komşu taşları değiştir"; return }
        val next = board.toMutableList(); val temp = next[selected]; next[selected] = next[index]; next[index] = temp
        val matches = findMatches(next, size); moves -= 1
        if (matches.isEmpty()) { message = "Eşleşme yok — başka hamle dene"; selected = -1; board = next; combo = 0; return }
        combo += 1
        val gained = matches.size * 20 * combo
        score += gained
        message = "+$gained • ${matches.size} taş • x$combo KOMBO"
        board = collapse(next, matches, size); selected = -1; onCoin(matches.size * 2)
        if (score >= 600 || moves <= 0) finished = true
    }

    Column(Modifier.fillMaxSize()) {
        Header("SEVİYE $level", "${if (moves > 0) "$moves hamle" else "Tur bitti"} • $message", onBack)
        Row(Modifier.fillMaxWidth().padding(horizontal = 14.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) { InfoCard("🏆", "$score", "PUAN", Modifier.weight(1f)); InfoCard("🔥", "x$combo", "KOMBO", Modifier.weight(1f)); InfoCard("🪙", "2+", "BONUS", Modifier.weight(1f)) }
        Spacer(Modifier.height(12.dp))
        Surface(color = Color.White.copy(.035f), shape = RoundedCornerShape(24.dp), modifier = Modifier.padding(horizontal = 12.dp).fillMaxWidth()) {
            LazyVerticalGrid(columns = GridCells.Fixed(size), contentPadding = PaddingValues(9.dp), horizontalArrangement = Arrangement.spacedBy(5.dp), verticalArrangement = Arrangement.spacedBy(5.dp), modifier = Modifier.fillMaxWidth()) {
                itemsIndexed(board) { index, type ->
                    val isSelected = index == selected
                    Box(Modifier.aspectRatio(1f).clip(RoundedCornerShape(13.dp)).background(GemColors[type].copy(if (isSelected) .42f else .18f)).border(1.5.dp, if (isSelected) Cream else GemColors[type].copy(.45f), RoundedCornerShape(13.dp)).clickable { tap(index) }, contentAlignment = Alignment.Center) {
                        Text(Gems[type], fontSize = 25.sp)
                        if (isSelected) Text("✦", color = Color.White, fontSize = 11.sp, modifier = Modifier.align(Alignment.TopEnd).padding(4.dp))
                    }
                }
            }
        }
        Spacer(Modifier.height(10.dp))
        Row(Modifier.fillMaxWidth().padding(horizontal = 14.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Booster("↩", "KARIŞTIR", Modifier.weight(1f)) { board = newBoard(size); selected = -1; message = "Tahta yenilendi" }
            Booster("💥", "BOMBA", Modifier.weight(1f)) { if (moves > 0) { val target = board[(size * size) / 2]; val changed = board.toMutableList(); for (i in changed.indices) if (changed[i] == target) changed[i] = Random.nextInt(Gems.size); board = changed; moves -= 1; message = "💥 Bomba kullanıldı" } }
        }
    }
    if (finished) {
        Surface(color = Color.Black.copy(.78f), modifier = Modifier.fillMaxSize()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Surface(color = Panel2, shape = RoundedCornerShape(28.dp), modifier = Modifier.fillMaxWidth().padding(28.dp)) {
                    Column(Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(if (score >= 600) "🎉" else "💪", fontSize = 54.sp); Text(if (score >= 600) "SEVİYE TAMAMLANDI" else "İYİ DENEMEYDİ", color = Cream, fontSize = 20.sp, fontWeight = FontWeight.Black, textAlign = TextAlign.Center); Text("$score PUAN", color = Gold, fontSize = 28.sp, fontWeight = FontWeight.Black); Text("x$combo kombo • tekrar oynayarak rekorunu kır", color = Color.White.copy(.58f), fontSize = 10.sp, textAlign = TextAlign.Center); Spacer(Modifier.height(14.dp))
                        Button(onClick = { onWin(score) }, modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(16.dp)) { Text("DEVAM ET  →", fontWeight = FontWeight.Black) }
                    }
                }
            }
        }
    }
}

@Composable
private fun RewardsScreen(coins: Int, streak: Int, onBack: () -> Unit) {
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item { Header("ÖDÜLLER", "İlerlemeni güçlendir", onBack) }
        item { RewardCard("🔥", "GÜNLÜK SERİ", "$streak gün", "Serini koru ve daha büyük bonuslar aç.", Green) }
        item { RewardCard("🪙", "COIN CÜZDANI", "$coins coin", "Coinleri booster ve özel içerikler için kullan.", Gold) }
        item { RewardCard("🏆", "REKOR AVCISI", "Yeni rekorlar", "Her seviyede daha yüksek kombo hedefle.", Purple) }
        item { RewardCard("💎", "KOLEKSİYON", "6 taş", "Yeni dünyalarda yeni taş setleri aç.", Blue) }
    }
}

@Composable
private fun Header(title: String, subtitle: String, onBack: () -> Unit) {
    Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(43.dp).clip(CircleShape).background(Color.White.copy(.07f)).clickable { onBack() }, contentAlignment = Alignment.Center) { Text("‹", color = Cream, fontSize = 35.sp) }
        Spacer(Modifier.width(10.dp)); Column(Modifier.weight(1f)) { Text(title, color = Cream, fontSize = 19.sp, fontWeight = FontWeight.Black); Text(subtitle, color = Color.White.copy(.48f), fontSize = 9.sp, maxLines = 1) }
    }
}

@Composable
private fun Pill(text: String, color: Color) { Surface(color = Color.White.copy(.07f), shape = RoundedCornerShape(14.dp)) { Text(text, Modifier.padding(horizontal = 10.dp, vertical = 7.dp), color = color, fontSize = 11.sp, fontWeight = FontWeight.Black) } }

@Composable
private fun StatCard(icon: String, value: String, label: String, modifier: Modifier) { Surface(color = Panel, shape = RoundedCornerShape(17.dp), modifier = modifier.height(75.dp)) { Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) { Text(icon, fontSize = 17.sp); Text(value, color = Cream, fontWeight = FontWeight.Black); Text(label, color = Color.White.copy(.43f), fontSize = 8.sp) } } }

@Composable
private fun ActionCard(icon: String, title: String, onClick: () -> Unit, modifier: Modifier) { Surface(color = Color.White.copy(.05f), shape = RoundedCornerShape(18.dp), modifier = modifier.clickable { onClick() }) { Column(Modifier.padding(15.dp), horizontalAlignment = Alignment.CenterHorizontally) { Text(icon, fontSize = 24.sp); Spacer(Modifier.height(5.dp)); Text(title, color = Cream, fontSize = 10.sp, fontWeight = FontWeight.Black) } } }

@Composable
private fun InfoCard(icon: String, value: String, label: String, modifier: Modifier) { Surface(color = Color.White.copy(.05f), shape = RoundedCornerShape(15.dp), modifier = modifier.height(64.dp)) { Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) { Row(verticalAlignment = Alignment.CenterVertically) { Text(icon, fontSize = 14.sp); Spacer(Modifier.width(3.dp)); Text(value, color = Cream, fontWeight = FontWeight.Black, fontSize = 13.sp) }; Text(label, color = Color.White.copy(.4f), fontSize = 7.sp, fontWeight = FontWeight.Bold) } } }

@Composable
private fun Booster(icon: String, title: String, modifier: Modifier, onClick: () -> Unit) { Button(onClick = onClick, modifier = modifier.height(45.dp), shape = RoundedCornerShape(13.dp), colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(.07f))) { Text("$icon  $title", fontSize = 9.sp, fontWeight = FontWeight.Black) } }

@Composable
private fun RewardCard(icon: String, title: String, value: String, desc: String, accent: Color) { Surface(color = Color.White.copy(.05f), shape = RoundedCornerShape(20.dp), modifier = Modifier.fillMaxWidth()) { Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) { Box(Modifier.size(54.dp).clip(RoundedCornerShape(16.dp)).background(accent.copy(.14f)), contentAlignment = Alignment.Center) { Text(icon, fontSize = 25.sp) }; Spacer(Modifier.width(12.dp)); Column(Modifier.weight(1f)) { Text(title, color = Cream, fontWeight = FontWeight.Black, fontSize = 12.sp); Text(value, color = accent, fontWeight = FontWeight.Black, fontSize = 18.sp); Text(desc, color = Color.White.copy(.48f), fontSize = 9.sp) } } } }

private fun newBoard(size: Int): List<Int> {
    val result = MutableList(size * size) { Random.nextInt(Gems.size) }
    repeat(50) {
        val matches = findMatches(result, size)
        if (matches.isNotEmpty()) for (i in matches) result[i] = Random.nextInt(Gems.size)
    }
    return result
}

private fun adjacent(a: Int, b: Int, size: Int): Boolean {
    val ax = a % size; val ay = a / size; val bx = b % size; val by = b / size
    return kotlin.math.abs(ax - bx) + kotlin.math.abs(ay - by) == 1
}

private fun findMatches(board: List<Int>, size: Int): Set<Int> {
    val result = mutableSetOf<Int>()
    for (row in 0 until size) {
        var start = 0
        while (start < size) {
            val type = board[row * size + start]; var end = start + 1
            while (end < size && board[row * size + end] == type) end++
            if (end - start >= 3) for (x in start until end) result.add(row * size + x)
            start = end
        }
    }
    for (col in 0 until size) {
        var start = 0
        while (start < size) {
            val type = board[start * size + col]; var end = start + 1
            while (end < size && board[end * size + col] == type) end++
            if (end - start >= 3) for (y in start until end) result.add(y * size + col)
            start = end
        }
    }
    return result
}

private fun collapse(board: List<Int>, matches: Set<Int>, size: Int): List<Int> {
    val result = board.toMutableList()
    for (index in matches) result[index] = -1
    for (col in 0 until size) {
        val values = mutableListOf<Int>()
        for (row in size - 1 downTo 0) { val value = result[row * size + col]; if (value >= 0) values.add(value) }
        var cursor = 0
        for (row in size - 1 downTo 0) { val pos = row * size + col; result[pos] = if (cursor < values.size) values[cursor++] else Random.nextInt(Gems.size) }
    }
    return result
}
