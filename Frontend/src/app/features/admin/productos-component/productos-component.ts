import { Component, OnInit, ChangeDetectorRef, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TableModule } from 'primeng/table';
import { SelectModule } from 'primeng/select';
import { InputTextModule } from 'primeng/inputtext';
import { InputNumberModule } from 'primeng/inputnumber';
import { DialogModule } from 'primeng/dialog';
import { SortEvent } from 'primeng/api';
import { Producto } from '../../../shared/interfaces/producto';
import { Categoria } from '../../../shared/interfaces/categoria';
import { ProductoService } from '../../../core/services/producto-service';
import { LoaderService } from '../../../core/services/loader-service';

@Component({
  selector: 'app-productos-component',
  standalone: true,
  imports: [CommonModule, FormsModule, TableModule, SelectModule, InputTextModule, InputNumberModule, DialogModule],
  templateUrl: './productos-component.html'
})
export class ProductosComponent implements OnInit {
  @ViewChild('dt') dt: any;

  productos: Producto[] = [];
  filteredProductos: Producto[] = [];

  filtroNombre: string = '';
  filtroEstado: number | null = null;
  filtroCategoria: number | null = null;

  estadosFiltro = [
    { label: 'Todos', value: null },
    { label: 'Activo', value: 1 },
    { label: 'Inactivo', value: 0 }
  ];

  categoriasFiltro: { label: string; value: number | null }[] = [];

  isSorted: boolean | null = null;
  private resetting = false;

  modalVisible = false;
  editando = false;
  formProducto = {
    id: null as number | null,
    nombre: '',
    descripcion: '',
    precioCompra: 0,
    precioVenta: 0,
    stock: 0,
    stockSeguridad: 0,
    id_categoria: null as number | null
  };
  nuevasImagenes: File[] = [];
  imagenesPreview: string[] = [];
  imagenesExistentes: string[] = [];
  private imagenesAEliminar: string[] = [];

  constructor(
    private productoService: ProductoService,
    private loaderService: LoaderService,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit(): void {
    this.obtenerProductos();
  }

  obtenerProductos(): void {
    this.productoService.listarProductos().subscribe({
      next: (data) => {
        this.productos = data;
        this.filteredProductos = [...data];
        this.isSorted = null;
        this.loaderService.hide();
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error(err);
        this.loaderService.hide();
      }
    });
    this.productoService.listarCategoriasActivas().subscribe({
      next: (cats) => {
        this.categoriasFiltro = [
          { label: 'Todos', value: null },
          ...cats.map(c => ({ label: c.nombre, value: c.id as number }))
        ];
        this.cdr.detectChanges();
      },
      error: (err) => console.error(err)
    });
  }

  customSort(event: SortEvent): void {
    if (this.resetting) return;

    if (this.isSorted == null) {
      this.isSorted = true;
      this.sortTableData(event);
    } else if (this.isSorted === true) {
      this.isSorted = false;
      this.sortTableData(event);
    } else {
      this.isSorted = null;
      this.resetting = true;
      this.aplicarFiltros();
      this.dt.reset();
      setTimeout(() => {
        this.resetting = false;
      }, 0);
    }
  }

  private sortTableData(event: SortEvent): void {
    this.filteredProductos.sort((data1, data2) => {
      const value1 = (data1 as any)[event.field!];
      const value2 = (data2 as any)[event.field!];
      let result: number;

      if (value1 == null && value2 != null) result = -1;
      else if (value1 != null && value2 == null) result = 1;
      else if (value1 == null && value2 == null) result = 0;
      else if (typeof value1 === 'string' && typeof value2 === 'string')
        result = value1.localeCompare(value2);
      else result = value1 < value2 ? -1 : value1 > value2 ? 1 : 0;

      return event.order! * result;
    });
  }

  aplicarFiltros(): void {
    this.isSorted = null;
    this.filteredProductos = this.productos.filter(p => {
      const coincideNombre = !this.filtroNombre ||
        p.nombre.toLowerCase().includes(this.filtroNombre.toLowerCase());
      const coincideEstado = this.filtroEstado === null || p.estado === this.filtroEstado;
      const coincideCategoria = this.filtroCategoria === null || p.categoria?.id === this.filtroCategoria;
      return coincideNombre && coincideEstado && coincideCategoria;
    });
  }

  limpiarFiltros(): void {
    this.filtroNombre = '';
    this.filtroEstado = null;
    this.filtroCategoria = null;
    this.aplicarFiltros();
  }

  obtenerImagenes(producto: Producto): string[] {
    try {
      return producto.imagen ? JSON.parse(producto.imagen) : [];
    } catch {
      return [];
    }
  }

  onImageError(event: Event): void {
    const img = event.target as HTMLImageElement;
    img.style.display = 'none';
    const parent = img.parentElement;
    if (parent) {
      const fallback = parent.querySelector('.image-fallback');
      if (fallback) {
        (fallback as HTMLElement).style.display = 'flex';
      }
    }
  }

  abrirModal(producto?: Producto): void {
    if (producto) {
      this.editando = true;
      this.formProducto = {
        id: producto.id ?? null,
        nombre: producto.nombre,
        descripcion: producto.descripcion,
        precioCompra: producto.precioCompra,
        precioVenta: producto.precioVenta,
        stock: producto.stock,
        stockSeguridad: producto.stockSeguridad,
        id_categoria: producto.categoria?.id ?? null
      };
      this.imagenesExistentes = this.obtenerImagenes(producto);
    } else {
      this.editando = false;
      this.formProducto = { id: null, nombre: '', descripcion: '', precioCompra: 0, precioVenta: 0, stock: 0, stockSeguridad: 0, id_categoria: null };
      this.imagenesExistentes = [];
    }
    this.nuevasImagenes = [];
    this.imagenesPreview = [];
    this.imagenesAEliminar = [];
    this.modalVisible = true;
  }

  onFilesSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (!input.files?.length) return;

    for (let i = 0; i < input.files.length; i++) {
      const file = input.files[i];
      this.nuevasImagenes.push(file);

      const reader = new FileReader();
      reader.onload = (e) => {
        this.imagenesPreview.push(e.target?.result as string);
        this.cdr.detectChanges();
      };
      reader.readAsDataURL(file);
    }
    input.value = '';
  }

