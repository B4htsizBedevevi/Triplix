package com.triplix.app

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.compose.ui.platform.LocalView
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.random.Random

private val BG4 = Color(0xFF06120D)
private val PANEL4 = Color(0xFF10271A)
private val PANEL4B = Color(0xFF173723)
private val CREAM4 = Color(0xFFFFF6E8)
private val GOLD4 = Color(0xFFFFD166)
private val GREEN4 = Color(0xFF42D879)
private val PURPLE4 = Color(0xFF8D64FF)
private val RED4 = Color(0xFFFF6177)
private val BLUE4 = Color(0xFF61C8FF)

private data class W4(val id:Int,val name:String,val icon:String,val desc:String,val tiles:List<String>,val accent:Color)
private data class T4(val id:Int,val type:Int,val x:Int,val y:Int,val layer:Int)
private enum class P4 { HOME,WORLDS,LEVELS,GAME,COLLECTION }

private val WW4 = listOf(
 W4(0,"Meyve Bahçesi","🍓","Tatlı eşleşmeler!",listOf("🍓","🍎","🍌","🍇","🍊","🍉"),RED4),
 W4(1,"Kristal Vadisi","💎","Parlak taşlar!",listOf("💎","🔷","💠","🔮","🟢","🟡"),BLUE4),
 W4(2,"Sihirli Objeler","🪄","Büyülü maceralar!",listOf("🧪","🪄","📖","🗝️","🔮","🧙"),PURPLE4),
 W4(3,"Kozmik Evren","🚀","Uzayın derinlikleri!",listOf("🚀","🪐","⭐","☄️","🌙","🌌"),Color(0xFF7770FF)),
 W4(4,"Sevimli Dostlar","🐱","Tatlı karakterler!",listOf("🐱","🐶","🐼","🐰","🐸","🐧"),Color(0xFFFFA864)),
 W4(5,"Antik Semboller","🏺","Zamana meydan oku!",listOf("☀️","👁️","🔺","🌀","🌿","🜁"),GOLD4),
 W4(6,"Doğa Elementleri","🌿","Doğanın gücü!",listOf("🌿","💧","🔥","🌪️","🪨","🌸"),GREEN4),
 W4(7,"Mevsimler","❄️","Her bölüm farklı!",listOf("❄️","🌸","☀️","🍁","🌧️","🌙"),BLUE4),
 W4(8,"Kraliyet","👑","Sadece TRIPLIX!",listOf("👑","❤️","⚡","💜","♾️","🌟"),GOLD4)
)

class TriplixV4Activity : ComponentActivity() {
 override fun onCreate(savedInstanceState: Bundle?) {
  super.onCreate(savedInstanceState)
  setContent { TriplixV4App() }
 }
}

@Composable fun TriplixV4App() {
 val ctx = androidx.compose.ui.platform.LocalContext.current
 val prefs = remember { ctx.getSharedPreferences("triplix_v4", Context.MODE_PRIVATE) }
 var page by rememberSaveable { mutableStateOf(P4.HOME) }
 var world by rememberSaveable { mutableIntStateOf(0) }
 var level by rememberSaveable { mutableIntStateOf(12) }
 var coins by rememberSaveable { mutableIntStateOf(prefs.getInt("coins",1250)) }
 var lives by rememberSaveable { mutableIntStateOf(prefs.getInt("lives",3)) }
 var streak by rememberSaveable { mutableIntStateOf(prefs.getInt("streak",4)) }
 var games by rememberSaveable { mutableIntStateOf(prefs.getInt("games",11)) }
 var unlocked by rememberSaveable { mutableIntStateOf(prefs.getInt("unlocked",12)) }
 var chest by rememberSaveable { mutableIntStateOf(prefs.getInt("chest",1)) }
 var collection by rememberSaveable { mutableIntStateOf(prefs.getInt("collection",6)) }
 fun save(){ prefs.edit().putInt("coins",coins).putInt("lives",lives).putInt("streak",streak).putInt("games",games).putInt("unlocked",unlocked).putInt("chest",chest).putInt("collection",collection).apply() }
 BackHandler(enabled=page!=P4.HOME){
  page=when(page){P4.GAME->P4.LEVELS;P4.LEVELS->P4.WORLDS;P4.WORLDS,P4.COLLECTION->P4.HOME;P4.HOME->P4.HOME}
 }
 MaterialTheme(colorScheme=darkColorScheme(background=BG4,surface=PANEL4,primary=GREEN4,onPrimary=Color(0xFF002A13),onSurface=CREAM4)){
  Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(BG4,Color(0xFF0A1B11),Color(0xFF031009))))){
   when(page){
    P4.HOME->Home4(coins,lives,streak,games,chest){page=P4.WORLDS}
    P4.WORLDS->Worlds4({page=P4.HOME}){world=it;page=P4.LEVELS}
    P4.LEVELS->Levels4(WW4[world],unlocked,{page=P4.WORLDS}){level=it;page=P4.GAME}
    P4.COLLECTION->Collection4(collection){page=P4.HOME}
    P4.GAME->Game4(WW4[world],level,coins,lives,{page=P4.LEVELS},{d->coins=(coins+d).coerceAtLeast(0);save()},{r,s->coins+=r;games++;chest++;if(s>=3)collection++;if(level>=unlocked&&unlocked<60)unlocked++;save()},{level=(level+1).coerceAtMost(unlocked.coerceAtLeast(1))})
   }
  }
 }
 }
}

