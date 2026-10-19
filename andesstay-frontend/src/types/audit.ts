export interface AuditEvent {
  id: number
  reservationId: number
  eventType: string
  previousStatus: string | null
  newStatus: string | null
  actorId: string | null
  eventTimestamp: string
  traceId: string | null
}

export interface AuditFilters {
  actorId: string
  eventType: string
  from: string
  to: string
}
