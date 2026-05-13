export function requireEnv(key: string): string {
  const value = import.meta.env[key] as string | undefined
  if (!value) throw new Error(`Missing required env variable: ${key}`)
  return value
}
