import { Box, Container } from '@mui/material'
import { Outlet } from 'react-router-dom'

import { AppHeader } from '../shared/ui/AppHeader'

export function AppLayout() {
  return (
    <Box
      sx={{
        minHeight: '100vh',
        position: 'relative',
        overflow: 'hidden',
        backgroundColor: '#020403',
        backgroundImage:
          'linear-gradient(rgb(144,246,15, 0.6) 2px, transparent 2px), linear-gradient(90deg, rgb(144,246,15, 0.6) 2px, transparent 2px)',
        backgroundSize: '45px 45px',
        backgroundPosition: 'center top',
      }}
    >
      <AppHeader />
      <Container maxWidth="xl" sx={{ position: 'relative', zIndex: 1, py: { xs: 3, md: 5 } }}>
        <Outlet />
      </Container>
    </Box>
  )
}
