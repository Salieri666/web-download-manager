import type { components, operations } from './openapi.generated'

export type FileDescriptionStatus = NonNullable<
  components['schemas']['FileDescriptionDto']['status']
>

export type CreateFileDto = components['schemas']['CreateFileDto']

export type FileDescriptionFilter = components['schemas']['FileDescriptionFilter']

export interface FileDescriptionDto
  extends Omit<
    components['schemas']['FileDescriptionDto'],
    'id' | 'status' | 'metadata' | 'createdDate' | 'updatedDate'
  > {
  id: string
  status: FileDescriptionStatus
  metadata?: Record<string, unknown>
  createdDate?: string
  updatedDate?: string
}

export interface FileChunkDto
  extends Omit<
    components['schemas']['FileChunkDto'],
    'id' | 'fileId' | 'status' | 'lastHeartbeat' | 'completedAt'
  > {
  id: string
  fileId: string
  status: NonNullable<components['schemas']['FileChunkDto']['status']>
  lastHeartbeat?: string
  completedAt?: string
}

export interface FileDescriptionWithChunksDto
  extends Omit<
    components['schemas']['FileDescriptionWithChunksDto'],
    'id' | 'status' | 'metadata' | 'createdDate' | 'updatedDate' | 'chunks'
  > {
  id: string
  status: FileDescriptionStatus
  metadata?: Record<string, unknown>
  createdDate?: string
  updatedDate?: string
  chunks?: FileChunkDto[]
}

export type SortObject = string

export interface PageableObject {
  page?: number
  size?: number
  sort?: SortObject[]
}

export interface PagedModelFileDescriptionDto
  extends Omit<components['schemas']['PagedModelFileDescriptionDto'], 'content'> {
  content?: FileDescriptionDto[]
}

export type PageFileDescriptionDto = PagedModelFileDescriptionDto

type GetAllFileDescriptionsQuery = NonNullable<
  operations['getAllFileDescriptions']['parameters']['query']
>
type GetAllFileDescriptionsRequestBody = NonNullable<
  NonNullable<operations['getAllFileDescriptions']['requestBody']>['content']['application/json']
>

export interface GetFileDescriptionsArgs extends GetAllFileDescriptionsQuery {
  filter?: GetAllFileDescriptionsRequestBody
}

export interface GetFileDescriptionByIdArgs {
  id: string
}
