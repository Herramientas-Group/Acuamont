import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Comentario } from '../../../shared/interfaces/comentario';
import { ComentarioService } from '../../../core/services/comentario-service';
import { NavbarComponent } from '../navbar-component/navbar-component';
import { FooterComponent } from '../footer-component/footer-component';

@Component({
  selector: 'app-comentarios-component',
  standalone: true,
  imports: [CommonModule, FormsModule, NavbarComponent, FooterComponent],
  templateUrl: './comentarios-component.html'
})
export class ComentariosComponent implements OnInit {
  comentarios: Comentario[] = [];
  formNombre = '';
  formMensaje = '';
  archivoImagen: File | null = null;
  enviando = false;

  constructor(
    private comentarioService: ComentarioService,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit(): void {
    this.cargarComentarios();
  }

  cargarComentarios(): void {
    this.comentarioService.listarComentarios().subscribe({
      next: (data) => {
        this.comentarios = data.filter(c => c.estado === 1);
        this.cdr.detectChanges();
      },
      error: () => {}
    });
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files?.length) {
      this.archivoImagen = input.files[0];
    }
  }

  enviarComentario(): void {
    if (!this.formNombre.trim() || !this.formMensaje.trim()) return;
    this.enviando = true;

    const formData = new FormData();
    formData.append('nombre', this.formNombre);
    formData.append('mensaje', this.formMensaje);
    if (this.archivoImagen) {
      formData.append('imagen', this.archivoImagen);
    }

    this.comentarioService.guardarComentario(formData).subscribe({
      next: () => {
        this.enviando = false;
        this.formNombre = '';
        this.formMensaje = '';
        this.archivoImagen = null;
        this.cargarComentarios();
        this.cdr.detectChanges();
      },
      error: () => {
        this.enviando = false;
        this.cdr.detectChanges();
      }
    });
  }

  getInitial(nombre: string): string {
    return nombre.charAt(0).toUpperCase();
  }

  onImageError(event: Event): void {
    (event.target as HTMLImageElement).style.display = 'none';
  }

  formatDate(fecha: string): string {
    if (!fecha) return '';
    const d = new Date(fecha);
    return d.toLocaleDateString('es-PE', { year: 'numeric', month: 'short', day: 'numeric' });
  }
}
