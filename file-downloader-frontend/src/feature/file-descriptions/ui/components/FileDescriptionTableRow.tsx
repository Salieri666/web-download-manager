import { Box, IconButton, TableCell, TableRow, Tooltip, Typography } from '@mui/material'
import DeleteIcon from '@mui/icons-material/DeleteOutlined'

import { formatBytes, formatDateTime } from '../../../../shared/lib/formatters'
import type { FileDescriptionDto } from '../../../../app/api/downloader-service/types'
import { FileDescriptionStatusChip } from './FileDescriptionStatusChip'

export function FileDescriptionTableRow({
  item,
  onClick,
  onDelete,
}: {
  item: FileDescriptionDto
  onClick?: (item: FileDescriptionDto) => void
  onDelete?: (item: FileDescriptionDto) => void
}) {
  const handleDelete = (e: React.MouseEvent) => {
    e.stopPropagation()
    onDelete?.(item)
  }

  return (
    <TableRow
      hover
      onClick={() => onClick?.(item)}
      sx={{
        backgroundColor: '#fbfdff',
        cursor: onClick ? 'pointer' : undefined,
        '&:hover': {
          backgroundColor: '#ebeced !important',
        },
      }}
    >
      <TableCell>
        <Typography
          variant="body2"
          sx={{
            fontWeight: 800,
            lineHeight: 1.2,
            color: '#0f172a',
            overflow: 'hidden',
            textOverflow: 'ellipsis',
            whiteSpace: 'nowrap',
            maxWidth: 360,
          }}
        >
          {item.filename}
        </Typography>
        <Typography
          variant="caption"
          sx={{
            color: 'text.secondary',
            display: 'block',
            mt: 0.4,
            overflow: 'hidden',
            textOverflow: 'ellipsis',
            whiteSpace: 'nowrap',
            maxWidth: { xs: 240, lg: 420 },
          }}
        >
          {item.sourceUrl}
        </Typography>
      </TableCell>
      <TableCell>
        <FileDescriptionStatusChip status={item.status} />
      </TableCell>
      <TableCell>
        <Box
          sx={{
            display: 'flex',
            alignItems: 'center',
            gap: 1,
          }}
        >
          <Box
            sx={{
              flex: 1,
              height: 8,
              borderRadius: 999,
              backgroundColor: '#e2e8f0',
              overflow: 'hidden',
            }}
          >
            <Box
              sx={{
                width: `${item.percentage}%`,
                height: '100%',
                borderRadius: 999,
                background:
                  item.status === 'FAILED'
                    ? 'linear-gradient(90deg, #ef4444 0%, #dc2626 100%)'
                    : 'linear-gradient(90deg, #2f9c48 0%, #7beb3f 100%)',
              }}
            />
          </Box>
          <Typography variant="caption" sx={{ color: 'text.secondary', minWidth: 28 }}>
            {item.percentage}%
          </Typography>
        </Box>
      </TableCell>
      <TableCell>
        <Typography variant="body2">{formatBytes(item.totalSize)}</Typography>
      </TableCell>
      <TableCell>
        <Typography variant="body2" sx={{ wordBreak: 'break-word' }}>
          {item.mimeType}
        </Typography>
      </TableCell>
      <TableCell>
        <Typography variant="caption" sx={{ display: 'block', color: 'text.secondary' }}>
          {formatDateTime(item.createdDate)}
        </Typography>
      </TableCell>
      <TableCell sx={{ width: 48 }}>
        <Tooltip title="Delete">
          <IconButton size="small" onClick={handleDelete} sx={{ color: '#dc2626' }}>
            <DeleteIcon fontSize="small" />
          </IconButton>
        </Tooltip>
      </TableCell>
    </TableRow>
  )
}
