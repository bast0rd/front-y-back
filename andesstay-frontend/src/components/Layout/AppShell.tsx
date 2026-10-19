import { useState } from 'react'
import type { ReactNode } from 'react'
import { Outlet, useNavigate, useLocation } from 'react-router-dom'
import {
  AppBar, Toolbar, Typography, Drawer, List, ListItemButton, ListItemIcon, ListItemText,
  Box, IconButton, Avatar, Menu, MenuItem, Divider
} from '@mui/material'
import DashboardIcon from '@mui/icons-material/Dashboard'
import EventNoteIcon from '@mui/icons-material/EventNote'
import ApartmentIcon from '@mui/icons-material/Apartment'
import AssessmentIcon from '@mui/icons-material/Assessment'
import HistoryIcon from '@mui/icons-material/History'
import { useAuth } from '../../hooks/useAuth'

const drawerWidth = 240

interface NavItem {
  label: string
  path: string
  icon: ReactNode
  roles: string[]
}

const navItems: NavItem[] = [
  { label: 'Dashboard', path: '/dashboard', icon: <DashboardIcon />, roles: ['Admin', 'Operador', 'Cliente'] },
  { label: 'Reservas', path: '/reservations', icon: <EventNoteIcon />, roles: ['Admin', 'Operador', 'Cliente'] },
  { label: 'Catálogo', path: '/catalog', icon: <ApartmentIcon />, roles: ['Admin', 'Operador'] },
  { label: 'Reportería', path: '/reports', icon: <AssessmentIcon />, roles: ['Admin'] },
  { label: 'Auditoría', path: '/audit', icon: <HistoryIcon />, roles: ['Admin', 'Auditor'] },
]

export function AppShell() {
  const navigate = useNavigate()
  const location = useLocation()
  const { name, roles, hasRole, logout } = useAuth()
  const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null)

  const visibleItems = navItems.filter((item) => hasRole(...item.roles))

  return (
    <Box sx={{ display: 'flex' }}>
      <AppBar
        position="fixed"
        sx={{ zIndex: (t) => t.zIndex.drawer + 1, bgcolor: 'background.paper', color: 'text.primary' }}
      >
        <Toolbar>
          <Typography variant="h6" sx={{ flexGrow: 1, fontWeight: 700 }}>
            AndesStay
          </Typography>
          <IconButton onClick={(e) => setAnchorEl(e.currentTarget)}>
            <Avatar sx={{ width: 32, height: 32, bgcolor: 'primary.main' }}>
              {name ? name.charAt(0).toUpperCase() : '?'}
            </Avatar>
          </IconButton>
          <Menu anchorEl={anchorEl} open={Boolean(anchorEl)} onClose={() => setAnchorEl(null)}>
            <MenuItem disabled>{name}</MenuItem>
            <MenuItem disabled>{roles.join(', ')}</MenuItem>
            <Divider />
            <MenuItem onClick={logout}>Cerrar sesión</MenuItem>
          </Menu>
        </Toolbar>
      </AppBar>
      <Drawer
        variant="permanent"
        sx={{
          width: drawerWidth,
          flexShrink: 0,
          '& .MuiDrawer-paper': { width: drawerWidth, boxSizing: 'border-box' },
        }}
      >
        <Toolbar />
        <List>
          {visibleItems.map((item) => (
            <ListItemButton
              key={item.path}
              selected={location.pathname === item.path}
              onClick={() => navigate(item.path)}
            >
              <ListItemIcon>{item.icon}</ListItemIcon>
              <ListItemText primary={item.label} />
            </ListItemButton>
          ))}
        </List>
      </Drawer>
      <Box component="main" sx={{ flexGrow: 1, p: 3, bgcolor: 'background.default', minHeight: '100vh' }}>
        <Toolbar />
        <Outlet />
      </Box>
    </Box>
  )
}