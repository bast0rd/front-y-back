import apiClient from './client'
import type { AuditEvent, AuditFilters } from '../types/audit'

const BASE = '/api/bff/audit/api/audit'

/** El input datetime-local entrega "yyyy-MM-ddTHH:mm"; el backend espera segundos. */
function toIso(value: string): string {
  return value.length === 16 ? `${value}:00` : value
}

export async function getAuditEvents(filters: AuditFilters): Promise<AuditEvent[]> {
  const params: Record<string, string> = {}
  if (filters.actorId.trim()) params.actorId = filters.actorId.trim()
  if (filters.eventType.trim()) params.eventType = filters.eventType.trim()
  if (filters.from) params.from = toIso(filters.from)
  if (filters.to) params.to = toIso(filters.to)

  const { data } = await apiClient.get<AuditEvent[]>(`${BASE}/events`, { params })
  return data
}

export async function getReservationTimeline(reservationId: number): Promise<AuditEvent[]> {
  const { data } = await apiClient.get<AuditEvent[]>(`${BASE}/reservations/${reservationId}`)
  return data
}
