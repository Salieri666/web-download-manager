import { TableCell, TableHead, TableRow } from '@mui/material'

export function FileDescriptionsTableHead() {
  return (
    <TableHead>
      <TableRow
        sx={{
          backgroundColor: '#fbfdff',
          textTransform: 'uppercase',
          fontSize: 12,
          '& .MuiTableCell-root': {
            fontWeight: 800,
            letterSpacing: 0.4,
            color: '#0f172a',
            borderBottom: '1px solid rgba(15, 23, 42, 0.08)',
          },
        }}
      >
        <TableCell>File</TableCell>
        <TableCell>Status</TableCell>
        <TableCell>Progress</TableCell>
        <TableCell>Size</TableCell>
        <TableCell>Mime type</TableCell>
        <TableCell>Dates</TableCell>
      </TableRow>
    </TableHead>
  )
}
