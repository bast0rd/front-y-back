import { useEffect, useMemo, useState } from 'react'
import {
  Box, Typography, Button, Chip, Alert, Dialog, DialogTitle, DialogContent,
  DialogActions, TextField, MenuItem, CircularProgress,
} from '@mui/material'
import { DataGrid, type GridColDef } from '@mui/x-data-grid'
import AddIcon from '@mui/icons-material/Add'
import RefreshIcon from '@mui/icons-material/Refresh'
import { useAuth } from '../hooks/useAuth'
import { getReservations, createReservation, updateReservationStatus } from '../api/reservations'
import { getUnits } from '../api/catalog'
import type { Reservation, ReservationStatus } from '../types/reservation'
import type { Unit } from '../types/catalog'
import { Toast, type ToastState } from '../components/common/Toast'
import { getErrorMessage } from '../utils/errors'
import { formatDateTime, formatMoney } from '../utils/format'

const statusColor: Record<ReservationStatus, 'default' | 'success' | 'warning' | 'error' | 'info'> = {
  CREADA: 'default',
  CONFIRMADA: 'info',
  CHECKIN_PENDIENTE: 'warning',
  EN_ESTADIA: 'success',
  CHECKOUT: 'default',
  CANCELADA: 'error',
}

/** Estados a los que se puede pasar desde cada estado (flujo normal de una reserva). */
const nextStatuses: Record<ReservationStatus, ReservationStatus[]> = {
  CREADA: ['CONFIRMADA', 'CANCELADA'],
  CONFIRMADA: ['CHECKIN_PENDIENTE', 'EN_ESTADIA', 'CANCELADA'],
  CHECKIN_PENDIENTE: ['EN_ESTADIA', 'CANCELADA'],
  EN_ESTADIA: ['CHECKOUT'],
  CHECKOUT: [],
  CANCELADA: [],
}

