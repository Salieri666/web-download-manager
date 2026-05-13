import { Box, Table, TableBody, TablePagination } from '@mui/material'

import type { FileDescriptionDto } from '../../../../app/api/downloader-service/types'
import { FILE_DESCRIPTION_TABLE_MIN_WIDTH } from './fileDescriptionsTable.constants'
import { FileDescriptionTableRow } from './FileDescriptionTableRow'
import { FileDescriptionsTableHead } from './FileDescriptionsTableHead'
import { FileDescriptionsTableMessageRow } from './FileDescriptionsTableMessageRow'
import { FileDescriptionsTableSkeleton } from './FileDescriptionsTableSkeleton'

export function FileDescriptionsTable({
  items,
  isLoading,
  isError,
  totalElements,
  page,
  rowsPerPage,
  onPageChange,
  onRowsPerPageChange,
  onRowClick,
}: {
  items: FileDescriptionDto[]
  isLoading: boolean
  isError: boolean
  totalElements: number
  page: number
  rowsPerPage: number
  onPageChange: (nextPage: number) => void
  onRowsPerPageChange: (nextRowsPerPage: number) => void
  onRowClick?: (item: FileDescriptionDto) => void
}) {
  return (
    <Box
      sx={{
        overflowX: 'auto',
        background: 'rgb(10,26,13)',
        borderRadius: '0.5rem',
      }}
    >
      <Table
        size="small"
        sx={{
          minWidth: FILE_DESCRIPTION_TABLE_MIN_WIDTH,
        }}
      >
        <FileDescriptionsTableHead />
        <TableBody>
          {isLoading ? (
            <FileDescriptionsTableSkeleton />
          ) : isError ? (
            <FileDescriptionsTableMessageRow color="error" message="Backend is unavailable." />
          ) : items.length === 0 ? (
            <FileDescriptionsTableMessageRow
              color="text.secondary"
              message="No records found."
            />
          ) : (
            items.map((item) => (
              <FileDescriptionTableRow key={item.id} item={item} onClick={onRowClick} />
            ))
          )}
        </TableBody>
      </Table>

      <TablePagination
        component="div"
        count={totalElements}
        page={page}
        onPageChange={(_event, nextPage) => {
          onPageChange(nextPage)
        }}
        rowsPerPage={rowsPerPage}
        onRowsPerPageChange={(event) => {
          onRowsPerPageChange(Number(event.target.value))
        }}
        rowsPerPageOptions={[10, 20, 50]}
        labelRowsPerPage="Rows per page"
        sx={{
          borderTop: '1px solid rgba(255, 255, 255, 0.08)',
          background:
            'linear-gradient(180deg, rgba(255,255,255,0.97) 0%, rgba(241,247,244,0.98) 100%)',
          color: '#0f172a',
          '& .MuiTablePagination-toolbar': {
            minHeight: 56,
            px: 2,
          },
          '& .MuiTablePagination-selectLabel, & .MuiTablePagination-displayedRows': {
            margin: 0,
            fontWeight: 600,
            color: 'rgba(15, 23, 42, 0.78)',
          },
          '& .MuiTablePagination-select': {
            borderRadius: 999,
            fontWeight: 700,
          },
          '& .MuiTablePagination-selectIcon': {
            color: '#1d4ed8',
          },
          '& .MuiIconButton-root': {
            color: '#1d4ed8',
          },
          '& .MuiIconButton-root.Mui-disabled': {
            color: 'rgba(15, 23, 42, 0.24)',
          },
        }}
      />
    </Box>
  )
}
