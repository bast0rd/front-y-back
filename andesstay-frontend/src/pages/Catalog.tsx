import { useEffect, useState } from 'react'
import {
  Alert, Box, Button, Chip, CircularProgress, Dialog, DialogActions, DialogContent,
  DialogTitle, MenuItem, TextField, Typography,
} from '@mui/material'
import { DataGrid, type GridColDef } from '@mui/x-data-grid'
import AddIcon from '@mui/icons-material/Add'
import RefreshIcon from '@mui/icons-material/Refresh'
import { useAuth } from '../hooks/useAuth'
import { createUnit, getUnits, updateUnit } from '../api/catalog'
import type { Unit, UnitRequest, UnitStatus } from '../types/catalog'
import { Toast, type ToastState } from '../components/common/Toast'
import { getErrorMessage } from '../utils/errors'
import { formatMoney } from '../utils/format'

const unitStatuses: UnitStatus[] = ['DISPONIBLE', 'OCUPADA', 'MANTENIMIENTO']

const statusColor: Record<UnitStatus, 'success' | 'warning' | 'error'> = {
  DISPONIBLE: 'success',
  OCUPADA: 'warning',
  MANTENIMIENTO: 'error',
}

export function Catalog() {
  const { hasRole } = useAuth()
  const isAdmin = hasRole('Admin')

  const [units, setUnits] = useState<Unit[]>([])
  const [filter, setFilter] = useState<UnitStatus | 'TODAS'>('TODAS')
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [toast, setToast] = useState<ToastState | null>(null)

  const [dialogOpen, setDialogOpen] = useState(false)
  const [editingId, setEditingId] = useState<number | null>(null)
  const [formError, setFormError] = useState<string | null>(null)
  const [saving, setSaving] = useState(false)
  const [name, setName] = useState('')
  const [type, setType] = useState('')
  const [capacity, setCapacity] = useState('')
  const [price, setPrice] = useState('')
  const [status, setStatus] = useState<UnitStatus>('DISPONIBLE')

  async function loadUnits(selected: UnitStatus | 'TODAS' = filter) {
    setLoading(true)
    setError(null)
    try {
      setUnits(await getUnits(selected === 'TODAS' ? undefined : selected))
    } catch (e) {
      setError(getErrorMessage(e, 'No se pudo cargar el catálogo.'))
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    loadUnits('TODAS')
  }, [])

  function handleFilter(value: UnitStatus | 'TODAS') {
    setFilter(value)
    loadUnits(value)
  }

  function openDialog(unit?: Unit) {
    setEditingId(unit?.id ?? null)
    setName(unit?.name ?? '')
    setType(unit?.type ?? '')
    setCapacity(unit ? String(unit.capacity) : '')
    setPrice(unit ? String(unit.pricePerNight) : '')
    setStatus(unit?.status ?? 'DISPONIBLE')
    setFormError(null)
    setDialogOpen(true)
  }

  function validate(): string | null {
    if (!name.trim()) return 'El nombre es obligatorio.'
    if (!type.trim()) return 'El tipo es obligatorio (por ejemplo: Cabaña, Departamento).'
    if (!capacity || !Number.isInteger(Number(capacity)) || Number(capacity) < 1) {
      return 'La capacidad debe ser un número entero mayor o igual a 1.'
    }
    if (!price || Number(price) <= 0) return 'El precio por noche debe ser mayor que 0.'
    return null
  }

  async function handleSave() {
    const validation = validate()
    if (validation) {
      setFormError(validation)
      return
    }
    const payload: UnitRequest = {
      name: name.trim(),
      type: type.trim(),
      capacity: Number(capacity),
      pricePerNight: Number(price),
      status,
    }
    setSaving(true)
    setFormError(null)
    try {
      if (editingId === null) {
        await createUnit(payload)
      } else {
        await updateUnit(editingId, payload)
      }
      setDialogOpen(false)
      setToast({
        message: editingId === null ? 'Unidad creada correctamente.' : 'Unidad actualizada correctamente.',
        severity: 'success',
      })
      loadUnits()
    } catch (e) {
      setFormError(getErrorMessage(e, 'No se pudo guardar la unidad.'))
    } finally {
      setSaving(false)
    }
  }

  const columns: GridColDef<Unit>[] = [
    { field: 'id', headerName: 'ID', width: 70 },
    { field: 'name', headerName: 'Nombre', flex: 1, minWidth: 160 },
    { field: 'type', headerName: 'Tipo', flex: 1, minWidth: 130 },
    { field: 'capacity', headerName: 'Capacidad', width: 110 },
    {
      field: 'pricePerNight', headerName: 'Precio por noche', flex: 1, minWidth: 150,
      valueFormatter: (value: number) => formatMoney(value),
    },
    {
      field: 'status', headerName: 'Estado', width: 170,
      renderCell: (params) => (
        <Chip label={params.value} color={statusColor[params.value as UnitStatus]} size="small" />
      ),
    },
    ...(isAdmin
      ? [{
          field: 'actions', headerName: 'Acciones', width: 110, sortable: false,
          renderCell: (params: { row: Unit }) => (
            <Button size="small" onClick={() => openDialog(params.row)}>Editar</Button>
          ),
        } as GridColDef<Unit>]
      : []),
  ]

  return (
    <Box>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3, flexWrap: 'wrap', gap: 2 }}>
        <Typography variant="h4">Catálogo</Typography>
        <Box sx={{ display: 'flex', gap: 1, alignItems: 'center' }}>
          <TextField
            select size="small" label="Estado" value={filter} sx={{ minWidth: 180 }}
            onChange={(e) => handleFilter(e.target.value as UnitStatus | 'TODAS')}
          >
            <MenuItem value="TODAS">Todas</MenuItem>
            {unitStatuses.map((s) => <MenuItem key={s} value={s}>{s}</MenuItem>)}
          </TextField>
          <Button startIcon={<RefreshIcon />} onClick={() => loadUnits()} disabled={loading}>Actualizar</Button>
          {isAdmin && (
            <Button variant="contained" startIcon={<AddIcon />} onClick={() => openDialog()}>
              Nueva unidad
            </Button>
          )}
        </Box>
      </Box>

      {!isAdmin && (
        <Alert severity="info" sx={{ mb: 2 }}>
          Estás en modo consulta: solo un administrador puede crear o editar unidades.
        </Alert>
      )}
      {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}

      {loading ? (
        <Box sx={{ display: 'flex', justifyContent: 'center', mt: 4 }}><CircularProgress /></Box>
      ) : (
        <Box sx={{ height: 520, bgcolor: 'background.paper', borderRadius: 2 }}>
          <DataGrid
            rows={units}
            columns={columns}
            getRowId={(row) => row.id}
            initialState={{ pagination: { paginationModel: { pageSize: 10 } } }}
            pageSizeOptions={[10, 25, 50]}
            localeText={{ noRowsLabel: 'No hay unidades para mostrar' }}
          />
        </Box>
      )}

      <Dialog open={dialogOpen} onClose={() => !saving && setDialogOpen(false)} fullWidth maxWidth="sm">
        <DialogTitle>{editingId === null ? 'Nueva unidad' : `Editar unidad #${editingId}`}</DialogTitle>
        <DialogContent sx={{ display: 'flex', flexDirection: 'column', gap: 2, mt: 1 }}>
          {formError && <Alert severity="error">{formError}</Alert>}
          <TextField label="Nombre" value={name} onChange={(e) => setName(e.target.value)} />
          <TextField label="Tipo" value={type} onChange={(e) => setType(e.target.value)} />
          <TextField label="Capacidad (personas)" type="number" value={capacity} onChange={(e) => setCapacity(e.target.value)} />
          <TextField label="Precio por noche" type="number" value={price} onChange={(e) => setPrice(e.target.value)} />
          <TextField select label="Estado" value={status} onChange={(e) => setStatus(e.target.value as UnitStatus)}>
            {unitStatuses.map((s) => <MenuItem key={s} value={s}>{s}</MenuItem>)}
          </TextField>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setDialogOpen(false)} disabled={saving}>Cancelar</Button>
          <Button variant="contained" onClick={handleSave} disabled={saving}>
            {saving ? 'Guardando...' : 'Guardar'}
          </Button>
        </DialogActions>
      </Dialog>

      <Toast toast={toast} onClose={() => setToast(null)} />
    </Box>
  )
}
