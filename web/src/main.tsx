import React, { useEffect, useMemo, useState } from 'react'
import { createRoot } from 'react-dom/client'
import './styles.css'

const themes = {
  Meyve: { emoji: '🍓', subtitle: 'Renkli meyveler, hızlı üçlüler', symbols: ['🍎','🍋','🍇','🍊','🥝','🍉'], colors: ['#ff587b','#ffc857','#73d881','#59c4ff','#a88bff','#ff9160'] },
  Klasik: { emoji: '◆', subtitle: 'Temiz, sakin, zamansız', symbols: ['◆','●','★','■','▲','✦'], colors: ['#ee6577','#57a8f4','#9b7cf2','#53c594','#f3bb58','#e47aa9'] },
  Uzay: { emoji: '🚀', subtitle: 'Kristaller ve kozmik güçler', symbols: ['💎','🌌','🪐','⭐','☄️','🌙'], colors: ['#54a9ff','#7c65ef','#57d8b2','#ffc95b','#ff6e88','#a98bff'] },
  Arcade: { emoji: '⚡', subtitle: 'Hızlı, neon ve tempolu', symbols: ['⚡','🎮','💠','🔶','🔥','💜'], colors: ['#52e8ff','#ff5ee6','#7d7bff','#ffc24d','#ff6767','#66ef9a'] },
} as const

type Mode = keyof typeof themes
type Screen = 'home' | 'modes' | 'levels' | 'game'
type GameStatus = 'playing' | 'won' | 'lost'
type Stone = { id: number; kind: number }

function makeBoard(seed = 1, mode: Mode = 'Meyve'): Stone[] {
  const offset = Object.keys(themes).indexOf(mode)
  return Array.from({ length: 49 }, (_, i) => ({
    id: seed * 1000 + i,
    kind: (i * 7 + seed * 3 + Math.floor(i / 7) + offset) % themes[mode].symbols.length,
  }))
}