export function Reservations() {
  const { hasRole, account } = useAuth()
  const [reservations, setReservations] = useState<Reservation[]>([])
  const [units, setUnits] = useState<Unit[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [toast, setToast] = useState<ToastState | null>(null)

  const [openCreate, setOpenCreate] = useState(false)
  const [formError, setFormError] = useState<string | null>(null)
  const [saving, setSaving] = useState(false)
  const [guestId, setGuestId] = useState('')
  const [unitId, setUnitId] = useState('')
  const [startDate, setStartDate] = useState('')
  const [endDate, setEndDate] = useState('')

  const [statusRow, setStatusRow] = useState<Reservation | null>(null)
  const [newStatus, setNewStatus] = useState<ReservationStatus | ''>('')

  const canCreate = hasRole('Cliente', 'Operador', 'Admin')
  const canChangeStatus = hasRole('Operador', 'Admin')

  const unitNames = useMemo(() => {
    const names: Record<number, string> = {}
    units.forEach((u) => { names[u.id] = u.name })
    return names
  }, [units])

  const availableUnits = useMemo(() => units.filter((u) => u.status === 'DISPONIBLE'), [units])

  async function loadData() {
    setLoading(true)
    setError(null)
    // Las unidades solo sirven para mostrar nombres: si fallan, la tabla igual se muestra
    const [reservationsResult, unitsResult] = await Promise.allSettled([getReservations(), getUnits()])
    if (reservationsResult.status === 'fulfilled') {
      setReservations(reservationsResult.value)
    } else {
      setError(getErrorMessage(reservationsResult.reason, 'No se pudieron cargar las reservas.'))
    }
    if (unitsResult.status === 'fulfilled') {
      setUnits(unitsResult.value)
    }
    setLoading(false)
  }

  useEffect(() => {
    loadData()
  }, [])

  function openCreateDialog() {
    setGuestId(account?.username ?? '')
    setUnitId('')
    setStartDate('')
    setEndDate('')
    setFormError(null)
    setOpenCreate(true)
  }

  function validateCreate(): string | null {
    if (!guestId.trim()) return 'Indica el huésped.'
    if (!unitId || Number(unitId) <= 0) return 'Indica la unidad.'
    if (!startDate || !endDate) return 'Indica las fechas de check-in y check-out.'
    if (new Date(endDate) <= new Date(startDate)) return 'El check-out debe ser posterior al check-in.'
    return null
  }

  async function handleCreate() {
    const validation = validateCreate()
    if (validation) {
      setFormError(validation)
      return
    }
    setSaving(true)
    setFormError(null)
    try {
      await createReservation({ guestId: guestId.trim(), unitId: Number(unitId), startDate, endDate })
      setOpenCreate(false)
      setToast({ message: 'Reserva creada correctamente.', severity: 'success' })
      loadData()
    } catch (e) {
      // El error se muestra DENTRO del diálogo para que no quede oculto detrás de él
      setFormError(getErrorMessage(e, 'No se pudo crear la reserva.'))
    } finally {
      setSaving(false)
    }
  }

  function openStatusDialog(row: Reservation) {
    setStatusRow(row)
    setNewStatus(nextStatuses[row.status][0] ?? '')
    setFormError(null)
  }

  async function handleStatusChange() {
    if (!statusRow || !newStatus) return
    setSaving(true)
    setFormError(null)
    try {
      await updateReservationStatus(statusRow.id, newStatus)
      setStatusRow(null)
      setToast({ message: `Reserva #${statusRow.id} actualizada a ${newStatus}.`, severity: 'success' })
      loadData()
    } catch (e) {
      setFormError(getErrorMessage(e, 'No se pudo cambiar el estado de la reserva.'))
    } finally {
      setSaving(false)
    }
  }

  const columns: GridColDef<Reservation>[] = [
    { field: 'id', headerName: 'ID', width: 70 },
    { field: 'guestId', headerName: 'Huésped', flex: 1, minWidth: 160 },
    {
      field: 'unitId', headerName: 'Unidad', flex: 1, minWidth: 140,
      valueGetter: (_value, row) => unitNames[row.unitId] ?? `Unidad ${row.unitId}`,
    },
    {
      field: 'status', headerName: 'Estado', width: 170,
      renderCell: (params) => (
        <Chip label={params.value} color={statusColor[params.value as ReservationStatus]} size="small" />
      ),
    },
    {
      field: 'startDate', headerName: 'Check-in', flex: 1, minWidth: 160,
      valueFormatter: (value: string) => formatDateTime(value),
    },
    {
      field: 'endDate', headerName: 'Check-out', flex: 1, minWidth: 160,
      valueFormatter: (value: string) => formatDateTime(value),
    },
    ...(canChangeStatus
      ? [{
          field: 'actions', headerName: 'Acciones', width: 170, sortable: false,
          renderCell: (params: { row: Reservation }) => (
            <Button
              size="small"
              disabled={nextStatuses[params.row.status].length === 0}
              onClick={() => openStatusDialog(params.row)}
            >
              Cambiar estado
            </Button>
          ),
        } as GridColDef<Reservation>]
      : []),
  ]

  const statusOptions = statusRow ? nextStatuses[statusRow.status] : []

  return (
    <Box>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3, gap: 2, flexWrap: 'wrap' }}>
        <Typography variant="h4">Reservas</Typography>
        <Box sx={{ display: 'flex', gap: 1 }}>
          <Button startIcon={<RefreshIcon />} onClick={loadData} disabled={loading}>
            Actualizar
          </Button>
          {canCreate && (
            <Button variant="contained" startIcon={<AddIcon />} onClick={openCreateDialog}>
              Nueva reserva
            </Button>
          )}
        </Box>
      </Box>

      {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}

      {loading ? (
        <Box sx={{ display: 'flex', justifyContent: 'center', mt: 4 }}><CircularProgress /></Box>
      ) : (
        <Box sx={{ height: 520, bgcolor: 'background.paper', borderRadius: 2 }}>
          <DataGrid
            rows={reservations}
            columns={columns}
            getRowId={(row) => row.id}
            initialState={{
              sorting: { sortModel: [{ field: 'id', sort: 'desc' }] },
              pagination: { paginationModel: { pageSize: 10 } },
            }}
            pageSizeOptions={[10, 25, 50]}
            localeText={{ noRowsLabel: 'Aún no hay reservas' }}
          />
        </Box>
      )}

      <Dialog open={openCreate} onClose={() => !saving && setOpenCreate(false)} fullWidth maxWidth="sm">
        <DialogTitle>Nueva reserva</DialogTitle>
        <DialogContent sx={{ display: 'flex', flexDirection: 'column', gap: 2, mt: 1 }}>
          {formError && <Alert severity="error">{formError}</Alert>}
          <TextField label="Huésped (guestId)" value={guestId} onChange={(e) => setGuestId(e.target.value)} />
          {availableUnits.length > 0 ? (
            <TextField select label="Unidad" value={unitId} onChange={(e) => setUnitId(e.target.value)}>
              {availableUnits.map((u) => (
                <MenuItem key={u.id} value={String(u.id)}>
                  {u.name} · {u.type} · {u.capacity} pers. · {formatMoney(u.pricePerNight)}/noche
                </MenuItem>
              ))}
            </TextField>
          ) : (
            <TextField
              label="Unidad (ID)" type="number" value={unitId} onChange={(e) => setUnitId(e.target.value)}
              helperText="No se pudieron cargar las unidades disponibles; indica el ID manualmente."
            />
          )}
          <TextField
            label="Check-in" type="datetime-local" value={startDate}
            onChange={(e) => setStartDate(e.target.value)}
            slotProps={{ inputLabel: { shrink: true } }}
          />
          <TextField
            label="Check-out" type="datetime-local" value={endDate}
            onChange={(e) => setEndDate(e.target.value)}
            slotProps={{ inputLabel: { shrink: true } }}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpenCreate(false)} disabled={saving}>Cancelar</Button>
          <Button variant="contained" onClick={handleCreate} disabled={saving}>
            {saving ? 'Creando...' : 'Crear'}
          </Button>
        </DialogActions>
      </Dialog>

      <Dialog open={statusRow !== null} onClose={() => !saving && setStatusRow(null)}>
        <DialogTitle>Cambiar estado de la reserva #{statusRow?.id}</DialogTitle>
        <DialogContent sx={{ minWidth: 320, display: 'flex', flexDirection: 'column', gap: 2, mt: 1 }}>
          {formError && <Alert severity="error">{formError}</Alert>}
          <Typography variant="body2" color="text.secondary">
            Estado actual: <strong>{statusRow?.status}</strong>
          </Typography>
          <TextField
            select fullWidth label="Nuevo estado" value={newStatus}
            onChange={(e) => setNewStatus(e.target.value as ReservationStatus)}
          >
            {statusOptions.map((s) => (
              <MenuItem key={s} value={s}>{s}</MenuItem>
            ))}
          </TextField>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setStatusRow(null)} disabled={saving}>Cancelar</Button>
          <Button variant="contained" onClick={handleStatusChange} disabled={saving || !newStatus}>
            {saving ? 'Guardando...' : 'Guardar'}
          </Button>
        </DialogActions>
      </Dialog>

      <Toast toast={toast} onClose={() => setToast(null)} />
    </Box>
  )
}
