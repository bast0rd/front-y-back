import { Box, Typography } from '@mui/material'

interface HourBarsProps {
  /** Reservas por hora del día; la clave es la hora (0-23) */
  data: Record<string, number>
}

const MAX_BAR_HEIGHT = 120

export function HourBars({ data }: HourBarsProps) {
  const hours = Array.from({ length: 24 }, (_, hour) => ({ hour, value: data[String(hour)] ?? 0 }))
  const max = Math.max(1, ...hours.map((h) => h.value))

  return (
    <Box sx={{ display: 'flex', alignItems: 'flex-end', gap: 0.5, overflowX: 'auto', pb: 1 }}>
      {hours.map(({ hour, value }) => (
        <Box
          key={hour}
          title={`${hour}:00 h · ${value} reserva(s)`}
          sx={{ flex: 1, minWidth: 18, display: 'flex', flexDirection: 'column', alignItems: 'center' }}
        >
          <Typography variant="caption" sx={{ fontSize: 10, height: 14 }}>
            {value > 0 ? value : ''}
          </Typography>
          <Box
            sx={{
              width: '100%',
              height: `${Math.max(value > 0 ? 4 : 1, Math.round((value / max) * MAX_BAR_HEIGHT))}px`,
              bgcolor: value > 0 ? 'primary.main' : 'divider',
              borderRadius: '2px 2px 0 0',
            }}
          />
          <Typography variant="caption" sx={{ fontSize: 10 }}>
            {hour}
          </Typography>
        </Box>
      ))}
    </Box>
  )
}