function App() {
  const [screen, setScreen] = useState<Screen>('home')
  const [mode, setMode] = useState<Mode>('Meyve')
  const [level, setLevel] = useState(1)
  const [score, setScore] = useState(0)
  const [combo, setCombo] = useState(0)
  const [moves, setMoves] = useState(30)
  const [triples, setTriples] = useState(0)
  const [tray, setTray] = useState<Stone[]>([])
  const [board, setBoard] = useState<Stone[]>(makeBoard(1))
  const [message, setMessage] = useState('Aynı türden 3 taş seç ve üçle!')
  const [sound, setSound] = useState(true)
  const [status, setStatus] = useState<GameStatus>('playing')
  const [animatingId, setAnimatingId] = useState<number | null>(null)
  const theme = themes[mode]
  const goal = 5 + Math.floor(level / 3)

  const grouped = useMemo(() => tray.reduce<Record<number, number>>((acc, stone) => {
    acc[stone.kind] = (acc[stone.kind] ?? 0) + 1
    return acc
  }, {}), [tray])

  useEffect(() => {
    if (status !== 'playing' || !tray.length) return
    const tripleKind = Object.entries(grouped).find(([, count]) => count >= 3)?.[0]
    if (tripleKind === undefined) {
      if (tray.length >= 7) {
        setStatus('lost')
        setMessage('Tepsi doldu! Bu tur kaçtı 😵')
      }
      return
    }
    const kind = Number(tripleKind)
    const timer = window.setTimeout(() => {
      setTray(current => {
        let removed = 0
        return current.filter(stone => {
          if (stone.kind === kind && removed < 3) { removed += 1; return false }
          return true
        })
      })
      setTriples(currentTriples => {
        const next = currentTriples + 1
        if (next >= goal) {
          setStatus('won')
          setMessage('BÖLÜM TAMAMLANDI! 🏆')
        } else {
          setMessage('ÜÇLEDİN! +100 ✨')
        }
        return next
      })
      setCombo(currentCombo => {
        const next = currentCombo + 1
        setScore(currentScore => currentScore + 100 + next * 25)
        return next
      })
    }, 240)
    return () => window.clearTimeout(timer)
  }, [grouped, goal, status, tray.length])

  function startGame(nextLevel = level, nextMode = mode) {
    const safeLevel = Math.min(Math.max(nextLevel, 1), 60)
    setMode(nextMode)
    setLevel(safeLevel)
    setScore(0); setCombo(0); setMoves(30); setTriples(0); setTray([])
    setBoard(makeBoard(safeLevel, nextMode))
    setMessage('Aynı türden 3 taş seç ve üçle!')
    setStatus('playing')
    setScreen('game')
  }

  function selectStone(stone: Stone) {
    if (status !== 'playing' || moves <= 0 || tray.length >= 7 || animatingId !== null) return
    setAnimatingId(stone.id)
    window.setTimeout(() => {
      const nextTray = [...tray, stone]
      setBoard(current => current.filter(item => item.id !== stone.id))
      setTray(nextTray)
      setMoves(value => Math.max(0, value - 1))
      setAnimatingId(null)
      setMessage('Taş tepsiye gitti…')
    }, 180)
  }

  function shuffle() {
    if (status !== 'playing') return
    setBoard(current => [...current].sort(() => Math.random() - 0.5))
    setMessage('Tahta karıştırıldı 🔀')
  }

  function hint() {
    if (status !== 'playing') return
    const first = board[0]
    setMessage(first ? `İpucu: ${theme.symbols[first.kind]} taşını seç 😉` : 'İpucu hazır!')
  }

  return (
    <main className={`app-shell theme-${mode.toLowerCase()}`}>
      <div className="ambient ambient-a" /><div className="ambient ambient-b" />
      <header className="topbar">
        <button className="brand" onClick={() => setScreen('home')}><span>{theme.emoji}</span> TRIPLIX</button>
        <div className="top-actions"><button className="wallet">🪙 400</button><button className="sound" onClick={() => setSound(value => !value)}>{sound ? '🔊' : '🔇'}</button></div>
      </header>

      {screen === 'home' && (
        <section className="home">
          <div className="hero">
            <div className="hero-glow" /><div className="hero-badge">{theme.emoji}</div>
            <p className="eyebrow">TRIPLIX WORLD</p><h1>Seç.<br /><em>Üçle.</em> Patlat.</h1>
            <p className="subtitle">Her bölüm yeni bir hedef, her üçlü biraz daha yüksek combo. 🎯</p>
            <button className="primary" onClick={() => startGame(1, mode)}>OYUNA BAŞLA <span>→</span></button>
            <div className="hero-mini-row"><span>🔥 Combo</span><b>x{combo}</b><span>⭐ Bölüm</span><b>{level}/60</b></div>
          </div>
          <div className="home-actions">
            <button className="big-link" onClick={() => setScreen('modes')}><span className="big-link-icon">🎨</span><div><b>OYUN MODLARI</b><small>{mode} · 4 farklı dünya</small></div><strong>›</strong></button>
            <button className="big-link" onClick={() => setScreen('levels')}><span className="big-link-icon">🗺️</span><div><b>BÖLÜMLER</b><small>60 bölüm · yıldızlarını topla</small></div><strong>›</strong></button>
          </div>
        </section>
      )}

      {screen === 'modes' && (
        <section className="subscreen">
          <div className="section-top"><button className="back" onClick={() => setScreen('home')}>‹</button><div><span>TRIPLIX WORLD</span><h2>Oyun Modları</h2></div></div>
          <p className="section-copy">Her dünyanın kendi taşı, rengi ve atmosferi var.</p>
          <div className="mode-grid">
            {(Object.keys(themes) as Mode[]).map(item => (
              <button key={item} className={`mode-card mode-${item.toLowerCase()} ${mode === item ? 'active' : ''}`} onClick={() => { setMode(item); setScreen('levels') }}>
                <div className="mode-art"><span>{themes[item].emoji}</span><i /></div><div className="mode-info"><b>{item}</b><small>{themes[item].subtitle}</small></div><strong>{mode === item ? '✓' : '›'}</strong>
              </button>
            ))}
          </div>
        </section>
      )}

      {screen === 'levels' && (
        <section className="subscreen">
          <div className="section-top"><button className="back" onClick={() => setScreen('home')}>‹</button><div><span>{mode.toUpperCase()}</span><h2>Bölümler</h2></div></div>
          <div className="world-banner"><div className="world-banner-art">{theme.emoji}</div><div><b>{mode}</b><small>Seviye {level} · 60 bölüm</small></div><span>{Math.round((level / 60) * 100)}%</span></div>
          <div className="level-grid">
            {Array.from({ length: 60 }, (_, i) => i + 1).map(item => {
              const locked = item > level + 1
              const stars = item < level ? 3 : item === level ? 1 : 0
              return <button key={item} className={`level-tile ${item === level ? 'current' : ''} ${locked ? 'locked' : ''}`} disabled={locked} onClick={() => startGame(item, mode)}>
                <b>{locked ? '🔒' : item}</b>{!locked && <span>{'★'.repeat(stars)}{'☆'.repeat(3-stars)}</span>}
              </button>
            })}
          </div>
        </section>
      )}

      {screen === 'game' && (
        <section className="game-screen">
          <div className="game-head"><button className="back" onClick={() => setScreen('levels')}>‹</button><div><span>BÖLÜM {level}</span><b>{mode.toUpperCase()}</b></div><div className="coins">🪙 400</div></div>
          <div className="stats"><div><small>SKOR</small><b>{score}</b></div><div><small>HAMLE</small><b>{moves}</b></div><div><small>HEDEF</small><b>{triples}/{goal}</b></div><div><small>COMBO</small><b className="combo">{combo ? `x${combo}` : 'x0'}</b></div></div>
          <div className="progress"><i style={{ width: `${Math.min(100, (triples / goal) * 100)}%` }} /></div>
          <div className="board-wrap"><div className="board">
            {board.map(stone => <button disabled={status !== 'playing'} key={stone.id} className={`stone ${animatingId === stone.id ? 'stone-pop' : ''}`} style={{ background: theme.colors[stone.kind] }} onClick={() => selectStone(stone)} aria-label={`Taş ${stone.kind + 1}`}><span>{theme.symbols[stone.kind]}</span></button>)}
          </div></div>
          <div className="tray-card"><div className="tray-title"><b>SEÇİLEN TAŞLAR</b><span>{tray.length}/7</span></div><div className="tray">
            {Array.from({ length: 7 }, (_, index) => { const stone = tray[index]; return stone ? <div key={`${stone.id}-${index}`} className="tray-stone" style={{ background: theme.colors[stone.kind] }}>{theme.symbols[stone.kind]}</div> : <div className="tray-slot" key={index} /> })}
          </div></div>
          <div className={`message ${status !== 'playing' ? `message-${status}` : ''}`}>{message}</div>
          {status !== 'playing' && <div className="result-card"><div className="result-icon">{status === 'won' ? '🏆' : '💥'}</div><b>{status === 'won' ? 'BÖLÜM BAŞARILI' : 'TEPSİ DOLDU'}</b><small>{status === 'won' ? `${goal} üçlü tamamlandı · ${score} puan` : `${triples}/${goal} üçlü yaptın · ${score} puan`}</small><div className="result-actions"><button className="result-primary" onClick={() => startGame(status === 'won' ? level + 1 : level, mode)}>{status === 'won' ? 'SONRAKİ BÖLÜM →' : 'TEKRAR OYNA'}</button><button className="result-secondary" onClick={() => setScreen('home')}>ANA MENÜ</button></div></div>}
          <div className="tools"><button disabled={status !== 'playing'} onClick={shuffle}>🔀 <span>KARIŞTIR</span></button><button disabled={status !== 'playing'} onClick={hint}>💡 <span>İPUCU</span></button><button onClick={() => startGame(level, mode)}>↻ <span>SIFIRLA</span></button></div>
        </section>
      )}
    </main>
  )
}

createRoot(document.getElementById('root')!).render(<React.StrictMode><App /></React.StrictMode>)
