import { useEffect, useState } from 'react'
import {
  Alert, Box, Button, CircularProgress, Dialog, DialogActions, DialogContent, DialogTitle,
  List, ListItem, ListItemText, TextField, Typography,
} from '@mui/material'
import { DataGrid, type GridColDef } from '@mui/x-data-grid'
import SearchIcon from '@mui/icons-material/Search'
import { getAuditEvents, getReservationTimeline } from '../api/audit'
import type { AuditEvent, AuditFilters } from '../types/audit'
import { getErrorMessage } from '../utils/errors'
import { formatDateTime } from '../utils/format'

const emptyFilters: AuditFilters = { actorId: '', eventType: '', from: '', to: '' }

export function Audit() {
  const [filters, setFilters] = useState<AuditFilters>(emptyFilters)
  const [events, setEvents] = useState<AuditEvent[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  const [reservationInput, setReservationInput] = useState('')
  const [timelineId, setTimelineId] = useState<number | null>(null)
  const [timeline, setTimeline] = useState<AuditEvent[]>([])
  const [timelineLoading, setTimelineLoading] = useState(false)
  const [timelineError, setTimelineError] = useState<string | null>(null)

  async function search(active: AuditFilters) {
    setLoading(true)
    setError(null)
    try {
      setEvents(await getAuditEvents(active))
    } catch (e) {
      setError(getErrorMessage(e, 'No se pudieron cargar los eventos de auditoría.'))
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    search(emptyFilters)
  }, [])

  function clearFilters() {
    setFilters(emptyFilters)
    search(emptyFilters)
  }

  async function openTimeline(reservationId: number) {
    setTimelineId(reservationId)
    setTimeline([])
    setTimelineError(null)
    setTimelineLoading(true)
    try {
      setTimeline(await getReservationTimeline(reservationId))
    } catch (e) {
      setTimelineError(getErrorMessage(e, 'No se pudo cargar la línea de tiempo.'))
    } finally {
      setTimelineLoading(false)
    }
  }

  function handleOpenFromInput() {
    const id = Number(reservationInput)
    if (Number.isInteger(id) && id > 0) {
      openTimeline(id)
    }
  }

  const columns: GridColDef<AuditEvent>[] = [
    { field: 'id', headerName: 'ID', width: 70 },
    {
      field: 'reservationId', headerName: 'Reserva', width: 120,
      renderCell: (params) => (
        <Button size="small" onClick={() => openTimeline(params.row.reservationId)}>
          #{params.row.reservationId}
        </Button>
      ),
    },
    { field: 'eventType', headerName: 'Evento', flex: 1, minWidth: 170 },
    {
      field: 'transition', headerName: 'Cambio de estado', flex: 1, minWidth: 220, sortable: false,
      valueGetter: (_value, row) =>
        row.previousStatus || row.newStatus ? `${row.previousStatus ?? '—'} → ${row.newStatus ?? '—'}` : '—',
    },
    { field: 'actorId', headerName: 'Actor', flex: 1, minWidth: 160 },
    {
      field: 'eventTimestamp', headerName: 'Fecha', flex: 1, minWidth: 170,
      valueFormatter: (value: string) => formatDateTime(value),
    },
    { field: 'traceId', headerName: 'Trace ID', flex: 1, minWidth: 200 },
  ]

  return (
    <Box>
      <Typography variant="h4" sx={{ mb: 3 }}>Auditoría</Typography>

      <Box sx={{ display: 'flex', gap: 2, flexWrap: 'wrap', alignItems: 'center', mb: 2 }}>
        <TextField
          size="small" label="Actor" value={filters.actorId}
          onChange={(e) => setFilters({ ...filters, actorId: e.target.value })}
        />
        <TextField
          size="small" label="Tipo de evento" value={filters.eventType}
          onChange={(e) => setFilters({ ...filters, eventType: e.target.value })}
        />
        <TextField
          size="small" label="Desde" type="datetime-local" value={filters.from}
          onChange={(e) => setFilters({ ...filters, from: e.target.value })}
          slotProps={{ inputLabel: { shrink: true } }}
        />
        <TextField
          size="small" label="Hasta" type="datetime-local" value={filters.to}
          onChange={(e) => setFilters({ ...filters, to: e.target.value })}
          slotProps={{ inputLabel: { shrink: true } }}
        />
        <Button variant="contained" startIcon={<SearchIcon />} onClick={() => search(filters)} disabled={loading}>
          Buscar
        </Button>
        <Button onClick={clearFilters} disabled={loading}>Limpiar</Button>
      </Box>

      <Box sx={{ display: 'flex', gap: 1, alignItems: 'center', mb: 3 }}>
        <TextField
          size="small" label="ID de reserva" type="number" value={reservationInput}
          onChange={(e) => setReservationInput(e.target.value)}
        />
        <Button onClick={handleOpenFromInput} disabled={!reservationInput}>
          Ver línea de tiempo
        </Button>
      </Box>

      {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}

      {loading ? (
        <Box sx={{ display: 'flex', justifyContent: 'center', mt: 4 }}><CircularProgress /></Box>
      ) : (
        <Box sx={{ height: 520, bgcolor: 'background.paper', borderRadius: 2 }}>
          <DataGrid
            rows={events}
            columns={columns}
            getRowId={(row) => row.id}
            initialState={{
              sorting: { sortModel: [{ field: 'id', sort: 'desc' }] },
              pagination: { paginationModel: { pageSize: 10 } },
            }}
            pageSizeOptions={[10, 25, 50]}
            localeText={{ noRowsLabel: 'No hay eventos para estos filtros' }}
          />
        </Box>
      )}

      <Dialog open={timelineId !== null} onClose={() => setTimelineId(null)} fullWidth maxWidth="sm">
        <DialogTitle>Línea de tiempo · Reserva #{timelineId}</DialogTitle>
        <DialogContent dividers>
          {timelineLoading && (
            <Box sx={{ display: 'flex', justifyContent: 'center', py: 3 }}><CircularProgress /></Box>
          )}
          {timelineError && <Alert severity="error">{timelineError}</Alert>}
          {!timelineLoading && !timelineError && timeline.length === 0 && (
            <Typography color="text.secondary">Esta reserva no tiene eventos registrados.</Typography>
          )}
          <List dense>
            {timeline.map((event) => (
              <ListItem key={event.id} divider>
                <ListItemText
                  primary={`${event.eventType}${event.newStatus ? ` · ${event.previousStatus ?? '—'} → ${event.newStatus}` : ''}`}
                  secondary={`${formatDateTime(event.eventTimestamp)} · ${event.actorId ?? 'sistema'}`}
                />
              </ListItem>
            ))}
          </List>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setTimelineId(null)}>Cerrar</Button>
        </DialogActions>
      </Dialog>
    </Box>
  )
}
