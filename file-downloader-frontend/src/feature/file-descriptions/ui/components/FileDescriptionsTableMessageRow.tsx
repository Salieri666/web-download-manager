import { TableCell, TableRow, Typography } from '@mui/material'

import { FILE_DESCRIPTION_TABLE_COLUMNS_COUNT } from './fileDescriptionsTable.constants'

export function FileDescriptionsTableMessageRow({
  color,
  message,
}: {
  color: 'error' | 'text.secondary'
  message: string
}) {
  return (
    <TableRow>
      <TableCell colSpan={FILE_DESCRIPTION_TABLE_COLUMNS_COUNT}
                 sx={{
                   py: 4,
                   backgroundColor: '#fbfdff',
      }}
      >
        <Typography variant="body2" color={color}>
          {message}
        </Typography>
      </TableCell>
    </TableRow>
  )
}
