import { useCallback, useEffect, useState } from 'react'

import { Refresh } from '@mui/icons-material'
import {
  Box,
  IconButton,
  ListItemIcon,
  ListItemText,
  Menu,
  MenuItem,
  Tooltip,
  Typography,
} from '@mui/material'
import { styled } from '@mui/material/styles'

const INTERVAL_OPTIONS = [
  { label: 'Off', value: 0 },
  { label: '5s', value: 5000 },
  { label: '10s', value: 10000 },
  { label: '30s', value: 30000 },
  { label: '1m', value: 60000 },
  { label: '5m', value: 300000 },
]

const DropdownButton = styled('button')({
  background: 'rgba(255,255,255,0.11)',
  border: 'none',
  borderRadius: '0.375rem',
  color: '#fbfdff',
  cursor: 'pointer',
  fontFamily: 'inherit',
  fontSize: 12,
  fontWeight: 600,
  lineHeight: 1,
  padding: '6px 10px',
  whiteSpace: 'nowrap',
  '&:hover': {
    background: 'rgba(255,255,255,0.27)',
  },
  '&:focus-visible': {
    outline: '2px solid #fbfdff',
    outlineOffset: 2,
  },
})

export function RefreshControl({
  pollingInterval,
  onPollingIntervalChange,
  onRefresh,
  isLoading,
}: {
  pollingInterval: number
  onPollingIntervalChange: (interval: number) => void
  onRefresh: () => void
  isLoading?: boolean
}) {
  const [anchorEl, setAnchorEl] = useState<HTMLElement | null>(null)
  const [countdown, setCountdown] = useState(0)

  useEffect(() => {
    if (pollingInterval > 0) {
      setCountdown(Math.round(pollingInterval / 1000))
      const id = setInterval(() => {
        setCountdown((prev) => {
          if (prev <= 1) return Math.round(pollingInterval / 1000)
          return prev - 1
        })
      }, 1000)
      return () => clearInterval(id)
    }
    setCountdown(0)
  }, [pollingInterval])

  const handleOpen = useCallback((e: React.MouseEvent<HTMLElement>) => {
    setAnchorEl(e.currentTarget)
  }, [])

  const handleClose = useCallback(() => {
    setAnchorEl(null)
  }, [])

  const handleSelect = useCallback(
    (value: number) => {
      onPollingIntervalChange(value)
      handleClose()
    },
    [onPollingIntervalChange, handleClose],
  )

  return (
    <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5 }}>
      <Tooltip title="Reload">
        <IconButton
          onClick={onRefresh}
          disabled={isLoading}
          sx={{
            backgroundColor: 'rgba(255,255,255,0.11)',
            color: '#fbfdff',
            '&:hover': { backgroundColor: 'rgba(255,255,255,0.27)' },
            '&.Mui-disabled': {
              backgroundColor: 'rgba(255,255,255,0.05)',
              color: 'rgba(255,255,255,0.3)',
            },
          }}
        >
          <Refresh />
        </IconButton>
      </Tooltip>

      <DropdownButton onClick={handleOpen} type="button">
        {INTERVAL_OPTIONS.find((o) => o.value === pollingInterval)?.label ?? 'Off'}
      </DropdownButton>

      <Menu
        anchorEl={anchorEl}
        open={Boolean(anchorEl)}
        onClose={handleClose}
        slotProps={{
          paper: {
            sx: {
              bgcolor: '#1e293b',
              borderRadius: '0.5rem',
              boxShadow: '0 4px 24px rgba(0,0,0,0.4)',
              py: 0.5,
              minWidth: 120,
            },
          },
        }}
      >
        {INTERVAL_OPTIONS.map((option) => (
          <MenuItem
            key={option.value}
            selected={option.value === pollingInterval}
            onClick={() => handleSelect(option.value)}
            sx={{
              color: '#fbfdff',
              fontSize: 13,
              fontWeight: option.value === pollingInterval ? 700 : 400,
              '&:hover': { backgroundColor: 'rgba(255,255,255,0.1)' },
              '&.Mui-selected': {
                backgroundColor: 'rgba(255,255,255,0.15)',
                '&:hover': { backgroundColor: 'rgba(255,255,255,0.2)' },
              },
            }}
          >
            <ListItemText primary={option.label} />
            {option.value === pollingInterval && (
              <ListItemIcon
                sx={{
                  color: '#1d4ed8',
                  minWidth: 'auto',
                  ml: 1,
                }}
              >
                <Typography component="span" sx={{ fontSize: 14, lineHeight: 1 }}>
                  ✓
                </Typography>
              </ListItemIcon>
            )}
          </MenuItem>
        ))}
      </Menu>


    </Box>
  )
}
