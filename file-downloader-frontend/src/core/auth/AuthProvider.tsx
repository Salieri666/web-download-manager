import { createContext, useContext, useEffect, useState, type ReactNode } from 'react'
import { CircularProgress, Box } from '@mui/material'
import keycloak from './keycloak'

interface AuthContextType {
  authenticated: boolean
  token: string | undefined
  username: string | undefined
  logout: () => void
}

const AuthContext = createContext<AuthContextType>({
  authenticated: false,
  token: undefined,
  username: undefined,
  logout: () => {},
})

export function AuthProvider({ children }: { children: ReactNode }) {
  const [initialized, setInitialized] = useState(false)

  const [value, setValue] = useState<AuthContextType>({
    authenticated: false,
    token: undefined,
    username: undefined,
    logout: () => {},
  })

  useEffect(() => {
    keycloak
      .init({
        onLoad: 'login-required',
        checkLoginIframe: false,
        pkceMethod: 'S256',
      })
      .then((authenticated) => {
        if (authenticated) {
          setValue({
            authenticated: true,
            token: keycloak.token,
            username: keycloak.tokenParsed?.preferred_username,
            logout: () => keycloak.logout({ redirectUri: window.location.origin }),
          })
        }
        setInitialized(true)
      })

    keycloak.onTokenExpired = () => {
      keycloak.updateToken(30).then(() => {
        setValue((prev) => ({ ...prev, token: keycloak.token }))
      })
    }
  }, [])

  if (!initialized) {
    return (
      <Box
        sx={{
          height: '100vh',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          backgroundColor: '#020403',
        }}
      >
        <CircularProgress sx={{ color: 'rgb(144,246,15)' }} />
      </Box>
    )
  }

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export function useAuth() {
  return useContext(AuthContext)
}