@Composable private fun Top4(title:String,sub:String,onBack:()->Unit,right:@Composable RowScope.()->Unit={}){
 Row(Modifier.fillMaxWidth().padding(12.dp,10.dp),verticalAlignment=Alignment.CenterVertically){
  Box(Modifier.size(44.dp).clip(CircleShape).background(Color.White.copy(.07f)).clickable{onBack()},Alignment.Center){Text("‹",color=CREAM4,fontSize=37.sp)}
  Spacer(Modifier.width(10.dp));Column(Modifier.weight(1f)){Text(title,color=CREAM4,fontSize=20.sp,fontWeight=FontWeight.Black);Text(sub,color=Color.White.copy(.55f),fontSize=10.sp)}
  right()
 }
}
@Composable private fun Pill4(text:String,color:Color){Surface(color=Color.White.copy(.07f),shape=RoundedCornerShape(14.dp)){Text(text,Modifier.padding(10.dp,7.dp),color=color,fontWeight=FontWeight.Black,fontSize=11.sp)}}

@Composable private fun Home4(coins:Int,lives:Int,streak:Int,games:Int,chest:Int,onPlay:()->Unit){
 val pulse by rememberInfiniteTransition(label="p").animateFloat(0.97f,1.04f,infiniteRepeatable(tween(1000),RepeatMode.Reverse),label="p")
 LazyColumn(Modifier.fillMaxSize(),contentPadding=PaddingValues(14.dp,16.dp,14.dp,28.dp)){
  item{
   Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically){
    Box(Modifier.size(46.dp).clip(CircleShape).background(Brush.linearGradient(listOf(Color(0xFFFFB968),PURPLE4))),Alignment.Center){Text("👑",fontSize=24.sp)}
    Spacer(Modifier.width(9.dp));Column(Modifier.weight(1f)){Text("Seviye 12",color=GOLD4,fontSize=12.sp,fontWeight=FontWeight.Black);Text("TRIPLIX",color=CREAM4,fontSize=22.sp,fontWeight=FontWeight.Black)}
    Pill4("🪙 "+coins,GOLD4);Spacer(Modifier.width(5.dp));Pill4("⚙",CREAM4)
   }
   Spacer(Modifier.height(10.dp))
   Box(Modifier.fillMaxWidth().height(286.dp).clip(RoundedCornerShape(30.dp)).background(Brush.verticalGradient(listOf(Color(0xFF1C4828),Color(0xFF12331E),Color(0xFF082015)))).border(1.dp,Color.White.copy(.12f),RoundedCornerShape(30.dp)),Alignment.Center){
    Column(horizontalAlignment=Alignment.CenterHorizontally){
     Text("🌿  ✨  🍃",fontSize=25.sp)
     Spacer(Modifier.height(7.dp))
     Box(Modifier.size(102.dp).scale(pulse).shadow(20.dp,RoundedCornerShape(30.dp)).clip(RoundedCornerShape(30.dp)).background(Brush.linearGradient(listOf(Color(0xFFFFC84D),Color(0xFFFF845C)))),Alignment.Center){Text("3",color=Color.White,fontSize=61.sp,fontWeight=FontWeight.Black)}
     Spacer(Modifier.height(11.dp));Text("TRIPLIX",color=Color(0xFFFFD786),fontSize=43.sp,fontWeight=FontWeight.Black,letterSpacing=2.sp)
     Text("AYNI 3'Ü BİRLEŞTİR, DAHA FAZLASINI KEŞFET!",color=CREAM4.copy(.8f),fontSize=10.sp,fontWeight=FontWeight.Bold,textAlign=TextAlign.Center)
     Spacer(Modifier.height(12.dp));Text("💎 100+ BÖLÜM   •   💥 ÖZEL TAŞLAR   •   🏆 ÖDÜLLER",color=GOLD4,fontSize=8.sp,fontWeight=FontWeight.Black,textAlign=TextAlign.Center)
    }
   }
   Spacer(Modifier.height(12.dp))
   Button(onClick=onPlay,modifier=Modifier.fillMaxWidth().height(60.dp),shape=RoundedCornerShape(19.dp),colors=ButtonDefaults.buttonColors(containerColor=GREEN4)){Text("▶  OYNA",fontSize=20.sp,fontWeight=FontWeight.Black)}
   Spacer(Modifier.height(12.dp))
  }
  item{Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(8.dp)){HStat4("🔥",""+streak,"Seri",Modifier.weight(1f));HStat4("🎮",""+games,"Oyun",Modifier.weight(1f));HStat4("🎁",""+chest,"Sandık",Modifier.weight(1f))};Spacer(Modifier.height(10.dp))}
  item{Card4{Text("🌎 DÜNYALAR",color=CREAM4,fontWeight=FontWeight.Black);Text("Meyve, kristal, sihir, kozmos ve daha fazlası.",color=Color.White.copy(.55f),fontSize=11.sp)};Spacer(Modifier.height(10.dp))}
  item{Card4{Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(8.dp)){Mini4("🎁","Günlük","Ödül");Mini4("📋","Görev","Serisi");Mini4("🏆","Başarı","Rozet")}}}
 }
}
@Composable private fun HStat4(icon:String,value:String,label:String,modifier:Modifier){Surface(color=PANEL4,shape=RoundedCornerShape(17.dp),modifier=modifier.height(72.dp)){Column(Modifier.fillMaxSize(),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.Center){Text(icon,fontSize=17.sp);Text(value,color=CREAM4,fontWeight=FontWeight.Black);Text(label,color=Color.White.copy(.48f),fontSize=9.sp)}}}
@Composable private fun Card4(content:@Composable ColumnScope.()->Unit){Surface(color=Color.White.copy(.045f),shape=RoundedCornerShape(20.dp),modifier=Modifier.fillMaxWidth()){Column(Modifier.padding(14.dp),content=content)}}
@Composable private fun Mini4(icon:String,a:String,b:String){Surface(color=Color.White.copy(.04f),shape=RoundedCornerShape(15.dp),modifier=Modifier.weight(1f).height(68.dp)){Column(Modifier.fillMaxSize(),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.Center){Text(icon,fontSize=18.sp);Text(a,color=CREAM4,fontSize=10.sp,fontWeight=FontWeight.Bold);Text(b,color=Color.White.copy(.45f),fontSize=8.sp)}}}

