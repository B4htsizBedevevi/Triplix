package com.triplix.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { TriplixApp() }
    }
}

@Composable
fun TriplixApp() {
    var started by remember { mutableStateOf(false) }
    MaterialTheme(colorScheme = darkColorScheme()) {
        Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFF0B0D14)) {
            if (started) GameScreen() else HomeScreen { started = true }
        }
    }
}

@Composable
private fun HomeScreen(onStart: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text("TRIPLIX", fontSize = 44.sp, fontWeight = FontWeight.Black, color = Color.White)
        Spacer(Modifier.height(8.dp))
        Text("Triple Match Puzzle", color = Color(0xFFB8BBC7), fontSize = 16.sp)
        Spacer(Modifier.height(44.dp))
        Button(onClick = onStart, modifier = Modifier.fillMaxWidth().height(58.dp), shape = RoundedCornerShape(18.dp)) {
            Text("OYNA", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun GameScreen() {
    var score by remember { mutableIntStateOf(0) }
    var tray by remember { mutableStateOf(listOf<Int>()) }
    val tiles = remember { (0 until 18).map { it % 6 } }
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("LEVEL 1", color = Color.White, fontWeight = FontWeight.Bold)
            Text("⭐ $score", color = Color.White, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(18.dp))
        Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            tiles.chunked(6).forEachIndexed { row, chunk ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(vertical = 4.dp)) {
                    chunk.forEachIndexed { col, type ->
                        val index = row * 6 + col
                        Button(onClick = { if (tray.size < 7) { tray = tray + type; score += 10 } }, modifier = Modifier.size(50.dp), contentPadding = PaddingValues(0.dp), shape = RoundedCornerShape(12.dp)) {
                            Text("●", fontSize = 22.sp)
                        }
                    }
                }
            }
        }
        Text("TRAY  ${tray.size}/7", color = Color(0xFFB8BBC7), fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth().height(64.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            repeat(7) { i ->
                Box(Modifier.weight(1f).fillMaxHeight().background(Color(0xFF171A24), RoundedCornerShape(10.dp)), contentAlignment = Alignment.Center) {
                    if (i < tray.size) Text("●", color = Color.White, fontSize = 20.sp)
                }
            }
        }
    }
}
