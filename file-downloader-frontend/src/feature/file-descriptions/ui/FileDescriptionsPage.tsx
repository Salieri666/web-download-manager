import { useState } from 'react'

import { useGetAllFileDescriptionsQuery } from '../../../app/api/downloader-service'
import type { FileDescriptionDto } from '../../../app/api/downloader-service/types'
import { FileDescriptionsTable } from './components/FileDescriptionsTable'
import { FileDescriptionDetailsModal } from './components/FileDescriptionDetailsModal'

export function FileDescriptionsPage() {
  const [page, setPage] = useState(0)
  const [rowsPerPage, setRowsPerPage] = useState(20)
  const [selectedFileId, setSelectedFileId] = useState<string | null>(null)

  const query = useGetAllFileDescriptionsQuery({
    page,
    size: rowsPerPage,
  })

  const items: FileDescriptionDto[] = query.data?.content ?? []
  const totalElements = query.data?.page?.totalElements ?? items.length
  const isLoading = query.isLoading || query.isFetching

  return (
    <>
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
      />
      <FileDescriptionDetailsModal
        open={selectedFileId !== null}
        fileId={selectedFileId}
        onClose={() => setSelectedFileId(null)}
      />
    </>
  )
}
