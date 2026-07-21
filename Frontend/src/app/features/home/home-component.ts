import { Component, OnInit, OnDestroy, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { NavbarComponent } from './navbar-component/navbar-component';
import { FooterComponent } from './footer-component/footer-component';
import { DialogModule } from 'primeng/dialog';
import { environment } from '../../../environments/environment';

interface Local {
  nombre: string;
  direccion: string;
  descripcion: string;
  productos: string[];
  horarios: { dias: string; horas: string }[];
}

@Component({
  selector: 'app-home-component',
  standalone: true,
  imports: [CommonModule, RouterLink, NavbarComponent, FooterComponent, DialogModule],
  templateUrl: './home-component.html',
})
export class HomeComponent implements OnInit, OnDestroy {
  slides: string[] = [];
  slidesError = false;
  currentSlide = 0;
  currentYear = new Date().getFullYear();
  private autoSlideTimer: ReturnType<typeof setInterval> | null = null;

  dialogVisible = false;
  localSeleccionado: Local | null = null;

  locales: Local[] = [
    {
      nombre: 'La Victoria',
      direccion: 'Puerto de Palos 182',
      descripcion: 'Local especializado en peces ornamentales, con gran variedad de especies para todo tipo de acuarios.',
      productos: ['Peces ornamentales', 'Alimento para peces'],
      horarios: [{ dias: 'Lunes - Sábado', horas: '10am - 10pm' }],
    },
    {
      nombre: 'Reque',
      direccion: 'Prol. Leoncio Prado 98',
      descripcion: 'Local dedicado a decoraciones, motores, medicamentos y accesorios para mantener tu acuario completo y saludable.',
      productos: ['Adornos para acuarios', 'Motores y filtros', 'Accesorios', 'Alimentos'],
      horarios: [{ dias: 'Lunes - Sábado', horas: '10am - 8pm' }],
    },
  ];

  servicios = [
    {
      nombre: 'Venta de Peces Ornamentales',
      icono: 'https://res.cloudinary.com/dukl00gcz/image/upload/v1780381878/fish_isrhbc.png'
    },
    {
      nombre: 'Decoración de Acuarios',
      icono: 'https://res.cloudinary.com/dukl00gcz/image/upload/v1780381876/hojas_wubh1r.png'
    },
    {
      nombre: 'Asesoría Personalizada',
      icono: 'https://res.cloudinary.com/dukl00gcz/image/upload/v1780381875/asesor_eazjot.png'
    },
  ];

  constructor(
    private http: HttpClient,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {
    this.cargarSlides();
  }

  ngOnDestroy(): void {
    this.stopAutoSlide();
  }

  private cargarSlides(): void {
    this.http.get<string[]>(`${environment.apiUrl}/slides/api/listar-urls`).subscribe({
      next: (urls) => {
        this.slides = urls || [];
        this.startAutoSlide();
        this.cdr.detectChanges();
      },
      error: () => {
        this.slidesError = true;
        this.cdr.detectChanges();
      },
    });
  }

  prevSlide(): void {
    this.goToSlide(this.currentSlide - 1);
  }

  nextSlide(): void {
    this.goToSlide(this.currentSlide + 1);
  }

  goToSlide(index: number): void {
    const total = this.slides.length;
    if (total === 0) return;
    this.currentSlide = ((index % total) + total) % total;
    this.resetAutoSlide();
  }

  private startAutoSlide(): void {
    if (this.slides.length > 1) {
      this.autoSlideTimer = setInterval(() => {
        this.nextSlide();
        this.cdr.detectChanges();
      }, 7000);
    }
  }

  private stopAutoSlide(): void {
    if (this.autoSlideTimer) {
      clearInterval(this.autoSlideTimer);
      this.autoSlideTimer = null;
    }
  }

  private resetAutoSlide(): void {
    this.stopAutoSlide();
    this.startAutoSlide();
  }

  onSlideError(event: Event): void {
    (event.target as HTMLImageElement).src = 'https://placehold.co/800x400?text=Error+Carga';
  }

  onPromoError(event: Event): void {
    (event.target as HTMLImageElement).style.display = 'none';
  }

  mostrarDetalleLocal(index: number): void {
    this.localSeleccionado = this.locales[index];
    this.dialogVisible = true;
  }
}
