import { useEffect, useState } from 'react'
import {
  Alert, Box, Button, CircularProgress, Paper, Table, TableBody, TableCell, TableHead,
  TableRow, ToggleButton, ToggleButtonGroup, Typography,
} from '@mui/material'
import RefreshIcon from '@mui/icons-material/Refresh'
import { getKpis, getTopUnits } from '../api/report'
import { getUnits } from '../api/catalog'
import type { Kpis, ReportRange, TopUnit } from '../types/report'
import { StatCard } from '../components/common/StatCard'
import { HourBars } from '../components/common/HourBars'
import { getErrorMessage } from '../utils/errors'

const rangeLabel: Record<ReportRange, string> = {
  last24h: 'las últimas 24 horas',
  last7d: 'los últimos 7 días',
}

export function Reports() {
  const [range, setRange] = useState<ReportRange>('last24h')
  const [kpis, setKpis] = useState<Kpis | null>(null)
  const [topUnits, setTopUnits] = useState<TopUnit[]>([])
  const [unitNames, setUnitNames] = useState<Record<number, string>>({})
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  async function loadData(selected: ReportRange) {
    setLoading(true)
    setError(null)
    const [kpisResult, topResult, unitsResult] = await Promise.allSettled([
      getKpis(selected),
      getTopUnits(selected),
      getUnits(),
    ])
    if (kpisResult.status === 'fulfilled') {
      setKpis(kpisResult.value)
    } else {
      setKpis(null)
      setError(getErrorMessage(kpisResult.reason, 'No se pudieron cargar los indicadores.'))
    }
    setTopUnits(topResult.status === 'fulfilled' ? topResult.value : [])
    if (unitsResult.status === 'fulfilled') {
      const names: Record<number, string> = {}
      unitsResult.value.forEach((u) => { names[u.id] = u.name })
      setUnitNames(names)
    }
    setLoading(false)
  }

  useEffect(() => {
    loadData(range)
  }, [])

  function handleRange(_: unknown, value: ReportRange | null) {
    if (!value) return
    setRange(value)
    loadData(value)
  }

  const maxTop = Math.max(1, ...topUnits.map((u) => u.totalReservations))

  return (
    <Box>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3, flexWrap: 'wrap', gap: 2 }}>
        <Typography variant="h4">Reportería</Typography>
        <Box sx={{ display: 'flex', gap: 1, alignItems: 'center' }}>
          <ToggleButtonGroup size="small" exclusive value={range} onChange={handleRange}>
            <ToggleButton value="last24h">Últimas 24 h</ToggleButton>
            <ToggleButton value="last7d">Últimos 7 días</ToggleButton>
          </ToggleButtonGroup>
          <Button startIcon={<RefreshIcon />} onClick={() => loadData(range)} disabled={loading}>
            Actualizar
          </Button>
        </Box>
      </Box>

      {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}

      {loading ? (
        <Box sx={{ display: 'flex', justifyContent: 'center', mt: 4 }}><CircularProgress /></Box>
      ) : (
        <>
          {kpis && (
            <>
              <Box
                sx={{
                  display: 'grid', gap: 2, mb: 3,
                  gridTemplateColumns: { xs: '1fr', md: 'repeat(3, 1fr)' },
                }}
              >
                <StatCard title="Reservas creadas" value={kpis.totalReservations} subtitle={`En ${rangeLabel[range]}`} />
                <StatCard title="Ocupación activa" value={kpis.activeOccupancy} subtitle="Reservas actualmente en estadía" />
                <StatCard title="Ciclo promedio" value={`${kpis.avgCycleTimeHours.toFixed(1)} h`} subtitle="Duración media de una reserva" />
              </Box>

              <Paper elevation={0} sx={{ p: 2.5, border: '1px solid #e0e0e0', borderRadius: 2, mb: 3 }}>
                <Typography variant="subtitle1" sx={{ mb: 1, fontWeight: 600 }}>
                  Reservas por hora del día
                </Typography>
                <HourBars data={kpis.reservationsByHour} />
              </Paper>
            </>
          )}

          <Typography variant="h6" sx={{ mb: 1.5 }}>Unidades más reservadas</Typography>
          <Paper elevation={0} sx={{ border: '1px solid #e0e0e0', borderRadius: 2, overflowX: 'auto' }}>
            <Table size="small">
              <TableHead>
                <TableRow>
                  <TableCell width={60}>#</TableCell>
                  <TableCell>Unidad</TableCell>
                  <TableCell>Reservas</TableCell>
                  <TableCell sx={{ width: '40%' }} />
                </TableRow>
              </TableHead>
              <TableBody>
                {topUnits.length === 0 ? (
                  <TableRow>
                    <TableCell colSpan={4} align="center">Sin datos para este período</TableCell>
                  </TableRow>
                ) : (
                  topUnits.map((u, index) => (
                    <TableRow key={u.unitId}>
                      <TableCell>{index + 1}</TableCell>
                      <TableCell>{unitNames[u.unitId] ?? `Unidad ${u.unitId}`}</TableCell>
                      <TableCell>{u.totalReservations}</TableCell>
                      <TableCell>
                        <Box
                          sx={{
                            height: 10, borderRadius: 1, bgcolor: 'primary.main',
                            width: `${(u.totalReservations / maxTop) * 100}%`,
                          }}
                        />
                      </TableCell>
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
