import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { RedSocial } from '../../../shared/interfaces/gestionweb';
import { GestionwebService } from '../../../core/services/gestionweb-service';
import { AuthService } from '../../../core/services/auth-service';

@Component({
  selector: 'app-footer-component',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './footer-component.html',
})
export class FooterComponent implements OnInit {
  redes: RedSocial[] = [];

  constructor(
    private gestionwebService: GestionwebService,
    private cdr: ChangeDetectorRef,
    private authService: AuthService
  ) { }

  get loginLink(): string {
    return this.authService.isLoggedIn() ? '/admin/dashboard' : '/login';
  }

  ngOnInit(): void {
    this.gestionwebService.listarRedesActivas().subscribe({
      next: (data) => {
        this.redes = data;
        this.cdr.detectChanges();
      },
      error: () => {}
    });
  }
}
