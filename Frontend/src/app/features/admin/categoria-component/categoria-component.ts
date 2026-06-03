import { Component, OnInit, ChangeDetectorRef, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TableModule } from 'primeng/table';
import { SelectModule } from 'primeng/select';
import { InputTextModule } from 'primeng/inputtext';
import { DialogModule } from 'primeng/dialog';
import { ConfirmationService, SortEvent } from 'primeng/api';
import { Categoria } from '../../../shared/interfaces/categoria';
import { CategoriaService } from '../../../core/services/categoria-service';
import { LoaderService } from '../../../core/services/loader-service';
import { ConfirmDialog } from 'primeng/confirmdialog';

@Component({
  selector: 'app-categoria-component',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    TableModule,
    SelectModule,
    InputTextModule,
    DialogModule,
    ConfirmDialog,
  ],
  templateUrl: './categoria-component.html',
})
export class CategoriaComponent implements OnInit {
  @ViewChild('dt') dt: any;

  categorias: Categoria[] = [];
  filteredCategorias: Categoria[] = [];

  filtroNombre: string = '';
  filtroEstado: number | null = null;

  estadosFiltro = [
    { label: 'Todos', value: null },
    { label: 'Activo', value: 1 },
    { label: 'Inactivo', value: 0 },
  ];

  isSorted: boolean | null = null;
  private resetting = false;

  modalVisible = false;
  editando = false;
  formCategoria: Categoria = { nombre: '', estado: 1 };

  constructor(
    private categoriaService: CategoriaService,
    private loaderService: LoaderService,
    private cdr: ChangeDetectorRef,
    private confirmationService: ConfirmationService,
  ) {}

  ngOnInit(): void {
    this.obtenerCategorias();
  }

  obtenerCategorias(): void {
    this.categoriaService.listarCategorias().subscribe({
      next: (data) => {
        this.categorias = data;
        this.filteredCategorias = [...data];
        this.isSorted = null;
        this.loaderService.hide();
        this.cdr.markForCheck();
      },
      error: (err) => {
        console.error(err);
        this.loaderService.hide();
        this.cdr.markForCheck();
      },
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
    this.filteredCategorias.sort((data1, data2) => {
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
    this.filteredCategorias = this.categorias.filter((c) => {
      const coincideNombre =
        !this.filtroNombre || c.nombre.toLowerCase().includes(this.filtroNombre.toLowerCase());
      const coincideEstado = this.filtroEstado === null || c.estado === this.filtroEstado;
      return coincideNombre && coincideEstado;
    });
  }

  limpiarFiltros(): void {
    this.filtroNombre = '';
    this.filtroEstado = null;
    this.aplicarFiltros();
  }

  abrirModal(categoria?: Categoria): void {
    if (categoria) {
      this.editando = true;
      this.formCategoria = { ...categoria };
    } else {
      this.editando = false;
      this.formCategoria = { nombre: '', estado: 1 };
    }
    Promise.resolve().then(() => {
      this.modalVisible = true;
      this.cdr.markForCheck();
    });
  }

  cerrarModal(): void {
    Promise.resolve().then(() => {
      this.modalVisible = false;
      this.cdr.markForCheck();
    });
  }

  guardarCategoria(): void {
    if (!this.formCategoria.nombre?.trim()) return;
    this.categoriaService.guardarCategoria(this.formCategoria).subscribe({
      next: () => {
        this.cerrarModal();
        this.obtenerCategorias();
      },
      error: (err) => console.error(err),
    });
  }

  eliminarCategoria(id: number): void {
    this.confirmationService.confirm({
      message: '¿Estás seguro de eliminar esta categoría?',
      header: 'Confirmar Eliminación',
      icon: 'pi pi-exclamation-triangle',

      // Estilos de botones para mantener la consistencia
      acceptLabel: 'Sí, eliminar',
      acceptButtonStyleClass: 'p-button-danger p-button-text',
      rejectLabel: 'Cancelar',
      rejectButtonStyleClass: 'p-button-secondary p-button-text',

      // Acción en caso de éxito
      accept: () => {
        this.categoriaService.eliminarCategoria(id).subscribe({
          next: () => this.obtenerCategorias(),
          error: (err) => console.error(err),
        });
      },
    });
  }

  toggleEstado(id: number): void {
    this.categoriaService.cambiarEstado(id).subscribe({
      next: () => this.obtenerCategorias(),
      error: (err) => console.error(err),
    });
  }
}
