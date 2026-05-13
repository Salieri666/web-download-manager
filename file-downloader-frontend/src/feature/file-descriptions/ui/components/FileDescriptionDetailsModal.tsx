import { useCallback, useState } from 'react'

import {
  Box,
  Button,
  Chip,
  CircularProgress,
  Dialog,
  DialogContent,
  DialogTitle,
  IconButton,
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableRow,
  Typography,
} from '@mui/material'
import { alpha } from '@mui/material/styles'
import CloseIcon from '@mui/icons-material/Close'
import FileDownloadIcon from '@mui/icons-material/FileDownload'

import { downloadFile, useGetFileDescriptionByIdQuery } from '../../../../app/api/downloader-service'
import type { FileChunkDto } from '../../../../app/api/downloader-service/types'
import { formatBytes, formatDateTime } from '../../../../shared/lib/formatters'
import { FileDescriptionStatusChip } from './FileDescriptionStatusChip'

const CHUNK_STATUS_COLORS: Record<string, string> = {
  PENDING: '#d97706',
  UPLOADING: '#2563eb',
  COMPLETED: '#15803d',
  FAILED: '#dc2626',
}

const CHUNK_STATUS_LABELS: Record<string, string> = {
  PENDING: 'Pending',
  UPLOADING: 'Uploading',
  COMPLETED: 'Completed',
  FAILED: 'Failed',
}

function ChunkStatusChip({ status }: { status?: string }) {
  const color = status ? CHUNK_STATUS_COLORS[status] ?? '#94a3b8' : '#94a3b8'
  const label = status ? CHUNK_STATUS_LABELS[status] ?? status : 'Unknown'

  return (
    <Chip
      size="small"
      label={label}
      sx={{
        fontWeight: 700,
        color,
        backgroundColor: alpha(color, 0.12),
        border: `1px solid ${alpha(color, 0.18)}`,
        '& .MuiChip-label': { px: 1 },
      }}
    />
  )
}

export function FileDescriptionDetailsModal({
  open,
  fileId,
  onClose,
}: {
  open: boolean
  fileId: string | null
  onClose: () => void
}) {
  const { data, isLoading, isError } = useGetFileDescriptionByIdQuery(
    { id: fileId ?? '' },
    { skip: !fileId || !open },
  )

  const [isDownloading, setIsDownloading] = useState(false)

  const handleDownload = useCallback(async () => {
    if (!fileId) return
    setIsDownloading(true)
    try {
      const blob = await downloadFile(fileId)
      const url = window.URL.createObjectURL(blob)
      const anchor = document.createElement('a')
      anchor.href = url
      anchor.download = data?.filename ?? 'download'
      document.body.appendChild(anchor)
      anchor.click()
      document.body.removeChild(anchor)
      window.URL.revokeObjectURL(url)
    } catch {
      console.error('Download failed')
    } finally {
      setIsDownloading(false)
    }
  }, [fileId, data?.filename])

  return (
    <Dialog
      open={open}
      onClose={onClose}
      maxWidth="md"
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
        File Details
        <IconButton onClick={onClose} sx={{ color: 'rgba(255,255,255,0.6)' }}>
          <CloseIcon />
        </IconButton>
      </DialogTitle>

      <DialogContent sx={{ px: 3, pb: 3 }}>
        {isLoading ? (
          <Box sx={{ display: 'flex', justifyContent: 'center', py: 6 }}>
            <CircularProgress sx={{ color: '#1d4ed8' }} />
          </Box>
        ) : isError || !data ? (
          <Typography sx={{ color: '#ef4444', textAlign: 'center', py: 6 }}>
            Failed to load file details.
          </Typography>
        ) : (
          <>
            <Box
              sx={{
                background: '#fbfdff',
                borderRadius: '0.5rem',
                p: 2.5,
                mb: 2.5,
              }}
            >
              <Typography
                variant="h6"
                sx={{ fontWeight: 800, color: '#0f172a', mb: 2, fontSize: 16 }}
              >
                {data.filename}
              </Typography>

              <Box sx={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 1.5 }}>
                <InfoField label="Status" value={<FileDescriptionStatusChip status={data.status} />} />
                <InfoField label="Size" value={formatBytes(data.totalSize)} />
                <InfoField label="MIME Type" value={data.mimeType} />
                <InfoField label="Created" value={formatDateTime(data.createdDate)} />
                <InfoField label="Updated" value={formatDateTime(data.updatedDate)} />
                <InfoField label="Checksum" value={data.checksum || '—'} />
              </Box>

              {data.sourceUrl && (
                <Typography
                  variant="caption"
                  sx={{ color: 'text.secondary', display: 'block', mt: 1.5, wordBreak: 'break-all' }}
                >
                  Source: {data.sourceUrl}
                </Typography>
              )}

              {data.errorMessage && (
                <Typography
                  variant="body2"
                  sx={{ color: '#dc2626', mt: 1.5, fontWeight: 600 }}
                >
                  Error: {data.errorMessage}
                </Typography>
              )}
            </Box>

            <Typography
              sx={{
                color: '#fbfdff',
                fontWeight: 700,
                fontSize: 14,
                mb: 1.5,
              }}
            >
              Chunks ({data.chunks?.length ?? 0})
            </Typography>

            <Box sx={{ overflowX: 'auto', background: '#fbfdff', borderRadius: '0.5rem' }}>
              <Table size="small">
                <ChunksTableHead />
                <TableBody>
                  {!data.chunks || data.chunks.length === 0 ? (
                    <TableRow>
                      <TableCell
                        colSpan={6}
                        align="center"
                        sx={{ color: 'text.secondary', py: 3 }}
                      >
                        No chunks available.
                      </TableCell>
                    </TableRow>
                  ) : (
                    [...data.chunks]
                      .sort((a, b) => (a.chunkIndex ?? 0) - (b.chunkIndex ?? 0))
                      .map((chunk) => (
                        <ChunkTableRow key={chunk.id} chunk={chunk} />
                      ))
                  )}
                </TableBody>
              </Table>
            </Box>

            <Button
              variant="contained"
              fullWidth
              disabled={isDownloading}
              onClick={handleDownload}
              startIcon={isDownloading ? <CircularProgress size={18} sx={{ color: '#fbfdff' }} /> : <FileDownloadIcon />}
              sx={{
                mt: 2.5,
                backgroundColor: '#1d4ed8',
                color: '#fbfdff',
                fontWeight: 700,
                textTransform: 'none',
                borderRadius: '0.5rem',
                py: 1.2,
                '&:hover': { backgroundColor: '#1e3a8a' },
              }}
            >
              {isDownloading ? 'Downloading...' : 'Download File'}
            </Button>
          </>
        )}
      </DialogContent>
    </Dialog>
  )
}

