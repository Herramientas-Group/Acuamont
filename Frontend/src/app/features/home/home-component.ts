import { Component, OnInit, OnDestroy, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { NavbarComponent } from './navbar-component/navbar-component';
import { FooterComponent } from './footer-component/footer-component';

@Component({
  selector: 'app-home-component',
  standalone: true,
  imports: [CommonModule, RouterLink, NavbarComponent, FooterComponent],
  templateUrl: './home-component.html',
})
export class HomeComponent implements OnInit, OnDestroy {
  slides: string[] = [];
  slidesError = false;
  currentSlide = 0;
  currentYear = new Date().getFullYear();
  private autoSlideTimer: ReturnType<typeof setInterval> | null = null;

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
    this.http.get<string[]>('http://localhost:8080/slides/api/listar-urls').subscribe({
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
}
