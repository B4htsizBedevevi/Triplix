import React, { useEffect, useMemo, useState } from 'react'
import { createRoot } from 'react-dom/client'
import { Icon } from '@iconify/react'
import strawberry from '@iconify-icons/game-icons/strawberry'
import banana from '@iconify-icons/game-icons/banana'
import grapes from '@iconify-icons/game-icons/grapes'
import lemon from '@iconify-icons/game-icons/lemon'
import watermelon from '@iconify-icons/game-icons/watermelon'
import crystalCluster from '@iconify-icons/game-icons/crystal-cluster'
import crystalShine from '@iconify-icons/game-icons/crystal-shine'
import crystalWand from '@iconify-icons/game-icons/crystal-wand'
import magicPotion from '@iconify-icons/game-icons/magic-potion'
import crystalBall from '@iconify-icons/game-icons/crystal-ball'
import rocket from '@iconify-icons/game-icons/rocket'
import topaz from '@iconify-icons/game-icons/topaz'
import minerals from '@iconify-icons/game-icons/minerals'
import cat from '@iconify-icons/game-icons/cat'
import bottle from '@iconify-icons/game-icons/health-potion'
import './styles.css'

const pack = {
  fruit:[strawberry,banana,grapes,lemon,watermelon,bottle],
  crystal:[crystalCluster,crystalShine,topaz,minerals,crystalWand,crystalBall],
  magic:[magicPotion,crystalBall,crystalWand,crystalShine,bottle,minerals],
  space:[rocket,crystalShine,topaz,crystalBall,minerals,crystalCluster],
  friends:[cat,bottle,crystalBall,crystalShine,strawberry,crystalWand],
} as const

const worlds = {
  Meyve:{title:'Meyve Bahçesi',tagline:'Tatlı eşleşmeler',icons:pack.fruit,colors:['#ef6680','#f7bb50','#63cf7d','#4faee9','#a37cf1','#ec8b59'],bg:'orchard'},
  Kristal:{title:'Kristal Dünyası',tagline:'Parla ve patla',icons:pack.crystal,colors:['#4baeff','#5978ff','#55d59d','#ad66ff','#ff6885','#e7be51'],bg:'crystal'},
  Sihir:{title:'Sihirli Objeler',tagline:'Büyük maceralar',icons:pack.magic,colors:['#9b70ef','#5e9dff','#ce6fe0','#78dbcf','#dd9b5d','#8784dc'],bg:'magic'},
  Kozmik:{title:'Kozmik Serüven',tagline:'Parlak üçlüler',icons:pack.space,colors:['#4ca8ff','#7e78ff','#6a9aff','#69d5ac','#ffc857','#ba86ff'],bg:'space'},
  Dostlar:{title:'Sevimli Dostlar',tagline:'Tatlı karakterler',icons:pack.friends,colors:['#ef8ea5','#b7a57e','#7e99cf','#af81e2','#e97878','#75c99e'],bg:'friends'},
  Antik:{title:'Antik Semboller',tagline:'Zamanı aşanlar',icons:pack.crystal,colors:['#d5ac58','#6c98c7','#d7b76c','#8c7454','#63a27c','#b88655'],bg:'ancient'},
  Dogal:{title:'Doğa Elementleri',tagline:'Doğanın gücü',icons:pack.magic,colors:['#63d17c','#4caee4','#ed6d5e','#7e9ef0','#9d8465','#e18bb1'],bg:'nature'},
  Mevsim:{title:'Mevsimler',tagline:'Her mevsim farklı',icons:pack.fruit,colors:['#71aef0','#ee9ab9','#ffc85a','#e18a67','#c87957','#83a8d9'],bg:'season'},
  Koleksiyon:{title:'Özel Koleksiyon',tagline:'TRIPLIX özel',icons:pack.crystal,colors:['#e0b74f','#e66f85','#57a9ed','#62ca90','#f4ba4f','#9b7be9'],bg:'collection'}
} as const

type World=keyof typeof worlds
type Screen='home'|'worlds'|'levels'|'game'
type Status='playing'|'won'|'lost'
type Stone={id:number;kind:number}

function makeBoard(seed:number,world:World):Stone[]{
  const n=worlds[world].icons.length
  return Array.from({length:49},(_,i)=>({id:seed*1000+i,kind:(seed*11+i*5+Math.floor(i/7)*3)%n}))
}

function GameIcon({icon}:{icon:any}){return <Icon icon={icon} className="game-icon" aria-hidden="true"/>}

