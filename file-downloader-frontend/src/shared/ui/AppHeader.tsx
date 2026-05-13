import { AppBar, Box, Container, Toolbar, Typography } from '@mui/material'
import { Link as RouterLink } from 'react-router-dom'

export function AppHeader() {
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
        </Toolbar>
      </Container>
    </AppBar>
  )
}
