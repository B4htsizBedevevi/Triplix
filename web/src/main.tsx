import React, { useEffect, useMemo, useState } from 'react'
import { createRoot } from 'react-dom/client'
import './styles.css'

const symbols = ['🍎', '🍋', '🍇', '🍊', '🥝', '🍉']
const colors = ['#ff5c7a', '#ffc857', '#7ee081', '#62c6ff', '#a98bff', '#ff8f5c']

type Screen = 'home' | 'game'

type Stone = { id: number; kind: number }

function makeBoard(seed = 0): Stone[] {
  return Array.from({ length: 49 }, (_, i) => ({ id: seed * 100 + i, kind: (i * 7 + seed * 3 + Math.floor(i / 7)) % symbols.length }))
}

function App() {
  const [screen, setScreen] = useState<Screen>('home')
  const [level, setLevel] = useState(1)
  const [score, setScore] = useState(0)
  const [combo, setCombo] = useState(0)
  const [moves, setMoves] = useState(30)
  const [triples, setTriples] = useState(0)
  const [tray, setTray] = useState<Stone[]>([])
  const [board, setBoard] = useState<Stone[]>(makeBoard())
  const [message, setMessage] = useState('Aynı türden 3 taş seç ve üçle!')
  const [sound, setSound] = useState(true)
  const goal = 5 + Math.floor(level / 3)

  const grouped = useMemo(() => tray.reduce<Record<number, number>>((acc, stone) => {
    acc[stone.kind] = (acc[stone.kind] ?? 0) + 1
    return acc
  }, {}), [tray])

  useEffect(() => {
    if (!tray.length) return
    const tripleKind = Object.entries(grouped).find(([, count]) => count >= 3)?.[0]
    if (tripleKind === undefined) return
    const kind = Number(tripleKind)
    const timer = window.setTimeout(() => {
      setTray(current => {
        let removed = 0
        return current.filter(stone => {
          if (stone.kind === kind && removed < 3) { removed += 1; return false }
          return true
        })
      })
      setTriples(v => v + 1)
      setCombo(v => v + 1)
      setScore(v => v + 100 + combo * 25)
      setBoard(current => current.length >= 49 ? current : [...current, ...makeBoard(level).slice(0, 49 - current.length)])
      setMessage('ÜÇLEDİN! +100 ✨')
    }, 180)
    return () => window.clearTimeout(timer)
  }, [grouped, combo, level, tray.length])

  function startGame(nextLevel = level) {
    setLevel(nextLevel)
    setScore(0)
    setCombo(0)
    setMoves(30)
    setTriples(0)
    setTray([])
    setBoard(makeBoard(nextLevel))
    setMessage('Aynı türden 3 taş seç ve üçle!')
    setScreen('game')
  }

  function selectStone(stone: Stone) {
    if (moves <= 0 || tray.length >= 7) return
    setBoard(current => current.filter(item => item.id !== stone.id))
    setTray(current => [...current, stone])
    setMoves(v => Math.max(0, v - 1))
    setMessage('Taş tepsiye gitti…')
  }

  function shuffle() {
    setBoard(current => [...current].sort(() => Math.random() - 0.5))
    setMessage('Tahta karıştırıldı 🔀')
  }

  function hint() {
    const first = board[0]
    setMessage(first ? `İpucu: ${symbols[first.kind]} taşını deneyebilirsin 😉` : 'İpucu hazır!')
  }

  return (
    <main className="app-shell">
      <header className="topbar">
        <button className="brand" onClick={() => setScreen('home')}><span>◆</span> TRIPLIX</button>
        <button className="sound" onClick={() => setSound(v => !v)}>{sound ? '🔊' : '🔇'}</button>
      </header>

      {screen === 'home' ? (
        <section className="home">
          <div className="hero">
            <div className="logo-mark">◆</div>
            <p className="eyebrow">SEÇ • ÜÇLE • PATLAT</p>
            <h1>TRIPLIX</h1>
            <p className="subtitle">Kısa bir tur. Bir combo daha. Bir bölüm daha. 🎯</p>
            <button className="primary" onClick={() => startGame(1)}>OYUNA BAŞLA <span>→</span></button>
          </div>
          <div className="feature-grid">
            <button className="feature-card" onClick={() => startGame(1)}><span>🍓</span><div><b>MEYVE</b><small>Renkli üçlüler</small></div><strong>›</strong></button>
            <button className="feature-card" onClick={() => startGame(1)}><span>◆</span><div><b>KLASİK</b><small>Saf TRIPLIX</small></div><strong>›</strong></button>
            <button className="feature-card" onClick={() => startGame(1)}><span>🚀</span><div><b>UZAY</b><small>Yakında</small></div><strong>›</strong></button>
          </div>
        </section>
      ) : (
        <section className="game-screen">
          <div className="game-head">
            <button className="back" onClick={() => setScreen('home')}>‹</button>
            <div><span>BÖLÜM {level}</span><b>MEYVE</b></div>
            <div className="coins">🪙 400</div>
          </div>

          <div className="stats">
            <div><small>SKOR</small><b>{score}</b></div>
            <div><small>HAMLE</small><b>{moves}</b></div>
            <div><small>HEDEF</small><b>{triples}/{goal}</b></div>
            <div><small>COMBO</small><b>x{combo}</b></div>
          </div>

          <div className="board-wrap">
            <div className="board">
              {board.map(stone => (
                <button key={stone.id} className="stone" style={{ background: colors[stone.kind] }} onClick={() => selectStone(stone)} aria-label={`Taş ${stone.kind + 1}`}>
                  <span>{symbols[stone.kind]}</span>
                </button>
              ))}
            </div>
          </div>

          <div className="tray-card">
            <div className="tray-title"><b>SEÇİLEN TAŞLAR</b><span>{tray.length}/7</span></div>
            <div className="tray">
              {Array.from({ length: 7 }, (_, index) => {
                const stone = tray[index]
                return stone ? <div key={`${stone.id}-${index}`} className="tray-stone" style={{ background: colors[stone.kind] }}>{symbols[stone.kind]}</div> : <div className="tray-slot" key={index} />
              })}
            </div>
          </div>

          <div className="message">{message}</div>
          <div className="tools">
            <button onClick={shuffle}>🔀 <span>KARIŞTIR</span></button>
            <button onClick={hint}>💡 <span>İPUCU</span></button>
            <button onClick={() => startGame(level)}>↻ <span>SIFIRLA</span></button>
          </div>
        </section>
      )}
    </main>
  )
}

createRoot(document.getElementById('root')!).render(<React.StrictMode><App /></React.StrictMode>)