function InfoField({ label, value }: { label: string; value: React.ReactNode }) {
  return (
    <Box>
      <Typography variant="caption" sx={{ color: 'text.secondary', display: 'block', fontWeight: 600 }}>
        {label}
      </Typography>
      <Typography variant="body2" sx={{ color: '#0f172a', fontWeight: 600 }}>
        {value}
      </Typography>
    </Box>
  )
}

function ChunksTableHead() {
  return (
    <TableHead>
      <TableRow
        sx={{
          backgroundColor: '#fbfdff',
          textTransform: 'uppercase',
          '& .MuiTableCell-root': {
            fontWeight: 800,
            fontSize: 11,
            letterSpacing: 0.4,
            color: '#0f172a',
            borderBottom: '1px solid rgba(15, 23, 42, 0.08)',
          },
        }}
      >
        <TableCell>Index</TableCell>
        <TableCell>Range</TableCell>
        <TableCell>Progress</TableCell>
        <TableCell>Status</TableCell>
        <TableCell>Retries</TableCell>
        <TableCell>Error</TableCell>
      </TableRow>
    </TableHead>
  )
}

function getChunkProgress(chunk: FileChunkDto): { percent: number; current: number; total: number } | null {
  if (chunk.startByte == null || chunk.endByte == null || chunk.currentSize == null) return null
  const total = chunk.endByte - chunk.startByte
  if (total <= 0) return null
  return { percent: Math.round((chunk.currentSize / total) * 100), current: chunk.currentSize, total }
}

function ChunkTableRow({ chunk }: { chunk: FileChunkDto }) {
  const range =
    chunk.startByte !== undefined && chunk.endByte !== undefined
      ? `${formatBytes(chunk.startByte)} – ${formatBytes(chunk.endByte)}`
      : '—'

  const progress = getChunkProgress(chunk)

  return (
    <TableRow
      sx={{
        backgroundColor: '#fbfdff',
        '&:hover': { backgroundColor: '#ebeced !important' },
      }}
    >
      <TableCell>
        <Typography variant="body2" sx={{ fontWeight: 700, color: '#0f172a' }}>
          {chunk.chunkIndex ?? '—'}
        </Typography>
      </TableCell>
      <TableCell>
        <Typography variant="caption" sx={{ color: 'text.secondary' }}>
          {range}
        </Typography>
      </TableCell>
      <TableCell>
        <Typography variant="body2" sx={{ color: '#0f172a' }}>
          {progress ? `${progress.percent}% (${formatBytes(progress.current)} / ${formatBytes(progress.total)})` : formatBytes(chunk.currentSize) || '—'}
        </Typography>
      </TableCell>
      <TableCell>
        <ChunkStatusChip status={chunk.status} />
      </TableCell>
      <TableCell>
        <Typography variant="body2" sx={{ color: '#0f172a' }}>
          {chunk.retryCount ?? 0}
        </Typography>
      </TableCell>
      <TableCell>
        <Typography variant="caption" sx={{ color: '#dc2626', wordBreak: 'break-word' }}>
          {chunk.errorMessage || '—'}
        </Typography>
      </TableCell>
    </TableRow>
  )
}
