export type ReservationStatus =
  | 'CREADA'
  | 'CONFIRMADA'
  | 'CHECKIN_PENDIENTE'
  | 'EN_ESTADIA'
  | 'CHECKOUT'
  | 'CANCELADA'

export interface Reservation {
  id: number
  guestId: string
  unitId: number
  status: ReservationStatus
  startDate: string
  endDate: string
  createdAt: string
}

export interface ReservationCreateRequest {
  guestId: string
  unitId: number
  startDate: string
  endDate: string
}