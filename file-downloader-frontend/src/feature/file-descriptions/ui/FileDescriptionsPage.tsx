import { useCallback, useState } from 'react'

import { Add, Refresh } from '@mui/icons-material'
import { Box, Button, IconButton, Tooltip } from '@mui/material'

import {
  useCreateFileDescriptionMutation,
  useDeleteFileDescriptionMutation,
  useGetAllFileDescriptionsQuery,
} from '../../../app/api/downloader-service'
import type { FileDescriptionDto } from '../../../app/api/downloader-service/types'
import { CreateFileDescriptionModal } from './components/CreateFileDescriptionModal'
import { FileDescriptionDetailsModal } from './components/FileDescriptionDetailsModal'
import { FileDescriptionsTable } from './components/FileDescriptionsTable'

export function FileDescriptionsPage() {
  const [page, setPage] = useState(0)
  const [rowsPerPage, setRowsPerPage] = useState(20)
  const [selectedFileId, setSelectedFileId] = useState<string | null>(null)
  const [isCreateModalOpen, setIsCreateModalOpen] = useState(false)

  const query = useGetAllFileDescriptionsQuery({
    page,
    size: rowsPerPage,
    sort: ['createdDate,desc'],
  })

  const [createFile] = useCreateFileDescriptionMutation()
  const [deleteFile] = useDeleteFileDescriptionMutation()

  const handleCreate = useCallback(
    async (data: { filename: string; sourceUrl: string }) => {
      await createFile(data).unwrap()
    },
    [createFile],
  )

  const handleDelete = useCallback(
    async (item: FileDescriptionDto) => {
      await deleteFile({ id: item.id }).unwrap()
    },
    [deleteFile],
  )

  const items: FileDescriptionDto[] = query.data?.content ?? []
  const totalElements = query.data?.page?.totalElements ?? items.length
  const isLoading = query.isLoading || query.isFetching

  return (
    <>
      <Box sx={{ display: 'flex', justifyContent: 'flex-end', gap: 1, mb: 2 }}>
        <Tooltip title="Reload">
          <IconButton
            onClick={() => query.refetch()}
            sx={{
              backgroundColor: 'rgba(255,255,255,0.11)',
              color: '#fbfdff',
              '&:hover': { backgroundColor: 'rgba(255,255,255,0.27)' },
            }}
          >
            <Refresh />
          </IconButton>
        </Tooltip>
        <Button
          variant="contained"
          startIcon={<Add />}
          onClick={() => setIsCreateModalOpen(true)}
          sx={{
            backgroundColor: '#1d4ed8',
            color: '#fbfdff',
            fontWeight: 700,
            textTransform: 'none',
            borderRadius: '0.5rem',
            py: 1,
            px: 3,
            '&:hover': { backgroundColor: '#1e3a8a' },
          }}
        >
          Add New File
        </Button>
      </Box>

      <FileDescriptionsTable
        items={items}
        isLoading={isLoading}
        isError={query.isError}
        totalElements={totalElements}
        page={page}
        rowsPerPage={rowsPerPage}
        onPageChange={setPage}
        onRowsPerPageChange={(nextRowsPerPage) => {
          setRowsPerPage(nextRowsPerPage)
          setPage(0)
        }}
        onRowClick={(item) => setSelectedFileId(item.id)}
        onDelete={handleDelete}
      />
      <FileDescriptionDetailsModal
        open={selectedFileId !== null}
        fileId={selectedFileId}
        onClose={() => setSelectedFileId(null)}
      />
      <CreateFileDescriptionModal
        open={isCreateModalOpen}
        onClose={() => setIsCreateModalOpen(false)}
        onSubmit={handleCreate}
      />
    </>
  )
}
