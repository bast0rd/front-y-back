import { Alert, Snackbar } from '@mui/material'

export interface ToastState {
  message: string
  severity: 'success' | 'error'
}

interface ToastProps {
  toast: ToastState | null
  onClose: () => void
}

/** Aviso flotante para confirmar (o informar el error de) una acción. */
export function Toast({ toast, onClose }: ToastProps) {
  return (
    <Snackbar
      open={toast !== null}
      autoHideDuration={4000}
      onClose={onClose}
      anchorOrigin={{ vertical: 'bottom', horizontal: 'center' }}
    >
      {toast ? (
        <Alert severity={toast.severity} variant="filled" onClose={onClose}>
          {toast.message}
        </Alert>
      ) : undefined}
    </Snackbar>
  )
}
