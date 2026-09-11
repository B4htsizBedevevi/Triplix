import { StrictMode, useMemo, useState } from 'react'
import { createRoot } from 'react-dom/client'
import './styles.css'

type Mode = 'MEYVE' | 'KLASİK' | 'UZAY'
type Tile = { id: number; type: number }

const SYMBOLS = ['●', '◆', '✦', '■', '▲', '✚']
const LABELS: Record<Mode, string> = { MEYVE: 'MEYVE', KLASİK: 'KLASİK', UZAY: 'UZAY' }

function randomBoard(nextId = 0): Tile[] {
  return Array.from({ length: 49 }, (_, i) => ({ id: nextId + i, type: Math.floor(Math.random() * SYMBOLS.length) }))
}

function App() {
  const [screen, setScreen] = useState<'home' | 'game'>('home')
  const [mode, setMode] = useState<Mode>('MEYVE')
  const [level, setLevel] = useState(1)
  const [score, setScore] = useState(0)
  const [combo, setCombo] = useState(0)
  const [triples, setTriples] = useState(0)
  const [moves, setMoves] = useState(30)
  const [tray, setTray] = useState<Tile[]>([])
  const [board, setBoard] = useState<Tile[]>(() => randomBoard())
  const [message, setMessage] = useState('Aynı sembolden 3 taş seç.')
  const [coins, setCoins] = useState(400)
  const [muted, setMuted] = useState(false)
  const [showModes, setShowModes] = useState(false)

  const goal = 5 + Math.floor(level / 3)
  const progress = Math.min(100, (triples / goal) * 100)

  const grouped = useMemo(() => {
    const counts = new Map<number, number>()
    tray.forEach((t) => counts.set(t.type, (counts.get(t.type) ?? 0) + 1))
    return counts
  }, [tray])

  function startGame(nextMode = mode, nextLevel = level) {
    setMode(nextMode)
    setLevel(nextLevel)
    setScore(0)
    setCombo(0)
    setTriples(0)
    setMoves(30)
    setTray([])
    setBoard(randomBoard())
    setMessage('Aynı sembolden 3 taş seç.')
    setScreen('game')
  }

  function selectTile(tile: Tile) {
    if (moves <= 0 || tray.length >= 7) return
    const nextTray = [...tray, tile]
    const nextBoard = board.filter((t) => t.id !== tile.id)
    setBoard(nextBoard)
    setTray(nextTray)
    setMoves((m) => m - 1)

    const count = grouped.get(tile.type) ?? 0
    if (count + 1 >= 3) {
      const remaining = nextTray.filter((t) => t.type !== tile.type)
      const newCombo = combo + 1
      setTray(remaining)
      setTriples((t) => t + 1)
      setCombo(newCombo)
      setScore((s) => s + 100 * newCombo)
      setCoins((c) => c + 5)
      setMessage(`💥 Üçlü patladı! x${newCombo} COMBO`)
      setBoard((current) => {
        const needed = 49 - current.length
        return [...current, ...randomBoard(Math.floor(Math.random() * 100000)).slice(0, needed)]
      })
      return
    }

    setCombo(0)
    if (nextTray.length === 7) setMessage('Tepsi doldu! Bir üçlü oluştur.')
    else setMessage(`${nextTray.length}/7 taş seçildi`)
  }

  function resetGame() {
    startGame(mode, level)
  }

  if (screen === 'home') {
    return (
      <div className="app-shell">
        <header className="topbar">
          <button className="brand" onClick={() => setScreen('home')} aria-label="TRIPLIX ana sayfa">
            <span className="brand-mark">T</span><span>TRIPLIX</span>
          </button>
          <div className="wallet"><span>🪙</span> {coins}</div>
        </header>

        <main className="home-content">
          <section className="hero-card">
            <div>
              <span className="eyebrow">YENİ NESİL BULMACA</span>
              <h1>Seç. Üçle.<br /><em>Patlat.</em> 🎯</h1>
              <p>Doğru taşları seç, tepsiyi yönet, combo'yu büyüt.</p>
              <button className="primary-btn" onClick={() => startGame()}>OYUNA BAŞLA <span>→</span></button>
            </div>
            <div className="hero-orbit" aria-hidden="true"><span>◆</span><span>●</span><span>✦</span><span>■</span></div>
          </section>

          <div className="section-head"><h2>Oyun Modları</h2><button onClick={() => setShowModes((v) => !v)}>TÜMÜ</button></div>
          <div className="mode-grid">
            {(showModes ? (Object.keys(LABELS) as Mode[]) : ['MEYVE', 'KLASİK'] as Mode[]).map((item) => (
              <button key={item} className={`mode-card ${mode === item ? 'active' : ''}`} onClick={() => { setMode(item); startGame(item) }}>
                <div className="mode-icon">{item === 'MEYVE' ? '🍊' : item === 'UZAY' ? '🚀' : '◆'}</div>
                <strong>{LABELS[item]}</strong><span>{item === 'MEYVE' ? 'Renkli başlangıç' : 'Saf TRIPLIX'}</span>
              </button>
            ))}
          </div>

          <section className="level-card">
            <div><span className="eyebrow">DEVAM ET</span><h2>Bölüm {level}</h2><p>Sonraki hedef: {goal} üçlü</p></div>
            <button className="circle-btn" onClick={() => startGame(mode, level)}>▶</button>
          </section>
        </main>
      </div>
    )
  }

  return (
    <div className="app-shell game-shell">
      <header className="topbar game-topbar">
        <button className="icon-btn" onClick={() => setScreen('home')}>‹</button>
        <div className="game-title"><span>{LABELS[mode]}</span><strong>Bölüm {level}</strong></div>
        <button className="icon-btn" onClick={() => setMuted((v) => !v)}>{muted ? '🔇' : '🔊'}</button>
      </header>

      <main className="game-content">
        <div className="stats-row">
          <div><span>HAMLE</span><strong>{moves}</strong></div>
          <div><span>HEDEF</span><strong>{triples}<small>/{goal}</small></strong></div>
          <div><span>COMBO</span><strong className={combo > 0 ? 'combo-hot' : ''}>x{combo}</strong></div>
          <div><span>SKOR</span><strong>{score.toLocaleString('tr-TR')}</strong></div>
        </div>
        <div className="progress"><i style={{ width: `${progress}%` }} /></div>

        <section className="board-card">
          <div className="board">
            {board.map((tile) => (
              <button key={tile.id} className={`tile tile-${tile.type}`} onClick={() => selectTile(tile)} aria-label={`${SYMBOLS[tile.type]} taş`}>
                <span>{SYMBOLS[tile.type]}</span>
              </button>
            ))}
          </div>
        </section>

        <section className="tray-card">
          <div className="tray-head"><strong>SEÇİLEN TAŞLAR</strong><span>{tray.length}/7</span></div>
          <div className="tray">
            {Array.from({ length: 7 }, (_, i) => {
              const tile = tray[i]
              return <div key={i} className={`tray-slot ${tile ? `tile-${tile.type}` : ''} ${tile && (grouped.get(tile.type) ?? 0) === 2 ? 'pair' : ''}`}>{tile && <span>{SYMBOLS[tile.type]}</span>}</div>
            })}
          </div>
          <div className="message">{message}</div>
        </section>

        <div className="tools">
          <button onClick={() => { setBoard(randomBoard()); setMessage('Tahta karıştırıldı.') }}>⤨ <span>KARIŞTIR</span></button>
          <button onClick={() => setMessage('İpucu: aynı sembolden iki taşın yanını ara 👀')}>💡 <span>İPUCU</span></button>
          <button onClick={resetGame}>↻ <span>SIFIRLA</span></button>
        </div>
      </main>
    </div>
  )
}

createRoot(document.getElementById('root')!).render(<StrictMode><App /></StrictMode>)
