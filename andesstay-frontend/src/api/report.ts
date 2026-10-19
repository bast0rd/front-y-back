import apiClient from './client'
import type { Kpis, ReportRange, TopUnit } from '../types/report'

const BASE = '/api/bff/report/api/report'

export async function getKpis(range: ReportRange): Promise<Kpis> {
  const { data } = await apiClient.get<Kpis>(`${BASE}/kpis`, { params: { range } })
  return data
}

export async function getTopUnits(range: ReportRange): Promise<TopUnit[]> {
  const { data } = await apiClient.get<TopUnit[]>(`${BASE}/top-units`, { params: { range } })
  return data
}