@Composable private fun Worlds4(onBack:()->Unit,onChoose:(Int)->Unit){
 Column(Modifier.fillMaxSize()){Top4("DÜNYALAR","Kendi maceranı seç!",onBack){Pill4("🌟 9 dünya",GOLD4)};LazyColumn(contentPadding=PaddingValues(14.dp),verticalArrangement=Arrangement.spacedBy(9.dp)){items(WW4){w->Surface(color=Color.White.copy(.05f),shape=RoundedCornerShape(20.dp),modifier=Modifier.fillMaxWidth().height(92.dp).clickable{onChoose(w.id)}){Row(Modifier.fillMaxSize().padding(10.dp),verticalAlignment=Alignment.CenterVertically){Box(Modifier.size(70.dp).clip(RoundedCornerShape(17.dp)).background(Brush.linearGradient(listOf(w.accent.copy(.8f),Color(0xFF0A1D12)))),Alignment.Center){Text(w.icon,fontSize=31.sp)};Spacer(Modifier.width(11.dp));Column(Modifier.weight(1f)){Text(w.name,color=CREAM4,fontWeight=FontWeight.Black,fontSize=14.sp);Text(w.desc,color=Color.White.copy(.55f),fontSize=10.sp);Spacer(Modifier.height(5.dp));Text(w.tiles.joinToString("  "),fontSize=14.sp)};Text("›",color=GOLD4,fontSize=28.sp)}}}}}
}

