import { useEffect, useMemo, useState } from 'react'
import {
  Alert, Box, Button, Chip, CircularProgress, Paper, Table, TableBody, TableCell,
  TableHead, TableRow, Typography,
} from '@mui/material'
import RefreshIcon from '@mui/icons-material/Refresh'
import { useAuth } from '../hooks/useAuth'
import { getReservations } from '../api/reservations'
import { getUnits } from '../api/catalog'
import { getKpis } from '../api/report'
import type { Reservation, ReservationStatus } from '../types/reservation'
import type { Unit } from '../types/catalog'
import type { Kpis } from '../types/report'
import { StatCard } from '../components/common/StatCard'
import { HourBars } from '../components/common/HourBars'
import { formatDateTime } from '../utils/format'

const statusColor: Record<ReservationStatus, 'default' | 'success' | 'warning' | 'error' | 'info'> = {
  CREADA: 'default',
  CONFIRMADA: 'info',
  CHECKIN_PENDIENTE: 'warning',
  EN_ESTADIA: 'success',
  CHECKOUT: 'default',
  CANCELADA: 'error',
}

const gridSx = {
  display: 'grid',
  gap: 2,
  gridTemplateColumns: { xs: '1fr', sm: 'repeat(2, 1fr)', md: 'repeat(4, 1fr)' },
  mb: 3,
}

export function Dashboard() {
  const { hasRole, name } = useAuth()
  const isAdmin = hasRole('Admin')

  const [reservations, setReservations] = useState<Reservation[]>([])
  const [units, setUnits] = useState<Unit[]>([])
  const [kpis, setKpis] = useState<Kpis | null>(null)
  const [loading, setLoading] = useState(true)
  const [warning, setWarning] = useState<string | null>(null)

  async function loadData() {
    setLoading(true)
    setWarning(null)
    const [reservationsResult, unitsResult, kpisResult] = await Promise.allSettled([
      getReservations(),
      getUnits(),
      isAdmin ? getKpis('last24h') : Promise.resolve(null),
    ])
    if (reservationsResult.status === 'fulfilled') setReservations(reservationsResult.value)
    if (unitsResult.status === 'fulfilled') setUnits(unitsResult.value)
    if (kpisResult.status === 'fulfilled') setKpis(kpisResult.value)
    if ([reservationsResult, unitsResult, kpisResult].some((r) => r.status === 'rejected')) {
      setWarning('Algunos datos no se pudieron cargar. Revisa que los servicios estén activos.')
    }
    setLoading(false)
  }

  useEffect(() => {
    loadData()
  }, [])

  const countByStatus = useMemo(() => {
    const counts: Record<string, number> = {}
    reservations.forEach((r) => { counts[r.status] = (counts[r.status] ?? 0) + 1 })
    return counts
  }, [reservations])

  const unitCount = (status: Unit['status']) => units.filter((u) => u.status === status).length

  const unitNames = useMemo(() => {
    const names: Record<number, string> = {}
    units.forEach((u) => { names[u.id] = u.name })
    return names
  }, [units])

  const latest = useMemo(
    () => [...reservations].sort((a, b) => b.id - a.id).slice(0, 5),
    [reservations],
  )

  return (
    <Box>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3, flexWrap: 'wrap', gap: 2 }}>
        <Box>
          <Typography variant="h4">Dashboard</Typography>
          {name && (
            <Typography variant="body2" color="text.secondary">
              Hola, {name}
            </Typography>
          )}
        </Box>
        <Button startIcon={<RefreshIcon />} onClick={loadData} disabled={loading}>
          Actualizar
        </Button>
      </Box>

      {warning && <Alert severity="warning" sx={{ mb: 2 }}>{warning}</Alert>}

      {loading ? (
        <Box sx={{ display: 'flex', justifyContent: 'center', mt: 4 }}><CircularProgress /></Box>
      ) : (
        <>
          <Typography variant="h6" sx={{ mb: 1.5 }}>Reservas</Typography>
          <Box sx={gridSx}>
            <StatCard title="Total de reservas" value={reservations.length} />
            <StatCard title="En estadía" value={countByStatus.EN_ESTADIA ?? 0} />
            <StatCard title="Check-in pendiente" value={(countByStatus.CONFIRMADA ?? 0) + (countByStatus.CHECKIN_PENDIENTE ?? 0)} subtitle="Confirmadas + pendientes" />
            <StatCard title="Canceladas" value={countByStatus.CANCELADA ?? 0} />
          </Box>

          <Typography variant="h6" sx={{ mb: 1.5 }}>Unidades</Typography>
          <Box sx={gridSx}>
            <StatCard title="Disponibles" value={unitCount('DISPONIBLE')} />
            <StatCard title="Ocupadas" value={unitCount('OCUPADA')} />
            <StatCard title="En mantenimiento" value={unitCount('MANTENIMIENTO')} />
            <StatCard title="Total de unidades" value={units.length} />
          </Box>

          {isAdmin && kpis && (
            <>
              <Typography variant="h6" sx={{ mb: 1.5 }}>Indicadores (últimas 24 horas)</Typography>
              <Box sx={gridSx}>
                <StatCard title="Reservas creadas" value={kpis.totalReservations} />
                <StatCard title="Ocupación activa" value={kpis.activeOccupancy} subtitle="Reservas en estadía" />
                <StatCard title="Ciclo promedio" value={`${kpis.avgCycleTimeHours.toFixed(1)} h`} />
              </Box>
              <Paper elevation={0} sx={{ p: 2.5, border: '1px solid #e0e0e0', borderRadius: 2, mb: 3 }}>
                <Typography variant="subtitle1" sx={{ mb: 1, fontWeight: 600 }}>Reservas por hora</Typography>
                <HourBars data={kpis.reservationsByHour} />
              </Paper>
            </>
          )}

          <Typography variant="h6" sx={{ mb: 1.5 }}>Últimas reservas</Typography>
          <Paper elevation={0} sx={{ border: '1px solid #e0e0e0', borderRadius: 2, overflowX: 'auto' }}>
            <Table size="small">
              <TableHead>
                <TableRow>
                  <TableCell>ID</TableCell>
                  <TableCell>Huésped</TableCell>
                  <TableCell>Unidad</TableCell>
                  <TableCell>Estado</TableCell>
                  <TableCell>Check-in</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {latest.length === 0 ? (
                  <TableRow>
                    <TableCell colSpan={5} align="center">Aún no hay reservas</TableCell>
                  </TableRow>
                ) : (
                  latest.map((r) => (
                    <TableRow key={r.id}>
                      <TableCell>{r.id}</TableCell>
                      <TableCell>{r.guestId}</TableCell>
                      <TableCell>{unitNames[r.unitId] ?? `Unidad ${r.unitId}`}</TableCell>
                      <TableCell><Chip size="small" label={r.status} color={statusColor[r.status]} /></TableCell>
                      <TableCell>{formatDateTime(r.startDate)}</TableCell>
                    </TableRow>
                  ))
                )}
              </TableBody>
            </Table>
          </Paper>
        </>
      )}
    </Box>
  )
}
