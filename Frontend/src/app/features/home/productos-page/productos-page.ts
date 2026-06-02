import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DialogModule } from 'primeng/dialog';
import { InputNumberModule } from 'primeng/inputnumber';
import { Producto } from '../../../shared/interfaces/producto';
import { Categoria } from '../../../shared/interfaces/categoria';
import { ProductoService } from '../../../core/services/producto-service';
import { CarritoService } from '../../../core/services/carrito-service';
import { NavbarComponent } from '../navbar-component/navbar-component';
import { FooterComponent } from '../footer-component/footer-component';

@Component({
  selector: 'app-productos-page',
  standalone: true,
  imports: [CommonModule, FormsModule, DialogModule, InputNumberModule, NavbarComponent, FooterComponent],
  templateUrl: './productos-page.html',
})
export class ProductosPage implements OnInit {
  productos: Producto[] = [];
  filteredProductos: Producto[] = [];
  categorias: Categoria[] = [];

  filtroNombre = '';
  filtroCategoria = 0;
  orden = 0;

  modalDetalleVisible = false;
  productoSeleccionado: Producto | null = null;
  cantidadDetalle = 1;

  modalCarritoVisible = false;
  carritoCount = 0;

  constructor(
    private productoService: ProductoService,
    public carritoService: CarritoService,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit(): void {
    this.cargarProductos();
    this.cargarCategorias();
    this.carritoService.items$.subscribe(() => {
      this.carritoCount = this.carritoService.getCount();
      this.cdr.detectChanges();
    });
  }

  cargarProductos(): void {
    this.productoService.listarProductos().subscribe({
      next: (data) => {
        this.productos = data.filter(p => p.estado === 1);
        this.aplicarFiltros();
        this.cdr.detectChanges();
      },
      error: () => {}
    });
  }

  cargarCategorias(): void {
    this.productoService.listarCategoriasActivas().subscribe({
      next: (data) => {
        this.categorias = data;
        this.cdr.detectChanges();
      },
      error: () => {}
    });
  }

  aplicarFiltros(): void {
    this.filteredProductos = this.productos.filter(p => {
      const coincideNombre = !this.filtroNombre ||
        p.nombre.toLowerCase().includes(this.filtroNombre.toLowerCase());
      const coincideCategoria = !this.filtroCategoria || p.categoria?.id === this.filtroCategoria;
      return coincideNombre && coincideCategoria;
    });
    this.ordenarProductos();
  }

  private ordenarProductos(): void {
    switch (this.orden) {
      case 1: this.filteredProductos.sort((a, b) => a.nombre.localeCompare(b.nombre)); break;
      case 2: this.filteredProductos.sort((a, b) => b.nombre.localeCompare(a.nombre)); break;
      case 3: this.filteredProductos.sort((a, b) => a.precioVenta - b.precioVenta); break;
      case 4: this.filteredProductos.sort((a, b) => b.precioVenta - a.precioVenta); break;
    }
  }

  abrirDetalle(producto: Producto): void {
    this.productoSeleccionado = producto;
    this.cantidadDetalle = 1;
    this.modalDetalleVisible = true;
  }

  agregarAlCarrito(producto: Producto, cantidad: number = 1): void {
    this.carritoService.agregar(producto, cantidad);
    if (this.modalDetalleVisible) {
      this.modalDetalleVisible = false;
    }
  }

  eliminarDelCarrito(productoId: number): void {
    this.carritoService.eliminar(productoId);
  }

  vaciarCarrito(): void {
    this.carritoService.vaciar();
  }

  obtenerPrimeraImagen(producto: Producto): string | null {
    try {
      const urls: string[] = producto.imagen ? JSON.parse(producto.imagen) : [];
      return urls.length > 0 ? urls[0] : null;
    } catch {
      return null;
    }
  }

  onImageError(event: Event): void {
    const img = event.target as HTMLImageElement;
    img.style.display = 'none';
    const parent = img.parentElement;
    if (parent) {
      const fallback = parent.querySelector('.fallback-icon') as HTMLElement;
      if (fallback) fallback.classList.remove('hidden');
    }
  }
}
