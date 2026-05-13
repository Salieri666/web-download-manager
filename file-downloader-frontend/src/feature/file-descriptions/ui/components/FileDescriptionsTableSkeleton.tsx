import { Skeleton, TableCell, TableRow } from '@mui/material'

export function FileDescriptionsTableSkeleton({ rows = 3 }: { rows?: number }) {
  return Array.from({ length: rows }).map((_, index) => (
    <TableRow
      key={index}
      sx={{
        backgroundColor: '#fbfdff',
      }}
    >
      <TableCell>
        <Skeleton width="65%" />
        <Skeleton width="45%" />
      </TableCell>
      <TableCell>
        <Skeleton width={110} />
      </TableCell>
      <TableCell>
        <Skeleton width={110} />
      </TableCell>
      <TableCell>
        <Skeleton width={90} />
      </TableCell>
      <TableCell>
        <Skeleton width={120} />
      </TableCell>
      <TableCell>
        <Skeleton width={140} />
      </TableCell>
    </TableRow>
  ))
}
