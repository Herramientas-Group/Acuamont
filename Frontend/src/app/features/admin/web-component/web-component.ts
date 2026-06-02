import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DialogModule } from 'primeng/dialog';
import { RedSocial } from '../../../shared/interfaces/gestionweb';
import { GestionwebService } from '../../../core/services/gestionweb-service';

@Component({
  selector: 'app-web-component',
  standalone: true,
  imports: [CommonModule, FormsModule, DialogModule],
  templateUrl: './web-component.html',
})
export class WebComponent implements OnInit {
  logoUrl = '';
  redes: RedSocial[] = [];
  slides: string[] = [];

  modalEditarVisible = false;
  formRed: { id: number; nombre: string; url: string } = { id: 0, nombre: '', url: '' };

  subiendoLogo = false;
  subiendoSlide = false;

  constructor(
    private gestionwebService: GestionwebService,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit(): void {
    this.cargarLogo();
    this.cargarRedes();
    this.cargarSlides();
  }

  cargarLogo(): void {
    this.gestionwebService.getLogoUrl().subscribe({
      next: (url) => {
        this.logoUrl = url;
        this.cdr.detectChanges();
      },
      error: () => {}
    });
  }

  cargarRedes(): void {
    this.gestionwebService.listarRedes().subscribe({
      next: (data) => {
        this.redes = data;
        this.cdr.detectChanges();
      },
      error: () => {}
    });
  }

  cargarSlides(): void {
    this.gestionwebService.listarSlides().subscribe({
      next: (data) => {
        this.slides = data;
        this.cdr.detectChanges();
      },
      error: () => {}
    });
  }

  onLogoSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (!input.files?.length) return;
    const file = input.files[0];
    this.subiendoLogo = true;
    this.gestionwebService.subirLogo(file).subscribe({
      next: () => {
        this.subiendoLogo = false;
        this.cargarLogo();
        input.value = '';
        this.cdr.detectChanges();
      },
      error: () => {
        this.subiendoLogo = false;
        this.cdr.detectChanges();
      }
    });
  }

  onSlideSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (!input.files?.length) return;
    const file = input.files[0];
    this.subiendoSlide = true;
    this.gestionwebService.subirSlide(file).subscribe({
      next: () => {
        this.subiendoSlide = false;
        this.cargarSlides();
        input.value = '';
        this.cdr.detectChanges();
      },
      error: () => {
        this.subiendoSlide = false;
        this.cdr.detectChanges();
      }
    });
  }

  abrirModalEditar(red: RedSocial): void {
    this.formRed = { id: red.id, nombre: red.nombre, url: red.url };
    this.modalEditarVisible = true;
  }

  guardarURL(): void {
    this.gestionwebService.actualizarRed(this.formRed.id, this.formRed.url).subscribe({
      next: () => {
        this.modalEditarVisible = false;
        this.cargarRedes();
      },
      error: () => {}
    });
  }

  toggleEstado(id: number): void {
    this.gestionwebService.toggleEstadoRed(id).subscribe({
      next: () => this.cargarRedes(),
      error: () => {}
    });
  }

  eliminarSlide(url: string): void {
    const nombre = url.substring(url.lastIndexOf('/') + 1);
    this.gestionwebService.eliminarSlide(nombre).subscribe({
      next: () => {
        this.slides = this.slides.filter(s => s !== url);
        this.cdr.detectChanges();
      },
      error: () => {}
    });
  }

  onImageError(event: Event): void {
    (event.target as HTMLImageElement).style.display = 'none';
  }
}
