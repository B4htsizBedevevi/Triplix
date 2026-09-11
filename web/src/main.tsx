import React, { useEffect, useMemo, useState } from 'react'
import { createRoot } from 'react-dom/client'
import './styles.css'

const worlds = {
  Meyve: { emoji:'🍓', title:'Meyve Bahçesi', tagline:'Tatlı eşleşmeler, büyük combo!', symbols:['🍓','🍌','🍇','🍎','🍊','🍉','🍒','🍍'], colors:['#ff6680','#ffc95d','#76d987','#67bfff','#ff8d61','#87c9ff','#ef5c78','#f3b84f'] },
  Kristal: { emoji:'💎', title:'Kristal Dünyası', tagline:'Parla, eşleş, patla!', symbols:['💎','🔷','🟢','🟣','🔴','🟡','🔹','💠'], colors:['#54b8ff','#4b7cff','#62dfaa','#a66bff','#ff6286','#ffc84f','#45d3ff','#cb72ff'] },
  Sihir: { emoji:'🪄', title:'Sihirli Objeler', tagline:'Küçük detaylar, büyük maceralar!', symbols:['🧙','🧪','📕','🗝️','🔮','🪄','🗺️','✨'], colors:['#9d75ff','#65b8ff','#dd70a9','#e6bd5e','#6f8df5','#ca78ff','#d69968','#60ddc0'] },
  Kozmik: { emoji:'🪐', title:'Kozmik Serüven', tagline:'Uzayın en parlak üçlüleri', symbols:['🪐','⭐','🚀','🌌','🌙','☄️','🌍','💫'], colors:['#4ca8ff','#ffd55d','#f17b6c','#7e75ff','#65d0ef','#ff7db0','#6ad79b','#b18cff'] },
  Dostlar: { emoji:'🐱', title:'Sevimli Dostlar', tagline:'Tatlı karakterler, renkli eşleşmeler', symbols:['🐱','🐶','🐼','🐰','🐻','🐸','🐧','🐥'], colors:['#f59aa8','#c8a579','#a7b9cd','#f6b8cb','#b98765','#73c9a1','#8fb3d9','#f4d06f'] },
  Antik: { emoji:'☀️', title:'Antik Semboller', tagline:'Zamanı aşan üçlüler', symbols:['☀️','👁️','△','🌀','🌿','🔱','◈','☼'], colors:['#d4a84d','#6aa9e8','#d9b86e','#9b7a4e','#59a876','#d8894f','#8f8dca','#d1b05d'] },
  Dogal: { emoji:'🍃', title:'Doğa Elementleri', tagline:'Doğanın gücü seninle', symbols:['🍃','💧','🔥','🌪️','🪨','🌸','🌱','💦'], colors:['#6ad983','#64baf5','#ff7164','#7a9df1','#9b8067','#ef9cc0','#76bb68','#5fc7e3'] },
  Mevsim: { emoji:'❄️', title:'Mevsimler', tagline:'Her mevsim farklı bir güzellik', symbols:['❄️','🌸','☀️','🍁','🍂','🌧️','🌻','⛄'], colors:['#78baff','#ff9fc1','#ffc54e','#e58b66','#c97857','#699ce5','#f2bd53','#c7e7ff'] },
  Koleksiyon: { emoji:'👑', title:'Özel Koleksiyon', tagline:'Sadece TRIPLIX’e özel!', symbols:['👑','❤️','💎','🍀','⚡','💜','♾️','⭐'], colors:['#e9be54','#ef6f85','#6cbfff','#6bd397','#ffc850','#aa78f0','#7c8eff','#f1cf58'] },
} as const
type World = keyof typeof worlds
type Screen='home'|'worlds'|'levels'|'game'
type Status='playing'|'won'|'lost'
type Stone={id:number;kind:number}

