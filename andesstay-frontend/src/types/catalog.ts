export type UnitStatus = 'DISPONIBLE' | 'OCUPADA' | 'MANTENIMIENTO'

export interface Unit {
  id: number
  name: string
  type: string
  capacity: number
  pricePerNight: number
  status: UnitStatus
  createdAt: string
}

export interface UnitRequest {
  name: string
  type: string
  capacity: number
  pricePerNight: number
  status: UnitStatus
}