@Composable private fun Levels4(world:W4,unlocked:Int,onBack:()->Unit,onChoose:(Int)->Unit){
 Column(Modifier.fillMaxSize()){Top4(world.name,"Bölümünü seç",onBack);Surface(color=Color.White.copy(.05f),shape=RoundedCornerShape(20.dp),modifier=Modifier.fillMaxWidth().padding(14.dp,0.dp,14.dp,10.dp)){Column(Modifier.padding(13.dp)){Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween){Text("İLERLEME",color=Color.White.copy(.5f),fontSize=10.sp,fontWeight=FontWeight.Black);Text(""+unlocked+" / 60",color=GOLD4,fontWeight=FontWeight.Black)};Spacer(Modifier.height(7.dp));LinearProgressIndicator(progress={unlocked/60f},modifier=Modifier.fillMaxWidth().height(7.dp),color=world.accent,trackColor=Color.White.copy(.06f))}};LazyVerticalGrid(GridCells.Fixed(4),contentPadding=PaddingValues(14.dp),verticalArrangement=Arrangement.spacedBy(9.dp),horizontalArrangement=Arrangement.spacedBy(9.dp)){items((1..60).toList()){n->val open=n<=unlocked;Surface(color=if(open)Color.White.copy(.06f)else Color.White.copy(.025f),shape=RoundedCornerShape(15.dp),modifier=Modifier.aspectRatio(1f).clickable(enabled=open){onChoose(n)}){Column(Modifier.fillMaxSize(),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.Center){Text(if(open)""+n else "🔒",color=if(open)CREAM4 else Color.White.copy(.25f),fontWeight=FontWeight.Black,fontSize=17.sp);Text(starString4(n),color=if(open)GOLD4 else Color.White.copy(.15f),fontSize=8.sp)}}}}}
}
private fun starString4(n:Int)=when{n%11==0->"⭐⭐⭐";n%3==0->"⭐⭐";else->"⭐"}

private fun board4(level:Int):List<T4>{
 val coords=(0 until 5).flatMap{y->(0 until 5).map{x->x to y}}
 val counts=when{level<8->listOf(15);level<20->listOf(14,6);level<35->listOf(12,7,4);level<50->listOf(11,7,4,2);else->listOf(10,7,4,3,2)}
 val out=mutableListOf<T4>();var id=0
 counts.forEachIndexed{layer,count->val pool=coords.shuffled(Random(level*1009L+layer));repeat(count){i->val p=pool[i%pool.size];out+=T4(id,(id*5+level)%6,p.first,p.second,layer);id++}}
 return out.shuffled(Random(level*7919L))
}
private fun blocked4(t:T4,b:List<T4>)=b.any{o->o.id!=t.id&&o.layer>t.layer&&o.x==t.x&&o.y==t.y}