function makeBoard(seed:number, world:World){
  const n=worlds[world].symbols.length
  return Array.from({length:49},(_,i)=>({id:seed*1000+i,kind:(seed*13+i*5+Math.floor(i/7)*3)%n}))
}

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
  const [sound,setSound]=useState(true)
  const [selectedId,setSelectedId]=useState<number|null>(null)
  const theme=worlds[world]
  const goal=5+Math.floor(level/5)

  const counts=useMemo(()=>tray.reduce<Record<number,number>>((a,s)=>{a[s.kind]=(a[s.kind]??0)+1;return a},{}),[tray])

  useEffect(()=>{
    if(status!=='playing')return
    const entry=Object.entries(counts).find(([,c])=>c>=3)
    if(!entry){
      if(tray.length>=7){setStatus('lost');setMessage('Tepsi doldu! Bu tur kaçtı.')}
      return
    }
    const kind=Number(entry[0])
    const t=window.setTimeout(()=>{
      setTray(cur=>{let removed=0;return cur.filter(s=>{if(s.kind===kind&&removed<3){removed++;return false}return true})})
      setTriples(v=>{const next=v+1;if(next>=goal){setStatus('won');setMessage('Mükemmel! Bölüm tamamlandı.')}else setMessage('ÜÇLÜ PATLADI! +100 ✨');return next})
      setCombo(v=>v+1)
      setScore(v=>v+125+combo*25)
    },260)
    return()=>window.clearTimeout(t)
  },[counts,status,goal,combo,tray.length])

  function start(nextLevel:number, nextWorld:World=world){
    const safe=Math.max(1,Math.min(60,nextLevel))
    setLevel(safe);setWorld(nextWorld);setScore(0);setCombo(0);setMoves(Math.max(20,32-Math.floor(safe/8)*2));setTriples(0);setTray([])
    setBoard(makeBoard(safe,nextWorld));setStatus('playing');setSelectedId(null);setMessage('3 aynı taşı seç • üçüncüde patlat!');setScreen('game')
  }
  function selectStone(stone:Stone){
    if(status!=='playing'||moves<=0||tray.length>=7||selectedId!==null)return
    setSelectedId(stone.id)
    window.setTimeout(()=>{
      setBoard(cur=>cur.filter(x=>x.id!==stone.id))
      setTray(cur=>[...cur,stone])
      setMoves(v=>Math.max(0,v-1))
      setSelectedId(null)
      setMessage('Taş tepsiye gidiyor…')
    },170)
  }
  function shuffle(){if(status==='playing'){setBoard(cur=>[...cur].sort(()=>Math.random()-.5));setMessage('Tahta yenilendi 🔀')}}
  function hint(){if(status==='playing'){const s=board.find(x=>counts[x.kind]>0)||board[0];if(s)setMessage(`İpucu: ${theme.symbols[s.kind]} ile üçleme kovala!`)}} 

  return <main className={`app-shell ${world.toLowerCase()}`}>
    <div className="bg-noise"/><div className="scene-glow scene-one"/><div className="scene-glow scene-two"/>
    <header className="topbar">
      <button className="brand" onClick={()=>setScreen('home')}><span>{theme.emoji}</span><b>TRIPLIX</b></button>
      <div className="top-actions"><div className="wallet">🪙 <b>400</b></div><button className="sound" onClick={()=>setSound(v=>!v)}>{sound?'🔊':'🔇'}</button></div>
    </header>

    {screen==='home'&&<section className="home">
      <div className="home-art">
        <div className="home-art-copy"><span>TRIPLIX WORLDS</span><h1>Bir Taştan<br/><em>Daha Fazlası!</em></h1><p>Bir üçlü seç. Patlat. Combo'yu büyüt. Kendi dünyanı keşfet.</p>
          <div className="home-buttons"><button className="primary" onClick={()=>setScreen('worlds')}>DÜNYANI SEÇ <span>→</span></button><button className="ghost" onClick={()=>start(1,'Meyve')}>HEMEN OYNA</button></div>
        </div>
        <div className="floating-tile t1">🍓</div><div className="floating-tile t2">💎</div><div className="floating-tile t3">🪐</div><div className="floating-tile t4">👑</div>
      </div>
      <div className="quick-row"><div><b>9</b><small>DÜNYA</small></div><div><b>60</b><small>BÖLÜM</small></div><div><b>3⭐</b><small>HEDEF</small></div></div>
      <div className="home-links"><button className="feature-link" onClick={()=>setScreen('worlds')}><span>🎨</span><div><b>Oyun Dünyaları</b><small>Her temada farklı taşlar</small></div><strong>›</strong></button><button className="feature-link" onClick={()=>setScreen('levels')}><span>🗺️</span><div><b>Bölümler</b><small>{level}/60 açık</small></div><strong>›</strong></button></div>
    </section>}

    {screen==='worlds'&&<section className="subscreen">
      <div className="page-head"><button className="back" onClick={()=>setScreen('home')}>‹</button><div><span>DÜNYANI SEÇ</span><h2>Macera Haritası</h2></div></div>
      <p className="page-copy">Her dünyanın kendi atmosferi, taşı ve sürprizi var.</p>
      <div className="world-grid">{(Object.keys(worlds) as World[]).map(w=><button key={w} className={`world-card ${w===world?'active':''}`} onClick={()=>{setWorld(w);setScreen('levels')}}><div className="world-preview" style={{background:worlds[w].colors[0]}}><span>{worlds[w].emoji}</span><i>{worlds[w].symbols.slice(0,4).join('')}</i></div><div><b>{worlds[w].title}</b><small>{worlds[w].tagline}</small><em>{w===world?'SEÇİLİ':'AÇ'}</em></div><strong>›</strong></button>)}</div>
    </section>}

    {screen==='levels'&&<section className="subscreen">
      <div className="page-head"><button className="back" onClick={()=>setScreen('worlds')}>‹</button><div><span>{theme.title.toUpperCase()}</span><h2>Bölüm Haritası</h2></div></div>
      <div className="map-banner"><div className="map-art">{theme.emoji}<span>WORLD</span></div><div><b>{theme.title}</b><small>Seviye {level} / 60</small><div className="bar"><i style={{width:`${Math.min(100,(level/60)*100)}%`}}/></div></div></div>
      <div className="level-path">{Array.from({length:60},(_,i)=>i+1).map(n=>{const locked=n>level+1;return <button disabled={locked} key={n} className={`level-node ${n===level?'current':''} ${locked?'locked':''}`} onClick={()=>start(n,world)}><b>{locked?'🔒':n}</b><span>{locked?'':'★'.repeat(n<level?3:1)+'☆'.repeat(n<level?0:2)}</span></button>})}</div>
    </section>}

    {screen==='game'&&<section className="game">
      <div className="game-head"><button className="back" onClick={()=>setScreen('levels')}>‹</button><div className="level-sign"><span>{theme.title}</span><b>BÖLÜM {level}</b></div><div className="coins">🪙 400</div></div>
      <div className="goal-strip"><div><small>HAMLE</small><b>{moves}</b></div><div><small>COMBO</small><b className="gold">x{combo}</b></div><div><small>HEDEF</small><b>{triples}/{goal}</b></div><div><small>SKOR</small><b>{score}</b></div></div>
      <div className="game-scene"><div className="forest-layer"/><div className="game-board"><div className="depth-shadow"/>{board.map((stone,i)=><button disabled={status!=='playing'} key={stone.id} className={`stone ${selectedId===stone.id?'selected':''}`} style={{background:theme.colors[stone.kind%theme.colors.length],animationDelay:`${(i%7)*12}ms`}} onClick={()=>selectStone(stone)}><span>{theme.symbols[stone.kind]}</span></button>)}</div></div>
      <div className="tray-wrap"><div className="tray-head"><b>SEÇİLEN TAŞLAR</b><span>{tray.length}/7</span></div><div className="tray">{Array.from({length:7},(_,i)=>{const s=tray[i];return s?<div className="tray-stone" key={i} style={{background:theme.colors[s.kind%theme.colors.length]}}>{theme.symbols[s.kind]}</div>:<div className="tray-slot" key={i}/>})}</div></div>
      <div className={`message ${status}`}>{message}</div>
      {status!=='playing'&&<div className="result"><div className="result-burst">{status==='won'?'🏆':'💥'}</div><b>{status==='won'?'BÖLÜM TAMAMLANDI!':'TEPSİ DOLDU!'}</b><small>{status==='won'?'${goal} üçlü tamamlandı':'Daha iyi bir sıra kurup tekrar dene'}</small><div><button className="primary" onClick={()=>start(status==='won'?level+1:level,world)}>{status==='won'?'SONRAKİ BÖLÜM':'TEKRAR OYNA'} →</button><button className="ghost" onClick={()=>setScreen('levels')}>BÖLÜMLER</button></div></div>}
      <div className="tools"><button disabled={status!=='playing'} onClick={shuffle}>🔀<span>Karıştır</span></button><button disabled={status!=='playing'} onClick={hint}>💡<span>İpucu</span></button><button onClick={()=>start(level,world)}>↻<span>Sıfırla</span></button></div>
    </section>}
  </main>
}
createRoot(document.getElementById('root')!).render(<React.StrictMode><App/></React.StrictMode>)
