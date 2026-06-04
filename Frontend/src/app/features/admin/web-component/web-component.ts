import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DialogModule } from 'primeng/dialog';
import { ToastModule } from 'primeng/toast';
import { MessageService } from 'primeng/api';
import { RedSocial } from '../../../shared/interfaces/gestionweb';
import { GestionwebService } from '../../../core/services/gestionweb-service';
import { LoaderService } from '../../../core/services/loader-service';

@Component({
  selector: 'app-web-component',
  standalone: true,
  imports: [CommonModule, FormsModule, DialogModule, ToastModule],
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
    private messageService: MessageService,
    private loaderService: LoaderService,
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

  private readonly MAX_SIZE_MB = 20;

  private validarTamaño(file: File, input: HTMLInputElement): boolean {
    const maxBytes = this.MAX_SIZE_MB * 1024 * 1024;
    if (file.size > maxBytes) {
      this.messageService.add({
        severity: 'error', summary: 'Imagen demasiado grande',
        detail: `La imagen no puede superar ${this.MAX_SIZE_MB}MB (tamaño: ${(file.size / 1024 / 1024).toFixed(2)}MB)`,
      });
      input.value = '';
      this.cdr.detectChanges();
      return false;
    }
    return true;
  }

  onLogoSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (!input.files?.length) return;
    const file = input.files[0];
    if (!this.validarTamaño(file, input)) return;
    this.subiendoLogo = true;
    this.loaderService.show();
    this.gestionwebService.subirLogo(file).subscribe({
      next: () => {
        this.subiendoLogo = false;
        this.loaderService.hide();
        this.cargarLogo();
        input.value = '';
        this.messageService.add({ severity: 'success', summary: 'Logo actualizado', detail: 'El logo se subió correctamente' });
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.subiendoLogo = false;
        this.loaderService.hide();
        this.messageService.add({ severity: 'error', summary: 'Error', detail: err.error?.message || 'No se pudo subir el logo' });
        this.cdr.detectChanges();
      }
    });
  }

  onSlideSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (!input.files?.length) return;
    const file = input.files[0];
    if (!this.validarTamaño(file, input)) return;
    this.subiendoSlide = true;
    this.loaderService.show();
    this.gestionwebService.subirSlide(file).subscribe({
      next: (res) => {
        this.subiendoSlide = false;
        this.loaderService.hide();
        this.cargarSlides();
        input.value = '';
        this.messageService.add({ severity: 'success', summary: 'Slide subido', detail: res.message || 'El slide se subió correctamente' });
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.subiendoSlide = false;
        this.loaderService.hide();
        this.messageService.add({ severity: 'error', summary: 'Error', detail: err.error?.message || 'No se pudo subir el slide' });
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
