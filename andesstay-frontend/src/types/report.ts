export type ReportRange = 'last24h' | 'last7d'

export interface Kpis {
  range: string
  totalReservations: number
  activeOccupancy: number
  avgCycleTimeHours: number
  /** La clave es la hora del día (0-23) */
  reservationsByHour: Record<string, number>
}

export interface TopUnit {
  unitId: number
  totalReservations: number
}
