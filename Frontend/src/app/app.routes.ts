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
import { CategoriaComponent } from './features/admin/categoria-component/categoria-component';
import { ProductosComponent } from './features/admin/productos-component/productos-component';
import { WebComponent } from './features/admin/web-component/web-component';
import { ClientesComponent } from './features/admin/clientes-component/clientes-component';
import { UtilidadesComponent } from './features/admin/utilidades-component/utilidades-component';
import { ContactoComponent } from './features/home/contacto-component/contacto-component';
import { ComentariosComponent } from './features/home/comentarios-component/comentarios-component';

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
    path: 'contacto',
    component: ContactoComponent,
    title: 'Contacto',
  },
  {
    path: 'comentarios',
    component: ComentariosComponent,
    title: 'Comentarios',
  },
  {
    path: 'admin',
    component: DashboardLayout,
    title: 'Dashboard',
    canActivateChild: [authGuard],
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      { path: 'dashboard', component: DashboardComponent, data: { title: 'Dashboard', opcionId: 1 } },
      { path: 'usuarios/listar', component: UsuarioComponent, data: { title: 'Usuarios', opcionId: 2 } },
      { path: 'perfiles/listar', component: PerfilesComponent, data: { title: 'Perfiles', opcionId: 3 } },
      { path: 'categorias/listar', component: CategoriaComponent, data: { title: 'Categorías', opcionId: 4 } },
      { path: 'productos/listar', component: ProductosComponent, data: { title: 'Productos', opcionId: 5 } },
      { path: 'slides/listar', component: WebComponent, data: { title: 'Gestión Web', opcionId: 6 } },
      { path: 'clientes/listar', component: ClientesComponent, data: { title: 'Clientes', opcionId: 7 } },
      { path: 'ventas/listar', component: VentasComponent, data: { title: 'Ventas', opcionId: 8 } },
      { path: 'inventario/listar', component: InventarioComponent, data: { title: 'Inventario', opcionId: 9 } },
      { path: 'proveedores/listar', component: ProveedoresComponent, data: { title: 'Proveedores', opcionId: 10 } },
      { path: 'reportes/listar', component: UtilidadesComponent, data: { title: 'Reportes Utilidades', opcionId: 11 } }
    ]
  },
  { path: '', redirectTo: 'inicio', pathMatch: 'full' },
  { path: '**', redirectTo: 'inicio' },
];
