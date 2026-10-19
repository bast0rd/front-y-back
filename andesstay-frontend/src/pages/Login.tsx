import { Box, Button, Paper, Typography } from '@mui/material'
import WindowIcon from '@mui/icons-material/Window'
import { Navigate } from 'react-router-dom'
import { useAuth } from '../hooks/useAuth'

export function Login() {
  const { isAuthenticated, login } = useAuth()

  if (isAuthenticated) {
    return <Navigate to="/dashboard" replace />
  }

  return (
    <Box
      sx={{
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
        minHeight: '100vh',
        bgcolor: 'background.default',
      }}
    >
      <Paper elevation={0} sx={{ p: 6, textAlign: 'center', border: '1px solid #e0e0e0', borderRadius: 3 }}>
        <Typography variant="h4" gutterBottom>
          AndesStay
        </Typography>
        <Typography variant="body1" color="text.secondary" sx={{ mb: 4 }}>
          Plataforma de gestión de reservas
        </Typography>
        <Button variant="contained" size="large" startIcon={<WindowIcon />} onClick={login}>
          Iniciar sesión con Microsoft
        </Button>
      </Paper>
    </Box>
  )
}