@Composable private fun Game4(world:W4,level:Int,coins:Int,lives:Int,onBack:()->Unit,onCoinChange:(Int)->Unit,onFinish:(Int,Int)->Unit,onNext:()->Unit){
 val view=LocalView.current
 var board by remember(level,world.id){mutableStateOf(board4(level))}
 var tray by remember(level,world.id){mutableStateOf(listOf<Int>())}
 var score by remember(level,world.id){mutableIntStateOf(0)}
 var bestCombo by remember(level,world.id){mutableIntStateOf(0)}
 var combo by remember(level,world.id){mutableIntStateOf(0)}
 var won by remember(level,world.id){mutableStateOf(false)}
 var over by remember(level,world.id){mutableStateOf(false)}
 var burst by remember{mutableStateOf(false)}
 var hintId by remember{mutableIntStateOf(-1)}
 var undo by remember{mutableIntStateOf(3)}
 var shuffle by remember{mutableIntStateOf(3)}
 var bomb by remember{mutableIntStateOf(3)}
 var hint by remember{mutableIntStateOf(3)}
 var history by remember{mutableStateOf(listOf<Pair<List<T4>,List<Int>>>())}

 fun check(){
  val groups=tray.groupingBy{it}.eachCount().filterValues{it>=3}.keys
  groups.forEach{type->
   var removed=0
   tray=tray.filter{if(it==type&&removed<3){removed++;false}else true}
   combo++;bestCombo=maxOf(bestCombo,combo);score+=100*combo;burst=true
   view.performHapticFeedback(android.view.HapticFeedbackConstants.LONG_PRESS)
  }
  if(board.isEmpty()&&tray.isEmpty())won=true
  else if(tray.size>=7)over=true
 }
 fun tap(t:T4){
  if(blocked4(t,board)||won||over)return
  history=(history+(board to tray)).takeLast(12)
  board=board.filterNot{it.id==t.id};tray=tray+t.type;hintId=-1;check()
 }
 LaunchedEffect(burst){if(burst)delay(330).also{burst=false}}
 Column(Modifier.fillMaxSize().padding(horizontal=10.dp,vertical=7.dp)){
  Top4("BÖLÜM "+level,world.name,onBack){Pill4("🪙 "+coins,GOLD4);Spacer(Modifier.width(5.dp));Pill4("❤️ "+lives,RED4)}
  Row(Modifier.fillMaxWidth().padding(horizontal=5.dp),horizontalArrangement=Arrangement.SpaceBetween){Text("⭐ "+(if(won)3 else 0),color=GOLD4,fontSize=19.sp,fontWeight=FontWeight.Black);Text("🔥 x"+bestCombo,color=GOLD4,fontSize=16.sp,fontWeight=FontWeight.Black)}
  Spacer(Modifier.height(5.dp))
  Box(Modifier.fillMaxWidth().weight(1f).clip(RoundedCornerShape(26.dp)).background(Brush.verticalGradient(listOf(Color(0xFF173D24),Color(0xFF0D2416),BG4))).border(1.dp,Color.White.copy(.11f),RoundedCornerShape(26.dp)),Alignment.Center){
   Column(Modifier.fillMaxSize().padding(9.dp)){
    Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween){Text("🍃 "+world.name,color=Color.White.copy(.55f),fontSize=9.sp,fontWeight=FontWeight.Black);if(combo>1)Text("💥 COMBO x"+combo,color=GOLD4,fontSize=10.sp,fontWeight=FontWeight.Black)}
    Box(Modifier.fillMaxSize(),Alignment.Center){
     board.sortedWith(compareBy<T4>{it.layer}.thenBy{it.id}).forEach{t->Stone4(t,world,blocked4(t,board),t.id==hintId){tap(t)}}
     AnimatedVisibility(burst){Text("💥✨",fontSize=60.sp)}
    }
   }
  }
  Spacer(Modifier.height(7.dp));Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween){Text("TEPSİ  "+tray.size+"/7",color=Color.White.copy(.55f),fontSize=11.sp,fontWeight=FontWeight.Black);Text("🪙 +"+(75+level*5),color=GOLD4,fontSize=11.sp,fontWeight=FontWeight.Black)};Spacer(Modifier.height(5.dp))
  Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(5.dp)){repeat(7){i->Tray4(if(i<tray.size)world.tiles[tray[i]] else null,world.accent)}};Spacer(Modifier.height(7.dp))
  Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(6.dp)){
   Boost4("🔀","Karıştır",shuffle>0,""+shuffle){if(shuffle>0&&!won&&!over){shuffle--;board=board.shuffled();hintId=-1}}
   Boost4("💡","İpucu",hint>0,""+hint){if(hint>0){hint--;hintId=board.firstOrNull{!blocked4(it,board)}?.id?:-1}}
   Boost4("💣","Bomba",bomb>0,""+bomb){if(bomb>0){bomb--;val t=board.firstOrNull{!blocked4(it,board)};if(t!=null){board=board.filterNot{it.id==t.id};score+=60;burst=true;check()}}}
   Boost4("↶","Geri Al",undo>0&&history.isNotEmpty(),""+undo){if(undo>0&&history.isNotEmpty()){val s=history.last();history=history.dropLast(1);board=s.first;tray=s.second;undo--}}
  }
 }
 if(won||over){
  AlertDialog(onDismissRequest={},containerColor=PANEL4B,title={Text(if(won)"🎉 BÖLÜM TAMAMLANDI!" else "💥 TEPSİ DOLDU",color=CREAM4,fontWeight=FontWeight.Black)},text={Column{Text(if(won)"Harika! Katmanları temizledin." else "Bir daha dene. Bu kez hamlelerini planla.",color=Color.White.copy(.62f));Spacer(Modifier.height(7.dp));Text("🔥 En iyi combo: x"+bestCombo,color=GREEN4,fontWeight=FontWeight.Bold);if(won)Text("⭐ "+(if(score>=900)3 else if(score>=550)2 else 1)+"    🪙 +"+(75+level*5),color=GOLD4,fontWeight=FontWeight.Black)}},confirmButton={Button(onClick={if(won){val stars=if(score>=900)3 else if(score>=550)2 else 1;onFinish(75+level*5,stars);onNext();won=false}else{board=board4(level);tray=emptyList();score=0;bestCombo=0;combo=0;over=false}}){Text(if(won)"SONRAKİ BÖLÜM ▶" else "TEKRAR OYNA",fontWeight=FontWeight.Black)}},dismissButton={TextButton(onClick=onBack){Text("BÖLÜMLER",color=CREAM4)}})
 }
}

