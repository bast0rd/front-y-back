import { Routes, Route, Navigate } from 'react-router-dom'
import { ProtectedRoute } from './components/Layout/ProtectedRoute'
import { AppShell } from './components/Layout/AppShell'
import { Login } from './pages/Login'
import { Dashboard } from './pages/Dashboard'
import { Reservations } from './pages/Reservations'
import { Catalog } from './pages/Catalog'
import { Reports } from './pages/Reports'
import { Audit } from './pages/Audit'

function App() {
  return (
    <Routes>
      <Route path="/login" element={<Login />} />
      <Route
        element={
          <ProtectedRoute>
            <AppShell />
          </ProtectedRoute>
        }
      >
        <Route path="/" element={<Navigate to="/dashboard" replace />} />
        <Route path="/dashboard" element={<Dashboard />} />
        <Route
          path="/reservations"
          element={
            <ProtectedRoute allowedRoles={['Admin', 'Operador', 'Cliente']}>
              <Reservations />
            </ProtectedRoute>
          }
        />
        <Route
          path="/catalog"
          element={
            <ProtectedRoute allowedRoles={['Admin', 'Operador']}>
              <Catalog />
            </ProtectedRoute>
          }
        />
        <Route
          path="/reports"
          element={
            <ProtectedRoute allowedRoles={['Admin']}>
              <Reports />
            </ProtectedRoute>
          }
        />
        <Route
          path="/audit"
          element={
            <ProtectedRoute allowedRoles={['Admin', 'Auditor']}>
              <Audit />
            </ProtectedRoute>
          }
        />
      </Route>
    </Routes>
  )
}

export default App