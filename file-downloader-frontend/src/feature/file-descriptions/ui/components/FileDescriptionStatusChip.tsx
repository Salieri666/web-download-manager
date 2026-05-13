import { Chip } from '@mui/material'
import { alpha } from '@mui/material/styles'

import {
  FILE_DESCRIPTION_STATUS_COLORS,
  FILE_DESCRIPTION_STATUS_LABELS,
} from './fileDescriptionsTable.constants'

function getStatusLabel(status?: string) {
  if (!status) return 'Unknown'
  return FILE_DESCRIPTION_STATUS_LABELS[status] ?? status
}

function getStatusColor(status?: string) {
  if (!status) return '#94a3b8'
  return FILE_DESCRIPTION_STATUS_COLORS[status] ?? '#94a3b8'
}

export function FileDescriptionStatusChip({ status }: { status?: string }) {
  const color = getStatusColor(status)

  return (
    <Chip
      size="small"
      label={getStatusLabel(status)}
      sx={{
        fontWeight: 700,
        color,
        backgroundColor: alpha(color, 0.12),
        border: `1px solid ${alpha(color, 0.18)}`,
        '& .MuiChip-label': {
          px: 1,
        },
      }}
    />
  )
}
