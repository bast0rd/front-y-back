import { isAxiosError } from 'axios'

/** Convierte un error de axios en un mensaje claro para mostrar al usuario. */
export function getErrorMessage(error: unknown, fallback: string): string {
  if (isAxiosError(error)) {
    if (!error.response) {
      return 'No se pudo conectar con el servidor. Revisa que el backend esté activo.'
    }
    const status = error.response.status
    if (status === 401) return 'Tu sesión expiró. Vuelve a iniciar sesión.'
    if (status === 403) return 'No tienes permiso para realizar esta acción.'
    const data = error.response.data as { message?: unknown } | undefined
    if (data && typeof data.message === 'string' && data.message.trim() !== '') {
      return data.message
    }
  }
  return fallback
}
