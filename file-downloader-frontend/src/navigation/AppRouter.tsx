import { createBrowserRouter, RouterProvider } from 'react-router-dom'

import { AppLayout } from './AppLayout'
import { FileDescriptionsPage } from '../feature/file-descriptions/ui/FileDescriptionsPage'

const router = createBrowserRouter([
  {
    element: <AppLayout />,
    children: [
      {
        index: true,
        element: <FileDescriptionsPage />,
      },
    ],
  },
])

export function AppRouter() {
  return <RouterProvider router={router} />
}
