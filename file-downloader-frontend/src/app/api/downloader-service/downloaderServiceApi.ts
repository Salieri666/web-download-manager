import { createApi, fetchBaseQuery } from '@reduxjs/toolkit/query/react'

import { apiBaseUrl } from '../../../core/config/apiConfig'
import keycloak from '../../../core/auth/keycloak'
import type {
  CreateFileDto,
  FileDescriptionDto,
  FileDescriptionFilter,
  FileDescriptionWithChunksDto,
  GetFileDescriptionByIdArgs,
  GetFileDescriptionsArgs,
  PageFileDescriptionDto,
} from './types'

const baseQuery = fetchBaseQuery({
  baseUrl: apiBaseUrl,
  prepareHeaders: (headers) => {
    if (keycloak.token) {
      headers.set('authorization', `Bearer ${keycloak.token}`)
    }

    headers.set('content-type', 'application/json')

    return headers
  },
})

function buildFilterBody(filter?: FileDescriptionFilter) {
  return filter && Object.keys(filter).length > 0 ? filter : {}
}

export const downloaderServiceApi = createApi({
  reducerPath: 'downloaderServiceApi',
  baseQuery,
  tagTypes: ['FileDescription'],
  endpoints: (build) => ({
    getAllFileDescriptions: build.query<
      PageFileDescriptionDto,
      GetFileDescriptionsArgs | void
    >({
      query: (args) => {
        const page = args?.page ?? 0
        const size = args?.size ?? 20

        return {
          url: '/file-description/all',
          method: 'POST',
          params: {
            page,
            size,
            ...(args?.sort?.length ? { sort: args.sort } : {}),
          },
          body: buildFilterBody(args?.filter),
        }
      },
      providesTags: (result) =>
        result?.content?.length
          ? [
              ...result.content.map(({ id }) => ({
                type: 'FileDescription' as const,
                id,
              })),
              { type: 'FileDescription' as const, id: 'LIST' },
            ]
          : [{ type: 'FileDescription' as const, id: 'LIST' }],
    }),
    getFileDescriptionById: build.query<
      FileDescriptionWithChunksDto,
      GetFileDescriptionByIdArgs
    >({
      query: ({ id }) => ({
        url: `/file-description/${id}`,
        method: 'GET',
      }),
      providesTags: (_result, _error, { id }) => [
        { type: 'FileDescription', id },
      ],
    }),
    createFileDescription: build.mutation<FileDescriptionDto, CreateFileDto>({
      query: (body) => ({
        url: '/file-description',
        method: 'POST',
        body,
      }),
      invalidatesTags: [{ type: 'FileDescription', id: 'LIST' }],
    }),
    deleteFileDescription: build.mutation<void, { id: string }>({
      query: ({ id }) => ({
        url: `/file-description/${id}`,
        method: 'DELETE',
      }),
      invalidatesTags: [{ type: 'FileDescription', id: 'LIST' }],
    }),
  }),
})

export async function downloadFile(id: string): Promise<Blob> {
  await keycloak.updateToken(10)
  const token = keycloak.token
  const response = await fetch(`${apiBaseUrl}/file-description/${id}/download`, {
    method: 'GET',
    headers: {
      ...(token ? { authorization: `Bearer ${token}` } : {}),
    },
  })
  if (!response.ok) throw new Error('Download failed')
  return response.blob()
}

export const {
  useGetAllFileDescriptionsQuery,
  useGetFileDescriptionByIdQuery,
  useCreateFileDescriptionMutation,
  useDeleteFileDescriptionMutation,
} = downloaderServiceApi