  removerNuevaImagen(index: number): void {
    this.nuevasImagenes.splice(index, 1);
    this.imagenesPreview.splice(index, 1);
  }

  eliminarImagenExistente(url: string): void {
    if (this.formProducto.id != null) {
      this.productoService.eliminarImagen(this.formProducto.id, url).subscribe({
        next: () => {
          this.imagenesExistentes = this.imagenesExistentes.filter(u => u !== url);
        },
        error: (err) => console.error(err)
      });
    }
  }

  guardarProducto(): void {
    if (!this.formProducto.nombre.trim()) return;

    const formData = new FormData();
    if (this.formProducto.id != null) formData.append('id', this.formProducto.id.toString());
    formData.append('nombre', this.formProducto.nombre);
    formData.append('descripcion', this.formProducto.descripcion);
    formData.append('precioCompra', this.formProducto.precioCompra.toString());
    formData.append('precioVenta', this.formProducto.precioVenta.toString());
    formData.append('stock', this.formProducto.stock.toString());
    formData.append('stockSeguridad', this.formProducto.stockSeguridad.toString());

    if (this.formProducto.id_categoria != null) {
      formData.append('id_categoria', this.formProducto.id_categoria.toString());
    }

    this.nuevasImagenes.forEach(file => formData.append('imagenes', file));

    this.productoService.guardarProducto(formData).subscribe({
      next: () => {
        this.modalVisible = false;
        this.obtenerProductos();
      },
      error: (err) => console.error(err)
    });
  }

  eliminarProducto(id: number): void {
    if (confirm('¿Estás seguro de eliminar este producto?')) {
      this.productoService.eliminarProducto(id).subscribe({
        next: () => this.obtenerProductos(),
        error: (err) => console.error(err)
      });
    }
  }

  toggleEstado(id: number): void {
    this.productoService.cambiarEstado(id).subscribe({
      next: () => this.obtenerProductos(),
      error: (err) => console.error(err)
    });
  }
}
