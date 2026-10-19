import { useMsal, useIsAuthenticated } from '@azure/msal-react'
import { useMemo } from 'react'

interface IdTokenClaims {
  roles?: string[]
  name?: string
}

export function useAuth() {
  const { instance, accounts } = useMsal()
  const isAuthenticated = useIsAuthenticated()

  const account = accounts[0]
  const claims = account?.idTokenClaims as IdTokenClaims | undefined

  const roles = useMemo(() => claims?.roles ?? [], [claims])
  const name = claims?.name ?? account?.name ?? ''

  function hasRole(...allowed: string[]) {
    return allowed.some((r) => roles.includes(r))
  }

  function login() {
    instance.loginRedirect()
  }

  function logout() {
    instance.logoutRedirect()
  }

  return { isAuthenticated, account, roles, name, hasRole, login, logout }
}