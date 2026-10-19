import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { BrowserRouter } from 'react-router-dom'
import { PublicClientApplication, EventType } from '@azure/msal-browser'
import { MsalProvider } from '@azure/msal-react'
import { ThemeProvider, CssBaseline } from '@mui/material'
import { msalConfig } from './authConfig'
import { theme } from './theme'
import './index.css'
import App from './App.tsx'
import { attachAuthInterceptor } from './api/client'

const msalInstance = new PublicClientApplication(msalConfig)
attachAuthInterceptor(msalInstance)

msalInstance.initialize().then(() => {
  const accounts = msalInstance.getAllAccounts()
  if (accounts.length > 0) {
    msalInstance.setActiveAccount(accounts[0])
  }

  msalInstance.addEventCallback((event) => {
    if (event.eventType === EventType.LOGIN_SUCCESS && event.payload) {
      const account = (event.payload as { account?: unknown }).account
      if (account) {
        msalInstance.setActiveAccount(account as Parameters<typeof msalInstance.setActiveAccount>[0])
      }
    }
  })

  createRoot(document.getElementById('root')!).render(
    <StrictMode>
      <MsalProvider instance={msalInstance}>
        <ThemeProvider theme={theme}>
          <CssBaseline />
          <BrowserRouter>
            <App />
          </BrowserRouter>
        </ThemeProvider>
      </MsalProvider>
    </StrictMode>,
  )
})