function App(){
  const [screen,setScreen]=useState<Screen>('home')
  const [world,setWorld]=useState<World>('Meyve')
  const [level,setLevel]=useState(1)
  const [score,setScore]=useState(0)
  const [combo,setCombo]=useState(0)
  const [moves,setMoves]=useState(30)
  const [triples,setTriples]=useState(0)
  const [tray,setTray]=useState<Stone[]>([])
  const [board,setBoard]=useState<Stone[]>(makeBoard(1,'Meyve'))
  const [status,setStatus]=useState<Status>('playing')
  const [message,setMessage]=useState('3 aynı taşı seç • üçüncüde patlat!')
  const [selectedId,setSelectedId]=useState<number|null>(null)
  const theme=worlds[world]
  const goal=5+Math.floor(level/5)
  const counts=useMemo(()=>tray.reduce<Record<number,number>>((a,s)=>{a[s.kind]=(a[s.kind]??0)+1;return a},{}),[tray])

  useEffect(()=>{
    const onPop=()=>{const s=window.history.state?.triplixScreen as Screen|undefined;setScreen(s??'home')}
    if(!window.history.state?.triplixScreen) window.history.replaceState({triplixScreen:'home'},'',window.location.href)
    window.addEventListener('popstate',onPop)
    return()=>window.removeEventListener('popstate',onPop)
  },[])

  function nav(next:Screen){window.history.pushState({triplixScreen:next},'',window.location.href);setScreen(next)}
  function home(){nav('home')}

  useEffect(()=>{
    if(status!=='playing')return
    const entry=Object.entries(counts).find(([,c])=>c>=3)
    if(!entry){if(tray.length>=7){setStatus('lost');setMessage('Tepsi doldu! Bu tur kaçtı.')}return}
    const kind=Number(entry[0])
    const t=window.setTimeout(()=>{
      setTray(cur=>{let removed=0;return cur.filter(s=>{if(s.kind===kind&&removed<3){removed++;return false}return true})})
      setTriples(v=>{const next=v+1;if(next>=goal){setStatus('won');setMessage('Mükemmel! Bölüm tamamlandı.')}else setMessage('ÜÇLÜ PATLADI! +100');return next})
      setCombo(v=>v+1)
      setScore(v=>v+125+combo*25)
    },260)
    return()=>window.clearTimeout(t)
  },[counts,status,goal,combo,tray.length])

  function start(nextLevel:number,nextWorld:World=world){
    const safe=Math.max(1,Math.min(60,nextLevel))
    setLevel(safe);setWorld(nextWorld);setScore(0);setCombo(0);setMoves(Math.max(20,32-Math.floor(safe/8)*2));setTriples(0);setTray([])
    setBoard(makeBoard(safe,nextWorld));setStatus('playing');setSelectedId(null);setMessage('3 aynı taşı seç • üçüncüde patlat!');nav('game')
  }
  function selectStone(stone:Stone){
    if(status!=='playing'||moves<=0||tray.length>=7||selectedId!==null)return
    setSelectedId(stone.id)
    window.setTimeout(()=>{setBoard(cur=>cur.filter(x=>x.id!==stone.id));setTray(cur=>[...cur,stone]);setMoves(v=>Math.max(0,v-1));setSelectedId(null);setMessage('Taş parıltı iziyle tepsiye iniyor…')},180)
  }
  function shuffle(){if(status==='playing'){setBoard(cur=>[...cur].sort(()=>Math.random()-.5));setMessage('Tahta karıştırıldı ✨')}}
  function hint(){if(status==='playing'){const s=board.find(x=>counts[x.kind]>0)||board[0];if(s)setMessage('İpucu: aynı simgeden üç tane kovala.')}}

  return <main className={`app-shell world-${theme.bg}`}>
    <div className="atmo atmo-one"/><div className="atmo atmo-two"/>
    <header className="topbar">
      <button className="brand" onClick={home}><span className="brand-mark">◆</span><b>TRIPLIX</b></button>
      <div className="top-actions"><div className="wallet">◉ <b>400</b></div><button className="sound" onClick={()=>{}}>♪</button></div>
    </header>

    {screen==='home'&&<section className="home">
      <div className="hero-world"><div className="hero-vignette"/><div className="hero-copy"><span className="kicker">TRIPLIX WORLDS</span><h1>Bir Taştan<br/><em>Daha Fazlası!</em></h1><p>Farklı dünyaları keşfet. Taşları seç, üçlüleri patlat, combo'yu büyüt.</p><div className="home-buttons"><button className="primary" onClick={()=>nav('worlds')}>DÜNYANI SEÇ <b>→</b></button><button className="ghost" onClick={()=>start(1,world)}>HEMEN OYNA</button></div></div><div className="showcase-stones"><div className="show-stone ss1"><GameIcon icon={theme.icons[0]}/></div><div className="show-stone ss2"><GameIcon icon={theme.icons[1]}/></div><div className="show-stone ss3"><GameIcon icon={theme.icons[2]}/></div><div className="show-stone ss4"><GameIcon icon={theme.icons[3]}/></div></div></div>
      <div className="quick-row"><div><b>9</b><small>DÜNYA</small></div><div><b>60</b><small>BÖLÜM</small></div><div><b>∞</b><small>COMBO</small></div></div>
      <div className="home-links"><button className="feature-link" onClick={()=>nav('worlds')}><span>◇</span><div><b>Oyun Dünyaları</b><small>Her dünyada farklı taş seti</small></div><strong>›</strong></button><button className="feature-link" onClick={()=>nav('levels')}><span>◎</span><div><b>Bölüm Haritası</b><small>{level}/60 ilerleme</small></div><strong>›</strong></button></div>
    </section>}

    {screen==='worlds'&&<section className="subscreen compact-worlds"><div className="page-head"><button className="back" onClick={()=>nav('home')}>‹</button><div><span>DÜNYANI SEÇ</span><h2>Macera Haritası</h2></div></div><p className="page-copy">Bir dünya seç ve kendi taş koleksiyonunu keşfet.</p><div className="world-grid">{(Object.keys(worlds) as World[]).map(w=><button key={w} className={`world-card ${w===world?'active':''}`} onClick={()=>{setWorld(w);nav('levels')}}><div className={`world-preview ${worlds[w].bg}`}><GameIcon icon={worlds[w].icons[0]}/><span className="mini-icons">{worlds[w].icons.slice(1,4).map((ic,i)=><GameIcon key={i} icon={ic}/>)}</span></div><div className="world-card-copy"><b>{worlds[w].title}</b><small>{worlds[w].tagline}</small></div><strong>{w===world?'✓':'›'}</strong></button>)}</div></section>}

    {screen==='levels'&&<section className="subscreen"><div className="page-head"><button className="back" onClick={()=>nav('worlds')}>‹</button><div><span>{theme.title.toUpperCase()}</span><h2>Bölüm Haritası</h2></div></div><div className="map-banner"><div className={`map-art ${theme.bg}`}><GameIcon icon={theme.icons[0]}/></div><div><b>{theme.title}</b><small>Seviye {level} / 60</small><div className="bar"><i style={{width:`${Math.min(100,level/60*100)}%`}}/></div></div></div><div className="level-path">{Array.from({length:60},(_,i)=>i+1).map(n=>{const locked=n>level+1;return <button disabled={locked} key={n} className={`level-node ${n===level?'current':''} ${locked?'locked':''}`} onClick={()=>start(n,world)}><b>{locked?'·':n}</b><span>{locked?'':'★'.repeat(n<level?3:1)+'☆'.repeat(n<level?0:2)}</span></button>})}</div></section>}

    {screen==='game'&&<section className="game"><div className="game-head"><button className="back" onClick={()=>nav('levels')}>‹</button><div className="level-sign"><span>{theme.title}</span><b>BÖLÜM {level}</b></div><div className="coins">◉ 400</div></div><div className="goal-strip"><div><small>HAMLE</small><b>{moves}</b></div><div><small>COMBO</small><b className="gold">x{combo}</b></div><div><small>HEDEF</small><b>{triples}/{goal}</b></div><div><small>SKOR</small><b>{score}</b></div></div><div className={`game-scene ${theme.bg}`}><div className="scene-lights"/><div className="game-board">{board.map((stone,i)=><button disabled={status!=='playing'} key={stone.id} className={`stone ${selectedId===stone.id?'selected':''}`} style={{background:theme.colors[stone.kind]}} onClick={()=>selectStone(stone)}><GameIcon icon={theme.icons[stone.kind]}/></button>)}</div></div><div className="tray-wrap"><div className="tray-head"><b>SEÇİLEN TAŞLAR</b><span>{tray.length}/7</span></div><div className="tray">{Array.from({length:7},(_,i)=>{const s=tray[i];return s?<div className="tray-stone" key={i} style={{background:theme.colors[s.kind]}}><GameIcon icon={theme.icons[s.kind]}/></div>:<div className="tray-slot" key={i}/>} )}</div></div><div className={`message ${status}`}>{message}</div>{status!=='playing'&&<div className="result"><div className="result-burst">{status==='won'?'★':'×'}</div><b>{status==='won'?'BÖLÜM TAMAMLANDI!':'TEPSİ DOLDU!'}</b><small>{status==='won'?'${goal} üçlü tamamlandı':'Daha iyi bir sıra kurup tekrar dene'}</small><div><button className="primary" onClick={()=>start(status==='won'?level+1:level,world)}>{status==='won'?'SONRAKİ BÖLÜM':'TEKRAR OYNA'} <b>→</b></button><button className="ghost" onClick={()=>nav('levels')}>BÖLÜMLER</button></div></div>}<div className="tools"><button disabled={status!=='playing'} onClick={shuffle}>↝<span>Karıştır</span></button><button disabled={status!=='playing'} onClick={hint}>◇<span>İpucu</span></button><button onClick={()=>start(level,world)}>↻<span>Sıfırla</span></button></div></section>}
  </main>
}
createRoot(document.getElementById('root')!).render(<React.StrictMode><App/></React.StrictMode>)