@Composable private fun Stone4(t:T4,w:W4,blocked:Boolean,hint:Boolean,onClick:()->Unit){
 val scale by animateFloatAsState(if(hint)1.07f else 1f,label="tile")
 val x=((t.x-2)*57+t.layer*13).dp
 val y=((t.y-2)*57-t.layer*18).dp
 val fill=if(blocked)Color(0xFFC9BFB0)else Color(0xFFFFF7EA)
 Box(Modifier.offset(x,y).zIndex(t.layer*10f+t.id/100f).size(67.dp).scale(scale).shadow(if(blocked)7.dp else 13.dp,RoundedCornerShape(20.dp)).clip(RoundedCornerShape(20.dp)).background(Brush.verticalGradient(listOf(fill,fill.copy(.84f)))).border(if(hint)3.dp else 2.dp,if(hint)GOLD4 else Color(0xFFB39770),RoundedCornerShape(20.dp)).clickable(enabled=!blocked){onClick()},Alignment.Center){
  Box(Modifier.fillMaxSize().padding(5.dp).clip(RoundedCornerShape(16.dp)).background(Brush.radialGradient(listOf(Color.White.copy(.65f),Color.Transparent))))
  Text(w.tiles[t.type],fontSize=31.sp)
  if(t.layer>0)Text("•".repeat(t.layer.coerceAtMost(3)),Modifier.align(Alignment.TopEnd).padding(6.dp),color=Color(0xFF7A6A58),fontSize=7.sp)
  if(blocked)Box(Modifier.fillMaxSize().background(Color.Black.copy(.08f)))
 }
}
@Composable private fun RowScope.Tray4(icon:String?,accent:Color){Box(Modifier.weight(1f).height(51.dp).clip(RoundedCornerShape(13.dp)).background(if(icon==null)Color.White.copy(.025f)Color(0xFF241E17)).border(1.dp,if(icon==null)Color.White.copy(.06f)else accent.copy(.45f),RoundedCornerShape(13.dp)),Alignment.Center){Text(icon?:"·",fontSize=20.sp,color=if(icon==null)Color.White.copy(.12f)else Color.Unspecified)}}
@Composable private fun RowScope.Boost4(icon:String,label:String,enabled:Boolean,count:String,onClick:()->Unit){Surface(color=if(enabled)PANEL4 else Color.White.copy(.02f),shape=RoundedCornerShape(16.dp),modifier=Modifier.weight(1f).height(62.dp).clickable(enabled=enabled){onClick()}){Box(Modifier.fillMaxSize()){Column(Modifier.fillMaxSize(),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.Center){Text(icon,fontSize=20.sp);Text(label,color=if(enabled)CREAM4 else Color.White.copy(.2f),fontSize=8.sp,fontWeight=FontWeight.Bold)}if(enabled)Box(Modifier.align(Alignment.TopEnd).padding(4.dp).size(17.dp).clip(CircleShape).background(GOLD4),Alignment.Center){Text(count,color=Color(0xFF3A2A00),fontSize=7.sp,fontWeight=FontWeight.Black)}}}}
@Composable private fun Collection4(count:Int,onBack:()->Unit){Column(Modifier.fillMaxSize()){Top4("KOLEKSİYON","Özel taşların burada",onBack){Pill4(count.toString()+" / 30",GOLD4)};LazyVerticalGrid(GridCells.Fixed(3),contentPadding=PaddingValues(14.dp),verticalArrangement=Arrangement.spacedBy(10.dp),horizontalArrangement=Arrangement.spacedBy(10.dp)){items(listOf("🍓","🍎","🍌","💎","🔮","🪄","🚀","⭐","🐱","🏺","🌿","👑","🧊","💣","🃏","✨","🔥","🌙")){i->Surface(color=Color.White.copy(.05f),shape=RoundedCornerShape(18.dp),modifier=Modifier.aspectRatio(1f)){Box(Modifier.fillMaxSize(),Alignment.Center){Text(i,fontSize=32.sp)}}}}}}
