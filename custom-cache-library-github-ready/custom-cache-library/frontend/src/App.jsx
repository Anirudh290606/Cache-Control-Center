import { useEffect, useState } from 'react'
import {
  clearCache, concurrencyTest, getEntries, getMetrics, getPolicy,
  getEntry, putEntry, removeEntry, samplePattern, setPolicy
} from './api'

function App() {
  const [metrics, setMetrics] = useState({})
  const [entries, setEntries] = useState({})
  const [policy, setPolicyState] = useState('LRU')
  const [key, setKey] = useState('')
  const [value, setValue] = useState('')
  const [ttl, setTtl] = useState(0)
  const [result, setResult] = useState('Ready')

  const refresh = async () => {
    try {
      const [m, e, p] = await Promise.all([getMetrics(), getEntries(), getPolicy()])
      setMetrics(m.data)
      setEntries(e.data)
      setPolicyState(p.data.policy)
    } catch {
      setResult('Backend is not running. Start Spring Boot on port 8080.')
    }
  }

  useEffect(() => {
    refresh()
    const timer = setInterval(refresh, 1500)
    return () => clearInterval(timer)
  }, [])

  const put = async () => {
    if (!key || !value) return setResult('Enter both key and value.')
    await putEntry(key, value, ttl)
    setResult(`PUT successful: ${key}`)
    setKey('')
    setValue('')
    refresh()
  }

  const read = async () => {
    if (!key) return setResult('Enter a key.')
    try {
      const response = await getEntry(key)
      setResult(`GET ${key}: ${response.data}`)
    } catch {
      setResult(`GET ${key}: MISS`)
    }
    refresh()
  }

  const remove = async () => {
    if (!key) return setResult('Enter a key.')
    try {
      await removeEntry(key)
      setResult(`Removed: ${key}`)
    } catch {
      setResult(`Key not found: ${key}`)
    }
    refresh()
  }

  const changePolicy = async (next) => {
    await setPolicy(next)
    setPolicyState(next)
    setResult(`Eviction policy changed to ${next}`)
    refresh()
  }

  const run = async (fn, message) => {
    const response = await fn()
    setResult(response.data || message)
    refresh()
  }

  return (
    <main className="container">
      <header>
        <div>
          <p className="eyebrow">ACENTRA HEALTH • CODEATHON</p>
          <h1>Custom Cache Control Center</h1>
          <p className="subtitle">Thread-safe Java cache with LRU, LFU, TTL and live metrics.</p>
        </div>
        <div className="policy">
          <span>Eviction Policy</span>
          <select value={policy} onChange={e => changePolicy(e.target.value)}>
            <option>LRU</option>
            <option>LFU</option>
          </select>
        </div>
      </header>

      <section className="metrics">
        <Metric title="Hit Rate" value={`${(metrics.hitRate ?? 0).toFixed(1)}%`} />
        <Metric title="Miss Rate" value={`${(metrics.missRate ?? 0).toFixed(1)}%`} />
        <Metric title="Cache Size" value={metrics.size ?? 0} />
        <Metric title="Evictions" value={metrics.evictions ?? 0} />
        <Metric title="Requests" value={metrics.totalRequests ?? 0} />
      </section>

      <section className="panel">
        <h2>Cache Operations</h2>
        <div className="form-grid">
          <input placeholder="Key" value={key} onChange={e => setKey(e.target.value)} />
          <input placeholder="Value" value={value} onChange={e => setValue(e.target.value)} />
          <input type="number" min="0" placeholder="TTL seconds" value={ttl}
                 onChange={e => setTtl(Number(e.target.value))} />
          <button onClick={put}>PUT</button>
          <button onClick={read}>GET</button>
          <button onClick={remove}>REMOVE</button>
          <button className="danger" onClick={() => run(clearCache, 'Cache cleared')}>CLEAR</button>
        </div>
        <div className="result">{result}</div>
      </section>

      <section className="panel">
        <h2>Demo & Testing</h2>
        <div className="actions">
          <button onClick={() => run(samplePattern, 'Sample pattern complete')}>Run Sample Pattern</button>
          <button onClick={() => run(concurrencyTest, 'Concurrency test complete')}>Run Concurrency Test</button>
          <button onClick={refresh}>Refresh</button>
        </div>
      </section>

      <section className="panel">
        <h2>Current Cache Entries</h2>
        <table>
          <thead>
            <tr><th>Key</th><th>Value</th><th>Frequency</th><th>Expires</th></tr>
          </thead>
          <tbody>
            {Object.values(entries).map(entry => (
              <tr key={entry.key}>
                <td>{entry.key}</td>
                <td>{entry.value}</td>
                <td>{entry.frequency}</td>
                <td>{entry.expiresAt === 9223372036854775807 ? 'Never' : new Date(entry.expiresAt).toLocaleTimeString()}</td>
              </tr>
            ))}
            {Object.keys(entries).length === 0 && (
              <tr><td colSpan="4" className="empty">Cache is empty</td></tr>
            )}
          </tbody>
        </table>
      </section>

      <footer>Custom Cache Library • Java 17 • Spring Boot • React</footer>
    </main>
  )
}

function Metric({ title, value }) {
  return (
    <div className="metric">
      <span>{title}</span>
      <strong>{value}</strong>
    </div>
  )
}

export default App
