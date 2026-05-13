const FALLBACK_API_BASE_URL = 'http://localhost:8081/downloader-service/api/v1'

export const apiBaseUrl =
  import.meta.env.VITE_API_BASE_URL?.trim() || FALLBACK_API_BASE_URL
