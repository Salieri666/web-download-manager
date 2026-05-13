import { configureStore } from '@reduxjs/toolkit'

import { downloaderServiceApi } from '../app/api/downloader-service'

export const store = configureStore({
  reducer: {
    [downloaderServiceApi.reducerPath]: downloaderServiceApi.reducer,
  },
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware().concat(downloaderServiceApi.middleware),
  devTools: import.meta.env.DEV,
})

export type RootState = ReturnType<typeof store.getState>
export type AppDispatch = typeof store.dispatch
