import apiClient from './client'
import type { Reservation, ReservationCreateRequest, ReservationStatus } from '../types/reservation'

const BASE = '/api/bff/reservations/api/reservations'

export async function getReservations(): Promise<Reservation[]> {
  const { data } = await apiClient.get<Reservation[]>(BASE)
  return data
}

export async function createReservation(payload: ReservationCreateRequest): Promise<Reservation> {
  const { data } = await apiClient.post<Reservation>(BASE, payload)
  return data
}

export async function updateReservationStatus(id: number, status: ReservationStatus): Promise<Reservation> {
  const { data } = await apiClient.put<Reservation>(`${BASE}/${id}/status`, { status })
  return data
}