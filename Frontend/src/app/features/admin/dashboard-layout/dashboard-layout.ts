import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { RouterOutlet, Router, NavigationEnd, ActivatedRoute, RouterLink, RouterLinkActive } from '@angular/router';
import { CommonModule } from '@angular/common';
import { filter, map, mergeMap } from 'rxjs/operators';
import { AuthService } from '../../../core/services/auth-service';
import { Opcion } from '../../../shared/interfaces/perfil';

@Component({
  selector: 'app-dashboard-layout',
  standalone: true,
  imports: [RouterOutlet, CommonModule, RouterLink, RouterLinkActive],
  templateUrl: './dashboard-layout.html',
})
export class DashboardLayout implements OnInit {
  titulo: string = 'Panel Administrativo';
  menuOpciones: Opcion[] = [];
  nombreUsuario: string = '';
  perfilUsuario: string = '';

  constructor(
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private cdr: ChangeDetectorRef,
    private authService: AuthService
  ) { }

  ngOnInit(): void {
    this.menuOpciones = this.transformarRutas(this.authService.getOpciones());
    this.nombreUsuario = this.authService.getNombre() || '';
    this.perfilUsuario = this.authService.getPerfil() || '';

    this.authService.opciones$.subscribe(opciones => {
      this.menuOpciones = this.transformarRutas(opciones);
      this.nombreUsuario = this.authService.getNombre() || '';
      this.perfilUsuario = this.authService.getPerfil() || '';
      this.cdr.detectChanges();
    });

    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd),
      map(() => this.activatedRoute),
      map(route => {
        while (route.firstChild) route = route.firstChild;
        return route;
      }),
      mergeMap(route => route.data)
    ).subscribe(data => {
      this.titulo = data['title'] || 'Panel Administrativo';
      this.cdr.detectChanges();
    });

    this.actualizarTituloManual();
  }

  private actualizarTituloManual(): void {
    let route = this.activatedRoute.root;
    while (route.firstChild) route = route.firstChild;
    route.data.subscribe(data => {
      this.titulo = data['title'] || 'Panel Administrativo';
      this.cdr.detectChanges();
    });
  }

  private transformarRutas(opciones: Opcion[]): Opcion[] {
    return opciones.map(op => ({
      ...op,
      ruta: op.ruta === '/' ? '/admin/dashboard' : '/admin' + op.ruta
    }));
  }

  cerrarSesion(): void {
    this.authService.logout();
  }
}