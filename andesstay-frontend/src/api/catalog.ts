import apiClient from './client'
import type { Unit, UnitRequest, UnitStatus } from '../types/catalog'

const BASE = '/api/bff/catalog/api/catalog/units'

export async function getUnits(status?: UnitStatus): Promise<Unit[]> {
  const { data } = await apiClient.get<Unit[]>(BASE, { params: status ? { status } : undefined })
  return data
}

export async function createUnit(payload: UnitRequest): Promise<Unit> {
  const { data } = await apiClient.post<Unit>(BASE, payload)
  return data
}

export async function updateUnit(id: number, payload: UnitRequest): Promise<Unit> {
  const { data } = await apiClient.put<Unit>(`${BASE}/${id}`, payload)
  return data
}
