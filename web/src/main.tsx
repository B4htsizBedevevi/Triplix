import React, { useEffect, useMemo, useState } from 'react'
import { createRoot } from 'react-dom/client'
import './styles.css'

const symbols = ['🍎', '🍋', '🍇', '🍊', '🥝', '🍉']
const colors = ['#ff5c7a', '#ffc857', '#7ee081', '#62c6ff', '#a98bff', '#ff8f5c']

type Screen = 'home' | 'game'
type GameStatus = 'playing' | 'won' | 'lost'
type Stone = { id: number; kind: number }

function makeBoard(seed = 0): Stone[] {
  return Array.from({ length: 49 }, (_, i) => ({
    id: seed * 1000 + i,
    kind: (i * 7 + seed * 3 + Math.floor(i / 7)) % symbols.length,
  }))
}

function App() {
  const [screen, setScreen] = useState<Screen>('home')
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
          if (stone.kind === kind && removed < 3) {
            removed += 1
            return false
          }
          return true
        })
      })

      setTriples(currentTriples => {
        const nextTriples = currentTriples + 1
        if (nextTriples >= goal) {
          setStatus('won')
          setMessage('BÖLÜM TAMAMLANDI! 🏆')
        } else {
          setMessage('ÜÇLEDİN! +100 ✨')
        }
        return nextTriples
      })

      setCombo(currentCombo => {
        const nextCombo = currentCombo + 1
        setScore(currentScore => currentScore + 100 + nextCombo * 25)
        return nextCombo
      })
    }, 180)

    return () => window.clearTimeout(timer)
  }, [grouped, goal, status, tray.length])

  function startGame(nextLevel = level) {
    setLevel(nextLevel)
    setScore(0)
    setCombo(0)
    setMoves(30)
    setTriples(0)
    setTray([])
    setBoard(makeBoard(nextLevel))
    setMessage('Aynı türden 3 taş seç ve üçle!')
    setStatus('playing')
    setScreen('game')
  }

  function selectStone(stone: Stone) {
    if (status !== 'playing' || moves <= 0 || tray.length >= 7) return

    const nextTray = [...tray, stone]
    setBoard(current => current.filter(item => item.id !== stone.id))
    setTray(nextTray)
    setMoves(value => Math.max(0, value - 1))

    const hasTriple = Object.values(nextTray.reduce<Record<number, number>>((acc, item) => {
      acc[item.kind] = (acc[item.kind] ?? 0) + 1
      return acc
    }, {})).some(count => count >= 3)

    if (!hasTriple && nextTray.length < 7) {
      setMessage('Taş tepsiye gitti…')
    }
  }

  function shuffle() {
    if (status !== 'playing') return
    setBoard(current => [...current].sort(() => Math.random() - 0.5))
    setMessage('Tahta karıştırıldı 🔀')
  }

  function hint() {
    if (status !== 'playing') return
    const first = board[0]
    setMessage(first ? `İpucu: ${symbols[first.kind]} taşını deneyebilirsin 😉` : 'İpucu hazır!')
  }

  return (
    <main className="app-shell">
      <header className="topbar">
        <button className="brand" onClick={() => setScreen('home')}><span>◆</span> TRIPLIX</button>
        <button className="sound" onClick={() => setSound(value => !value)} aria-label="Ses aç/kapat">{sound ? '🔊' : '🔇'}</button>
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
                <button disabled={status !== 'playing'} key={stone.id} className="stone" style={{ background: colors[stone.kind] }} onClick={() => selectStone(stone)} aria-label={`Taş ${stone.kind + 1}`}>
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

          <div className={`message ${status !== 'playing' ? `message-${status}` : ''}`}>{message}</div>

          {status !== 'playing' && (
            <div className="result-card">
              <div className="result-icon">{status === 'won' ? '🏆' : '💥'}</div>
              <b>{status === 'won' ? 'BÖLÜM BAŞARILI' : 'TEPSİ DOLDU'}</b>
              <small>{status === 'won' ? `${goal} üçlü tamamlandı · ${score} puan` : `${triples}/${goal} üçlü yaptın · ${score} puan`}</small>
              <div className="result-actions">
                <button className="result-primary" onClick={() => startGame(status === 'won' ? level + 1 : level)}>{status === 'won' ? 'SONRAKİ BÖLÜM →' : 'TEKRAR OYNA'}</button>
                <button className="result-secondary" onClick={() => setScreen('home')}>ANA MENÜ</button>
              </div>
            </div>
          )}

          <div className="tools">
            <button disabled={status !== 'playing'} onClick={shuffle}>🔀 <span>KARIŞTIR</span></button>
            <button disabled={status !== 'playing'} onClick={hint}>💡 <span>İPUCU</span></button>
            <button onClick={() => startGame(level)}>↻ <span>SIFIRLA</span></button>
          </div>
        </section>
      )}
    </main>
  )
}

createRoot(document.getElementById('root')!).render(<React.StrictMode><App /></React.StrictMode>)
