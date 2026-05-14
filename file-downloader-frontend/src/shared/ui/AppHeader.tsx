import { AppBar, Box, Button, Container, Toolbar, Typography } from '@mui/material'
import { Link as RouterLink } from 'react-router-dom'
import LogoutIcon from '@mui/icons-material/Logout'
import { useAuth } from '../../core/auth'

export function AppHeader() {
  const { authenticated, username, logout } = useAuth()

  return (
    <AppBar
      position="sticky"
      elevation={0}
      sx={{
        borderBottom: '1px solid',
        borderColor: 'rgb(144,246,15, 0.16)',
        background:
          'rgb(10,26,13)',
        color: 'text.primary',
        backdropFilter: 'blur(14px)',
      }}
    >
      <Container maxWidth="xl">
        <Toolbar disableGutters sx={{ minHeight: 68, gap: 2 }}>
          <Box sx={{ flex: 1, minWidth: 0 }}>
            <Typography
              variant="h6"
              component={RouterLink}
              to="/"
              sx={{
                fontWeight: 800,
                lineHeight: 1.1,
                textDecoration: 'none',
                color: 'rgb(144,246,15, 1)',
                display: 'inline-flex',
                alignItems: 'center',
                gap: 1,
              }}
            >
              File Downloader
            </Typography>
            <Typography variant="body2" sx={{ color: 'rgb(144,246,15, 0.8)' }}>
              Browser-style download manager
            </Typography>
          </Box>

          {authenticated && (
            <Box sx={{ display: 'flex', alignItems: 'center', gap: 1.5 }}>
              <Typography variant="body2" sx={{ color: 'rgb(144,246,15, 0.9)', fontWeight: 600 }}>
                {username}
              </Typography>
              <Button
                variant="outlined"
                size="small"
                onClick={logout}
                startIcon={<LogoutIcon />}
                sx={{
                  color: 'rgb(144,246,15, 0.8)',
                  borderColor: 'rgb(144,246,15, 0.3)',
                  '&:hover': {
                    borderColor: 'rgb(144,246,15, 0.8)',
                    backgroundColor: 'rgb(144,246,15, 0.08)',
                  },
                }}
              >
                Logout
              </Button>
            </Box>
          )}
        </Toolbar>
      </Container>
    </AppBar>
  )
}
