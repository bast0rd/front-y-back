import axios from 'axios'
import type { IPublicClientApplication } from '@azure/msal-browser'
import { apiRequest } from '../authConfig'

const apiClient = axios.create({
  baseURL: import.meta.env.VITE_BFF_URL as string,
})

export function attachAuthInterceptor(msalInstance: IPublicClientApplication) {
  apiClient.interceptors.request.use(async (config) => {
    const account = msalInstance.getActiveAccount()
    if (!account) {
      return config
    }

    try {
      const result = await msalInstance.acquireTokenSilent({
        ...apiRequest,
        account,
      })
      config.headers.Authorization = `Bearer ${result.accessToken}`
    } catch (error) {
      console.error('No se pudo obtener el token de acceso', error)
    }

    return config
  })
}

export default apiClient