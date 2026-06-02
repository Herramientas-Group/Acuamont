import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NavbarComponent } from '../navbar-component/navbar-component';
import { FooterComponent } from '../footer-component/footer-component';
import { RedSocial } from '../../../shared/interfaces/gestionweb';
import { GestionwebService } from '../../../core/services/gestionweb-service';

@Component({
  selector: 'app-contacto-component',
  standalone: true,
  imports: [CommonModule, NavbarComponent, FooterComponent],
  templateUrl: './contacto-component.html'
})
export class ContactoComponent implements OnInit {
  redes: RedSocial[] = [];

  constructor(
    private gestionwebService: GestionwebService,
    private cdr: ChangeDetectorRef
  ) { }

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
