import axios from 'axios'

const api = axios.create({
  baseURL: 'http://localhost:8080/api/cache'
})

export const getMetrics = () => api.get('/metrics')
export const getEntries = () => api.get('/entries')
export const getPolicy = () => api.get('/policy')
export const putEntry = (key, value, ttlSeconds) =>
  api.put(`/${encodeURIComponent(key)}`, null, { params: { value, ttlSeconds } })
export const getEntry = (key) => api.get(`/${encodeURIComponent(key)}`)
export const removeEntry = (key) => api.delete(`/${encodeURIComponent(key)}`)
export const clearCache = () => api.delete('/')
export const setPolicy = (policy) => api.put(`/policy/${policy}`)
export const samplePattern = () => api.post('/sample-pattern')
export const concurrencyTest = () => api.post('/concurrency-test')
