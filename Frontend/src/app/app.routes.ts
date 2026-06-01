import { Routes } from '@angular/router';
import { LoginComponent } from './features/auth/login-component/login-component';
import { HomeComponent } from './features/home/home-component';
import { ProductosPage } from './features/home/productos-page/productos-page';
import { ServiciosPage } from './features/home/servicios-page/servicios-page';
import { DashboardLayout } from './features/admin/dashboard-layout/dashboard-layout';
import { UsuarioComponent } from './features/admin/usuario-component/usuario-component';
import { DashboardComponent } from './features/admin/dashboard-component/dashboard-component';
import { InventarioComponent } from './features/admin/inventario-component/inventario-component';
import { ProveedoresComponent } from './features/admin/proveedores-component/proveedores-component';
import { VentasComponent } from './features/admin/ventas-component/ventas-component';
import { authGuard } from './core/guards/auth.guard';
import { PerfilesComponent } from './features/admin/perfiles-component/perfiles-component';

export const routes: Routes = [
  {
    path: 'inicio',
    component: HomeComponent,
    title: 'Acuamont - Bienvenidos',
  },
  {
    path: 'login',
    component: LoginComponent,
    title: 'Iniciar Sesión',
  },
  {
    path: 'ver-productos',
    component: ProductosPage,
    title: 'Productos',
  },
  {
    path: 'ver-servicios',
    component: ServiciosPage,
    title: 'Servicios',
  },
  {
    path: 'admin',
    component: DashboardLayout,
    title: 'Dashboard',
    canActivate: [authGuard],
    children: [
      { path: 'usuarios', component: UsuarioComponent, data: { title: 'Usuarios', opcionId: 2 } },
      { path: 'dashboard', component: DashboardComponent, data: { title: 'Dashboard', opcionId: 1 } },
      { path: 'inventario', component: InventarioComponent, data: { title: 'Inventario', opcionId: 9 } },
      { path: 'proveedores', component: ProveedoresComponent, data: { title: 'Proveedores', opcionId: 10 } },
      { path: 'ventas', component: VentasComponent, data: { title: 'Ventas', opcionId: 8 } },
      { path: 'perfiles', component: PerfilesComponent, data: { title: 'Perfiles', opcionId: 3 } }
    ]
  },
  { path: '', redirectTo: 'inicio', pathMatch: 'full' },
  { path: '**', redirectTo: 'inicio' },
];
