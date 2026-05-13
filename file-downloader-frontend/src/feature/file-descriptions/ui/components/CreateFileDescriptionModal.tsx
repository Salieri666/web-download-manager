import { useState } from 'react'

import {
  Box,
  Button,
  Dialog,
  DialogContent,
  DialogTitle,
  IconButton,
  TextField,
} from '@mui/material'
import CloseIcon from '@mui/icons-material/Close'

export function CreateFileDescriptionModal({
  open,
  onClose,
  onSubmit,
}: {
  open: boolean
  onClose: () => void
  onSubmit: (data: { filename: string; sourceUrl: string }) => Promise<void>
}) {
  const [filename, setFilename] = useState('')
  const [sourceUrl, setSourceUrl] = useState('')
  const [isSubmitting, setIsSubmitting] = useState(false)

  const handleSubmit = async () => {
    if (!filename.trim() || !sourceUrl.trim()) return
    setIsSubmitting(true)
    try {
      await onSubmit({ filename: filename.trim(), sourceUrl: sourceUrl.trim() })
      setFilename('')
      setSourceUrl('')
      onClose()
    } catch {
      console.error('Failed to create file description')
    } finally {
      setIsSubmitting(false)
    }
  }

  const handleClose = () => {
    if (isSubmitting) return
    onClose()
  }

  return (
    <Dialog
      open={open}
      onClose={handleClose}
      maxWidth="sm"
      fullWidth
      PaperProps={{
        sx: {
          background: '#0f2814',
          borderRadius: '0.75rem',
          backgroundImage: 'none',
        },
      }}
    >
      <DialogTitle
        sx={{
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'space-between',
          color: '#fbfdff',
          fontWeight: 700,
          fontSize: 18,
          px: 3,
          pt: 2.5,
          pb: 2,
        }}
      >
        Add New File
        <IconButton onClick={handleClose} sx={{ color: 'rgba(255,255,255,0.6)' }}>
          <CloseIcon />
        </IconButton>
      </DialogTitle>

      <DialogContent sx={{ px: 3, pb: 3 }}>
        <Box
          sx={{
            background: '#fbfdff',
            borderRadius: '0.5rem',
            p: 2.5,
            display: 'flex',
            flexDirection: 'column',
            gap: 2,
          }}
        >
          <TextField
            label="Filename"
            value={filename}
            onChange={(e) => setFilename(e.target.value)}
            fullWidth
            size="small"
            required
            sx={{
              '& .MuiOutlinedInput-root': {
                backgroundColor: '#fbfdff',
                '& fieldset': { borderColor: 'rgba(15, 23, 42, 0.18)' },
                '&:hover fieldset': { borderColor: '#1d4ed8' },
                '&.Mui-focused fieldset': { borderColor: '#1d4ed8' },
              },
              '& .MuiInputLabel-root': { color: 'rgba(15, 23, 42, 0.6)', fontWeight: 600 },
              '& .MuiInputBase-input': { color: '#0f172a', fontWeight: 600 },
            }}
          />
          <TextField
            label="Source URL"
            value={sourceUrl}
            onChange={(e) => setSourceUrl(e.target.value)}
            fullWidth
            size="small"
            required
            sx={{
              '& .MuiOutlinedInput-root': {
                backgroundColor: '#fbfdff',
                '& fieldset': { borderColor: 'rgba(15, 23, 42, 0.18)' },
                '&:hover fieldset': { borderColor: '#1d4ed8' },
                '&.Mui-focused fieldset': { borderColor: '#1d4ed8' },
              },
              '& .MuiInputLabel-root': { color: 'rgba(15, 23, 42, 0.6)', fontWeight: 600 },
              '& .MuiInputBase-input': { color: '#0f172a', fontWeight: 600 },
            }}
          />
          <Button
            variant="contained"
            fullWidth
            disabled={!filename.trim() || !sourceUrl.trim() || isSubmitting}
            onClick={handleSubmit}
            sx={{
              mt: 1,
              backgroundColor: '#1d4ed8',
              color: '#fbfdff',
              fontWeight: 700,
              textTransform: 'none',
              borderRadius: '0.5rem',
              py: 1.2,
              '&:hover': { backgroundColor: '#1e3a8a' },
            }}
          >
            {isSubmitting ? 'Creating...' : 'Create'}
          </Button>
        </Box>
      </DialogContent>
    </Dialog>
  )
}
